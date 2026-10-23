package com.avito.logistics.service;

import com.avito.logistics.model.dto.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class ConsolidationService {

    public List<HblGroupDto> groupHblDocuments(List<ExtractedPackingListDto> documents, List<String> groupHints) {
        if (groupHints != null && groupHints.size() != documents.size()) {
            throw new IllegalArgumentException("Received " + groupHints.size() + " group ids for " + documents.size() + " documents.");
        }

        Map<String, List<ExtractedPackingListDto>> buckets = new LinkedHashMap<>();

        for (int i = 0; i < documents.size(); i++) {
            ExtractedPackingListDto doc = documents.get(i);
            String hint = (groupHints != null && i < groupHints.size() && groupHints.get(i) != null && !groupHints.get(i).isBlank())
                    ? groupHints.get(i).trim() : null;

            String key;
            if (hint != null) {
                key = "client:" + hint;
            } else {
                String partyKey = buildPartyKey(doc.getExtraction());
                if (partyKey != null) {
                    key = "auto:" + partyKey;
                } else {
                    key = "auto:unmatched-" + (i + 1);
                }
            }
            buckets.computeIfAbsent(key, k -> new ArrayList<>()).add(doc);
        }

        List<HblGroupDto> groups = new ArrayList<>();
        int groupIndex = 1;

        for (Map.Entry<String, List<ExtractedPackingListDto>> entry : buckets.entrySet()) {
            String bucketKey = entry.getKey();
            List<ExtractedPackingListDto> matches = entry.getValue();

            boolean fromClient = bucketKey.startsWith("client:");
            String groupId = fromClient ? bucketKey.substring(7) : ("hbl-group-" + (groupIndex++));
            boolean sameParties = matches.size() > 1;

            HblGroupDto group = new HblGroupDto();
            group.setGroupId(groupId);
            group.setDocumentIds(matches.stream().map(ExtractedPackingListDto::getDocumentId).collect(Collectors.toList()));
            group.setSameShipperAndConsignee(sameParties);
            group.setHblAction(sameParties ? "create_combined_hbl" : "create_separate_hbl");
            group.setReason(fromClient ? "These documents were assigned to this HBL group by the client."
                    : (sameParties ? "All documents have the same extracted shipper and consignee."
                    : "This document has different or incomplete shipper/consignee details."));

            group.setCombinedExtraction(combinePackingLists(
                    matches.stream().map(ExtractedPackingListDto::getExtraction).collect(Collectors.toList()),
                    matches.stream().map(ExtractedPackingListDto::getFilename).collect(Collectors.toList())
            ));

            groups.add(group);
        }

        return groups;
    }

    public PackingListDto combinePackingLists(List<PackingListDto> packingLists, List<String> filenames) {
        if (packingLists == null || packingLists.isEmpty()) {
            throw new IllegalArgumentException("At least one packing list is required.");
        }

        PackingListDto primary = packingLists.get(0);
        PackingListDto combined = new PackingListDto();

        // Copy primary scalar routing & party info
        combined.setPackingListNumber(primary.getPackingListNumber());
        combined.setPackingListDate(primary.getPackingListDate());
        combined.setInvoiceNumber(primary.getInvoiceNumber());
        combined.setInvoiceDate(primary.getInvoiceDate());
        combined.setShipperExporter(primary.getShipperExporter());
        combined.setConsignee(primary.getConsignee());
        combined.setBuyer(primary.getBuyer());
        combined.setNotifyParty(primary.getNotifyParty());
        combined.setVendor(primary.getVendor());
        combined.setCountryOfOrigin(primary.getCountryOfOrigin());
        combined.setCountryOfFinalDestination(primary.getCountryOfFinalDestination());
        combined.setPreCarriageBy(primary.getPreCarriageBy());
        combined.setPlaceOfReceipt(primary.getPlaceOfReceipt());
        combined.setVesselName(primary.getVesselName());
        combined.setVoyageOrFlightNumber(primary.getVoyageOrFlightNumber());
        combined.setPortOfLoading(primary.getPortOfLoading());
        combined.setPortOfDischarge(primary.getPortOfDischarge());
        combined.setPlaceOfFinalDelivery(primary.getPlaceOfFinalDelivery());
        combined.setIncoterms(primary.getIncoterms());
        combined.setFreightTerms(primary.getFreightTerms());
        combined.setMainItemDescription(primary.getMainItemDescription());

        // Merge Items with source provenance
        List<PackingListItemDto> combinedItems = new ArrayList<>();
        for (int i = 0; i < packingLists.size(); i++) {
            PackingListDto pl = packingLists.get(i);
            String fn = (filenames != null && i < filenames.size()) ? filenames.get(i) : null;
            if (pl.getItems() != null) {
                for (PackingListItemDto item : pl.getItems()) {
                    item.setSourceInvoiceNumber(pl.getInvoiceNumber());
                    item.setSourceFilename(fn);
                    item.setSourcePoNumber(pl.getBuyerOrderNumber());
                    item.setSourceTotalPackageCount(pl.getTotalPackageCount());
                    item.setSourceTotalPackageType(pl.getTotalPackageType());
                    item.setSourceMainItemDescription(pl.getMainItemDescription());
                    combinedItems.add(item);
                }
            }
        }
        combined.setItems(combinedItems);

        // Merge Containers
        Map<String, ContainerDetailDto> containerMap = new LinkedHashMap<>();
        for (PackingListDto pl : packingLists) {
            if (pl.getContainers() != null) {
                for (ContainerDetailDto c : pl.getContainers()) {
                    String num = c.getContainerNumber() != null ? c.getContainerNumber().trim() : "";
                    if (!num.isEmpty()) {
                        containerMap.putIfAbsent(num, c);
                    }
                }
            }
        }
        combined.setContainers(new ArrayList<>(containerMap.values()));

        // Aggregate Totals
        aggregateTotals(packingLists, combined);

        return combined;
    }

    private void aggregateTotals(List<PackingListDto> packingLists, PackingListDto target) {
        BigDecimal totalPkg = BigDecimal.ZERO;
        BigDecimal totalGross = BigDecimal.ZERO;
        BigDecimal totalNet = BigDecimal.ZERO;
        BigDecimal totalCbm = BigDecimal.ZERO;
        String grossUnit = "KGS";
        String netUnit = "KGS";

        boolean hasPkg = false;
        boolean hasGross = false;
        boolean hasNet = false;
        boolean hasCbm = false;

        Pattern numPattern = Pattern.compile("([0-9]+(?:\\.[0-9]+)?)");

        for (PackingListDto pl : packingLists) {
            if (pl.getTotalPackageCount() != null) {
                Matcher m = numPattern.matcher(pl.getTotalPackageCount().replace(",", ""));
                if (m.find()) {
                    totalPkg = totalPkg.add(new BigDecimal(m.group(1)));
                    hasPkg = true;
                }
            }
            if (pl.getTotalGrossWeight() != null) {
                Matcher m = numPattern.matcher(pl.getTotalGrossWeight().replace(",", ""));
                if (m.find()) {
                    totalGross = totalGross.add(new BigDecimal(m.group(1)));
                    hasGross = true;
                    if (pl.getTotalGrossWeight().toUpperCase().contains("LBS")) {
                        grossUnit = "LBS";
                    }
                }
            }
            if (pl.getTotalNetWeight() != null) {
                Matcher m = numPattern.matcher(pl.getTotalNetWeight().replace(",", ""));
                if (m.find()) {
                    totalNet = totalNet.add(new BigDecimal(m.group(1)));
                    hasNet = true;
                }
            }
            if (pl.getTotalMeasurement() != null) {
                Matcher m = numPattern.matcher(pl.getTotalMeasurement().replace(",", ""));
                if (m.find()) {
                    totalCbm = totalCbm.add(new BigDecimal(m.group(1)));
                    hasCbm = true;
                }
            }
        }

        if (hasPkg) {
            target.setTotalPackageCount(totalPkg.stripTrailingZeros().toPlainString());
        }
        if (hasGross) {
            target.setTotalGrossWeight(totalGross.setScale(2, java.math.RoundingMode.HALF_UP).toPlainString() + " " + grossUnit);
        }
        if (hasNet) {
            target.setTotalNetWeight(totalNet.setScale(2, java.math.RoundingMode.HALF_UP).toPlainString() + " " + netUnit);
        }
        if (hasCbm) {
            target.setTotalMeasurement(totalCbm.setScale(3, java.math.RoundingMode.HALF_UP).toPlainString() + " CBM");
        }
    }

    public MblManualDetailsDto enrichMblDetails(MblManualDetailsDto manualDetails, List<PackingListDto> hblExtractions) {
        if (hblExtractions == null || hblExtractions.isEmpty()) {
            return manualDetails;
        }

        PackingListDto primary = hblExtractions.get(0);
        if (manualDetails.getPreCarriageBy() == null || manualDetails.getPreCarriageBy().isBlank()) {
            manualDetails.setPreCarriageBy(primary.getPreCarriageBy());
        }
        if (manualDetails.getPlaceOfReceipt() == null || manualDetails.getPlaceOfReceipt().isBlank()) {
            manualDetails.setPlaceOfReceipt(primary.getPlaceOfReceipt());
        }
        if (manualDetails.getVesselName() == null || manualDetails.getVesselName().isBlank()) {
            manualDetails.setVesselName(primary.getVesselName());
        }
        if (manualDetails.getVoyageNumber() == null || manualDetails.getVoyageNumber().isBlank()) {
            manualDetails.setVoyageNumber(primary.getVoyageOrFlightNumber());
        }
        if (manualDetails.getPortOfLoading() == null || manualDetails.getPortOfLoading().isBlank()) {
            manualDetails.setPortOfLoading(primary.getPortOfLoading());
        }
        if (manualDetails.getPortOfDischarge() == null || manualDetails.getPortOfDischarge().isBlank()) {
            manualDetails.setPortOfDischarge(primary.getPortOfDischarge());
        }
        if (manualDetails.getPlaceOfFinalDelivery() == null || manualDetails.getPlaceOfFinalDelivery().isBlank()) {
            manualDetails.setPlaceOfFinalDelivery(primary.getPlaceOfFinalDelivery());
        }
        if (manualDetails.getFreightTerms() == null || manualDetails.getFreightTerms().isBlank()) {
            manualDetails.setFreightTerms(primary.getFreightTerms());
        }

        // Aggregate Totals across HBLs
        PackingListDto combinedTotals = new PackingListDto();
        aggregateTotals(hblExtractions, combinedTotals);

        if (manualDetails.getTotalPackages() == null || manualDetails.getTotalPackages().isBlank()) {
            manualDetails.setTotalPackages(combinedTotals.getTotalPackageCount());
        }
        if (manualDetails.getTotalGrossWeight() == null || manualDetails.getTotalGrossWeight().isBlank()) {
            manualDetails.setTotalGrossWeight(combinedTotals.getTotalGrossWeight());
        }
        if (manualDetails.getTotalNetWeight() == null || manualDetails.getTotalNetWeight().isBlank()) {
            manualDetails.setTotalNetWeight(combinedTotals.getTotalNetWeight());
        }
        if (manualDetails.getTotalMeasurement() == null || manualDetails.getTotalMeasurement().isBlank()) {
            manualDetails.setTotalMeasurement(combinedTotals.getTotalMeasurement());
        }

        return manualDetails;
    }

    private String buildPartyKey(PackingListDto pl) {
        if (pl == null) return null;
        String sName = (pl.getShipperExporter() != null && pl.getShipperExporter().getName() != null)
                ? normalizeText(pl.getShipperExporter().getName()) : "";
        String cName = (pl.getConsignee() != null && pl.getConsignee().getName() != null)
                ? normalizeText(pl.getConsignee().getName()) : "";

        if (sName.isEmpty() && cName.isEmpty()) {
            return null;
        }
        return sName + "||" + cName;
    }

    private String normalizeText(String text) {
        return text.toLowerCase().replaceAll("[^a-z0-9]", "");
    }
}

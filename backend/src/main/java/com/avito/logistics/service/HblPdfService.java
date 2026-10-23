package com.avito.logistics.service;

import com.avito.logistics.model.dto.HblManualDetailsDto;
import com.avito.logistics.model.dto.PackingListDto;
import com.avito.logistics.model.dto.PackingListItemDto;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class HblPdfService {

    private static final Logger log = LoggerFactory.getLogger(HblPdfService.class);

    @Value("${app.hbl.issuer:Global Logistics Partners (HK) Ltd. - NVOCC License #MC-445721}")
    private String hblIssuer;

    public byte[] generateHblPdf(PackingListDto packingList, String hblNumber, HblManualDetailsDto manualDetails) {
        try {
            PDDocument document = loadTemplate();
            if (document != null) {
                try {
                    overlayDataOnTemplate(document, packingList, hblNumber, manualDetails);
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    document.save(out);
                    return out.toByteArray();
                } finally {
                    document.close();
                }
            } else {
                return generateFallbackPdf(packingList, hblNumber, manualDetails);
            }
        } catch (Exception ex) {
            log.error("Failed to generate HBL PDF from template, falling back to programmatic generation: {}", ex.getMessage());
            return generateFallbackPdf(packingList, hblNumber, manualDetails);
        }
    }

    private PDDocument loadTemplate() {
        try {
            // Check filesystem first
            Path localPath = Paths.get("HBL_MULTI_TEMPLATE.pdf");
            if (Files.exists(localPath)) {
                return Loader.loadPDF(localPath.toFile());
            }
            // Check classpath resources
            ClassPathResource resource = new ClassPathResource("templates/HBL_MULTI_TEMPLATE.pdf");
            if (resource.exists()) {
                try (InputStream is = resource.getInputStream()) {
                    return Loader.loadPDF(is.readAllBytes());
                }
            }
        } catch (Exception e) {
            log.warn("Could not load HBL template: {}", e.getMessage());
        }
        return null;
    }

    private void overlayDataOnTemplate(PDDocument document, PackingListDto pl, String hblNumber, HblManualDetailsDto manual) throws Exception {
        PDPage firstPage = document.getPage(0);
        float pageHeight = firstPage.getMediaBox().getHeight();

        PDFont fontRegular = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
        PDFont fontBold = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);

        try (PDPageContentStream stream = new PDPageContentStream(document, firstPage, PDPageContentStream.AppendMode.APPEND, true, true)) {
            // HBL Number top right
            drawText(stream, fontBold, 8.5f, 410, pageHeight - 50, hblNumber);

            // Shipper (Top Left Box)
            if (pl.getShipperExporter() != null) {
                float y = pageHeight - 70;
                if (pl.getShipperExporter().getName() != null) {
                    drawText(stream, fontBold, 7f, 45, y, pl.getShipperExporter().getName());
                    y -= 9;
                }
                if (pl.getShipperExporter().getAddress() != null) {
                    y = drawWrappedText(stream, fontRegular, 6f, 45, y, 230, pl.getShipperExporter().getAddress());
                }
                if (pl.getShipperExporter().getTaxId() != null) {
                    drawText(stream, fontRegular, 6f, 45, y - 2, "Tax ID: " + pl.getShipperExporter().getTaxId());
                }
            }

            // Consignee (Second Box)
            if (pl.getConsignee() != null) {
                float y = pageHeight - 150;
                if (pl.getConsignee().getName() != null) {
                    drawText(stream, fontBold, 7f, 45, y, pl.getConsignee().getName());
                    y -= 9;
                }
                if (pl.getConsignee().getAddress() != null) {
                    y = drawWrappedText(stream, fontRegular, 6f, 45, y, 230, pl.getConsignee().getAddress());
                }
                if (pl.getConsignee().getTaxId() != null) {
                    drawText(stream, fontRegular, 6f, 45, y - 2, "Tax ID: " + pl.getConsignee().getTaxId());
                }
            }

            // Notify Party (Third Box)
            if (manual != null && manual.getNotifyParty() != null && manual.getNotifyParty().getName() != null) {
                float y = pageHeight - 235;
                drawText(stream, fontBold, 7f, 45, y, manual.getNotifyParty().getName());
                y -= 9;
                if (manual.getNotifyParty().getAddress() != null) {
                    drawWrappedText(stream, fontRegular, 6f, 45, y, 230, manual.getNotifyParty().getAddress());
                }
            } else if (pl.getNotifyParty() != null) {
                float y = pageHeight - 235;
                if (pl.getNotifyParty().getName() != null) {
                    drawText(stream, fontBold, 7f, 45, y, pl.getNotifyParty().getName());
                    y -= 9;
                }
                if (pl.getNotifyParty().getAddress() != null) {
                    drawWrappedText(stream, fontRegular, 6f, 45, y, 230, pl.getNotifyParty().getAddress());
                }
            }

            // Routing
            float routeY1 = pageHeight - 315;
            if (pl.getPreCarriageBy() != null) drawText(stream, fontRegular, 7f, 45, routeY1, pl.getPreCarriageBy());
            if (pl.getPlaceOfReceipt() != null) drawText(stream, fontRegular, 7f, 195, routeY1, pl.getPlaceOfReceipt());

            float routeY2 = pageHeight - 335;
            String vesselVoyage = (pl.getVesselName() != null ? pl.getVesselName() : "") +
                    (pl.getVoyageOrFlightNumber() != null ? " / " + pl.getVoyageOrFlightNumber() : "");
            if (!vesselVoyage.isBlank()) drawText(stream, fontRegular, 7f, 45, routeY2, vesselVoyage.trim());
            if (pl.getPortOfLoading() != null) drawText(stream, fontRegular, 7f, 195, routeY2, pl.getPortOfLoading());
            if (pl.getPortOfDischarge() != null) drawText(stream, fontRegular, 7f, 310, routeY2, pl.getPortOfDischarge());
            if (pl.getPlaceOfFinalDelivery() != null) drawText(stream, fontRegular, 7f, 455, routeY2, pl.getPlaceOfFinalDelivery());

            // Cargo Table Lines
            float cargoY = pageHeight - 370;
            if (pl.getMainItemDescription() != null) {
                drawText(stream, fontBold, 6.5f, 200, cargoY, pl.getMainItemDescription().toUpperCase());
                cargoY -= 10;
            }

            List<PackingListItemDto> items = pl.getItems();
            if (items != null) {
                for (int i = 0; i < Math.min(items.size(), 8); i++) {
                    PackingListItemDto item = items.get(i);
                    if (item.getShippingMarks() != null) drawText(stream, fontRegular, 5.5f, 45, cargoY, truncate(item.getShippingMarks(), 25));
                    if (item.getPackageCount() != null) drawText(stream, fontRegular, 5.5f, 130, cargoY, item.getPackageCount() + " " + (item.getPackageType() != null ? item.getPackageType() : "PKGS"));
                    if (item.getItemProductDescription() != null) drawText(stream, fontRegular, 5.5f, 200, cargoY, truncate(item.getItemProductDescription(), 45));
                    if (item.getGrossWeight() != null) drawText(stream, fontRegular, 5.5f, 455, cargoY, item.getGrossWeight());
                    if (item.getCbmVolume() != null) drawText(stream, fontRegular, 5.5f, 520, cargoY, item.getCbmVolume());
                    cargoY -= 9;
                }
            }

            // Totals Bottom
            if (pl.getTotalPackageCount() != null) {
                drawText(stream, fontBold, 7f, 130, pageHeight - 550, "TOTAL: " + pl.getTotalPackageCount() + " " + (pl.getTotalPackageType() != null ? pl.getTotalPackageType() : "PACKAGES"));
            }
            if (pl.getTotalGrossWeight() != null) {
                drawText(stream, fontBold, 7f, 455, pageHeight - 550, pl.getTotalGrossWeight());
            }

            // Date & Issuer
            String today = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            drawText(stream, fontRegular, 6.5f, 320, pageHeight - 745, today);
            drawText(stream, fontRegular, 6.5f, 80, pageHeight - 775, hblIssuer);
        }
    }

    private byte[] generateFallbackPdf(PackingListDto pl, String hblNumber, HblManualDetailsDto manual) {
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.LETTER);
            doc.addPage(page);
            float pageHeight = page.getMediaBox().getHeight();

            PDFont fontBold = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
            PDFont fontRegular = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

            try (PDPageContentStream stream = new PDPageContentStream(doc, page)) {
                drawText(stream, fontBold, 14f, 50, pageHeight - 50, "HOUSE BILL OF LADING");
                drawText(stream, fontBold, 10f, 400, pageHeight - 50, "B/L NO: " + hblNumber);

                drawText(stream, fontBold, 9f, 50, pageHeight - 90, "SHIPPER / EXPORTER:");
                if (pl.getShipperExporter() != null && pl.getShipperExporter().getName() != null) {
                    drawText(stream, fontRegular, 8f, 50, pageHeight - 105, pl.getShipperExporter().getName());
                }

                drawText(stream, fontBold, 9f, 50, pageHeight - 140, "CONSIGNEE:");
                if (pl.getConsignee() != null && pl.getConsignee().getName() != null) {
                    drawText(stream, fontRegular, 8f, 50, pageHeight - 155, pl.getConsignee().getName());
                }

                drawText(stream, fontBold, 9f, 50, pageHeight - 190, "PORT OF LOADING: " + (pl.getPortOfLoading() != null ? pl.getPortOfLoading() : ""));
                drawText(stream, fontBold, 9f, 300, pageHeight - 190, "PORT OF DISCHARGE: " + (pl.getPortOfDischarge() != null ? pl.getPortOfDischarge() : ""));

                drawText(stream, fontBold, 9f, 50, pageHeight - 230, "CARGO PARTICULARS:");
                float y = pageHeight - 250;
                if (pl.getItems() != null) {
                    for (PackingListItemDto item : pl.getItems()) {
                        String desc = (item.getItemProductDescription() != null ? item.getItemProductDescription() : "GOODS");
                        drawText(stream, fontRegular, 8f, 50, y, "- " + desc + " (" + (item.getGrossWeight() != null ? item.getGrossWeight() : "") + ")");
                        y -= 14;
                        if (y < 100) break;
                    }
                }

                drawText(stream, fontBold, 9f, 50, 70, "ISSUER: " + hblIssuer);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            doc.save(out);
            return out.toByteArray();
        } catch (Exception ex) {
            throw new RuntimeException("Fallback PDF generation failed", ex);
        }
    }

    private void drawText(PDPageContentStream stream, PDFont font, float fontSize, float x, float y, String text) throws Exception {
        if (text == null || text.isBlank()) return;
        stream.beginText();
        stream.setFont(font, fontSize);
        stream.newLineAtOffset(x, y);
        stream.showText(cleanText(text));
        stream.endText();
    }

    private float drawWrappedText(PDPageContentStream stream, PDFont font, float fontSize, float x, float startY, float maxWidth, String text) throws Exception {
        if (text == null || text.isBlank()) return startY;
        String[] words = text.replace("\n", " ").split("\\s+");
        StringBuilder currentLine = new StringBuilder();
        float y = startY;

        for (String word : words) {
            String testLine = currentLine.length() == 0 ? word : currentLine + " " + word;
            float lineWidth = (font.getStringWidth(testLine) / 1000f) * fontSize;
            if (lineWidth > maxWidth && currentLine.length() > 0) {
                drawText(stream, font, fontSize, x, y, currentLine.toString());
                y -= (fontSize + 2);
                currentLine = new StringBuilder(word);
            } else {
                currentLine = new StringBuilder(testLine);
            }
        }
        if (currentLine.length() > 0) {
            drawText(stream, font, fontSize, x, y, currentLine.toString());
            y -= (fontSize + 2);
        }
        return y;
    }

    private String cleanText(String text) {
        return text.replace("\t", " ").replace("\r", "").replace("\n", " ");
    }

    private String truncate(String text, int max) {
        if (text == null) return "";
        return text.length() > max ? text.substring(0, max) + "..." : text;
    }
}

package com.avito.logistics.service;

import com.avito.logistics.model.dto.*;
import com.avito.logistics.model.entity.*;
import com.avito.logistics.repository.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BatchStorageService {

    private final BatchRepository batchRepository;
    private final DocumentRepository documentRepository;
    private final HblGroupRepository hblGroupRepository;
    private final HblRecordRepository hblRecordRepository;
    private final MblRecordRepository mblRecordRepository;
    private final ObjectMapper objectMapper;
    private final ConsolidationService consolidationService;

    public BatchStorageService(BatchRepository batchRepository,
                               DocumentRepository documentRepository,
                               HblGroupRepository hblGroupRepository,
                               HblRecordRepository hblRecordRepository,
                               MblRecordRepository mblRecordRepository,
                               ObjectMapper objectMapper,
                               ConsolidationService consolidationService) {
        this.batchRepository = batchRepository;
        this.documentRepository = documentRepository;
        this.hblGroupRepository = hblGroupRepository;
        this.hblRecordRepository = hblRecordRepository;
        this.mblRecordRepository = mblRecordRepository;
        this.objectMapper = objectMapper;
        this.consolidationService = consolidationService;
    }

    @Transactional
    public String createBatch(List<ExtractedPackingListDto> documents, List<HblGroupDto> groups) {
        String batchId = UUID.randomUUID().toString();
        batchRepository.save(new BatchEntity(batchId));

        saveDocuments(batchId, documents);
        saveGroups(batchId, groups);

        return batchId;
    }

    @Transactional(readOnly = true)
    public List<ExtractedPackingListDto> getDocuments(String batchId) {
        ensureBatchExists(batchId);
        List<DocumentEntity> entities = documentRepository.findByBatchIdOrderByIdAsc(batchId);
        return entities.stream().map(this::toDocumentDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<HblGroupDto> getGroups(String batchId) {
        ensureBatchExists(batchId);
        List<HblGroupEntity> entities = hblGroupRepository.findByBatchIdOrderByIdAsc(batchId);
        return entities.stream().map(this::toGroupDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public HblGroupDto getGroup(String batchId, String groupId) {
        ensureBatchExists(batchId);
        HblGroupEntity entity = hblGroupRepository.findByBatchIdAndGroupId(batchId, groupId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "The HBL group was not found in this extraction batch."));
        return toGroupDto(entity);
    }

    @Transactional
    public BatchExtractionResponseDto appendDocuments(String batchId, List<ExtractedPackingListDto> newDocs, List<String> hints) {
        ensureBatchExists(batchId);
        List<ExtractedPackingListDto> existingDocs = getDocuments(batchId);
        List<ExtractedPackingListDto> allDocs = new ArrayList<>(existingDocs);
        allDocs.addAll(newDocs);

        saveDocuments(batchId, newDocs);

        List<HblGroupDto> newGroups = consolidationService.groupHblDocuments(allDocs, null);
        hblGroupRepository.deleteByBatchId(batchId);
        saveGroups(batchId, newGroups);

        return new BatchExtractionResponseDto(batchId, newDocs, newGroups);
    }

    @Transactional
    public List<HblGroupDto> reassignDocuments(String batchId, String sourceGroupId, String targetGroupId,
                                               List<String> addedDocIds, boolean createTargetGroup) {
        ensureBatchExists(batchId);
        List<HblGroupDto> currentGroups = getGroups(batchId);
        List<ExtractedPackingListDto> allDocs = getDocuments(batchId);
        Map<String, ExtractedPackingListDto> docMap = allDocs.stream()
                .collect(Collectors.toMap(ExtractedPackingListDto::getDocumentId, d -> d));

        HblGroupDto source = currentGroups.stream()
                .filter(g -> g.getGroupId().equals(sourceGroupId))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Source HBL group not found."));

        HblGroupDto target = currentGroups.stream()
                .filter(g -> g.getGroupId().equals(targetGroupId))
                .findFirst()
                .orElse(null);

        if (target == null && !createTargetGroup) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Target HBL group not found.");
        }

        // Validate source has addedDocIds
        for (String id : addedDocIds) {
            if (!source.getDocumentIds().contains(id)) {
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Document " + id + " is not part of the source HBL group.");
            }
        }

        source.getDocumentIds().removeAll(addedDocIds);

        if (target == null) {
            target = new HblGroupDto();
            target.setGroupId(targetGroupId);
            target.setDocumentIds(new ArrayList<>(addedDocIds));
            currentGroups.add(target);
        } else {
            target.getDocumentIds().addAll(addedDocIds);
        }

        // Remove empty source group if needed
        if (source.getDocumentIds().isEmpty()) {
            currentGroups.remove(source);
        } else {
            recalculateCombinedExtraction(source, docMap);
        }
        recalculateCombinedExtraction(target, docMap);

        hblGroupRepository.deleteByBatchId(batchId);
        saveGroups(batchId, currentGroups);

        return currentGroups;
    }

    @Transactional
    public void saveHbl(String batchId, String groupId, String hblNumber, HblManualDetailsDto manual) {
        ensureBatchExists(batchId);
        // Check for duplicate HBL number across the batch
        Optional<HblRecordEntity> existing = hblRecordRepository.findByBatchIdAndHblNumber(batchId, hblNumber);
        if (existing.isPresent() && !existing.get().getGroupId().equals(groupId)) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "The HBL number must be unique within this extraction batch.");
        }

        try {
            String manualJson = objectMapper.writeValueAsString(manual);
            HblRecordEntity record = hblRecordRepository.findByBatchIdAndGroupId(batchId, groupId)
                    .orElse(new HblRecordEntity(batchId, groupId, hblNumber, manualJson));
            record.setHblNumber(hblNumber);
            record.setManualDetailsJson(manualJson);
            hblRecordRepository.save(record);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to serialize HBL details", ex);
        }
    }

    @Transactional(readOnly = true)
    public List<HblRecordEntity> getGeneratedHbls(String batchId) {
        ensureBatchExists(batchId);
        List<HblGroupDto> groups = getGroups(batchId);
        List<HblRecordEntity> records = hblRecordRepository.findByBatchId(batchId);

        if (records.size() < groups.size()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Generate an HBL for every HBL group in this batch before generating the MBL.");
        }
        return records;
    }

    @Transactional(readOnly = true)
    public Optional<HblRecordEntity> getHbl(String batchId, String groupId) {
        return hblRecordRepository.findByBatchIdAndGroupId(batchId, groupId);
    }

    private void recalculateCombinedExtraction(HblGroupDto group, Map<String, ExtractedPackingListDto> docMap) {
        List<PackingListDto> pls = new ArrayList<>();
        List<String> fns = new ArrayList<>();
        for (String docId : group.getDocumentIds()) {
            ExtractedPackingListDto doc = docMap.get(docId);
            if (doc != null) {
                pls.add(doc.getExtraction());
                fns.add(doc.getFilename());
            }
        }
        if (!pls.isEmpty()) {
            group.setCombinedExtraction(consolidationService.combinePackingLists(pls, fns));
        }
    }

    private void ensureBatchExists(String batchId) {
        if (!batchRepository.existsById(batchId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The extraction batch was not found. Upload the packing lists again.");
        }
    }

    private void saveDocuments(String batchId, List<ExtractedPackingListDto> documents) {
        for (ExtractedPackingListDto doc : documents) {
            try {
                String json = objectMapper.writeValueAsString(doc.getExtraction());
                documentRepository.save(new DocumentEntity(batchId, doc.getDocumentId(), doc.getFilename(), doc.getMimeType(), json));
            } catch (Exception ex) {
                throw new RuntimeException("Failed to serialize document extraction", ex);
            }
        }
    }

    private void saveGroups(String batchId, List<HblGroupDto> groups) {
        for (HblGroupDto g : groups) {
            try {
                String docIdsJson = objectMapper.writeValueAsString(g.getDocumentIds());
                String combinedJson = objectMapper.writeValueAsString(g.getCombinedExtraction());
                HblGroupEntity entity = new HblGroupEntity();
                entity.setBatchId(batchId);
                entity.setGroupId(g.getGroupId());
                entity.setDocumentIdsJson(docIdsJson);
                entity.setSameShipperAndConsignee(g.isSameShipperAndConsignee());
                entity.setHblAction(g.getHblAction());
                entity.setReason(g.getReason());
                entity.setCombinedExtractionJson(combinedJson);
                hblGroupRepository.save(entity);
            } catch (Exception ex) {
                throw new RuntimeException("Failed to serialize HBL group", ex);
            }
        }
    }

    private ExtractedPackingListDto toDocumentDto(DocumentEntity entity) {
        try {
            PackingListDto extraction = objectMapper.readValue(entity.getExtractionJson(), PackingListDto.class);
            return new ExtractedPackingListDto(entity.getDocumentId(), entity.getFilename(), entity.getMimeType(), extraction);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to deserialize document extraction", ex);
        }
    }

    private HblGroupDto toGroupDto(HblGroupEntity entity) {
        try {
            List<String> docIds = objectMapper.readValue(entity.getDocumentIdsJson(), new TypeReference<List<String>>() {});
            PackingListDto combined = objectMapper.readValue(entity.getCombinedExtractionJson(), PackingListDto.class);
            HblGroupDto dto = new HblGroupDto();
            dto.setGroupId(entity.getGroupId());
            dto.setDocumentIds(docIds);
            dto.setSameShipperAndConsignee(entity.isSameShipperAndConsignee());
            dto.setHblAction(entity.getHblAction());
            dto.setReason(entity.getReason());
            dto.setCombinedExtraction(combined);
            return dto;
        } catch (Exception ex) {
            throw new RuntimeException("Failed to deserialize HBL group", ex);
        }
    }
}

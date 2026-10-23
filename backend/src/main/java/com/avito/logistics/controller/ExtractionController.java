package com.avito.logistics.controller;

import com.avito.logistics.model.dto.BatchExtractionResponseDto;
import com.avito.logistics.model.dto.ExtractedPackingListDto;
import com.avito.logistics.model.dto.HblGroupDto;
import com.avito.logistics.model.dto.PackingListDto;
import com.avito.logistics.service.AuthService;
import com.avito.logistics.service.BatchStorageService;
import com.avito.logistics.service.ConsolidationService;
import com.avito.logistics.service.VertexGeminiService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/v1/extractions")
public class ExtractionController {

    private final VertexGeminiService vertexGeminiService;
    private final ConsolidationService consolidationService;
    private final BatchStorageService batchStorageService;
    private final AuthService authService;

    public ExtractionController(VertexGeminiService vertexGeminiService,
                                ConsolidationService consolidationService,
                                BatchStorageService batchStorageService,
                                AuthService authService) {
        this.vertexGeminiService = vertexGeminiService;
        this.consolidationService = consolidationService;
        this.batchStorageService = batchStorageService;
        this.authService = authService;
    }

    @PostMapping(value = "/batch", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BatchExtractionResponseDto> createBatchExtraction(
            @RequestParam("documents") List<MultipartFile> documents,
            @RequestParam(value = "group_ids", required = false) List<String> groupIds,
            @RequestParam(value = "existing_batch_id", required = false) String existingBatchId,
            @AuthenticationPrincipal UserDetails userDetails) {

        authService.checkMblPermission(userDetails != null ? userDetails.getUsername() : null);

        if (documents == null || documents.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "At least one document is required.");
        }

        List<ExtractedPackingListDto> extractedDocs = new ArrayList<>();

        for (int i = 0; i < documents.size(); i++) {
            MultipartFile file = documents.get(i);
            if (file.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "The uploaded file is empty.");
            }

            try {
                byte[] bytes = file.getBytes();
                String mimeType = file.getContentType() != null ? file.getContentType() : "application/pdf";
                PackingListDto extraction = vertexGeminiService.extract(bytes, mimeType);

                extractedDocs.add(new ExtractedPackingListDto(
                        "document-" + (i + 1),
                        file.getOriginalFilename() != null ? file.getOriginalFilename() : ("document-" + (i + 1)),
                        mimeType,
                        extraction
                ));
            } catch (Exception ex) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Extraction failed: " + ex.getMessage());
            }
        }

        if (existingBatchId != null && !existingBatchId.isBlank()) {
            BatchExtractionResponseDto response = batchStorageService.appendDocuments(existingBatchId.trim(), extractedDocs, groupIds);
            return ResponseEntity.ok(response);
        } else {
            List<HblGroupDto> groups = consolidationService.groupHblDocuments(extractedDocs, groupIds);
            String batchId = batchStorageService.createBatch(extractedDocs, groups);
            return ResponseEntity.ok(new BatchExtractionResponseDto(batchId, extractedDocs, groups));
        }
    }
}

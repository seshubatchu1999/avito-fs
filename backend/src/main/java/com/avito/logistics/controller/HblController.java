package com.avito.logistics.controller;

import com.avito.logistics.model.dto.*;
import com.avito.logistics.model.entity.HblRecordEntity;
import com.avito.logistics.service.AuthService;
import com.avito.logistics.service.BatchStorageService;
import com.avito.logistics.service.HblPdfService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Base64;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/v1")
public class HblController {

    private final BatchStorageService batchStorageService;
    private final HblPdfService hblPdfService;
    private final AuthService authService;
    private final ObjectMapper objectMapper;

    public HblController(BatchStorageService batchStorageService,
                         HblPdfService hblPdfService,
                         AuthService authService,
                         ObjectMapper objectMapper) {
        this.batchStorageService = batchStorageService;
        this.hblPdfService = hblPdfService;
        this.authService = authService;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/hbls")
    public ResponseEntity<?> createHblPdf(
            @Valid @RequestBody HblGenerationRequestDto request,
            @RequestParam(value = "include_base64", defaultValue = "false") boolean includeBase64,
            @AuthenticationPrincipal UserDetails userDetails) {

        authService.checkMblPermission(userDetails != null ? userDetails.getUsername() : null);

        if (request.getManualDetails() == null || request.getManualDetails().getHblNumber() == null || request.getManualDetails().getHblNumber().isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "manual_details.hbl_number is required to generate an HBL PDF.");
        }

        String hblNumber = request.getManualDetails().getHblNumber().trim();
        HblGroupDto group = batchStorageService.getGroup(request.getBatchId(), request.getGroupId());

        byte[] pdf = hblPdfService.generateHblPdf(group.getCombinedExtraction(), hblNumber, request.getManualDetails());
        batchStorageService.saveHbl(request.getBatchId(), request.getGroupId(), hblNumber, request.getManualDetails());

        String filename = hblNumber.replaceAll("[^A-Za-z0-9._-]+", "-") + "-HBL.pdf";

        if (includeBase64) {
            return ResponseEntity.ok(Map.of(
                    "filename", filename,
                    "base64", Base64.getEncoder().encodeToString(pdf)
            ));
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @PostMapping("/hbl/preview")
    public ResponseEntity<Map<String, String>> previewHblPdf(
            @Valid @RequestBody HblPreviewRequestDto request,
            @AuthenticationPrincipal UserDetails userDetails) {

        authService.checkMblPermission(userDetails != null ? userDetails.getUsername() : null);

        HblGroupDto group = batchStorageService.getGroup(request.getBatchId(), request.getGroupId());
        Optional<HblRecordEntity> saved = batchStorageService.getHbl(request.getBatchId(), request.getGroupId());

        String hblNumber;
        HblManualDetailsDto manual;

        if (saved.isPresent()) {
            hblNumber = saved.get().getHblNumber();
            try {
                manual = objectMapper.readValue(saved.get().getManualDetailsJson(), HblManualDetailsDto.class);
            } catch (Exception e) {
                manual = new HblManualDetailsDto();
            }
        } else {
            hblNumber = "PREVIEW-HBL-" + request.getGroupId();
            manual = new HblManualDetailsDto();
            manual.setHblNumber(hblNumber);
        }

        byte[] pdf = hblPdfService.generateHblPdf(group.getCombinedExtraction(), hblNumber, manual);
        String filename = hblNumber + "-HBL.pdf";

        return ResponseEntity.ok(Map.of(
                "filename", filename,
                "base64", Base64.getEncoder().encodeToString(pdf)
        ));
    }

    @PostMapping("/hbl-groups/reassign")
    public ResponseEntity<ReassignDocumentsResponseDto> reassignDocuments(
            @Valid @RequestBody ReassignDocumentsRequestDto request,
            @AuthenticationPrincipal UserDetails userDetails) {

        authService.checkMblPermission(userDetails != null ? userDetails.getUsername() : null);

        var updatedGroups = batchStorageService.reassignDocuments(
                request.getBatchId(),
                request.getSourceGroupId(),
                request.getTargetGroupId(),
                request.getAddedDocumentIds(),
                request.isCreateTargetGroup()
        );

        return ResponseEntity.ok(new ReassignDocumentsResponseDto(request.getBatchId(), updatedGroups));
    }
}

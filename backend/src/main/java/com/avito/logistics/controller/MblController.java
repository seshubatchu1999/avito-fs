package com.avito.logistics.controller;

import com.avito.logistics.model.dto.*;
import com.avito.logistics.model.entity.HblRecordEntity;
import com.avito.logistics.service.AuthService;
import com.avito.logistics.service.BatchStorageService;
import com.avito.logistics.service.ConsolidationService;
import com.avito.logistics.service.MblPdfService;
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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1")
public class MblController {

    private final BatchStorageService batchStorageService;
    private final ConsolidationService consolidationService;
    private final MblPdfService mblPdfService;
    private final AuthService authService;

    public MblController(BatchStorageService batchStorageService,
                         ConsolidationService consolidationService,
                         MblPdfService mblPdfService,
                         AuthService authService) {
        this.batchStorageService = batchStorageService;
        this.consolidationService = consolidationService;
        this.mblPdfService = mblPdfService;
        this.authService = authService;
    }

    @PostMapping("/mbls")
    public ResponseEntity<?> createMblPdf(
            @Valid @RequestBody MblGenerationRequestDto request,
            @RequestParam(value = "include_base64", defaultValue = "false") boolean includeBase64,
            @AuthenticationPrincipal UserDetails userDetails) {

        authService.checkMblPermission(userDetails != null ? userDetails.getUsername() : null);

        if (request.getManualDetails() == null || request.getManualDetails().getMblNumber() == null || request.getManualDetails().getMblNumber().isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "manual_details.mbl_number is required to generate an MBL PDF.");
        }

        String mblNumber = request.getManualDetails().getMblNumber().trim();
        List<HblRecordEntity> hbls = batchStorageService.getGeneratedHbls(request.getBatchId());
        List<String> hblNumbers = hbls.stream().map(HblRecordEntity::getHblNumber).collect(Collectors.toList());

        List<HblGroupDto> groups = batchStorageService.getGroups(request.getBatchId());
        List<PackingListDto> hblExtractions = groups.stream().map(HblGroupDto::getCombinedExtraction).collect(Collectors.toList());

        MblManualDetailsDto enrichedDetails = consolidationService.enrichMblDetails(request.getManualDetails(), hblExtractions);
        byte[] pdf = mblPdfService.generateMblPdf(mblNumber, enrichedDetails, hblNumbers);

        String filename = mblNumber.replaceAll("[^A-Za-z0-9._-]+", "-") + "-MBL.pdf";

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

    @PostMapping("/mbl/preview")
    public ResponseEntity<Map<String, String>> previewMblPdf(
            @Valid @RequestBody MblPreviewRequestDto request,
            @AuthenticationPrincipal UserDetails userDetails) {

        authService.checkMblPermission(userDetails != null ? userDetails.getUsername() : null);

        List<HblRecordEntity> hbls = batchStorageService.getGeneratedHbls(request.getBatchId());
        List<String> hblNumbers = hbls.stream().map(HblRecordEntity::getHblNumber).collect(Collectors.toList());

        String mblNumber = (request.getManualDetails().getMblNumber() != null && !request.getManualDetails().getMblNumber().isBlank())
                ? request.getManualDetails().getMblNumber().trim()
                : "PREVIEW-MBL-" + request.getBatchId().substring(0, Math.min(8, request.getBatchId().length()));

        List<HblGroupDto> groups = batchStorageService.getGroups(request.getBatchId());
        List<PackingListDto> hblExtractions = groups.stream().map(HblGroupDto::getCombinedExtraction).collect(Collectors.toList());

        MblManualDetailsDto enrichedDetails = consolidationService.enrichMblDetails(request.getManualDetails(), hblExtractions);
        byte[] pdf = mblPdfService.generateMblPdf(mblNumber, enrichedDetails, hblNumbers);

        String filename = mblNumber + "-MBL.pdf";

        return ResponseEntity.ok(Map.of(
                "filename", filename,
                "base64", Base64.getEncoder().encodeToString(pdf)
        ));
    }
}

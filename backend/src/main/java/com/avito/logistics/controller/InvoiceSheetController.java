package com.avito.logistics.controller;

import com.avito.logistics.model.dto.ExtractedPackingListDto;
import com.avito.logistics.model.dto.InvoiceSheetDto;
import com.avito.logistics.model.dto.InvoiceSheetRequestDto;
import com.avito.logistics.model.dto.PackingListDto;
import com.avito.logistics.service.AuthService;
import com.avito.logistics.service.BatchStorageService;
import com.avito.logistics.service.InvoiceSheetService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1")
public class InvoiceSheetController {

    private final BatchStorageService batchStorageService;
    private final InvoiceSheetService invoiceSheetService;
    private final AuthService authService;

    public InvoiceSheetController(BatchStorageService batchStorageService,
                                  InvoiceSheetService invoiceSheetService,
                                  AuthService authService) {
        this.batchStorageService = batchStorageService;
        this.invoiceSheetService = invoiceSheetService;
        this.authService = authService;
    }

    @PostMapping("/invoice-sheets")
    public ResponseEntity<?> createInvoiceSheet(
            @Valid @RequestBody InvoiceSheetRequestDto request,
            @RequestParam(value = "format", defaultValue = "xlsx") String format,
            @RequestParam(value = "include_base64", defaultValue = "false") boolean includeBase64,
            @AuthenticationPrincipal UserDetails userDetails) {

        authService.checkInvoicePermission(userDetails != null ? userDetails.getUsername() : null);

        List<ExtractedPackingListDto> documents = batchStorageService.getDocuments(request.getBatchId());
        List<PackingListDto> extractions = documents.stream().map(ExtractedPackingListDto::getExtraction).collect(Collectors.toList());

        List<InvoiceSheetDto> rows = invoiceSheetService.extractInvoiceSummaries(extractions);

        if ("csv".equalsIgnoreCase(format)) {
            byte[] csv = invoiceSheetService.generateCsv(rows);
            String filename = "invoice-sheets.csv";
            if (includeBase64) {
                return ResponseEntity.ok(Map.of("filename", filename, "base64", Base64.getEncoder().encodeToString(csv)));
            }
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.parseMediaType("text/csv; charset=utf-8"))
                    .body(csv);
        } else {
            byte[] xlsx = invoiceSheetService.generateWorkbook(rows);
            String filename = "invoice-sheets.xlsx";
            if (includeBase64) {
                return ResponseEntity.ok(Map.of("filename", filename, "base64", Base64.getEncoder().encodeToString(xlsx)));
            }
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(xlsx);
        }
    }
}

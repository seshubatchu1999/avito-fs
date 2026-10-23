package com.avito.logistics;

import com.avito.logistics.model.dto.InvoiceSheetDto;
import com.avito.logistics.service.InvoiceSheetService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class DocumentProcessingApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private InvoiceSheetService invoiceSheetService;

    @Test
    void contextLoads() {
    }

    @Test
    void testHealthEndpoint() throws Exception {
        mockMvc.perform(get("/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ok"));
    }

    @Test
    void testRegisterFirstUserBecomesAdminAndCanLogin() throws Exception {
        String adminJson = objectMapper.writeValueAsString(Map.of(
                "username", "admin_user",
                "password", "secretpass123"
        ));

        // 1. Register Admin
        mockMvc.perform(post("/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(adminJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("admin_user"))
                .andExpect(jsonPath("$.is_admin").value(true))
                .andExpect(jsonPath("$.can_invoice_processing").value(true))
                .andExpect(jsonPath("$.can_mbl_processing").value(true));

        // 2. Login as Admin
        String loginResp = mockMvc.perform(post("/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(adminJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").isNotEmpty())
                .andReturn().getResponse().getContentAsString();

        String token = objectMapper.readTree(loginResp).get("access_token").asText();
        assertNotNull(token);

        // 3. Call /v1/auth/me with Bearer token
        mockMvc.perform(get("/v1/auth/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("admin_user"));
    }

    @Test
    void testInvoiceSheetServiceGeneratesXlsxAndCsv() {
        InvoiceSheetDto row = new InvoiceSheetDto();
        row.setInvoiceDate("20/07/2026");
        row.setInvoiceNumber("INV-001");
        row.setVendorName("Coastal Apparels");
        row.setVendorGstNumber("33AAACC1234D1Z5");
        row.setTaxableValue("5000.00");
        row.setGstValue("900.00");
        row.setTotalInvValue("5900.00");
        row.setContainerNumber("MSCU1234567");

        byte[] xlsx = invoiceSheetService.generateWorkbook(List.of(row));
        assertNotNull(xlsx);
        assertTrue(xlsx.length > 0);

        byte[] csv = invoiceSheetService.generateCsv(List.of(row));
        assertNotNull(csv);
        String csvText = new String(csv);
        assertTrue(csvText.contains("INV-001"));
        assertTrue(csvText.contains("Coastal Apparels"));
    }
}

package com.avito.logistics.service;

import com.avito.logistics.model.dto.InvoiceSheetDto;
import com.avito.logistics.model.dto.PackingListDto;
import com.avito.logistics.model.dto.PackingListItemDto;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class InvoiceSheetService {

    private static final String[] HEADERS = {
            "Inv Date", "Inv No", "Name of Vendor", "Vendor GST No",
            "Taxable Value", "GST Value", "Total Inv Value", "Container No"
    };

    public List<InvoiceSheetDto> extractInvoiceSummaries(List<PackingListDto> packingLists) {
        List<InvoiceSheetDto> result = new ArrayList<>();
        if (packingLists == null) return result;

        for (PackingListDto pl : packingLists) {
            InvoiceSheetDto sheet = new InvoiceSheetDto();
            sheet.setInvoiceDate(pl.getInvoiceDate());
            sheet.setInvoiceNumber(pl.getInvoiceNumber());

            // Name of Vendor
            if (pl.getVendor() != null && pl.getVendor().getName() != null) {
                sheet.setVendorName(pl.getVendor().getName());
            } else if (pl.getShipperExporter() != null && pl.getShipperExporter().getName() != null) {
                sheet.setVendorName(pl.getShipperExporter().getName());
            }

            // Vendor GST No
            if (pl.getExporterGstNumber() != null) {
                sheet.setVendorGstNumber(pl.getExporterGstNumber());
            } else if (pl.getVendor() != null && pl.getVendor().getTaxId() != null) {
                sheet.setVendorGstNumber(pl.getVendor().getTaxId());
            } else if (pl.getShipperExporter() != null && pl.getShipperExporter().getTaxId() != null) {
                sheet.setVendorGstNumber(pl.getShipperExporter().getTaxId());
            }

            // Taxable Value & GST Value
            BigDecimal taxable = BigDecimal.ZERO;
            BigDecimal gst = BigDecimal.ZERO;

            if (pl.getItems() != null) {
                for (PackingListItemDto item : pl.getItems()) {
                    if (item.getTaxableValue() != null) {
                        taxable = taxable.add(parseAmount(item.getTaxableValue()));
                    } else if (item.getAmount() != null) {
                        taxable = taxable.add(parseAmount(item.getAmount()));
                    }

                    if (item.getCgstAmount() != null) gst = gst.add(parseAmount(item.getCgstAmount()));
                    if (item.getSgstAmount() != null) gst = gst.add(parseAmount(item.getSgstAmount()));
                }
            }

            if (taxable.compareTo(BigDecimal.ZERO) > 0) {
                sheet.setTaxableValue(taxable.setScale(2, java.math.RoundingMode.HALF_UP).toPlainString());
            }
            if (gst.compareTo(BigDecimal.ZERO) > 0) {
                sheet.setGstValue(gst.setScale(2, java.math.RoundingMode.HALF_UP).toPlainString());
            } else if (pl.getCgstAmount() != null || pl.getSgstAmount() != null) {
                BigDecimal invoiceGst = parseAmount(pl.getCgstAmount()).add(parseAmount(pl.getSgstAmount()));
                if (invoiceGst.compareTo(BigDecimal.ZERO) > 0) {
                    sheet.setGstValue(invoiceGst.setScale(2, java.math.RoundingMode.HALF_UP).toPlainString());
                }
            }

            // Total Inv Value
            sheet.setTotalInvValue(pl.getTotalAmount());

            // Container No
            if (pl.getContainers() != null && !pl.getContainers().isEmpty()) {
                sheet.setContainerNumber(pl.getContainers().get(0).getContainerNumber());
            }

            result.add(sheet);
        }

        return result;
    }

    public byte[] generateWorkbook(List<InvoiceSheetDto> rows) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Invoices");

            // Header Style: Dark Navy, White Text, Bold
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            // Data Style: Borders
            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);

            // Create Header Row
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < HEADERS.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(HEADERS[i]);
                cell.setCellStyle(headerStyle);
            }

            // Freeze top row
            sheet.createFreezePane(0, 1);

            // Populate Data Rows
            int rowIdx = 1;
            for (InvoiceSheetDto dto : rows) {
                Row row = sheet.createRow(rowIdx++);
                createStyledCell(row, 0, dto.getInvoiceDate(), dataStyle);
                createStyledCell(row, 1, dto.getInvoiceNumber(), dataStyle);
                createStyledCell(row, 2, dto.getVendorName(), dataStyle);
                createStyledCell(row, 3, dto.getVendorGstNumber(), dataStyle);
                createStyledCell(row, 4, dto.getTaxableValue(), dataStyle);
                createStyledCell(row, 5, dto.getGstValue(), dataStyle);
                createStyledCell(row, 6, dto.getTotalInvValue(), dataStyle);
                createStyledCell(row, 7, dto.getContainerNumber(), dataStyle);
            }

            // Enable AutoFilter
            if (rowIdx > 1) {
                sheet.setAutoFilter(new CellRangeAddress(0, rowIdx - 1, 0, HEADERS.length - 1));
            }

            // Auto-size columns
            for (int i = 0; i < HEADERS.length; i++) {
                sheet.autoSizeColumn(i);
                int currentWidth = sheet.getColumnWidth(i);
                sheet.setColumnWidth(i, Math.max(currentWidth + 1200, 3500));
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        } catch (Exception ex) {
            throw new RuntimeException("Excel workbook generation failed", ex);
        }
    }

    public byte[] generateCsv(List<InvoiceSheetDto> rows) {
        StringWriter sw = new StringWriter();
        sw.append(String.join(",", HEADERS)).append("\n");

        for (InvoiceSheetDto r : rows) {
            sw.append(escapeCsv(r.getInvoiceDate())).append(",");
            sw.append(escapeCsv(r.getInvoiceNumber())).append(",");
            sw.append(escapeCsv(r.getVendorName())).append(",");
            sw.append(escapeCsv(r.getVendorGstNumber())).append(",");
            sw.append(escapeCsv(r.getTaxableValue())).append(",");
            sw.append(escapeCsv(r.getGstValue())).append(",");
            sw.append(escapeCsv(r.getTotalInvValue())).append(",");
            sw.append(escapeCsv(r.getContainerNumber())).append("\n");
        }

        return sw.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
    }

    private void createStyledCell(Row row, int col, String value, CellStyle style) {
        Cell cell = row.createCell(col);
        cell.setCellValue(value != null ? value : "");
        cell.setCellStyle(style);
    }

    private BigDecimal parseAmount(String text) {
        if (text == null) return BigDecimal.ZERO;
        try {
            String cleaned = text.replaceAll("[^0-9.]", "");
            if (cleaned.isEmpty()) return BigDecimal.ZERO;
            return new BigDecimal(cleaned);
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}

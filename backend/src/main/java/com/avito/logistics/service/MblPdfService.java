package com.avito.logistics.service;

import com.avito.logistics.model.dto.MblManualDetailsDto;
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
public class MblPdfService {

    private static final Logger log = LoggerFactory.getLogger(MblPdfService.class);

    @Value("${app.mbl.issuer:Ocean Carrier}")
    private String mblIssuer;

    public byte[] generateMblPdf(String mblNumber, MblManualDetailsDto details, List<String> hblNumbers) {
        try {
            PDDocument document = loadTemplate();
            if (document != null) {
                try {
                    overlayDataOnTemplate(document, mblNumber, details, hblNumbers);
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    document.save(out);
                    return out.toByteArray();
                } finally {
                    document.close();
                }
            } else {
                return generateFallbackPdf(mblNumber, details, hblNumbers);
            }
        } catch (Exception ex) {
            log.error("Failed to generate MBL PDF from template, falling back to programmatic generation: {}", ex.getMessage());
            return generateFallbackPdf(mblNumber, details, hblNumbers);
        }
    }

    private PDDocument loadTemplate() {
        try {
            Path localPath = Paths.get("mbl_final_updated.pdf");
            if (Files.exists(localPath)) {
                return Loader.loadPDF(localPath.toFile());
            }
            ClassPathResource resource = new ClassPathResource("templates/mbl_final_updated.pdf");
            if (resource.exists()) {
                try (InputStream is = resource.getInputStream()) {
                    return Loader.loadPDF(is.readAllBytes());
                }
            }
        } catch (Exception e) {
            log.warn("Could not load MBL template: {}", e.getMessage());
        }
        return null;
    }

    private void overlayDataOnTemplate(PDDocument document, String mblNumber, MblManualDetailsDto details, List<String> hblNumbers) throws Exception {
        PDPage firstPage = document.getPage(0);
        float pageHeight = firstPage.getMediaBox().getHeight();

        PDFont fontRegular = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
        PDFont fontBold = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);

        try (PDPageContentStream stream = new PDPageContentStream(document, firstPage, PDPageContentStream.AppendMode.APPEND, true, true)) {
            // MBL Number (top right)
            drawText(stream, fontBold, 8.5f, 410, pageHeight - 50, mblNumber);

            // Shipper (Top Left Box)
            if (details.getShipper() != null) {
                float y = pageHeight - 70;
                if (details.getShipper().getName() != null) {
                    drawText(stream, fontBold, 7f, 45, y, details.getShipper().getName());
                    y -= 9;
                }
                if (details.getShipper().getAddress() != null) {
                    y = drawWrappedText(stream, fontRegular, 6f, 45, y, 230, details.getShipper().getAddress());
                }
                if (details.getShipper().getTaxId() != null) {
                    drawText(stream, fontRegular, 6f, 45, y - 2, "Tax ID: " + details.getShipper().getTaxId());
                }
            }

            // Consignee (Second Box)
            if (details.getConsignee() != null) {
                float y = pageHeight - 150;
                if (details.getConsignee().getName() != null) {
                    drawText(stream, fontBold, 7f, 45, y, details.getConsignee().getName());
                    y -= 9;
                }
                if (details.getConsignee().getAddress() != null) {
                    drawWrappedText(stream, fontRegular, 6f, 45, y, 230, details.getConsignee().getAddress());
                }
            }

            // Routing
            float routeY1 = pageHeight - 315;
            if (details.getPreCarriageBy() != null) drawText(stream, fontRegular, 7f, 45, routeY1, details.getPreCarriageBy());
            if (details.getPlaceOfReceipt() != null) drawText(stream, fontRegular, 7f, 195, routeY1, details.getPlaceOfReceipt());

            float routeY2 = pageHeight - 335;
            String vesselVoyage = (details.getVesselName() != null ? details.getVesselName() : "") +
                    (details.getVoyageNumber() != null ? " / " + details.getVoyageNumber() : "");
            if (!vesselVoyage.isBlank()) drawText(stream, fontRegular, 7f, 45, routeY2, vesselVoyage.trim());
            if (details.getPortOfLoading() != null) drawText(stream, fontRegular, 7f, 195, routeY2, details.getPortOfLoading());
            if (details.getPortOfDischarge() != null) drawText(stream, fontRegular, 7f, 310, routeY2, details.getPortOfDischarge());
            if (details.getPlaceOfFinalDelivery() != null) drawText(stream, fontRegular, 7f, 455, routeY2, details.getPlaceOfFinalDelivery());

            // Consolidated Cargo Description
            float cargoY = pageHeight - 370;
            if (hblNumbers != null && !hblNumbers.isEmpty()) {
                drawText(stream, fontBold, 7f, 45, cargoY, "CONSOLIDATED SHIPMENT AS PER HBLS:");
                cargoY -= 10;
                String hblsStr = String.join(", ", hblNumbers);
                cargoY = drawWrappedText(stream, fontRegular, 6f, 45, cargoY, 500, hblsStr);
                cargoY -= 5;
            }

            if (details.getCargoDescription() != null) {
                cargoY = drawWrappedText(stream, fontRegular, 6.5f, 200, cargoY, 240, details.getCargoDescription());
            }

            // Totals Bottom
            if (details.getTotalPackages() != null) {
                drawText(stream, fontBold, 7f, 130, pageHeight - 550, "TOTAL PKGS: " + details.getTotalPackages());
            }
            if (details.getTotalGrossWeight() != null) {
                drawText(stream, fontBold, 7f, 455, pageHeight - 550, details.getTotalGrossWeight());
            }
            if (details.getTotalMeasurement() != null) {
                drawText(stream, fontBold, 7f, 520, pageHeight - 550, details.getTotalMeasurement());
            }

            String today = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            drawText(stream, fontRegular, 6.5f, 320, pageHeight - 745, today);
            drawText(stream, fontRegular, 6.5f, 80, pageHeight - 775, mblIssuer);
        }
    }

    private byte[] generateFallbackPdf(String mblNumber, MblManualDetailsDto details, List<String> hblNumbers) {
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.LETTER);
            doc.addPage(page);
            float pageHeight = page.getMediaBox().getHeight();

            PDFont fontBold = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
            PDFont fontRegular = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

            try (PDPageContentStream stream = new PDPageContentStream(doc, page)) {
                drawText(stream, fontBold, 14f, 50, pageHeight - 50, "MASTER BILL OF LADING");
                drawText(stream, fontBold, 10f, 400, pageHeight - 50, "MBL NO: " + mblNumber);

                drawText(stream, fontBold, 9f, 50, pageHeight - 90, "SHIPPER:");
                if (details.getShipper() != null && details.getShipper().getName() != null) {
                    drawText(stream, fontRegular, 8f, 50, pageHeight - 105, details.getShipper().getName());
                }

                drawText(stream, fontBold, 9f, 50, pageHeight - 140, "CONSIGNEE:");
                if (details.getConsignee() != null && details.getConsignee().getName() != null) {
                    drawText(stream, fontRegular, 8f, 50, pageHeight - 155, details.getConsignee().getName());
                }

                drawText(stream, fontBold, 9f, 50, pageHeight - 190, "VESSEL: " + (details.getVesselName() != null ? details.getVesselName() : ""));
                drawText(stream, fontBold, 9f, 300, pageHeight - 190, "VOYAGE: " + (details.getVoyageNumber() != null ? details.getVoyageNumber() : ""));

                drawText(stream, fontBold, 9f, 50, pageHeight - 230, "PORT OF LOADING: " + (details.getPortOfLoading() != null ? details.getPortOfLoading() : ""));
                drawText(stream, fontBold, 9f, 300, pageHeight - 230, "PORT OF DISCHARGE: " + (details.getPortOfDischarge() != null ? details.getPortOfDischarge() : ""));

                drawText(stream, fontBold, 9f, 50, pageHeight - 270, "CONSOLIDATED HBLS: " + (hblNumbers != null ? String.join(", ", hblNumbers) : ""));
                drawText(stream, fontBold, 9f, 50, pageHeight - 300, "TOTAL PACKAGES: " + (details.getTotalPackages() != null ? details.getTotalPackages() : ""));
                drawText(stream, fontBold, 9f, 50, pageHeight - 320, "TOTAL GROSS WEIGHT: " + (details.getTotalGrossWeight() != null ? details.getTotalGrossWeight() : ""));

                drawText(stream, fontBold, 9f, 50, 70, "CARRIER: " + mblIssuer);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            doc.save(out);
            return out.toByteArray();
        } catch (Exception ex) {
            throw new RuntimeException("Fallback MBL generation failed", ex);
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
}

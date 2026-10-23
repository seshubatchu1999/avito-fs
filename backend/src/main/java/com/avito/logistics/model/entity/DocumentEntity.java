package com.avito.logistics.model.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "documents")
public class DocumentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "batch_id", nullable = false, length = 64)
    private String batchId;

    @Column(name = "document_id", nullable = false, length = 64)
    private String documentId;

    @Column(name = "filename", nullable = false)
    private String filename;

    @Column(name = "mime_type", nullable = false, length = 64)
    private String mimeType;

    @Lob
    @Column(name = "extraction_json", columnDefinition = "TEXT", nullable = false)
    private String extractionJson;

    public DocumentEntity() {}

    public DocumentEntity(String batchId, String documentId, String filename, String mimeType, String extractionJson) {
        this.batchId = batchId;
        this.documentId = documentId;
        this.filename = filename;
        this.mimeType = mimeType;
        this.extractionJson = extractionJson;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getBatchId() { return batchId; }
    public void setBatchId(String batchId) { this.batchId = batchId; }

    public String getDocumentId() { return documentId; }
    public void setDocumentId(String documentId) { this.documentId = documentId; }

    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }

    public String getMimeType() { return mimeType; }
    public void setMimeType(String mimeType) { this.mimeType = mimeType; }

    public String getExtractionJson() { return extractionJson; }
    public void setExtractionJson(String extractionJson) { this.extractionJson = extractionJson; }
}

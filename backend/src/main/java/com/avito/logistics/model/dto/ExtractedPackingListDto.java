package com.avito.logistics.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExtractedPackingListDto {

    @JsonProperty("document_id")
    private String documentId;

    @JsonProperty("filename")
    private String filename;

    @JsonProperty("mime_type")
    private String mimeType;

    @JsonProperty("extraction")
    private PackingListDto extraction;

    public ExtractedPackingListDto() {}

    public ExtractedPackingListDto(String documentId, String filename, String mimeType, PackingListDto extraction) {
        this.documentId = documentId;
        this.filename = filename;
        this.mimeType = mimeType;
        this.extraction = extraction;
    }

    public String getDocumentId() { return documentId; }
    public void setDocumentId(String documentId) { this.documentId = documentId; }

    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }

    public String getMimeType() { return mimeType; }
    public void setMimeType(String mimeType) { this.mimeType = mimeType; }

    public PackingListDto getExtraction() { return extraction; }
    public void setExtraction(PackingListDto extraction) { this.extraction = extraction; }
}

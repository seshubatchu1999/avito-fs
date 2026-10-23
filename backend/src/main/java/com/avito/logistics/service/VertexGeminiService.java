package com.avito.logistics.service;

import com.avito.logistics.model.dto.PackingListDto;
import com.avito.logistics.prompt.ExtractionPrompts;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.Content;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.api.GenerationConfig;
import com.google.cloud.vertexai.api.Part;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.google.protobuf.ByteString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;

@Service
public class VertexGeminiService {

    private static final Logger log = LoggerFactory.getLogger(VertexGeminiService.class);

    @Value("${app.google.cloud.project:}")
    private String projectId;

    @Value("${app.google.cloud.location:us-central1}")
    private String location;

    @Value("${app.google.credentials:}")
    private String credentialsPath;

    @Value("${app.vertex.model:gemini-2.5-flash}")
    private String modelName;

    private final ObjectMapper objectMapper;

    public VertexGeminiService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public PackingListDto extract(byte[] documentBytes, String mimeType) {
        try {
            String activeProject = resolveProjectId();
            if (activeProject == null || activeProject.isBlank()) {
                throw new IllegalStateException("Google Cloud Project ID is not configured.");
            }

            GoogleCredentials credentials = resolveCredentials();

            try (VertexAI vertexAI = new VertexAI.Builder()
                    .setProjectId(activeProject)
                    .setLocation(location)
                    .setCredentials(credentials)
                    .build()) {

                GenerativeModel model = new GenerativeModel(modelName, vertexAI);

                GenerationConfig config = GenerationConfig.newBuilder()
                        .setTemperature(0.0f)
                        .setResponseMimeType("application/json")
                        .build();

                Part textPart = Part.newBuilder().setText(ExtractionPrompts.PACKING_LIST_EXTRACTION_PROMPT).build();
                Part filePart = Part.newBuilder()
                        .setInlineData(com.google.cloud.vertexai.api.Blob.newBuilder()
                                .setData(ByteString.copyFrom(documentBytes))
                                .setMimeType(mimeType)
                                .build())
                        .build();

                Content content = Content.newBuilder()
                        .setRole("user")
                        .addParts(textPart)
                        .addParts(filePart)
                        .build();

                log.info("Sending extraction request to Vertex AI Gemini model [{}] (payload size: {} bytes, mime: {})",
                        modelName, documentBytes.length, mimeType);

                model = model.withGenerationConfig(config);
                GenerateContentResponse response = model.generateContent(content);

                String responseText = response.getCandidates(0).getContent().getParts(0).getText();
                log.debug("Received raw extraction response from Gemini: {}", responseText);

                // Sanitize potential markdown wrap
                String cleanedJson = cleanJson(responseText);

                return objectMapper.readValue(cleanedJson, PackingListDto.class);
            }
        } catch (Exception e) {
            log.error("Failed to extract document via Vertex AI Gemini", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Vertex AI Gemini extraction failed: " + e.getMessage(), e);
        }
    }

    private String cleanJson(String raw) {
        if (raw == null) return "{}";
        String trimmed = raw.trim();
        if (trimmed.startsWith("```json")) {
            trimmed = trimmed.substring(7);
        } else if (trimmed.startsWith("```")) {
            trimmed = trimmed.substring(3);
        }
        if (trimmed.endsWith("```")) {
            trimmed = trimmed.substring(0, trimmed.length() - 3);
        }
        return trimmed.trim();
    }

    private String resolveProjectId() {
        if (projectId != null && !projectId.isBlank()) {
            return projectId;
        }
        String envProject = System.getenv("GOOGLE_CLOUD_PROJECT");
        if (envProject != null && !envProject.isBlank()) {
            return envProject;
        }
        return System.getenv("GCP_PROJECT");
    }

    private GoogleCredentials resolveCredentials() throws Exception {
        if (credentialsPath != null && !credentialsPath.isBlank() && Files.exists(Paths.get(credentialsPath))) {
            try (InputStream is = new FileInputStream(credentialsPath)) {
                return GoogleCredentials.fromStream(is)
                        .createScoped(Collections.singletonList("https://www.googleapis.com/auth/cloud-platform"));
            }
        }

        String envCreds = System.getenv("GOOGLE_APPLICATION_CREDENTIALS");
        if (envCreds != null && !envCreds.isBlank() && Files.exists(Paths.get(envCreds))) {
            try (InputStream is = new FileInputStream(envCreds)) {
                return GoogleCredentials.fromStream(is)
                        .createScoped(Collections.singletonList("https://www.googleapis.com/auth/cloud-platform"));
            }
        }

        return GoogleCredentials.getApplicationDefault()
                .createScoped(Collections.singletonList("https://www.googleapis.com/auth/cloud-platform"));
    }
}

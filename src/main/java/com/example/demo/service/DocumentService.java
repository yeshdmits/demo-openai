package com.example.demo.service;

import com.example.demo.domain.model.Document;
import com.example.demo.domain.repository.DocumentRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentService {
  private final DocumentRepository documentRepository;

  public String getPrompt(UUID documentId, String request) {
    var document = documentRepository.findById(documentId).orElseThrow();
    return "You have following text data and you have to take important information from it:\n" +
        document.getPromptText() + "\n\n\nHere is information what I want to get as json response from you:"
        + request + "\nMaximum symbols for each field = 100";
  }

  public String getPrompt(String description, String text) {
    return "Based on the following resume :\n" +
        text + "\n\n\nyou need to make decision how good this candidate is matching job description:"
        + description + "\n Response should be json {\"decision\": boolean, \"match\": \"0%-100%\"}";
  }
  @SneakyThrows
  public String parseDocument(byte[] content) {
    try (var documentPdf = Loader.loadPDF(content)) {
      var stripper = new PDFTextStripper();
      return stripper.getText(documentPdf);
    }
  }

  public Document save(Document build) {
    return documentRepository.save(build);
  }

  public Document findById(UUID documentId) {
    return documentRepository.findById(documentId).orElseThrow();
  }

  @Transactional
  public Document updateDocumentResponse(UUID id, String response) {
    var document = documentRepository.findById(id).orElseThrow();
    document.setResponse(response);
    return document;
  }
}

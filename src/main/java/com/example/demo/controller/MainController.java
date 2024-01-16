package com.example.demo.controller;

import com.example.demo.domain.dto.DocumentDto;
import com.example.demo.service.OpenAIService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class MainController {


  private final OpenAIService openAIService;

  @PostMapping("/document/{documentId}")
  public ResponseEntity<DocumentDto> create(@PathVariable UUID documentId, @RequestPart String request) {
    return ResponseEntity.ok(openAIService.requestOpenAIBot(documentId, request));
  }

  @PostMapping("/upload")
  public ResponseEntity<DocumentDto> uploadFile(@RequestPart MultipartFile file) {
    return ResponseEntity.ok(openAIService.saveRequest(file));
  }

  @PostMapping("/speech")
  public ResponseEntity<String> speech2Text(@RequestPart MultipartFile file) {
    String s = openAIService.transformSpeechToText(file);
    return ResponseEntity.ok(s);
  }
}

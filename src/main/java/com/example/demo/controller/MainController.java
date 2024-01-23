package com.example.demo.controller;

import com.example.demo.domain.DocumentMapper;
import com.example.demo.domain.dto.DocumentDto;
import com.example.demo.exception.ForbiddenException;
import com.example.demo.service.OpenAIService;
import com.example.demo.util.AuthUtil;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MainController {
  @Value("${openAI.apiKey}")
  public String apiKey;

  private final OpenAIService openAIService;
  private final DocumentMapper documentMapper;

  @PostMapping("/document/{documentId}")
  public ResponseEntity<DocumentDto> create(@PathVariable UUID documentId, @RequestHeader("apiKey") String apiKey, @RequestPart String request) {
    try {
      AuthUtil.validate(apiKey, this.apiKey);
    } catch (ForbiddenException e) {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
    return ResponseEntity.ok(documentMapper.toDto(openAIService.requestOpenAIBot(documentId, request)));
  }

  @PostMapping("/upload")
  public ResponseEntity<DocumentDto> uploadFile(@RequestPart MultipartFile file, @RequestHeader("apiKey") String apiKey) {
    try {
      AuthUtil.validate(apiKey, this.apiKey);
    } catch (ForbiddenException e) {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
    return ResponseEntity.ok(documentMapper.toDto(openAIService.uploadFile(file)));
  }


  @GetMapping("/profile/{profileId}")
  public ResponseEntity<String> validateLinkedinJobDescription(@RequestParam("url") String url,
      @PathVariable UUID profileId, @RequestHeader("apiKey") String apiKey) {
    try {
      AuthUtil.validate(apiKey, this.apiKey);
    } catch (ForbiddenException e) {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
//    https://www.linkedin.com/jobs/view/3801438680
    return ResponseEntity.ok(openAIService.getDecisionForDocument(profileId, url));
  }

  @PostMapping("/speech")
  public ResponseEntity<String> speech2Text(@RequestPart MultipartFile file, @RequestHeader("apiKey") String apiKey) {
    try {
      AuthUtil.validate(apiKey, this.apiKey);
    } catch (ForbiddenException e) {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
    String s = openAIService.transformSpeechToText(file);
    return ResponseEntity.ok(s);
  }
}

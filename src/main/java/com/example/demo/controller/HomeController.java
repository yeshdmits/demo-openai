package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
public class HomeController {
  @Value("${openAI.apiKey}")
  public String apiKey;

  @GetMapping("/")
  public ResponseEntity<Object> index() {
    return ResponseEntity.ok().build();
  }


  @GetMapping("/api/v1")
  public ResponseEntity<Object> apiV1(@RequestHeader("apiKey") String apiKey) {
    if (this.apiKey.equals(apiKey)) {
      return ResponseEntity.ok().build();
    }
    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
  }
}

package com.example.demo.domain.dto;

import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DocumentDto {

  private UUID uuid;

  private String text;

  private String response;
}

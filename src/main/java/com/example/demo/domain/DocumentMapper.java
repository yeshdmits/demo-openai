package com.example.demo.domain;

import com.example.demo.domain.dto.DocumentDto;
import com.example.demo.domain.model.Document;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DocumentMapper {

  @Mapping(target = "uuid", source = "documentId")
  @Mapping(target = "text", source = "promptText")
  @Mapping(target = "response", source = "response")
  DocumentDto toDto(Document document);

}

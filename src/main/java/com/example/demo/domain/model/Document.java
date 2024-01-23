package com.example.demo.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Document {

  private static final String ID = "id";
  private static final String PROMPT_TEXT = "prompt_text";
  private static final String DOCUMENT_TYPE = "document_type";
  @Id
  @GeneratedValue(generator = "UUID")
  @Column(name = ID, updatable = false, nullable = false)
  @ColumnDefault("random_uuid()")
  private UUID documentId;
  @Column
  private String name;
  @Lob
  private byte[] content;
  @Nationalized
  private String request;
  @Nationalized
  private String response;
  @Column(name = PROMPT_TEXT)
  @Nationalized
  private String promptText;
  @Column(name = DOCUMENT_TYPE)
  @Enumerated(EnumType.STRING)
  private ProfileTypeEnum documentType;
}

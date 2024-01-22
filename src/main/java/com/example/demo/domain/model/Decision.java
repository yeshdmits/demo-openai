package com.example.demo.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
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
public class Decision {

  private static final String ID = "id";
  private static final String DECISION_RESULT = "decision_result";
  @Id
  @GeneratedValue(generator = "UUID")
  @Column(name = ID, updatable = false, nullable = false)
  @ColumnDefault("random_uuid()")
  private UUID decisionId;
  @Nationalized
  private String description;
  @Column(name = DECISION_RESULT)
  private Boolean decisionResult;
  @OneToOne
  private Document document;
}

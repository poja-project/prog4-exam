package com.example.demo.repository.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "submission")
@Getter
@Setter
public class Submission {
    @Id private UUID id;
    private String email;
    private String filename;
    private LocalDateTime createdAt;
}
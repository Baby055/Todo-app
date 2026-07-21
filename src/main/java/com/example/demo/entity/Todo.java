package com.example.demo.entity;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Todo {
    private long id;
    private UUID uuid;
    private String title;
    private String description;
    private boolean is_completed;
    private Instant created_at;
    private Instant updated_at;
}

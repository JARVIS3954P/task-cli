package com.taskcli;

import java.time.LocalDateTime;

class Task {
    private final int id;
    private String description;
    private Status status;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    Task(int id, String description) {
        this(id, description, Status.TODO, LocalDateTime.now(), LocalDateTime.now());
    }

    Task(int id, String description, Status status,
         LocalDateTime createdAt, LocalDateTime updatedAt) {
        if (id <= 0) {
            throw new IllegalArgumentException("Task ID must be positive.");
        }
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Task description cannot be empty.");
        }
        if (status == null || createdAt == null || updatedAt == null) {
            throw new IllegalArgumentException("Task fields cannot be null.");
        }

        this.id = id;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    int getId() {
        return id;
    }

    String getDescription() {
        return description;
    }

    void setDescription(String description) {
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Task description cannot be empty.");
        }
        this.description = description;
    }

    Status getStatus() {
        return status;
    }

    void setStatus(Status status) {
        if (status == null) {
            throw new IllegalArgumentException("Task status cannot be null.");
        }
        this.status = status;
    }

    LocalDateTime getCreatedAt() {
        return createdAt;
    }

    LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    void setUpdatedAt(LocalDateTime updatedAt) {
        if (updatedAt == null) {
            throw new IllegalArgumentException("Updated timestamp cannot be null.");
        }
        this.updatedAt = updatedAt;
    }
}

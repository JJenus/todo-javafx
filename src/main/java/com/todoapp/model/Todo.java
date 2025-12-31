package com.todoapp.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Todo {
    private final String id;
    private String title;
    private boolean done;
    private LocalDateTime time;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String category;

    public Todo(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Todo title cannot be empty");
        }
        this.id = UUID.randomUUID().toString();
        this.title = title.trim();
        this.done = false;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
        this.category = "General";
    }

    public Todo(String id, String title, boolean done, LocalDateTime time, 
                LocalDateTime createdAt, LocalDateTime updatedAt, String category) {
        this.id = id;
        this.title = title;
        this.done = done;
        this.time = time;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.category = category;
    }

    // Getters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public boolean isDone() { return done; }
    public LocalDateTime getTime() { return time; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public String getCategory() { return category; }

    // Setters
    public void setTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Todo title cannot be empty");
        }
        this.title = title.trim();
        this.updatedAt = LocalDateTime.now();
    }

    public void setDone(boolean done) {
        this.done = done;
        this.updatedAt = LocalDateTime.now();
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void setCategory(String category) {
        this.category = category;
        this.updatedAt = LocalDateTime.now();
    }

    public void toggleDone() {
        this.done = !this.done;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isOverdue() {
        if (time == null || done) return false;
        return time.isBefore(LocalDateTime.now());
    }

    public boolean isDueToday() {
        if (time == null) return false;
        LocalDateTime now = LocalDateTime.now();
        return time.toLocalDate().equals(now.toLocalDate());
    }

    public boolean hasDueDate() {
        return time != null;
    }

    public String getFormattedDueDate() {
        if (time == null) return "";
        return time.format(java.time.format.DateTimeFormatter.ofPattern("MMM dd"));
    }
    
    public String getDayOfWeek() {
        if (time == null) return "";
        return time.format(java.time.format.DateTimeFormatter.ofPattern("EEEE"));
    }
    
    public String getDateNumber() {
        if (time == null) return "";
        return time.format(java.time.format.DateTimeFormatter.ofPattern("dd"));
    }
    
    public String getMonthYear() {
        if (time == null) return "";
        return time.format(java.time.format.DateTimeFormatter.ofPattern("MMM yyyy"));
    }
}

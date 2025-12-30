package com.todoapp.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Project {
    private final String id;
    private String name;
    private final LocalDateTime createdAt;
    private final List<Todo> todos;

    public Project(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Project name cannot be empty");
        }
        this.id = UUID.randomUUID().toString();
        this.name = name.trim();
        this.createdAt = LocalDateTime.now();
        this.todos = new ArrayList<>();
    }

    public Project(String id, String name, LocalDateTime createdAt, List<Todo> todos) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
        this.todos = new ArrayList<>(todos);
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public List<Todo> getTodos() { return new ArrayList<>(todos); }

    public void setName(String name) { 
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Project name cannot be empty");
        }
        this.name = name.trim(); 
    }

    public void addTodo(Todo todo) {
        if (todo == null) throw new IllegalArgumentException("Todo cannot be null");
        todos.add(todo);
    }

    public void removeTodo(String todoId) {
        todos.removeIf(todo -> todo.getId().equals(todoId));
    }

    public Todo findTodo(String todoId) {
        return todos.stream()
                .filter(todo -> todo.getId().equals(todoId))
                .findFirst()
                .orElse(null);
    }

    public long getCompletedCount() {
        return todos.stream().filter(Todo::isDone).count();
    }

    public long getTotalCount() { return todos.size(); }

    public String getCompletionPercentage() {
        if (todos.isEmpty()) return "0%";
        double percentage = (getCompletedCount() * 100.0) / todos.size();
        return String.format("%.0f%%", percentage);
    }
}

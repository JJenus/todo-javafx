package com.todoapp.repository;

import com.todoapp.model.Project;
import com.todoapp.model.Todo;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class InMemoryTodoRepository implements TodoRepository {
    private final Map<String, Project> projects = new ConcurrentHashMap<>();
    
    public InMemoryTodoRepository() {
        initializeDefaultData();
    }
    
    @Override
    public List<Project> getAllProjects() {
        return new ArrayList<>(projects.values());
    }
    
    @Override
    public Optional<Project> getProject(String projectId) {
        return Optional.ofNullable(projects.get(projectId));
    }
    
    @Override
    public Project saveProject(Project project) {
        projects.put(project.getId(), project);
        return project;
    }
    
    @Override
    public boolean deleteProject(String projectId) {
        return projects.remove(projectId) != null;
    }
    
    @Override
    public boolean projectExists(String projectId) {
        return projects.containsKey(projectId);
    }
    
    @Override
    public Optional<Todo> getTodo(String projectId, String todoId) {
        return getProject(projectId)
                .flatMap(project -> project.getTodos().stream()
                        .filter(todo -> todo.getId().equals(todoId))
                        .findFirst());
    }
    
    @Override
    public Todo saveTodo(String projectId, Todo todo) {
        return getProject(projectId).map(project -> {
            project.getTodos().removeIf(t -> t.getId().equals(todo.getId()));
            project.addTodo(todo);
            return todo;
        }).orElseThrow(() -> new IllegalArgumentException("Project not found: " + projectId));
    }
    
    @Override
    public boolean deleteTodo(String projectId, String todoId) {
        return getProject(projectId).map(project -> {
            project.removeTodo(todoId);
            return true;
        }).orElse(false);
    }
    
    @Override
    public List<Todo> searchTodos(String query) {
        if (query == null || query.trim().isEmpty()) return List.of();
        String searchTerm = query.trim().toLowerCase();
        return projects.values().stream()
                .flatMap(project -> project.getTodos().stream())
                .filter(todo -> todo.getTitle().toLowerCase().contains(searchTerm))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Todo> getTodosDueToday() {
        return projects.values().stream()
                .flatMap(project -> project.getTodos().stream())
                .filter(Todo::isDueToday)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Todo> getOverdueTodos() {
        return projects.values().stream()
                .flatMap(project -> project.getTodos().stream())
                .filter(Todo::isOverdue)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Todo> getTodosByDateRange(LocalDateTime start, LocalDateTime end) {
        return projects.values().stream()
                .flatMap(project -> project.getTodos().stream())
                .filter(todo -> todo.getTime() != null 
                        && !todo.getTime().isBefore(start) 
                        && todo.getTime().isBefore(end))
                .collect(Collectors.toList());
    }
    
    @Override
    public int getProjectCount() { return projects.size(); }
    
    @Override
    public int getTotalTodoCount() {
        return projects.values().stream()
                .mapToInt(project -> project.getTodos().size())
                .sum();
    }
    
    @Override
    public int getCompletedTodoCount() {
        return projects.values().stream()
                .flatMap(project -> project.getTodos().stream())
                .filter(Todo::isDone)
                .mapToInt(todo -> 1)
                .sum();
    }
    
    @Override
    public void initializeDefaultData() {
        if (projects.isEmpty()) {
            Project personal = new Project("Personal");
            personal.addTodo(new Todo("Buy groceries"));
            personal.addTodo(new Todo("Call mom"));
            saveProject(personal);
            
            Project work = new Project("Work");
            work.addTodo(new Todo("Team meeting"));
            work.addTodo(new Todo("Finish report"));
            saveProject(work);
        }
    }
    
    @Override
    public void clearAllData() { projects.clear(); }
}

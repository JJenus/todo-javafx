package com.todoapp.service;

import com.todoapp.model.Project;
import com.todoapp.model.Todo;
import com.todoapp.repository.TodoRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class TodoService {
    private final TodoRepository repository;
    
    public TodoService(TodoRepository repository) {
        this.repository = repository;
        this.repository.initializeDefaultData();
    }
    
    public List<Project> getAllProjects() {
        return repository.getAllProjects();
    }
    
    public Project createProject(String name) {
        validateProjectName(name);
        Project project = new Project(name.trim());
        return repository.saveProject(project);
    }
    
    public boolean deleteProject(String projectId) {
        return repository.deleteProject(projectId);
    }
    
    public Optional<Project> getProject(String projectId) {
        return repository.getProject(projectId);
    }
    
    public Project updateProjectName(String projectId, String newName) {
        validateProjectName(newName);
        return repository.getProject(projectId).map(project -> {
            project.setName(newName.trim());
            return repository.saveProject(project);
        }).orElse(null);
    }
    
    public Todo createTodo(String projectId, String title, LocalDateTime dueTime) {
        validateTodoTitle(title);
        validateProjectExists(projectId);
        
        Todo todo = new Todo(title.trim());
        if (dueTime != null) {
            validateDueDate(dueTime);
            todo.setTime(dueTime);
        }
        
        return repository.saveTodo(projectId, todo);
    }
    
    public boolean toggleTodoDone(String projectId, String todoId) {
        return repository.getTodo(projectId, todoId).map(todo -> {
            todo.toggleDone();
            repository.saveTodo(projectId, todo);
            return true;
        }).orElse(false);
    }
    
    public boolean updateTodo(String projectId, String todoId, String newTitle, LocalDateTime newTime) {
        return repository.getTodo(projectId, todoId).map(todo -> {
            boolean updated = false;
            
            if (newTitle != null && !newTitle.trim().isEmpty() && !newTitle.trim().equals(todo.getTitle())) {
                validateTodoTitle(newTitle);
                todo.setTitle(newTitle.trim());
                updated = true;
            }
            
            if (newTime != null) {
                validateDueDate(newTime);
                todo.setTime(newTime);
                updated = true;
            } else if (newTime == null && todo.getTime() != null) {
                todo.setTime(null);
                updated = true;
            }
            
            if (updated) repository.saveTodo(projectId, todo);
            return updated;
        }).orElse(false);
    }
    
    public boolean deleteTodo(String projectId, String todoId) {
        return repository.deleteTodo(projectId, todoId);
    }
    
    public List<Todo> searchTodos(String query) {
        if (query == null || query.trim().isEmpty()) return List.of();
        return repository.searchTodos(query.trim());
    }
    
    public List<Todo> getTodayTodos() {
        return repository.getTodosDueToday();
    }
    
    public List<Todo> getOverdueTodos() {
        return repository.getOverdueTodos();
    }
    
    public List<Todo> getTodosByProject(String projectId) {
        return repository.getProject(projectId)
                .map(Project::getTodos)
                .orElse(List.of());
    }
    
    public int getTotalProjects() { return repository.getProjectCount(); }
    
    public int getTotalTodos() { return repository.getTotalTodoCount(); }
    
    public int getCompletedTodosCount() { return repository.getCompletedTodoCount(); }
    
    public double getOverallCompletionPercentage() {
        int total = getTotalTodos();
        if (total == 0) return 0.0;
        return (getCompletedTodosCount() * 100.0) / total;
    }
    
    private void validateProjectName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Project name cannot be empty");
        }
        if (name.trim().length() > 100) {
            throw new IllegalArgumentException("Project name cannot exceed 100 characters");
        }
    }
    
    private void validateTodoTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Todo title cannot be empty");
        }
        if (title.trim().length() > 500) {
            throw new IllegalArgumentException("Todo title cannot exceed 500 characters");
        }
    }
    
    private void validateProjectExists(String projectId) {
        if (!repository.projectExists(projectId)) {
            throw new IllegalArgumentException("Project not found: " + projectId);
        }
    }
    
    private void validateDueDate(LocalDateTime dueTime) {
        if (dueTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Due date cannot be in the past");
        }
    }
}

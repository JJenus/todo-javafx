package com.todoapp.service;

import com.todoapp.model.Todo;
import com.todoapp.repository.SqliteTodoRepository;
import com.todoapp.repository.TodoRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class TodoService {
    private final TodoRepository repository;
    
    public TodoService() {
        this.repository = new SqliteTodoRepository();
    }
    
    public List<Todo> getAllTodos() {
        return repository.getAllTodos();
    }
    
    public Todo createTodo(String title) {
        validateTodoTitle(title);
        Todo todo = new Todo(title.trim());
        return repository.saveTodo(todo);
    }
    
    public Todo createTodo(String title, String category, LocalDateTime dueDate) {
        validateTodoTitle(title);
        Todo todo = new Todo(title.trim());
        todo.setCategory(category != null ? category.trim() : "General");
        if (dueDate != null) {
            todo.setTime(dueDate);
        }
        return repository.saveTodo(todo);
    }
    
    public boolean toggleTodoDone(String todoId) {
        return repository.getTodo(todoId).map(todo -> {
            todo.toggleDone();
            repository.saveTodo(todo);
            return true;
        }).orElse(false);
    }
    
    public boolean updateTodo(String todoId, String newTitle, String newCategory, LocalDateTime newTime) {
        return repository.getTodo(todoId).map(todo -> {
            boolean updated = false;
            
            if (newTitle != null && !newTitle.trim().isEmpty() && !newTitle.trim().equals(todo.getTitle())) {
                validateTodoTitle(newTitle);
                todo.setTitle(newTitle.trim());
                updated = true;
            }
            
            if (newCategory != null && !newCategory.equals(todo.getCategory())) {
                todo.setCategory(newCategory);
                updated = true;
            }
            
            if (newTime != null) {
                todo.setTime(newTime);
                updated = true;
            } else if (newTime == null && todo.getTime() != null) {
                todo.setTime(null);
                updated = true;
            }
            
            if (updated) repository.saveTodo(todo);
            return updated;
        }).orElse(false);
    }
    
    public boolean deleteTodo(String todoId) {
        return repository.deleteTodo(todoId);
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
    
    public List<Todo> getTodosByCategory(String category) {
        return repository.getTodosByCategory(category);
    }
    
    public List<String> getAllCategories() {
        return repository.getAllCategories();
    }
    
    public int getTotalTodoCount() { return repository.getTotalTodoCount(); }
    
    public int getCompletedTodoCount() { return repository.getCompletedTodoCount(); }
    
    public int getPendingTodoCount() {
        return getTotalTodoCount() - getCompletedTodoCount();
    }
    
    public double getCompletionPercentage() {
        int total = getTotalTodoCount();
        if (total == 0) return 0.0;
        return (getCompletedTodoCount() * 100.0) / total;
    }
    
    public String getStatsText() {
        int total = getTotalTodoCount();
        int completed = getCompletedTodoCount();
        int pending = total - completed;
        return String.format("%d total • %d done • %d pending", total, completed, pending);
    }
    
    private void validateTodoTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Todo title cannot be empty");
        }
        if (title.trim().length() > 200) {
            throw new IllegalArgumentException("Todo title cannot exceed 200 characters");
        }
    }
}

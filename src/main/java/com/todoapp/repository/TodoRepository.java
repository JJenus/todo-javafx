package com.todoapp.repository;

import com.todoapp.model.Todo;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TodoRepository {
    List<Todo> getAllTodos();
    Optional<Todo> getTodo(String todoId);
    Todo saveTodo(Todo todo);
    boolean deleteTodo(String todoId);
    
    List<Todo> searchTodos(String query);
    List<Todo> getTodosDueToday();
    List<Todo> getOverdueTodos();
    List<Todo> getTodosByCategory(String category);
    
    int getTotalTodoCount();
    int getCompletedTodoCount();
    List<String> getAllCategories();
    
    void initializeDefaultData();
}

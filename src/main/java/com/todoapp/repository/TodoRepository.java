package com.todoapp.repository;

import com.todoapp.model.Project;
import com.todoapp.model.Todo;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TodoRepository {
    List<Project> getAllProjects();
    Optional<Project> getProject(String projectId);
    Project saveProject(Project project);
    boolean deleteProject(String projectId);
    boolean projectExists(String projectId);
    
    Optional<Todo> getTodo(String projectId, String todoId);
    Todo saveTodo(String projectId, Todo todo);
    boolean deleteTodo(String projectId, String todoId);
    
    List<Todo> searchTodos(String query);
    List<Todo> getTodosDueToday();
    List<Todo> getOverdueTodos();
    List<Todo> getTodosByDateRange(LocalDateTime start, LocalDateTime end);
    
    int getProjectCount();
    int getTotalTodoCount();
    int getCompletedTodoCount();
    
    void initializeDefaultData();
    void clearAllData();
}

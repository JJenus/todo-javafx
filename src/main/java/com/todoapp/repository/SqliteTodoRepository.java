package com.todoapp.repository;

import com.todoapp.model.Todo;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class SqliteTodoRepository implements TodoRepository {
    private static final String DB_URL = "jdbc:sqlite:todos.db";
    private final Map<String, Todo> todoCache = new LinkedHashMap<>();
    
    public SqliteTodoRepository() {
        initializeDatabase();
        loadCache();
        initializeDefaultData();
    }
    
    private void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            String createTodosTable = """
                CREATE TABLE IF NOT EXISTS todos (
                    id TEXT PRIMARY KEY,
                    title TEXT NOT NULL,
                    done BOOLEAN NOT NULL DEFAULT 0,
                    time TEXT,
                    created_at TEXT NOT NULL,
                    updated_at TEXT NOT NULL,
                    category TEXT DEFAULT 'General'
                )
            """;
            
            stmt.execute(createTodosTable);
            
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_todos_done ON todos(done)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_todos_time ON todos(time)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_todos_category ON todos(category)");
            
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize database", e);
        }
    }
    
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }
    
    private void loadCache() {
        String sql = "SELECT id, title, done, time, created_at, updated_at, category FROM todos ORDER BY created_at DESC";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                String id = rs.getString("id");
                String title = rs.getString("title");
                boolean done = rs.getBoolean("done");
                LocalDateTime time = rs.getString("time") != null ? 
                    LocalDateTime.parse(rs.getString("time")) : null;
                LocalDateTime createdAt = LocalDateTime.parse(rs.getString("created_at"));
                LocalDateTime updatedAt = LocalDateTime.parse(rs.getString("updated_at"));
                String category = rs.getString("category");
                
                Todo todo = new Todo(id, title, done, time, createdAt, updatedAt, category);
                todoCache.put(id, todo);
            }
        } catch (SQLException e) {
            System.err.println("Failed to load cache: " + e.getMessage());
        }
    }
    
    @Override
    public List<Todo> getAllTodos() {
        synchronized (todoCache) {
            return new ArrayList<>(todoCache.values());
        }
    }
    
    @Override
    public Optional<Todo> getTodo(String todoId) {
        synchronized (todoCache) {
            return Optional.ofNullable(todoCache.get(todoId));
        }
    }
    
    @Override
    public Todo saveTodo(Todo todo) {
        String sql = """
            INSERT OR REPLACE INTO todos(id, title, done, time, created_at, updated_at, category) 
            VALUES(?, ?, ?, ?, ?, ?, ?)
        """;
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, todo.getId());
            pstmt.setString(2, todo.getTitle());
            pstmt.setBoolean(3, todo.isDone());
            pstmt.setString(4, todo.getTime() != null ? todo.getTime().toString() : null);
            pstmt.setString(5, todo.getCreatedAt().toString());
            pstmt.setString(6, todo.getUpdatedAt().toString());
            pstmt.setString(7, todo.getCategory());
            
            pstmt.executeUpdate();
            
            synchronized (todoCache) {
                todoCache.put(todo.getId(), todo);
            }
            
            return todo;
            
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save todo", e);
        }
    }
    
    @Override
    public boolean deleteTodo(String todoId) {
        String sql = "DELETE FROM todos WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, todoId);
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                synchronized (todoCache) {
                    todoCache.remove(todoId);
                }
                return true;
            }
            return false;
            
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete todo", e);
        }
    }
    
    @Override
    public List<Todo> searchTodos(String query) {
        if (query == null || query.trim().isEmpty()) return List.of();
        String searchTerm = query.trim().toLowerCase();
        
        synchronized (todoCache) {
            return todoCache.values().stream()
                    .filter(todo -> todo.getTitle().toLowerCase().contains(searchTerm))
                    .collect(Collectors.toList());
        }
    }
    
    @Override
    public List<Todo> getTodosDueToday() {
        LocalDateTime today = LocalDateTime.now();
        LocalDateTime tomorrow = today.plusDays(1).withHour(0).withMinute(0).withSecond(0);
        
        synchronized (todoCache) {
            return todoCache.values().stream()
                    .filter(todo -> todo.getTime() != null 
                            && !todo.getTime().isBefore(today) 
                            && todo.getTime().isBefore(tomorrow))
                    .collect(Collectors.toList());
        }
    }
    
    @Override
    public List<Todo> getOverdueTodos() {
        synchronized (todoCache) {
            return todoCache.values().stream()
                    .filter(Todo::isOverdue)
                    .collect(Collectors.toList());
        }
    }
    
    @Override
    public List<Todo> getTodosByCategory(String category) {
        synchronized (todoCache) {
            return todoCache.values().stream()
                    .filter(todo -> todo.getCategory().equals(category))
                    .collect(Collectors.toList());
        }
    }
    
    @Override
    public int getTotalTodoCount() {
        synchronized (todoCache) {
            return todoCache.size();
        }
    }
    
    @Override
    public int getCompletedTodoCount() {
        synchronized (todoCache) {
            return (int) todoCache.values().stream()
                    .filter(Todo::isDone)
                    .count();
        }
    }
    
    @Override
    public List<String> getAllCategories() {
        synchronized (todoCache) {
            return todoCache.values().stream()
                    .map(Todo::getCategory)
                    .distinct()
                    .collect(Collectors.toList());
        }
    }
    
    @Override
    public void initializeDefaultData() {
        if (getTotalTodoCount() == 0) {
            // Add sample todos from the reference image
            LocalDateTime sampleDate = LocalDateTime.of(2016, 1, 12, 0, 0);
            
            String[] sampleTodos = {
                "Buy new sweatshirt",
                "Begin promotional phase",
                "Read an article",
                "Try not to fall asleep",
                "Watch 'Sherlock'",
                "Begin QA for the product",
                "Go for a walk"
            };
            
//            for (String todoTitle : sampleTodos) {
//                Todo todo = new Todo(todoTitle);
//                todo.setTime(sampleDate);
//                saveTodo(todo);
//            }
        }
    }
}

package com.todoapp.repository;

import com.todoapp.model.Project;
import com.todoapp.model.Todo;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class SqliteTodoRepository implements TodoRepository {
    private static final String DB_URL = "jdbc:sqlite:todo.db";
    private final Map<String, Project> projectCache = new HashMap<>();
    
    public SqliteTodoRepository() {
        initializeDatabase();
        loadCache();
    }
    
    private void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            stmt.execute("PRAGMA foreign_keys = ON");
            
            String createProjectsTable = """
                CREATE TABLE IF NOT EXISTS projects (
                    id TEXT PRIMARY KEY,
                    name TEXT NOT NULL,
                    created_at TEXT NOT NULL
                )
            """;
            
            String createTodosTable = """
                CREATE TABLE IF NOT EXISTS todos (
                    id TEXT PRIMARY KEY,
                    project_id TEXT NOT NULL,
                    title TEXT NOT NULL,
                    done BOOLEAN NOT NULL DEFAULT 0,
                    time TEXT,
                    created_at TEXT NOT NULL,
                    updated_at TEXT NOT NULL,
                    FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE
                )
            """;
            
            stmt.execute(createProjectsTable);
            stmt.execute(createTodosTable);
            
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_todos_project_id ON todos(project_id)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_todos_done ON todos(done)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_todos_time ON todos(time)");
            
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize database", e);
        }
    }
    
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }
    
    private void loadCache() {
        String sql = "SELECT id, name, created_at FROM projects";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                String id = rs.getString("id");
                String name = rs.getString("name");
                LocalDateTime createdAt = LocalDateTime.parse(rs.getString("created_at"));
                List<Todo> todos = loadTodosForProject(id, conn);
                Project project = new Project(id, name, createdAt, todos);
                projectCache.put(id, project);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load cache", e);
        }
    }
    
    private List<Todo> loadTodosForProject(String projectId, Connection conn) throws SQLException {
        List<Todo> todos = new ArrayList<>();
        String sql = "SELECT id, title, done, time, created_at, updated_at FROM todos WHERE project_id = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, projectId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                String id = rs.getString("id");
                String title = rs.getString("title");
                boolean done = rs.getBoolean("done");
                LocalDateTime time = rs.getString("time") != null ? 
                    LocalDateTime.parse(rs.getString("time")) : null;
                LocalDateTime createdAt = LocalDateTime.parse(rs.getString("created_at"));
                LocalDateTime updatedAt = LocalDateTime.parse(rs.getString("updated_at"));
                
                todos.add(new Todo(id, title, done, time, createdAt, updatedAt));
            }
        }
        return todos;
    }
    
    @Override
    public List<Project> getAllProjects() {
        synchronized (projectCache) {
            return new ArrayList<>(projectCache.values());
        }
    }
    
    @Override
    public Optional<Project> getProject(String projectId) {
        synchronized (projectCache) {
            return Optional.ofNullable(projectCache.get(projectId));
        }
    }
    
    @Override
    public Project saveProject(Project project) {
        String sql = "INSERT OR REPLACE INTO projects(id, name, created_at) VALUES(?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            conn.setAutoCommit(false);
            pstmt.setString(1, project.getId());
            pstmt.setString(2, project.getName());
            pstmt.setString(3, project.getCreatedAt().toString());
            pstmt.executeUpdate();
            saveProjectTodos(project, conn);
            conn.commit();
            
            synchronized (projectCache) {
                projectCache.put(project.getId(), project);
            }
            return project;
            
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save project", e);
        }
    }
    
    private void saveProjectTodos(Project project, Connection conn) throws SQLException {
        String deleteSql = "DELETE FROM todos WHERE project_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(deleteSql)) {
            pstmt.setString(1, project.getId());
            pstmt.executeUpdate();
        }
        
        String insertSql = """
            INSERT INTO todos(id, project_id, title, done, time, created_at, updated_at) 
            VALUES(?, ?, ?, ?, ?, ?, ?)
        """;
        
        try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
            for (Todo todo : project.getTodos()) {
                pstmt.setString(1, todo.getId());
                pstmt.setString(2, project.getId());
                pstmt.setString(3, todo.getTitle());
                pstmt.setBoolean(4, todo.isDone());
                pstmt.setString(5, todo.getTime() != null ? todo.getTime().toString() : null);
                pstmt.setString(6, todo.getCreatedAt().toString());
                pstmt.setString(7, todo.getUpdatedAt().toString());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }
    
    @Override
    public boolean deleteProject(String projectId) {
        String sql = "DELETE FROM projects WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, projectId);
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                synchronized (projectCache) {
                    projectCache.remove(projectId);
                }
                return true;
            }
            return false;
            
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete project", e);
        }
    }
    
    @Override
    public boolean projectExists(String projectId) {
        synchronized (projectCache) {
            return projectCache.containsKey(projectId);
        }
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
            saveProject(project);
            return todo;
        }).orElseThrow(() -> new IllegalArgumentException("Project not found: " + projectId));
    }
    
    @Override
    public boolean deleteTodo(String projectId, String todoId) {
        return getProject(projectId).map(project -> {
            project.removeTodo(todoId);
            saveProject(project);
            return true;
        }).orElse(false);
    }
    
    @Override
    public List<Todo> searchTodos(String query) {
        if (query == null || query.trim().isEmpty()) return List.of();
        String searchTerm = "%" + query.trim().toLowerCase() + "%";
        List<Todo> results = new ArrayList<>();
        synchronized (projectCache) {
            for (Project project : projectCache.values()) {
                results.addAll(project.getTodos().stream()
                        .filter(todo -> todo.getTitle().toLowerCase().contains(searchTerm))
                        .collect(Collectors.toList()));
            }
        }
        return results;
    }
    
    @Override
    public List<Todo> getTodosDueToday() {
        LocalDateTime today = LocalDateTime.now();
        LocalDateTime tomorrow = today.plusDays(1).withHour(0).withMinute(0).withSecond(0);
        return getTodosByDateRange(today, tomorrow);
    }
    
    @Override
    public List<Todo> getOverdueTodos() {
        List<Todo> results = new ArrayList<>();
        synchronized (projectCache) {
            for (Project project : projectCache.values()) {
                results.addAll(project.getTodos().stream()
                        .filter(Todo::isOverdue)
                        .collect(Collectors.toList()));
            }
        }
        return results;
    }
    
    @Override
    public List<Todo> getTodosByDateRange(LocalDateTime start, LocalDateTime end) {
        List<Todo> results = new ArrayList<>();
        synchronized (projectCache) {
            for (Project project : projectCache.values()) {
                results.addAll(project.getTodos().stream()
                        .filter(todo -> todo.getTime() != null 
                                && !todo.getTime().isBefore(start) 
                                && todo.getTime().isBefore(end))
                        .collect(Collectors.toList()));
            }
        }
        return results;
    }
    
    @Override
    public int getProjectCount() {
        synchronized (projectCache) {
            return projectCache.size();
        }
    }
    
    @Override
    public int getTotalTodoCount() {
        synchronized (projectCache) {
            return projectCache.values().stream()
                    .mapToInt(project -> project.getTodos().size())
                    .sum();
        }
    }
    
    @Override
    public int getCompletedTodoCount() {
        synchronized (projectCache) {
            return projectCache.values().stream()
                    .flatMap(project -> project.getTodos().stream())
                    .filter(Todo::isDone)
                    .mapToInt(todo -> 1)
                    .sum();
        }
    }
    
    @Override
    public void initializeDefaultData() {
        if (getProjectCount() == 0) {
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
    public void clearAllData() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM todos");
            stmt.execute("DELETE FROM projects");
            synchronized (projectCache) {
                projectCache.clear();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to clear data", e);
        }
    }
}

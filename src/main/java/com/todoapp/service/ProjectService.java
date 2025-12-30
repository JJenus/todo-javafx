package com.todoapp.service;

import com.todoapp.model.Project;
import com.todoapp.repository.TodoRepository;
import java.util.List;

public class ProjectService {
    private final TodoRepository repository;
    
    public ProjectService(TodoRepository repository) {
        this.repository = repository;
    }
    
    public Object[] getProjectStats(String projectId) {
        return repository.getProject(projectId).map(project -> {
            long total = project.getTotalCount();
            long completed = project.getCompletedCount();
            double percentage = total > 0 ? (completed * 100.0) / total : 0.0;
            return new Object[]{total, completed, percentage};
        }).orElse(null);
    }
    
    public List<Project> getProjectsSortedByDate() {
        List<Project> projects = repository.getAllProjects();
        projects.sort((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()));
        return projects;
    }
    
    public List<Project> getProjectsSortedByName() {
        List<Project> projects = repository.getAllProjects();
        projects.sort((p1, p2) -> p1.getName().compareToIgnoreCase(p2.getName()));
        return projects;
    }
    
    public boolean projectNameExists(String name) {
        return repository.getAllProjects().stream()
                .anyMatch(project -> project.getName().equalsIgnoreCase(name.trim()));
    }
}

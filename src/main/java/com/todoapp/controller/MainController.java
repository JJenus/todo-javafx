package com.todoapp.controller;

import com.todoapp.model.Project;
import com.todoapp.model.Todo;
import com.todoapp.repository.SqliteTodoRepository;
import com.todoapp.service.TodoService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    
    @FXML private ListView<Project> projectListView;
    @FXML private ListView<Todo> todoListView;
    @FXML private TextField searchField;
    @FXML private Label statsLabel;
    @FXML private VBox projectDetailView;
    @FXML private TextField newProjectField;
    @FXML private TextField newTodoField;
    @FXML private DatePicker dueDatePicker;
    @FXML private Label projectTitleLabel;
    @FXML private Label projectStatsLabel;
    @FXML private Button addProjectButton;
    @FXML private Button addTodoButton;
    @FXML private Button deleteProjectButton;
    @FXML private VBox searchResultsView;
    @FXML private ListView<Todo> searchResultsListView;
    @FXML private Label searchResultsLabel;
    
    private final TodoService todoService;
    private final ObservableList<Project> projects = FXCollections.observableArrayList();
    private final ObservableList<Todo> todos = FXCollections.observableArrayList();
    private final ObservableList<Todo> searchResults = FXCollections.observableArrayList();
    
    private Project currentProject;
    private boolean isSearching = false;
    
    public MainController() {
        this.todoService = new TodoService(new SqliteTodoRepository());
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupProjectListView();
        setupTodoListView();
        setupSearchResultsListView();
        setupSearchField();
        loadInitialData();
        updateStats();
        
        dueDatePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isBefore(LocalDate.now()));
            }
        });
    }
    
    private void setupProjectListView() {
        projectListView.setItems(projects);
        projectListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Project project, boolean empty) {
                super.updateItem(project, empty);
                if (empty || project == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(formatProjectDisplay(project));
                }
            }
        });
        
        projectListView.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldVal, newVal) -> {
                    if (newVal != null) showProjectDetails(newVal);
                });
    }
    
    private void setupTodoListView() {
        todoListView.setItems(todos);
        todoListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Todo todo, boolean empty) {
                super.updateItem(todo, empty);
                if (empty || todo == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setGraphic(createTodoCellContent(todo));
                }
            }
        });
    }
    
    private void setupSearchResultsListView() {
        searchResultsListView.setItems(searchResults);
        searchResultsListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Todo todo, boolean empty) {
                super.updateItem(todo, empty);
                if (empty || todo == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setGraphic(createTodoCellContent(todo));
                }
            }
        });
    }
    
    private VBox createTodoCellContent(Todo todo) {
        CheckBox checkBox = new CheckBox();
        Label titleLabel = new Label(todo.getTitle());
        Label detailsLabel = new Label();
        
        if (todo.isDone()) {
            titleLabel.getStyleClass().add("done-text");
            titleLabel.setTextFill(Color.GRAY);
            detailsLabel.setText("Completed");
        } else if (todo.isOverdue()) {
            titleLabel.getStyleClass().add("overdue-text");
            titleLabel.setTextFill(Color.RED);
            detailsLabel.setText("Overdue since " + todo.getFormattedDueDate());
        } else if (todo.isDueToday()) {
            detailsLabel.setText("Due Today");
            detailsLabel.setTextFill(Color.ORANGE);
        } else if (todo.hasDueDate()) {
            detailsLabel.setText("Due: " + todo.getFormattedDueDate());
        }
        
        checkBox.setSelected(todo.isDone());
        checkBox.setOnAction(e -> {
            if (currentProject != null) {
                todoService.toggleTodoDone(currentProject.getId(), todo.getId());
                refreshCurrentView();
                updateStats();
            }
        });
        
        VBox content = new VBox(5, 
            new javafx.scene.layout.HBox(10, checkBox, titleLabel), 
            detailsLabel
        );
        content.setPadding(new javafx.geometry.Insets(5));
        
        content.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) editTodo(todo);
        });
        
        return content;
    }
    
    private void setupSearchField() {
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null || newVal.trim().isEmpty()) {
                exitSearchMode();
            } else {
                enterSearchMode(newVal.trim());
            }
        });
    }
    
    private void loadInitialData() {
        projects.setAll(todoService.getAllProjects());
        if (!projects.isEmpty()) projectListView.getSelectionModel().select(0);
    }
    
    private void showProjectDetails(Project project) {
        currentProject = project;
        isSearching = false;
        projectDetailView.setVisible(true);
        searchResultsView.setVisible(false);
        projectTitleLabel.setText(project.getName());
        projectStatsLabel.setText(String.format("(%d/%d completed)", 
            project.getCompletedCount(), project.getTotalCount()));
        todos.setAll(project.getTodos());
    }
    
    private void enterSearchMode(String query) {
        isSearching = true;
        projectDetailView.setVisible(false);
        searchResultsView.setVisible(true);
        List<Todo> results = todoService.searchTodos(query);
        searchResults.setAll(results);
        searchResultsLabel.setText(String.format("Found %d results for \"%s\"", 
            results.size(), query));
    }
    
    private void exitSearchMode() {
        isSearching = false;
        searchResultsView.setVisible(false);
        if (currentProject != null) {
            projectDetailView.setVisible(true);
            todos.setAll(currentProject.getTodos());
        }
    }
    
    @FXML
    private void handleAddProject() {
        String name = newProjectField.getText().trim();
        if (!name.isEmpty()) {
            try {
                Project project = todoService.createProject(name);
                projects.add(project);
                newProjectField.clear();
                projectListView.getSelectionModel().select(project);
                updateStats();
            } catch (IllegalArgumentException e) {
                showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }
    
    @FXML
    private void handleAddTodo() {
        if (currentProject == null) return;
        String title = newTodoField.getText().trim();
        if (!title.isEmpty()) {
            try {
                LocalDateTime dueTime = dueDatePicker.getValue() != null 
                    ? dueDatePicker.getValue().atStartOfDay() 
                    : null;
                Todo todo = todoService.createTodo(currentProject.getId(), title, dueTime);
                todos.add(todo);
                newTodoField.clear();
                dueDatePicker.setValue(null);
                updateStats();
                updateProjectStats();
            } catch (IllegalArgumentException e) {
                showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }
    
    @FXML
    private void handleDeleteProject() {
        if (currentProject != null) confirmDeleteProject(currentProject);
    }
    
    private void confirmDeleteProject(Project project) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Project");
        alert.setHeaderText("Delete '" + project.getName() + "'?");
        alert.setContentText("This will delete all todos in this project.");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean deleted = todoService.deleteProject(project.getId());
            if (deleted) {
                projects.remove(project);
                if (currentProject == project) {
                    currentProject = null;
                    projectDetailView.setVisible(false);
                }
                updateStats();
                showAlert("Success", "Project deleted.", Alert.AlertType.INFORMATION);
            }
        }
    }
    
    private void editTodo(Todo todo) {
        Dialog<Todo> dialog = new Dialog<>();
        dialog.setTitle("Edit Todo");
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        
        TextField titleField = new TextField(todo.getTitle());
        DatePicker datePicker = new DatePicker();
        if (todo.getTime() != null) datePicker.setValue(todo.getTime().toLocalDate());
        
        VBox form = new VBox(10, 
            new Label("Title:"), titleField,
            new Label("Due Date:"), datePicker);
        form.setPadding(new javafx.geometry.Insets(20));
        dialog.getDialogPane().setContent(form);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                LocalDateTime newTime = datePicker.getValue() != null 
                    ? datePicker.getValue().atStartOfDay() 
                    : null;
                todoService.updateTodo(currentProject.getId(), todo.getId(), 
                    titleField.getText(), newTime);
                refreshCurrentView();
                updateStats();
                updateProjectStats();
                return todo;
            }
            return null;
        });
        dialog.showAndWait();
    }
    
    private void refreshCurrentView() {
        if (isSearching) {
            enterSearchMode(searchField.getText().trim());
        } else if (currentProject != null) {
            todoService.getProject(currentProject.getId()).ifPresent(project -> {
                currentProject = project;
                todos.setAll(project.getTodos());
            });
        }
    }
    
    private void updateStats() {
        int totalProjects = todoService.getTotalProjects();
        int totalTodos = todoService.getTotalTodos();
        int completed = todoService.getCompletedTodosCount();
        double percentage = todoService.getOverallCompletionPercentage();
        statsLabel.setText(String.format(
            "Projects: %d | Todos: %d | Completed: %d (%.1f%%)",
            totalProjects, totalTodos, completed, percentage
        ));
    }
    
    private void updateProjectStats() {
        if (currentProject != null) {
            projectStatsLabel.setText(String.format("(%d/%d completed)", 
                currentProject.getCompletedCount(), currentProject.getTotalCount()));
        }
    }
    
    private String formatProjectDisplay(Project project) {
        return String.format("%s - %d/%d (%s)", 
            project.getName(),
            project.getCompletedCount(),
            project.getTotalCount(),
            project.getCompletionPercentage());
    }
    
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

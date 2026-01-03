package com.todoapp.controller;

import com.todoapp.model.Todo;
import com.todoapp.service.TodoService;
import com.todoapp.util.AppColors;
import io.github.palexdev.materialfx.controls.*;
import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML private BorderPane rootPane;
    @FXML private VBox mainContainer;
    @FXML private ScrollPane scrollPane;
    @FXML private Label dateNumberLabel;
    @FXML private Label monthYearLabel;
    @FXML private Label dayLabel;
    @FXML private Label titleLabel;
    @FXML private Label statsLabel;
    @FXML private MFXTextField newTodoField;
    @FXML private MFXButton addButton;
    @FXML private MFXButton themeToggleButton;
    @FXML private HBox headerBox;
    @FXML private HBox addTodoBox;

    private final TodoService todoService = new TodoService();
    private final ObservableList<Todo> todos = FXCollections.observableArrayList();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
    private final DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("EEEE");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupUI();
        loadTodos();
        updateDateDisplay();
        updateStats();
    }

    private void setupUI() {
        // Apply initial theme
        applyTheme();
        
        // Set up theme toggle button
        updateThemeToggleButton();
        themeToggleButton.setOnAction(e -> toggleTheme());
        
        // Configure scroll pane
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");

        // Set up main container
        mainContainer.setSpacing(8);
        mainContainer.setPadding(new Insets(20));

        // Configure MaterialFX text field
        newTodoField.setFloatingText("Add a new todo");
        newTodoField.setPrefWidth(400);
        styleTextField(newTodoField);

        // Override MaterialFX focus color
        newTodoField.getStyleClass().add("custom-mfx-text-field");

        // Configure MaterialFX add button
        addButton.setText("ADD");
        stylePrimaryButton(addButton);
        addButton.setRippleAnimateBackground(false);

        // Set up add button and field actions
        addButton.setOnAction(e -> handleAddTodo());
        newTodoField.setOnAction(e -> handleAddTodo());

        // Custom hover effects for button
        addButton.setOnMouseEntered(e -> 
            addButton.setStyle(
                "-mfx-background-color: " + AppColors.toCss(AppColors.getPrimaryActionHover()) + ";" +
                "-mfx-text-fill: " + AppColors.toCss(AppColors.getPrimaryActionText()) + ";" +
                "-mfx-background-radius: 4;" +
                "-mfx-depth-level: LEVEL2;"
            )
        );

        addButton.setOnMouseExited(e -> stylePrimaryButton(addButton));
        
        // Set up keyboard shortcut for theme toggle (Ctrl+T or Cmd+T)
        rootPane.setOnKeyPressed(event -> {
            if (event.isShortcutDown() && event.getCode().toString().equals("T")) {
                toggleTheme();
            }
        });
    }
    
    private void styleTextField(MFXTextField textField) {
        textField.setStyle(
            "-mfx-background-color: " + AppColors.toCss(AppColors.getInputBackground()) + ";" +
            "-mfx-border-color: " + AppColors.toCss(AppColors.getInputBorder()) + ";" +
            "-mfx-border-radius: 4;" +
            "-fx-prompt-text-fill: " + AppColors.toCss(AppColors.getPlaceholderText()) + ";" +
            "-fx-text-fill: " + AppColors.toCss(AppColors.getPrimaryText()) + ";"
        );
    }
    
    private void stylePrimaryButton(MFXButton button) {
        button.setStyle(
            "-mfx-background-color: " + AppColors.toCss(AppColors.getPrimaryAction()) + ";" +
            "-mfx-text-fill: " + AppColors.toCss(AppColors.getPrimaryActionText()) + ";" +
            "-mfx-font-weight: bold;" +
            "-mfx-background-radius: 4;" +
            "-mfx-depth-level: LEVEL2;"
        );
    }
    
    private void styleSecondaryButton(MFXButton button) {
        button.setStyle(
            "-mfx-background-color: " + AppColors.toCss(AppColors.getSecondaryAction()) + ";" +
            "-mfx-text-fill: " + AppColors.toCss(AppColors.getSecondaryActionText()) + ";" +
            "-mfx-background-radius: 20;" +
            "-fx-font-size: 20px;" +
            "-fx-padding: 5 10;"
        );
    }

    private void updateDateDisplay() {
        LocalDate today = LocalDate.now();
        dateNumberLabel.setText(today.format(DateTimeFormatter.ofPattern("dd")));
        monthYearLabel.setText(today.format(DateTimeFormatter.ofPattern("MMM yyyy")).toUpperCase());
        dayLabel.setText(today.format(dayFormatter).toUpperCase());

        // Set text colors via inline styles
        dateNumberLabel.setStyle("-fx-text-fill: " + AppColors.toCss(AppColors.getPrimaryText()) + "; " +
                "-fx-font-size: 48px; -fx-font-weight: bold;");
        monthYearLabel.setStyle("-fx-text-fill: " + AppColors.toCss(AppColors.getSecondaryText()) + "; " +
                "-fx-font-size: 14px; -fx-font-weight: bold; -fx-letter-spacing: 1px;");
        dayLabel.setStyle("-fx-text-fill: " + AppColors.toCss(AppColors.getSecondaryText()) + "; " +
                "-fx-font-size: 12px; -fx-font-weight: bold; -fx-letter-spacing: 2px;");
    }

    private void loadTodos() {
        todos.setAll(todoService.getAllTodos());
        renderTodos();
    }

    private void renderTodos() {
        VBox todosContainer = new VBox(8);
        todosContainer.setPadding(new Insets(20));

        if (todos.isEmpty()) {
            Label emptyLabel = new Label("No todos yet. Add one above!");
            emptyLabel.setFont(Font.font("System", FontWeight.NORMAL, 14));
            emptyLabel.setTextFill(AppColors.getDisabledText());
            emptyLabel.setPadding(new Insets(20));
            emptyLabel.setAlignment(Pos.CENTER);
            todosContainer.getChildren().add(emptyLabel);
        } else {
            for (Todo todo : todos) {
                HBox todoItem = createTodoItem(todo);
                todosContainer.getChildren().add(todoItem);
            }
        }

        scrollPane.setContent(todosContainer);
    }

    private HBox createTodoItem(Todo todo) {
        HBox container = new HBox(12);
        container.setPadding(new Insets(12, 16, 12, 12));
        container.setAlignment(Pos.CENTER_LEFT);
        container.setMaxWidth(Double.MAX_VALUE);

        // Set base style
        container.setStyle("-fx-background-color: " + AppColors.toCss(AppColors.getDefaultItemBg()) + "; " +
                "-fx-background-radius: 4; " +
                "-fx-border-color: " + AppColors.toCss(AppColors.getCardBorder()) + "; " +
                "-fx-border-width: 1; " +
                "-fx-border-radius: 4; " +
                "-fx-cursor: hand;");

        // Add hover effect
        container.setOnMouseEntered(e -> {
            if (!todo.isDone()) {
                container.setStyle("-fx-background-color: " + AppColors.toCss(AppColors.getHoverItemBg()) + "; " +
                        "-fx-background-radius: 4; " +
                        "-fx-border-color: " + AppColors.toCss(AppColors.getCardBorder()) + "; " +
                        "-fx-border-width: 1; " +
                        "-fx-border-radius: 4; " +
                        "-fx-cursor: hand;");
            }
        });

        container.setOnMouseExited(e -> {
            if (todo.isDone()) {
                container.setStyle("-fx-background-color: " + AppColors.toCss(AppColors.getCompletedItemBg()) + "; " +
                        "-fx-background-radius: 4; " +
                        "-fx-border-color: " + AppColors.toCss(AppColors.getCardBorder()) + "; " +
                        "-fx-border-width: 1; " +
                        "-fx-border-radius: 4; " +
                        "-fx-cursor: hand;");
            } else {
                container.setStyle("-fx-background-color: " + AppColors.toCss(AppColors.getDefaultItemBg()) + "; " +
                        "-fx-background-radius: 4; " +
                        "-fx-border-color: " + AppColors.toCss(AppColors.getCardBorder()) + "; " +
                        "-fx-border-width: 1; " +
                        "-fx-border-radius: 4; " +
                        "-fx-cursor: hand;");
            }
        });

        // Use MaterialFX checkbox
        MFXCheckbox checkBox = new MFXCheckbox("");
        checkBox.setSelected(todo.isDone());
        checkBox.setStyle("-mfx-main-color: " + AppColors.toCss(AppColors.getPrimaryAction()) + ";" +
                "-mfx-secondary-color: " + AppColors.toCss(AppColors.getSecondaryAction()) + ";");

        checkBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            todoService.toggleTodoDone(todo.getId());
            updateTodoItemStyle(container, todo, newVal);
            updateStats();

            // Animation
            FadeTransition ft = new FadeTransition(Duration.millis(200), container);
            ft.setFromValue(0.8);
            ft.setToValue(1.0);
            ft.play();
        });

        // Todo text
        Label titleLabel = new Label(todo.getTitle());
        titleLabel.setFont(Font.font("System", FontWeight.NORMAL, 14));
        titleLabel.setWrapText(true);
        titleLabel.setMaxWidth(400);
        titleLabel.setTextFill(todo.isDone() ? AppColors.getDisabledText() : AppColors.getPrimaryText());

        if (todo.isDone()) {
            titleLabel.setStyle("-fx-strikethrough: true;");
            titleLabel.setTextFill(AppColors.getDisabledText());
        }

        // Right container for actions
        HBox rightContainer = new HBox(8);
        rightContainer.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(rightContainer, Priority.ALWAYS);

        // Due date if exists
        if (todo.hasDueDate()) {
            Label dateLabel = new Label(todo.getFormattedDueDate());
            dateLabel.setFont(Font.font("System", FontWeight.MEDIUM, 11));
            dateLabel.setTextFill(AppColors.getSecondaryText());
            dateLabel.setPadding(new Insets(2, 8, 2, 8));
            dateLabel.setStyle("-fx-background-color: " + AppColors.toCss(AppColors.getElevatedSurface()) + "; " +
                    "-fx-background-radius: 10;");
            rightContainer.getChildren().add(dateLabel);
        }

        // Action buttons
        HBox actionButtons = new HBox(4);
        actionButtons.setAlignment(Pos.CENTER_RIGHT);

        // Edit button
        MFXButton editButton = new MFXButton(null);
        FontIcon editIcon = new FontIcon(FontAwesomeSolid.PENCIL_ALT);
        editIcon.setIconSize(16);
        editIcon.setIconColor(AppColors.toCss(AppColors.getDefaultIcon()));
        editButton.setGraphic(editIcon);
        editButton.setStyle("-mfx-background-color: transparent;" +
                "-fx-padding: 4;" +
                "-fx-cursor: hand;" +
                "-fx-min-width: 36px;" +
                "-fx-min-height: 36px;");
        editButton.setOnAction(e -> editTodo(todo));

        editButton.setOnMouseEntered(e -> {
            editIcon.setIconColor(AppColors.toCss(AppColors.getActionIcon()));
            editButton.setStyle("-mfx-background-color: " + AppColors.toCss(AppColors.getElevatedSurface()) + ";" +
                    "-fx-padding: 4;" +
                    "-fx-background-radius: 4;" +
                    "-fx-cursor: hand;" +
                    "-fx-min-width: 36px;" +
                    "-fx-min-height: 36px;");
        });

        editButton.setOnMouseExited(e -> {
            editIcon.setIconColor(AppColors.toCss(AppColors.getDefaultIcon()));
            editButton.setStyle("-mfx-background-color: transparent;" +
                    "-fx-padding: 4;" +
                    "-fx-cursor: hand;" +
                    "-fx-min-width: 36px;" +
                    "-fx-min-height: 36px;");
        });

        // Delete button
        MFXButton deleteButton = new MFXButton(null);
        FontIcon deleteIcon = new FontIcon(FontAwesomeSolid.TRASH_ALT);
        deleteIcon.setIconSize(16);
        deleteIcon.setIconColor(AppColors.toCss(AppColors.getDefaultIcon()));
        deleteButton.setGraphic(deleteIcon);
        deleteButton.setStyle("-mfx-background-color: transparent;" +
                "-fx-padding: 4;" +
                "-fx-cursor: hand;" +
                "-fx-min-width: 36px;" +
                "-fx-min-height: 36px;");
        deleteButton.setOnAction(e -> deleteTodo(todo, container));

        deleteButton.setOnMouseEntered(e -> {
            deleteIcon.setIconColor(AppColors.toCss(AppColors.getDangerIcon()));
            deleteButton.setStyle("-mfx-background-color: " + AppColors.toCss(AppColors.getElevatedSurface()) + ";" +
                    "-fx-padding: 4;" +
                    "-fx-background-radius: 4;" +
                    "-fx-cursor: hand;" +
                    "-fx-min-width: 36px;" +
                    "-fx-min-height: 36px;");
        });

        deleteButton.setOnMouseExited(e -> {
            deleteIcon.setIconColor(AppColors.toCss(AppColors.getDefaultIcon()));
            deleteButton.setStyle("-mfx-background-color: transparent;" +
                    "-fx-padding: 4;" +
                    "-fx-cursor: hand;" +
                    "-fx-min-width: 36px;" +
                    "-fx-min-height: 36px;");
        });

        actionButtons.getChildren().addAll(editButton, deleteButton);
        rightContainer.getChildren().add(actionButtons);

        // Assemble container
        container.getChildren().addAll(checkBox, titleLabel, rightContainer);

        // Set initial completed style if needed
        if (todo.isDone()) {
            container.setStyle("-fx-background-color: " + AppColors.toCss(AppColors.getCompletedItemBg()) + "; " +
                    "-fx-background-radius: 4; " +
                    "-fx-border-color: " + AppColors.toCss(AppColors.getCardBorder()) + "; " +
                    "-fx-border-width: 1; " +
                    "-fx-border-radius: 4; " +
                    "-fx-cursor: hand;");
        }

        return container;
    }

    private void updateTodoItemStyle(HBox container, Todo todo, boolean isDone) {
        if (isDone) {
            container.setStyle("-fx-background-color: " + AppColors.toCss(AppColors.getCompletedItemBg()) + "; " +
                    "-fx-background-radius: 4; " +
                    "-fx-border-color: " + AppColors.toCss(AppColors.getCardBorder()) + "; " +
                    "-fx-border-width: 1; " +
                    "-fx-border-radius: 4; " +
                    "-fx-cursor: hand;");

            for (javafx.scene.Node node : container.getChildren()) {
                if (node instanceof Label) {
                    ((Label) node).setTextFill(AppColors.getDisabledText());
                    node.setStyle("-fx-strikethrough: true;");
                }
            }
        } else {
            container.setStyle("-fx-background-color: " + AppColors.toCss(AppColors.getDefaultItemBg()) + "; " +
                    "-fx-background-radius: 4; " +
                    "-fx-border-color: " + AppColors.toCss(AppColors.getCardBorder()) + "; " +
                    "-fx-border-width: 1; " +
                    "-fx-border-radius: 4; " +
                    "-fx-cursor: hand;");

            for (javafx.scene.Node node : container.getChildren()) {
                if (node instanceof Label) {
                    ((Label) node).setTextFill(AppColors.getPrimaryText());
                    node.setStyle("-fx-strikethrough: false;");
                }
            }
        }
    }

    @FXML
    private void handleAddTodo() {
        String title = newTodoField.getText().trim();
        if (!title.isEmpty()) {
            try {
                todoService.createTodo(title);
                newTodoField.clear();
                newTodoField.requestFocus();
                loadTodos();
                updateStats();

                // Scroll to top (new items are added at top)
                scrollPane.setVvalue(0);

            } catch (IllegalArgumentException e) {
                showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    private void editTodo(Todo todo) {
        Dialog<Todo> dialog = new Dialog<>();
        dialog.setTitle("Edit Todo");
        dialog.setHeaderText(null);

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, cancelButtonType);

        // Create form with MaterialFX controls
        MFXTextField titleField = new MFXTextField(todo.getTitle());
        titleField.setFloatingText("Todo title");
        titleField.setPrefWidth(300);
        styleTextField(titleField);

        DatePicker datePicker = new DatePicker();
        if (todo.getTime() != null) {
            datePicker.setValue(todo.getTime().toLocalDate());
        }
        datePicker.setPromptText("Due date (optional)");
        datePicker.setStyle("-fx-background-color: " + AppColors.toCss(AppColors.getInputBackground()) + "; " +
                "-fx-border-color: " + AppColors.toCss(AppColors.getInputBorder()) + ";");

        VBox form = new VBox(12,
                new Label("Title:"), titleField,
                new Label("Due Date:"), datePicker);
        form.setPadding(new Insets(20));

        // Style labels
        for (javafx.scene.Node node : form.getChildren()) {
            if (node instanceof Label) {
                ((Label) node).setStyle("-fx-text-fill: " + AppColors.toCss(AppColors.getPrimaryText()) + ";");
            }
        }

        dialog.getDialogPane().setContent(form);

        // Style dialog
        dialog.getDialogPane().setStyle("-fx-background-color: " + AppColors.toCss(AppColors.getSurface()) + ";");

        // Convert result
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                LocalDateTime newTime = datePicker.getValue() != null
                        ? datePicker.getValue().atStartOfDay()
                        : null;

                todoService.updateTodo(todo.getId(), titleField.getText(),
                        todo.getCategory(), newTime);

                loadTodos();
                updateStats();
                return todo;
            }
            return null;
        });

        dialog.showAndWait();
    }

    private void deleteTodo(Todo todo, HBox container) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Todo");
        alert.setHeaderText("Delete '" + todo.getTitle() + "'?");
        alert.setContentText("This action cannot be undone.");

        DialogPane alertPane = alert.getDialogPane();
        alertPane.setStyle("-fx-background-color: " + AppColors.toCss(AppColors.getSurface()) + ";");

        ButtonType okButton = ButtonType.OK;
        ButtonType cancelButton = ButtonType.CANCEL;
        alert.getButtonTypes().setAll(okButton, cancelButton);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Fade out animation
            FadeTransition ft = new FadeTransition(Duration.millis(200), container);
            ft.setFromValue(1.0);
            ft.setToValue(0.0);
            ft.setOnFinished(e -> {
                boolean deleted = todoService.deleteTodo(todo.getId());
                if (deleted) {
                    loadTodos();
                    updateStats();
                }
            });
            ft.play();
        }
    }

    private void updateStats() {
        statsLabel.setText(todoService.getStatsText());
        statsLabel.setStyle("-fx-text-fill: " + AppColors.toCss(AppColors.getSecondaryText()) + "; " +
                "-fx-font-size: 13px;");
        
        // Update title label color
        titleLabel.setStyle("-fx-text-fill: " + AppColors.toCss(AppColors.getPrimaryText()) + "; " +
                "-fx-font-size: 24px; -fx-font-weight: bold;");
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: " + AppColors.toCss(AppColors.getSurface()) + ";");

        alert.showAndWait();
    }
    
    private void updateThemeToggleButton() {
        if (AppColors.getCurrentTheme() == AppColors.Theme.LIGHT) {
            themeToggleButton.setText("🌙");  // Moon icon for switching to dark
        } else {
            themeToggleButton.setText("☀️");  // Sun icon for switching to light
        }
        styleSecondaryButton(themeToggleButton);
    }
    
    private void toggleTheme() {
        // Toggle the theme
        AppColors.toggleTheme();
        
        // Update the button appearance
        updateThemeToggleButton();
        
        // Apply the theme to the entire UI
        applyTheme();
        
        // Refresh todos to use new theme colors
        loadTodos();
    }
    
    private void applyTheme() {
        // Apply theme to root pane
        rootPane.setStyle("-fx-background-color: " + AppColors.toCss(AppColors.getAppBackground()) + ";");
        
        // Apply theme to main container
        mainContainer.setStyle("-fx-background-color: " + AppColors.toCss(AppColors.getSurface()) + ";");
        
        // Apply theme to header boxes
        headerBox.setStyle("-fx-background-color: " + AppColors.toCss(AppColors.getSurface()) + "; -fx-padding: 30 40;");
        addTodoBox.setStyle("-fx-background-color: " + AppColors.toCss(AppColors.getElevatedSurface()) + "; -fx-padding: 20 40;");
        
        // Update UI components
        updateDateDisplay();
        updateStats();
        
        // Update text field and buttons
        styleTextField(newTodoField);
        stylePrimaryButton(addButton);
        styleSecondaryButton(themeToggleButton);
    }
}
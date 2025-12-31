# Todo Application

A JavaFX desktop todo application with SQLite persistence and Material Design UI.

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher

## Project Structure

```
src/main/java/com/todoapp/
├── App.java                    # Application entry point
├── controller/
│   └── MainController.java     # Main UI controller
├── model/
│   └── Todo.java              # Todo data model
├── repository/
│   ├── TodoRepository.java     # Repository interface
│   └── SqliteTodoRepository.java # SQLite implementation
├── service/
│   └── TodoService.java        # Business logic layer
└── util/
    └── AppColors.java          # Color constants

src/main/resources/com/todoapp/view/
├── main.fxml                   # Main UI layout
└── styles.css                  # Custom styles
```

## Core Classes

### Todo (Model)
Represents a todo item with the following properties:
- `id`: Unique identifier (UUID)
- `title`: Todo description
- `done`: Completion status
- `time`: Optional due date/time
- `category`: Classification (default: "General")
- `createdAt`: Creation timestamp
- `updatedAt`: Last modification timestamp

Key methods:
- `toggleDone()`: Toggles completion status
- `isOverdue()`: Checks if due date has passed
- `isDueToday()`: Checks if due today
- `getFormattedDueDate()`: Returns formatted date string

### TodoService (Service Layer)
Main business logic class providing:
- Todo CRUD operations
- Search and filtering
- Statistics calculation
- Category management

Key methods:
- `createTodo(String title)`: Creates new todo
- `toggleTodoDone(String todoId)`: Toggles completion
- `updateTodo()`: Updates title, category, or due date
- `searchTodos(String query)`: Searches todos
- `getTodayTodos()`: Returns todos due today
- `getAllCategories()`: Returns all categories

### SqliteTodoRepository (Persistence)
SQLite implementation of TodoRepository with:
- Automatic database initialization
- In-memory caching for performance
- Indexed queries for common operations

Database schema:
```sql
CREATE TABLE todos (
    id TEXT PRIMARY KEY,
    title TEXT NOT NULL,
    done BOOLEAN NOT NULL DEFAULT 0,
    time TEXT,
    created_at TEXT NOT NULL,
    updated_at TEXT NOT NULL,
    category TEXT DEFAULT 'General'
)
```

### MainController (UI Controller)
Handles UI interactions and updates:
- Manages todo list display
- Handles user input events
- Updates statistics and date display
- Applies custom styling

## Building and Running

### Build with Maven
```bash
mvn clean compile
```

### Run Application
```bash
mvn javafx:run
```

Or run the main class directly:
```bash
java --module-path <path-to-javafx-sdk>/lib \
     --add-modules javafx.controls,javafx.fxml \
     -cp target/classes:target/dependency/* \
     com.todoapp.App
```

### Create Executable JAR
```bash
mvn clean package
```

## Database

The application uses SQLite with a local file `todos.db` in the working directory. The database is automatically created on first run.

## Dependencies

- **JavaFX 21**: UI framework
- **MaterialFX 11.16.0**: Material Design components
- **SQLite JDBC 3.42.0.0**: Database driver
- **ControlsFX 11.2.0**: Additional UI controls
- **Ikonli 12.3.1**: Icon library

## Module Configuration (Java Platform Module System)

The application uses JPMS with the following module requirements:
- `javafx.controls` and `javafx.fxml`: JavaFX modules
- `java.sql`: Database access
- `MaterialFX`: UI components
- `org.controlsfx.controls`: Additional controls
- `org.kordamp.ikonli.*`: Icon libraries

## UI Features

- Material Design interface with custom color palette
- Real-time statistics display
- Date and time display
- Add new todos via text input
- Scrollable todo list
- Visual feedback for interactions

## Key Functionality

1. **Create Todo**: Enter text in the input field and press Enter or click ADD
2. **Toggle Completion**: Click on a todo item to mark as complete/incomplete
3. **Statistics**: View total, completed, and pending counts in the header
4. **Date Display**: Current date shown in multiple formats
5. **Persistent Storage**: Todos are automatically saved to SQLite database

## Color System

Custom color palette defined in `AppColors.java` with constants for:
- Background and surface colors
- Text colors (primary, secondary, disabled)
- Action colors (primary, secondary, danger)
- UI state colors (hover, selected, completed)
- Form element colors

## Troubleshooting

### Common Issues

1. **JavaFX not found**: Ensure JavaFX SDK is properly installed and module path is set
2. **Database errors**: Check write permissions in the working directory
3. **UI not loading**: Verify FXML file path and controller configuration
4. **Module errors**: Check `module-info.java` for correct requires/opens statements

### Logs
Check console output for SQL errors or initialization issues. The application logs database operations and errors to standard error.

## Development Notes

- The application uses a layered architecture (Model-Service-Repository)
- UI is separated from business logic
- Database operations are cached in memory for performance
- All UI styling is applied programmatically with CSS overrides
- MaterialFX components are customized via CSS classes and inline styles
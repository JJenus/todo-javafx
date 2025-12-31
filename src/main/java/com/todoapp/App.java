package com.todoapp;

import io.github.palexdev.materialfx.css.themes.MFXThemeManager;
import io.github.palexdev.materialfx.css.themes.Themes;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        // Load FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/todoapp/view/main.fxml"));
        Parent root = loader.load();

        // Create scene
        Scene scene = new Scene(root, 800, 700);

        // Apply MaterialFX theme
        MFXThemeManager.addOn(scene, Themes.DEFAULT, Themes.LEGACY);

        // Add our custom CSS (overrides MaterialFX defaults)
        scene.getStylesheets().add(Objects.requireNonNull(
                getClass().getResource("/com/todoapp/view/styles.css")
        ).toExternalForm());

        // Setup stage
        primaryStage.setTitle("Todo");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(600);
        primaryStage.setMinHeight(500);

        // Show stage
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

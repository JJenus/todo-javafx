module com.todoapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;

    // Third-party modules (check actual module names from their JARs)
    requires MaterialFX;  // MaterialFX
    requires org.controlsfx.controls;        // ControlsFX
    requires org.kordamp.ikonli.javafx;      // Ikonli core
    requires org.kordamp.ikonli.coreui;   // Material icons
    requires org.kordamp.ikonli.fontawesome5; // FontAwesome icons

    opens com.todoapp to javafx.fxml;
    opens com.todoapp.controller to javafx.fxml;
    opens com.todoapp.model to javafx.base;

    exports com.todoapp;
    exports com.todoapp.controller;
    exports com.todoapp.model;
    exports com.todoapp.service;
    exports com.todoapp.repository;
}
module com.todoapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires MaterialFX;
    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.material2;
    requires mfx.resources;
    requires javafx.graphics;
    requires org.kordamp.ikonli.fontawesome5;

    opens com.todoapp to javafx.fxml;
    opens com.todoapp.controller to javafx.fxml;
    opens com.todoapp.model to javafx.base;

    exports com.todoapp;
    exports com.todoapp.controller;
    exports com.todoapp.model;
    exports com.todoapp.service;
    exports com.todoapp.repository;
}

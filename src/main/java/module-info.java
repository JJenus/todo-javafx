module com.todoapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    opens com.todoapp to javafx.fxml;
    opens com.todoapp.controller to javafx.fxml;
    opens com.todoapp.model to javafx.base;
    exports com.todoapp;
    exports com.todoapp.controller;
    exports com.todoapp.model;
    exports com.todoapp.service;
    exports com.todoapp.repository;
}

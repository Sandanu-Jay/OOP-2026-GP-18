
package com.faculty.management;

import com.faculty.management.util.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        SceneManager.setPrimaryStage(primaryStage);
        SceneManager.switchScene("/fxml/login.fxml", "Faculty Management System - Login");
    }

    public static void main(String[] args) {
        launch(args);
    }
}

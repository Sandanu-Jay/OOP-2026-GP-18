/*
 * This utility class provides reusable helper logic.
 */
package com.faculty.management.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import java.io.IOException;


public class SceneManager {
    private static Stage primaryStage;

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }


    public static void switchScene(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));
            Parent root = loader.load();
            Scene scene = new Scene(root, 1150, 720);
            
            // Attach main CSS stylesheet
            String css = SceneManager.class.getResource("/css/styles.css") != null 
                ? SceneManager.class.getResource("/css/styles.css").toExternalForm() : null;
            if (css != null) {
                scene.getStylesheets().add(css);
            }

            primaryStage.setTitle(title);
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(1000);
            primaryStage.setMinHeight(650);
            primaryStage.centerOnScreen();
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtil.showError("Navigation Error", "Could not load view: " + fxmlPath + "\n" + e.getMessage());
        }
    }


    public static <T> T loadViewIntoPane(Pane targetPane, String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));
            Parent view = loader.load();
            targetPane.getChildren().setAll(view);
            return loader.getController();
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtil.showError("View Error", "Could not load sub-view: " + fxmlPath + "\n" + e.getMessage());
            return null;
        }
    }
}

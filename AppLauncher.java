package com.faculty.management;

/**
 * Standard Entry Point Launcher for JavaFX Applications.
 *
 * Why this class exists:
 * Since Java 11, JavaFX is not bundled with the JDK. If you directly execute a class
 * that extends 'javafx.application.Application' without module path arguments, Java throws:
 * "Error: JavaFX runtime components are missing, and are required to run this application".
 *
 * By having AppLauncher (which does NOT extend Application) call Main.main(args),
 * the application can be run directly from any IDE (IntelliJ IDEA, VS Code, Eclipse)
 * or terminal with a single click without needing extra VM arguments!
 */
public class AppLauncher {
    public static void main(String[] args) {
        Main.main(args);
    }
}

# Evaluation-Friendly Update

This version keeps the existing application logic and JavaFX screens intact while making the source easier to study.

## Changes
- Added `CLASS_BY_CLASS_EXPLANATION.md`.
- Added a short EASY EVALUATION NOTE to each main Java source class.
- Documented the role of Model, DAO, Controller, Utility and Database classes.
- Added simple viva answers for Classes/Objects, Encapsulation, Inheritance, Abstraction, Polymorphism, Exception Handling, JDBC and JavaFX.
- Did not replace the existing business logic with a different architecture.

## Recommended explanation flow
LoginController -> UserDAO -> DatabaseConnection -> MySQL -> User object -> Dashboard Controller.

## Important
The project should still be tested in the user's local Java/Maven/MySQL environment because this environment does not have Maven installed, so a full Maven build could not be executed here.

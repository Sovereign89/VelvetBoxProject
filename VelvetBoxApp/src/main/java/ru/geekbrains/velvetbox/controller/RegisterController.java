package ru.geekbrains.velvetbox.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.stage.Stage;
import ru.geekbrains.velvetbox.global.Database;
import ru.geekbrains.velvetbox.global.RegisterResult;
import ru.geekbrains.velvetbox.global.User;

import java.sql.SQLException;

public class RegisterController {

    public boolean isRegistered = false;
    public User currentUser = null;

    @FXML public TextField NameField;
    @FXML public TextField LoginField;
    @FXML public PasswordField PasswordField;
    @FXML public Button RegisterButton;
    @FXML public Button CancelButton;

    public void UsernameOnKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode().equals(KeyCode.ENTER)) {
            LoginField.requestFocus();
        }
    }

    public void LoginOnKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode().equals(KeyCode.ENTER)) {
            PasswordField.requestFocus();
        }
    }

    public void PasswordOnKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode().equals(KeyCode.ENTER)) {
            RegisterButton.requestFocus();
        }
    }

    public void registerUser() {
        try {
            Database database = new Database();
            User user = new User(NameField.getText(), LoginField.getText(), PasswordField.getText());
            RegisterResult registerResult = database.register(user);
            if (registerResult.getResult() && registerResult.getResultCode() == 1) {
                isRegistered = true;
                currentUser = user;
                Stage currentStage = (Stage) CancelButton.getScene().getWindow();
                currentStage.close();
            } else if (!registerResult.getResult() || registerResult.getResultCode() != 1) {
                Platform.runLater(new Runnable() {
                    public void run() {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Результат");
                        alert.setHeaderText("Ошибка регистрации");
                        alert.setContentText(registerResult.getResultMsg());
                        alert.initOwner(CancelButton.getScene().getWindow());
                        alert.showAndWait();
                    }
                });
            }
        } catch (Exception e) {
            Platform.runLater(new Runnable() {
                public void run() {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Результат");
                    alert.setHeaderText("Ошибка регистрации");
                    alert.setContentText(e.getMessage());
                    alert.initOwner(CancelButton.getScene().getWindow());
                    alert.showAndWait();
                }
            });
        }
    }

    public void RegisterButtonAction(ActionEvent actionEvent) throws SQLException {
        registerUser();
    }

    public void CancelButtonAction(ActionEvent actionEvent) {
        Stage currentStage = (Stage) CancelButton.getScene().getWindow();
        currentStage.close();
    }

    public void RegisterOnKeyPressed(KeyEvent keyEvent) {
        registerUser();
    }

    public void CancelOnKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode().equals(KeyCode.ENTER)) {
            Stage currentStage = (Stage) CancelButton.getScene().getWindow();
            currentStage.close();
        }
    }
}

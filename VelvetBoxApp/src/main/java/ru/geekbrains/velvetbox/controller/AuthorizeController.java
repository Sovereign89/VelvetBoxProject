package ru.geekbrains.velvetbox.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import ru.geekbrains.velvetbox.VelvetBox;
import ru.geekbrains.velvetbox.global.AuthorizationResult;
import ru.geekbrains.velvetbox.global.Database;
import ru.geekbrains.velvetbox.global.User;

public class AuthorizeController {
    @FXML public TextField LoginField;
    @FXML public PasswordField PasswordField;
    @FXML public Button AuthButton;
    @FXML public Button CancelButton;

    private boolean isFirst = false;

    private User authUser;

    public void setFirst(boolean first) {
        isFirst = first;
    }
    public User getAuthUser() {
        return authUser;
    }

    public void LoginFieldOnKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode().equals(KeyCode.ENTER)) {
            PasswordField.requestFocus();
        }
    }

    public void PasswordFieldOnKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode().equals(KeyCode.ENTER)) {
            AuthButton.requestFocus();
        }
    }

    private void cancelAction() {
        Stage currentStage = (Stage) CancelButton.getScene().getWindow();
        try {
            if (isFirst) {
                FXMLLoader fxmlLoader = new FXMLLoader(VelvetBox.class.getResource("velvet_box.fxml"));
                Stage stage = new Stage();
                stage.getIcons().add(new Image(VelvetBox.class.getResourceAsStream("icons/velvetbox.png")));
                stage.setTitle("VelvetBox");
                stage.setScene(new Scene(fxmlLoader.load(), 800, 600));
                stage.show();
            }
            currentStage.close();
        } catch (Exception e) {
            if (isFirst) {
                Platform.exit();
            } else {
                e.printStackTrace();
                currentStage.close();
            }
        }
    }

    private void authUser() {
        try {
            Stage currentStage = (Stage) CancelButton.getScene().getWindow();
            Database database = new Database();
            User user = new User(LoginField.getText(),PasswordField.getText());
            AuthorizationResult authorizationResult = database.authorization(user);
            if (authorizationResult.getResult() && authorizationResult.getResultCode() == 1) {
                authUser = authorizationResult.getUser();
                if (isFirst) {
                    FXMLLoader fxmlLoader = new FXMLLoader(VelvetBox.class.getResource("velvet_box.fxml"));
                    Stage stage = new Stage();
                    stage.getIcons().add(new Image(VelvetBox.class.getResourceAsStream("icons/velvetbox.png")));
                    stage.setTitle("VelvetBox");
                    stage.setScene(new Scene(fxmlLoader.load(), 800, 600));
                    VelvetBoxController velvetBoxController = fxmlLoader.getController();
                    velvetBoxController.setCurrentUser(authorizationResult.getUser());
                    velvetBoxController.init(authorizationResult.getUser());
                    stage.show();
                }
                currentStage.close();
            } else if (authorizationResult.getResultCode() != 1) {
                Platform.runLater(new Runnable() {
                    public void run() {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Результат");
                        alert.setHeaderText("Ошибка авторизации");
                        alert.setContentText("Не удалось авторизоваться в системе!");
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

    public void AuthButtonAction(ActionEvent actionEvent) {
        authUser();
    }

    public void AuthButtonOnKeyPressed(KeyEvent keyEvent) {
        authUser();
    }

    public void CancelButtonAction(ActionEvent actionEvent) {
        cancelAction();
    }

    public void CancelButtonOnKeyPressed(KeyEvent keyEvent) {
        cancelAction();
    }
}

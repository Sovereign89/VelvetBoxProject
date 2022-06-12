package ru.geekbrains.velvetbox.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class ConfirmController {

    public boolean isConfirmed = false;
    @FXML
    public Button YesButton;
    @FXML
    public Button NoButton;

    public void YesButtonAction(ActionEvent actionEvent) {
        isConfirmed = true;
        Stage currentStage = (Stage) NoButton.getScene().getWindow();
        currentStage.close();
    }

    public void NoButtonAction(ActionEvent actionEvent) {
        isConfirmed = false;
        Stage currentStage = (Stage) NoButton.getScene().getWindow();
        currentStage.close();
    }
}

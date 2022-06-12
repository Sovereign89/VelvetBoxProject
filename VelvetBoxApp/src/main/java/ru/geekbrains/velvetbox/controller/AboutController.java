package ru.geekbrains.velvetbox.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class AboutController {

    @FXML
    Button CloseButton;

    public void CloseButtonOnClick(ActionEvent actionEvent) {
        Stage currentStage = (Stage) CloseButton.getScene().getWindow();
        currentStage.close();
    }
}

package ru.geekbrains.velvetbox.controller;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.extern.slf4j.Slf4j;
import ru.geekbrains.velvetbox.VelvetBox;
import ru.geekbrains.velvetbox.global.*;
import ru.geekbrains.velvetbox.network.Network;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
@Slf4j
public class VelvetBoxController implements Initializable {

    private static final String CLASS_NAME_VARIABLE = "[" + VelvetBoxController.class.getName() + "]";
    private final String AUTH_LABEL_TEXT = "Пользователь: ";
    @FXML
    MenuItem MenuRefreshButton;
    @FXML
    MenuItem MenuAuthButton;
    @FXML
    Label AuthLabel;
    @FXML
    MenuItem MenuRegisterButton;
    @FXML
    SplitPane SplitPaneFiles;
    @FXML
    Label ClientFilesLabel;
    @FXML
    Label ServerFilesLabel;
    @FXML
    MenuItem MenuExitButton;
    @FXML
    MenuItem MenuAboutButton;
    @FXML
    ListView<String> ClientFiles;
    @FXML
    ListView<String> ServerFiles;

    private String usersDirectory = "Users";
    private String homeDirectory;
    private String rootDirectory;
    private Network network;
    private User currentUser = null;

    public void init(User user) {
        try {
            AuthLabel.setText(AUTH_LABEL_TEXT+user.getName());
            homeDirectory = usersDirectory+"\\"+user.getLogin();
            rootDirectory = homeDirectory;
            File clientDir = new File(String.valueOf(homeDirectory));
            if (!clientDir.exists()){
                clientDir.mkdir();
            }
            SplitPaneFiles.setVisible(true);
            ClientFiles.getItems().clear();
            ClientFiles.getItems().addAll(getFiles(homeDirectory,homeDirectory.equals(rootDirectory)));
            network = new Network(8189);
            Thread readThread = new Thread(this::readLoop);
            readThread.setDaemon(true);
            readThread.start();
            network.write(user);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void dragAndDropClientServer() {
        dragAndDrop(ClientFiles, ServerFiles);
    }

    private void dragAndDropServerClient() {
        dragAndDrop(ServerFiles, ClientFiles);
    }

    private void dragAndDrop(ListView<String> listView1, ListView<String> listView2) {
        listView1.setOnDragDetected(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                Dragboard db = listView1.startDragAndDrop(TransferMode.ANY);

                ClipboardContent content = new ClipboardContent();
                content.putString(listView1.getSelectionModel().getSelectedItem());
                db.setContent(content);

                event.consume();
            }
        });

        listView2.setOnDragOver(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {
                if (event.getGestureSource() != listView2) {
                    event.acceptTransferModes(TransferMode.ANY);
                }

                event.consume();
            }
        });

        listView2.setOnDragDropped(new EventHandler() {
            @Override
            public void handle(Event event) {
                if (confirmDialogOpen()) {
                    DragEvent dragEvent = (DragEvent) event;
                    Dragboard db = dragEvent.getDragboard();
                    boolean success = false;
                    if (db.hasString()) {
                        if (listView1.equals(ServerFiles)) {
                            transferFrom(ClientFiles.getSelectionModel().getSelectedItem());
                        } else {
                            transferTo(ClientFiles.getSelectionModel().getSelectedItem());
                        }
                        success = true;
                    }
                    dragEvent.setDropCompleted(success);
                    dragEvent.consume();
                }
            }
        });
    }

    private void transferTo(String file) {
        try {
            network.write(new FileMessage(Path.of(homeDirectory).resolve(file)));
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    private void transferFrom(String file) {
        try {
            network.write(new FileRequest(false, file));
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public boolean confirmDialogOpen() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(VelvetBox.class.getResource("confirm_dialog.fxml"));
            Parent root = (Parent) fxmlLoader.load();
            ConfirmController confirmController = fxmlLoader.getController();
            Stage stage = new Stage();
            Scene scene = new Scene(root,320,125);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.getIcons().add(new Image(VelvetBox.class.getResourceAsStream("icons/velvetbox.png")));
            stage.setResizable(false);
            stage.setTitle("Подтверждение переноса файла");
            stage.setAlwaysOnTop(true);
            stage.showAndWait();
            return confirmController.isConfirmed;
        } catch(Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void isClickedOnClient() {
        isClicked(ClientFiles, false);
    }

    private void isClickedOnServer() {
        isClicked(ServerFiles, true);
    }

    private void isClicked(ListView<String> listView, boolean isServer) {
        listView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
                    if(mouseEvent.getClickCount() == 2){
                        String fileName = listView.getSelectionModel().getSelectedItem();
                        if(fileName.equals("..")) {
                            listView.getItems().clear();
                            if(isServer) {
                                try {
                                    network.write(new FileRequest(true,".."));
                                    return;
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            char[] arr = homeDirectory.toCharArray();
                            for (int i = arr.length - 1; i >= 0; i--) {
                                if(arr[i] == '\\') {
                                    arr[i] = '!';
                                    break;
                                }

                                arr[i] = '_';
                            }

                            homeDirectory = String.valueOf(arr).split("!")[0];
                            listView.getItems().addAll(getFiles(homeDirectory, rootDirectory.equals(homeDirectory)));
                            return;
                        }
                        if(isServer) {
                            try {
                                FileRequest fileRequest = new FileRequest(true, fileName);
                                network.write(fileRequest);
                                return;
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        File dir = new File(homeDirectory);
                        File[] arrFiles = dir.listFiles();
                        assert arrFiles != null;
                        Optional<File> lst = Arrays.stream(arrFiles)
                                .filter(x -> x.getName().equals(fileName)).findFirst();
                        if(lst.isPresent() && lst.get().isDirectory()) {
                            listView.getItems().clear();
                            homeDirectory = String.valueOf(Path.of(homeDirectory).resolve(lst.get().getName()));
                            listView.getItems().addAll(getFiles(homeDirectory, homeDirectory.equals(rootDirectory)));

                        }
                    }
                }
            }
        });
    }

    private void readLoop() {
        try {
            while (true) {
                dragAndDropClientServer();
                dragAndDropServerClient();
                isClickedOnClient();
                isClickedOnServer();
                GlobalMessagingService message = network.read();
                if (message instanceof ListFiles listFiles) {
                    Platform.runLater(() -> {
                        ServerFiles.getItems().clear();
                        ServerFiles.getItems().addAll(listFiles.getFiles());
                        ServerFiles.refresh();
                    });
                } else if (message instanceof FileMessage fileMessage) {
                    Path current = Path.of(homeDirectory).resolve(fileMessage.getName());
                    Files.write(current, fileMessage.getData());
                    Platform.runLater(() -> {
                        ClientFiles.getItems().clear();
                        ClientFiles.getItems().addAll(getFiles(homeDirectory, homeDirectory.equals(rootDirectory)));
                        ClientFiles.refresh();
                    });
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            log.error(e.getMessage(), e);
        }
    }

    private List<String> getFiles(String dir, boolean isRootDir) {
        String[] list;
        if(!isRootDir) {
            String[] fileList = new File(dir).list();
            assert fileList != null;
            list = new String[fileList.length + 1];
            list[0] = "..";
            for (int i = 1, j = 0; i < list.length; i++, j++) {
                list[i] = fileList[j];
            }
            return Arrays.asList(list);
        }
        list = new File(dir).list();
        assert list != null;
        return Arrays.asList(list);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            File clientDir = new File(String.valueOf(usersDirectory));
            if (!clientDir.exists()){
                clientDir.mkdir();
            }
            SplitPaneFiles.setVisible(false);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @FXML
    private void MenuExitButtonOnClick(ActionEvent actionEvent) {
        Platform.exit();
    }

    @FXML
    private void MenuAboutButtonOnClick(ActionEvent actionEvent) throws Exception {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(VelvetBox.class.getResource("about.fxml"));
            Parent root = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
            Scene scene = new Scene(root,320,240);
            stage.setScene(scene);
            scene.addEventFilter(KeyEvent.KEY_PRESSED, evt -> {
                if (evt.getCode().equals(KeyCode.ESCAPE)) {
                    stage.close();
                }
            });
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.getIcons().add(new Image(VelvetBox.class.getResourceAsStream("icons/velvetbox.png")));
            stage.setResizable(false);
            stage.setTitle("О программе");
            stage.setAlwaysOnTop(true);
            stage.show();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void MenuRegisterButtonOnClick(ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(VelvetBox.class.getResource("register.fxml"));
            Parent root = (Parent) fxmlLoader.load();
            RegisterController registerController = fxmlLoader.getController();
            Stage stage = new Stage();
            Scene scene = new Scene(root,330,160);
            stage.setScene(scene);
            scene.addEventFilter(KeyEvent.KEY_PRESSED, evt -> {
                if (evt.getCode().equals(KeyCode.ESCAPE)) {
                    stage.close();
                }
            });
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.getIcons().add(new Image(VelvetBox.class.getResourceAsStream("icons/velvetbox.png")));
            stage.setResizable(false);
            stage.setTitle("Зарегистрироваться");
            stage.setAlwaysOnTop(true);
            stage.showAndWait();
            if (registerController.isRegistered) {
                currentUser = registerController.currentUser;
                init(currentUser);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public void MenuAuthButtonOnClick(ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(VelvetBox.class.getResource("authorize.fxml"));
            Stage stage = new Stage();
            stage.getIcons().add(new Image(VelvetBox.class.getResourceAsStream("icons/velvetbox.png")));
            stage.setTitle("VelvetBox");
            stage.initStyle(StageStyle.UTILITY);
            stage.setAlwaysOnTop(true);
            stage.setScene(new Scene(fxmlLoader.load(), 250, 110));
            stage.showAndWait();
            AuthorizeController authorizeController = fxmlLoader.getController();
            setCurrentUser(authorizeController.getAuthUser());
            init(authorizeController.getAuthUser());
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void MenuRefreshButtonOnClick(ActionEvent actionEvent) {

    }
}
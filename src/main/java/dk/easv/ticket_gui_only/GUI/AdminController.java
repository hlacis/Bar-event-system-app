package dk.easv.ticket_gui_only.GUI;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.ToggleButton;
import javafx.fxml.FXML;



public class AdminController {

    @FXML private ToggleButton usersBtn;
    @FXML private ToggleButton assignBtn;
    @FXML private ToggleButton eventsBtn;
    @FXML private ToggleButton settingsBtn;
    @FXML
    private StackPane contentHost;

    @FXML
    public void initialize() {
        usersBtn.setSelected(true);
        loadIntoContent("/dk/easv/ticket_gui_only/UsersView.fxml");
    }


    public void showUsers() {
        usersBtn.setSelected(true);
        loadIntoContent("/dk/easv/ticket_gui_only/UsersView.fxml");
    }

    public void showAssign() {
        assignBtn.setSelected(true);
        loadIntoContent("/dk/easv/ticket_gui_only/AssignCoordinatorsView.fxml");
    }

    public void showEvents() {
        eventsBtn.setSelected(true);
        loadIntoContent("/dk/easv/ticket_gui_only/EventsView.fxml");
    }

    public void showSettings() {
        settingsBtn.setSelected(true);
        loadIntoContent("/dk/easv/ticket_gui_only/SettingsView.fxml");
    }


    private void loadIntoContent(String fxmlPath) {
        try {
            Parent view = FXMLLoader.load(getClass().getResource(fxmlPath));
            contentHost.getChildren().setAll(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void logout(ActionEvent event) throws Exception {
        Parent loginRoot = FXMLLoader.load(
                getClass().getResource("/dk/easv/ticket_gui_only/LoginView.fxml")
        );

        Scene scene = ((Node) event.getSource()).getScene();
        scene.setRoot(loginRoot);

        Stage stage = (Stage) scene.getWindow();
        stage.sizeToScene();      // shrink to login layout
        stage.centerOnScreen();
        stage.setResizable(false);
    }


}

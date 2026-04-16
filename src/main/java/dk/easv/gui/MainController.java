package dk.easv.gui;

import dk.easv.be.CurrentUser;
import dk.easv.be.Users;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.ToggleButton;


public class MainController {

    @FXML private ToggleButton usersBtn;
    @FXML private ToggleButton assignBtn;
    @FXML private ToggleButton eventsBtn;
    @FXML
    private StackPane contentHost;

    @FXML
    private Label userNameLabel;

    @FXML
    private Label userRoleLabel;

    @FXML
    private Label panelTitleLabel;

    @FXML
    public void initialize() {
        Users user = CurrentUser.getUser();

        if (user != null) {
            userNameLabel.setText(user.getName());
            userRoleLabel.setText("Role: " + user.getRole());
            if (CurrentUser.isAdmin()) {
                panelTitleLabel.setText("EASV Tickets — Admin Panel");
            } else if (CurrentUser.isCoordinator()) {
                panelTitleLabel.setText("EASV Tickets — E. Coordinator Panel");
            } else {
                panelTitleLabel.setText("EASV Tickets");
            }
        }

        boolean isAdmin = CurrentUser.isAdmin();
        boolean isCoordinator = CurrentUser.isCoordinator();

        usersBtn.setVisible(isAdmin);
        usersBtn.setManaged(isAdmin);

        assignBtn.setVisible(isAdmin || isCoordinator);
        assignBtn.setManaged(isAdmin || isCoordinator);

        eventsBtn.setVisible(true);
        eventsBtn.setManaged(true);

        if (isAdmin) {
            usersBtn.setSelected(true);
            loadIntoContent("/dk/easv/gui/CoordinatorManagement.fxml");
        } else {
            eventsBtn.setSelected(true);
            loadIntoContent("/dk/easv/gui/EventsView.fxml");
        }

    }


    public void showUsers() {
        usersBtn.setSelected(true);
        loadIntoContent("/dk/easv/gui/CoordinatorManagement.fxml");
    }

    public void showAssign() {
        assignBtn.setSelected(true);
        loadIntoContent("/dk/easv/gui/AssignCoordinatorsView.fxml");
    }

    public void showEvents() {
        eventsBtn.setSelected(true);
        loadIntoContent("/dk/easv/gui/EventsView.fxml");
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
        CurrentUser.clear();

        Parent loginRoot = FXMLLoader.load(
                getClass().getResource("/dk/easv/gui/LoginView.fxml")
        );

        Scene scene = ((Node) event.getSource()).getScene();
        scene.setRoot(loginRoot);

        Stage stage = (Stage) scene.getWindow();
        stage.sizeToScene();
        stage.centerOnScreen();
        stage.setResizable(false);
    }


}

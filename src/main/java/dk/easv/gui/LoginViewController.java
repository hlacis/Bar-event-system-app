package dk.easv.gui;

import dk.easv.be.CurrentUser;
import dk.easv.be.Users;
import dk.easv.bll.UserManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginViewController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    private final UserManager userManager = new UserManager();

    @FXML
    public void onLoginClicked(ActionEvent event) {
        String username = usernameField.getText() == null ? "" : usernameField.getText().trim();
        String password = passwordField.getText() == null ? "" : passwordField.getText().trim();

        try {
            Users loggedInUser = userManager.login(username, password);
            System.out.println("loggedInUser = " + loggedInUser);

            if (loggedInUser == null) {
                showError("Invalid username or password.");
                return;
            }

            CurrentUser.setUser(loggedInUser);

            Parent mainRoot = FXMLLoader.load(
                    getClass().getResource("/dk/easv/gui/MainView.fxml")
            );

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = stage.getScene();
            scene.setRoot(mainRoot);

            stage.setWidth(1280);
            stage.setHeight(780);
            stage.centerOnScreen();

        } catch (Exception e) {
            e.printStackTrace();
            showError(e.getMessage() == null ? "Login failed." : e.getMessage());
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Login Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
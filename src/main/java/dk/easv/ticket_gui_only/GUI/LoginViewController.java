package dk.easv.ticket_gui_only.GUI;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class LoginViewController {

    public void onLoginClicked(ActionEvent event) throws Exception {

        Parent adminRoot = FXMLLoader.load(
                getClass().getResource("/dk/easv/ticket_gui_only/AdminView.fxml")
        );

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        Scene scene = stage.getScene();     // reuse existing scene (keeps CSS)
        scene.setRoot(adminRoot);

        // admin window size
        stage.setWidth(1280);
        stage.setHeight(780);
        stage.centerOnScreen();
    }

}

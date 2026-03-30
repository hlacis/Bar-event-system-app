package dk.easv.ticket_gui_only;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("/dk/easv/ticket_gui_only/LoginView.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root);

        String css = MainApplication.class.getResource("/dk/easv/ticket_gui_only/app.css").toExternalForm();
        scene.getStylesheets().add(css);

        stage.setTitle("EASV Ticket GUI");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

}

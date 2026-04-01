package dk.easv;

import dk.easv.be.Event;
import dk.easv.dal.ConnectionManager;
import dk.easv.dal.dao.EventDAO;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.Connection;
import java.time.LocalDateTime;

public class MainApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        /*try {
            Connection conn = new ConnectionManager().getConnection();
            System.out.println("Connected!");
        } catch (Exception e) {
            e.printStackTrace();
        }
        */

        /*
        try {
            EventDAO dao = new EventDAO();

            Event event = new Event(
                    0,
                    "Test Event",
                    "Esbjerg",
                    LocalDateTime.now(),
                    null,
                    "Test note"
            );

            dao.createEvent(event);

            System.out.println("Event created!");
        } catch (Exception e) {
            e.printStackTrace();
        }

         */

        FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("/dk/easv/ticket_gui_only/LoginView.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root);

        String css = MainApplication.class.getResource("/dk/easv/ticket_gui_only/app.css").toExternalForm();
        scene.getStylesheets().add(css);

        stage.setTitle("EASV Ticket gui");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

}

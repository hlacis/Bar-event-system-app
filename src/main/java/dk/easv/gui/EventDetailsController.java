package dk.easv.gui;

import dk.easv.be.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class EventDetailsController {

    @FXML private Label nameLabel;
    @FXML private Label locationLabel;
    @FXML private Label timeLabel;
    @FXML private Label notesLabel;

    private Event event;

    public void setEvent(Event event) {
        this.event = event;

        nameLabel.setText(event.getName());
        locationLabel.setText(event.getLocation());
        notesLabel.setText(event.getNotes());

        if (event.getStartTime() != null) {
            timeLabel.setText(event.getStartTime().toString());
        }
    }
}
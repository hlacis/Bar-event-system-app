package dk.easv.gui;
import dk.easv.be.Event;
import dk.easv.bll.EventManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class EventCreatorController {

    private Event eventToEdit;

    @FXML
    private Label titleLabel;

    @FXML
    private Button saveButton;

    @FXML
    private TextField nameField;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private TextField locationField;

    @FXML
    private TextField notesField;

    private final EventManager eventManager = new EventManager();

    @FXML
    private void saveEvent() {
        try {
            String name = nameField.getText().trim();
            String location = locationField.getText().trim();
            String notes = notesField.getText().trim();

            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();

            LocalDateTime startTime = startDate != null ? startDate.atStartOfDay() : null;
            LocalDateTime endTime = endDate != null ? endDate.atTime(23, 59) : null;

            if (eventToEdit == null) {
                Event event = new Event(0, name, location, startTime, endTime, notes);
                eventManager.createEvent(event);
                showInfo("Success", "Event created successfully.");
            } else {
                eventToEdit.setName(name);
                eventToEdit.setLocation(location);
                eventToEdit.setStartTime(startTime);
                eventToEdit.setEndTime(endTime);
                eventToEdit.setNotes(notes);

                eventManager.updateEvent(eventToEdit);
                showInfo("Success", "Event updated successfully.");
            }

            loadEventsView();

        } catch (Exception e) {
            e.printStackTrace();
            showError(e.getMessage());
        }
    }

    @FXML
    private void cancel() {
        loadEventsView();
    }

    private void loadEventsView() {
        try {
            Parent eventsView = FXMLLoader.load(
                    getClass().getResource("/dk/easv/gui/EventsView.fxml")
            );

            StackPane contentHost = findContentHost(nameField);
            if (contentHost != null) {
                contentHost.getChildren().setAll(eventsView);
            }

        } catch (Exception e) {
            e.printStackTrace();
            showError("Could not return to Events view.");
        }
    }

    private StackPane findContentHost(Node node) {
        Node current = node;
        while (current != null) {
            if (current instanceof StackPane stackPane) {
                if ("contentHost".equals(stackPane.getId()) ||
                        stackPane.getStyleClass().contains("content-host")) {
                    return stackPane;
                }
            }
            current = current.getParent();
        }
        return null;
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Validation Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void setEvent(Event event) {
        this.eventToEdit = event;

        titleLabel.setText("Edit Event");
        saveButton.setText("Update Event");

        nameField.setText(event.getName());
        locationField.setText(event.getLocation());
        notesField.setText(event.getNotes());

        if (event.getStartTime() != null) {
            startDatePicker.setValue(event.getStartTime().toLocalDate());
        }

        if (event.getEndTime() != null) {
            endDatePicker.setValue(event.getEndTime().toLocalDate());
        }
    }
}
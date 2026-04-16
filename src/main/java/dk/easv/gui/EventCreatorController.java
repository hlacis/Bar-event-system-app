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
import java.time.LocalTime;
import javafx.scene.control.ComboBox;
import dk.easv.be.CurrentUser;
import dk.easv.be.Users;
import java.util.List;

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

    // Time selectors for start
    @FXML
    private ComboBox<Integer> startHourBox;
    @FXML
    private ComboBox<Integer> startMinuteBox;

    // Time selectors for end
    @FXML
    private ComboBox<Integer> endHourBox;
    @FXML
    private ComboBox<Integer> endMinuteBox;

    private final EventManager eventManager = new EventManager();

    @FXML
    private void saveEvent() {
        try {
            String name = nameField.getText().trim();
            String location = locationField.getText().trim();
            String notes = notesField.getText().trim();

            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();

            Integer startHour = startHourBox.getValue();
            Integer startMinute = startMinuteBox.getValue();

            Integer endHour = endHourBox.getValue();
            Integer endMinute = endMinuteBox.getValue();

            // Validation
            if (startDate == null || startHour == null || startMinute == null) {
                showError("Please select start date and time");
                return;
            }

            if (endDate == null || endHour == null || endMinute == null) {
                showError("Please select end date and time");
                return;
            }

            // Combine date + time
            LocalDateTime startTime = LocalDateTime.of(
                    startDate,
                    LocalTime.of(startHour, startMinute)
            );

            LocalDateTime endTime = LocalDateTime.of(
                    endDate,
                    LocalTime.of(endHour, endMinute)
            );

            // Make sure end time is after start time
            if (endTime.isBefore(startTime)) {
                showError("End time cannot be before start time");
                return;
            }

            if (eventToEdit == null) {
                Event event = new Event(0, name, location, startTime, endTime, notes);
                eventManager.createEvent(event);

                if (CurrentUser.isCoordinator()) {
                    Users currentUser = CurrentUser.getUser();
                    if (currentUser != null) {
                        eventManager.assignUserToEvent(currentUser.getId(), event.getId());
                    }
                }

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
            if (eventToEdit == null) {
                loadEventsView();
            } else {
                loadEventDetailsView(eventToEdit);
            }

        } catch (Exception e) {
            showError("Something went wrong: " + e.getMessage());
        }
    }

    @FXML
    public void initialize() {

        // Populate time selection dropdowns for event creation/editing

        // Add hours (0–23)
        for (int i = 0; i < 24; i++) {
            startHourBox.getItems().add(i);
            endHourBox.getItems().add(i);
        }
        // Format hours to 2 digits
        startHourBox.setCellFactory(_ -> new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : String.format("%02d", item));
            }
        });

        startHourBox.setButtonCell(startHourBox.getCellFactory().call(null));

        endHourBox.setCellFactory(startHourBox.getCellFactory());
        endHourBox.setButtonCell(endHourBox.getCellFactory().call(null));

        // Add minutes
        for (int i = 0; i < 60; i += 15) {
            startMinuteBox.getItems().add(i);
            endMinuteBox.getItems().add(i);
        }
        // Format numbers to always show 2 digits
        startMinuteBox.setCellFactory(_ -> new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : String.format("%02d", item));
            }
        });

        startMinuteBox.setButtonCell(startMinuteBox.getCellFactory().call(null));

        endMinuteBox.setCellFactory(startMinuteBox.getCellFactory());
        endMinuteBox.setButtonCell(endMinuteBox.getCellFactory().call(null));
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
    private void loadEventDetailsView(Event event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/dk/easv/gui/EventDetailsView.fxml")
            );

            Parent view = loader.load();

            EventDetailsController controller = loader.getController();
            controller.setEvent(event);

            StackPane contentHost = findContentHost(nameField);
            if (contentHost != null) {
                contentHost.getChildren().setAll(view);
            }

        } catch (Exception e) {
            e.printStackTrace();
            showError("Could not return to event details.");
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
            startHourBox.setValue(event.getStartTime().getHour());

            int minute = event.getStartTime().getMinute();
            minute = (minute / 15) * 15;

            startMinuteBox.setValue(minute);
        }

        if (event.getEndTime() != null) {
            endDatePicker.setValue(event.getEndTime().toLocalDate());
            endHourBox.setValue(event.getEndTime().getHour());

            int minute = event.getEndTime().getMinute();
            minute = (minute / 15) * 15;

            endMinuteBox.setValue(minute);
        }
    }
}
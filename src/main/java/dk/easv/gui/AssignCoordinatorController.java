package dk.easv.gui;

import dk.easv.be.Event;
import dk.easv.be.Users;
import dk.easv.bll.EventManager;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssignCoordinatorController {

    @FXML
    private VBox cardContainer;

    @FXML
    private VBox coordinatorCheckboxContainer;

    @FXML
    private Label rightTitleLabel;

    @FXML
    private Label selectedEventLabel;

    private final EventManager eventManager = new EventManager();

    private final Map<Integer, CheckBox> coordinatorCheckBoxMap = new HashMap<>();
    private final List<HBox> eventCards = new ArrayList<>();

    private Event selectedEvent;

    @FXML
    public void initialize() {
        showLoadingState();
        loadDataAsync();
    }

    private void showLoadingState() {
        cardContainer.getChildren().clear();
        coordinatorCheckboxContainer.getChildren().clear();

        Label loadingEvents = new Label("Loading events...");
        loadingEvents.getStyleClass().add("muted");

        Label loadingCoordinators = new Label("Loading coordinators...");
        loadingCoordinators.getStyleClass().add("muted");

        cardContainer.getChildren().add(loadingEvents);
        coordinatorCheckboxContainer.getChildren().add(loadingCoordinators);

        rightTitleLabel.setText("Select Coordinators");
        selectedEventLabel.setText("Loading data...");
    }

    private void loadDataAsync() {
        Task<AssignDataBundle> loadTask = new Task<>() {
            @Override
            protected AssignDataBundle call() throws Exception {
                List<Event> events = eventManager.getAllEvents();
                List<Users> coordinators = eventManager.getAllCoordinators();
                return new AssignDataBundle(events, coordinators);
            }
        };

        loadTask.setOnSucceeded(event -> {
            AssignDataBundle data = loadTask.getValue();
            loadEvents(data.events());
            loadCoordinatorCheckboxes(data.coordinators());
            clearSelection();
        });

        loadTask.setOnFailed(event -> {
            loadTask.getException().printStackTrace();
            showError("Could not load assign coordinator data.");
            selectedEventLabel.setText("Could not load data.");
        });

        Thread thread = new Thread(loadTask);
        thread.setDaemon(true);
        thread.start();
    }

    private void loadEvents(List<Event> events) {
        cardContainer.getChildren().clear();
        eventCards.clear();

        for (Event event : events) {
            HBox card = createEventCard(event);
            eventCards.add(card);
            cardContainer.getChildren().add(card);
        }

        if (events.isEmpty()) {
            Label emptyLabel = new Label("No events found.");
            emptyLabel.getStyleClass().add("muted");
            cardContainer.getChildren().add(emptyLabel);
        }
    }

    private HBox createEventCard(Event event) {
        HBox card = new HBox(14);
        card.getStyleClass().add("coordinator-card");
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPickOnBounds(true);
        card.setStyle("-fx-cursor: hand;");

        VBox left = new VBox(4);
        HBox.setHgrow(left, Priority.ALWAYS);

        Label title = new Label(event.getName());
        title.getStyleClass().add("card-title");

        Label details = new Label(formatEventDetails(event));
        details.getStyleClass().add("card-subtext");

        left.getChildren().addAll(title, details);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        card.getChildren().addAll(left, spacer);

        card.setOnMouseClicked(e -> selectEvent(event, card));

        return card;
    }

    private String formatEventDetails(Event event) {
        if (event.getStartTime() == null) {
            return event.getLocation();
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd 'at' hh:mm a");
        return event.getStartTime().format(formatter) + " • " + event.getLocation();
    }

    private void loadCoordinatorCheckboxes(List<Users> coordinators) {
        coordinatorCheckboxContainer.getChildren().clear();
        coordinatorCheckBoxMap.clear();

        for (Users coordinator : coordinators) {
            CheckBox checkBox = new CheckBox(coordinator.getName());
            checkBox.getStyleClass().add("muted");
            coordinatorCheckBoxMap.put(coordinator.getId(), checkBox);
            coordinatorCheckboxContainer.getChildren().add(checkBox);
        }

        if (coordinators.isEmpty()) {
            Label emptyLabel = new Label("No coordinators found.");
            emptyLabel.getStyleClass().add("muted");
            coordinatorCheckboxContainer.getChildren().add(emptyLabel);
        }
    }

    private void selectEvent(Event event, HBox selectedCard) {
        try {
            selectedEvent = event;

            for (HBox card : eventCards) {
                card.getStyleClass().remove("selected-event-card");
            }
            selectedCard.getStyleClass().add("selected-event-card");

            rightTitleLabel.setText("Select Coordinators");
            selectedEventLabel.setText("Selected event: " + event.getName());

            List<Integer> assignedCoordinatorIds = eventManager.getCoordinatorIdsForEvent(event.getId());

            for (Map.Entry<Integer, CheckBox> entry : coordinatorCheckBoxMap.entrySet()) {
                entry.getValue().setSelected(assignedCoordinatorIds.contains(entry.getKey()));
            }

        } catch (Exception e) {
            e.printStackTrace();
            showError("Could not load assigned coordinators.");
        }
    }

    @FXML
    private void saveAssignments() {
        if (selectedEvent == null) {
            showError("Select an event first.");
            return;
        }

        try {
            List<Integer> selectedCoordinatorIds = new ArrayList<>();

            for (Map.Entry<Integer, CheckBox> entry : coordinatorCheckBoxMap.entrySet()) {
                if (entry.getValue().isSelected()) {
                    selectedCoordinatorIds.add(entry.getKey());
                }
            }

            eventManager.replaceCoordinatorsForEvent(selectedEvent.getId(), selectedCoordinatorIds);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText("Coordinators assigned successfully.");
            alert.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            showError("Could not save coordinator assignments.");
        }
    }

    @FXML
    private void cancelSelection() {
        clearSelection();
    }

    private void clearSelection() {
        selectedEvent = null;
        rightTitleLabel.setText("Select Coordinators");
        selectedEventLabel.setText("Click an event on the left.");

        for (HBox card : eventCards) {
            card.getStyleClass().remove("selected-event-card");
        }

        for (CheckBox checkBox : coordinatorCheckBoxMap.values()) {
            checkBox.setSelected(false);
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private record AssignDataBundle(List<Event> events, List<Users> coordinators) {}
}
package dk.easv.gui;

import dk.easv.be.EventCoordinator;
import dk.easv.bll.EventManager;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.VPos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class UsersController {

    @FXML
    private VBox userCardContainer;

    @FXML
    private TextField searchField;

    @FXML
    private VBox addCoordinatorPanel;

    @FXML
    private TextField nameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField usernameField;

    @FXML
    private TextField passwordField;

    private final EventManager eventManager = new EventManager();
    private final List<UserCardData> loadedCoordinators = new ArrayList<>();

    @FXML
    public void initialize() {
        hideAddPanel();

        if (searchField != null) {
            searchField.textProperty().addListener((obs, oldValue, newValue) -> renderCards(filterData(newValue)));
        }

        loadCoordinatorsAsync();
    }

    @FXML
    private void startAddCoordinator() {
        clearForm();
        showAddPanel();
        if (nameField != null) {
            nameField.requestFocus();
        }
    }

    @FXML
    private void confirmAddCoordinator() {
        String name = nameField.getText() == null ? "" : nameField.getText().trim();
        String email = emailField.getText() == null ? "" : emailField.getText().trim();
        String username = usernameField.getText() == null ? "" : usernameField.getText().trim();
        String password = passwordField.getText() == null ? "" : passwordField.getText().trim();

        if (name.isBlank() || email.isBlank() || username.isBlank() || password.isBlank()) {
            showError("Fill in Name, Email, Username, and Password.");
            return;
        }

        Task<UserCardData> createTask = new Task<>() {
            @Override
            protected UserCardData call() throws Exception {
                EventCoordinator coordinator = new EventCoordinator(name, email, username, password);
                EventCoordinator created = eventManager.createCoordinator(coordinator);
                return new UserCardData(created, 0);
            }
        };

        createTask.setOnSucceeded(event -> {
            UserCardData created = createTask.getValue();
            loadedCoordinators.add(created);
            renderCards(filterData(searchField != null ? searchField.getText() : ""));
            clearForm();
            hideAddPanel();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText("Coordinator created successfully.");
            alert.showAndWait();
        });

        createTask.setOnFailed(event -> {
            showError("Could not create coordinator.");
            if (createTask.getException() != null) {
                createTask.getException().printStackTrace();
            }
        });

        Thread thread = new Thread(createTask);
        thread.setDaemon(true);
        thread.start();
    }

    @FXML
    private void cancelAddCoordinator() {
        clearForm();
        hideAddPanel();
    }

    private void showAddPanel() {
        addCoordinatorPanel.setVisible(true);
        addCoordinatorPanel.setManaged(true);
    }

    private void hideAddPanel() {
        addCoordinatorPanel.setVisible(false);
        addCoordinatorPanel.setManaged(false);
    }

    private void clearForm() {
        if (nameField != null) nameField.clear();
        if (emailField != null) emailField.clear();
        if (usernameField != null) usernameField.clear();
        if (passwordField != null) passwordField.clear();
    }

    private void loadCoordinatorsAsync() {
        userCardContainer.getChildren().clear();

        Label loadingLabel = new Label("Loading coordinators...");
        loadingLabel.getStyleClass().add("muted");
        userCardContainer.getChildren().add(loadingLabel);

        Task<List<UserCardData>> loadTask = new Task<>() {
            @Override
            protected List<UserCardData> call() throws Exception {
                List<EventCoordinator> coordinators = eventManager.getAllCoordinators();
                List<UserCardData> result = new ArrayList<>();

                for (EventCoordinator coordinator : coordinators) {
                    int eventCount = eventManager.getEventCountForCoordinator(coordinator.getId());
                    result.add(new UserCardData(coordinator, eventCount));
                }

                return result;
            }
        };

        loadTask.setOnSucceeded(event -> {
            loadedCoordinators.clear();
            loadedCoordinators.addAll(loadTask.getValue());
            renderCards(filterData(searchField != null ? searchField.getText() : ""));
        });

        loadTask.setOnFailed(event -> {
            userCardContainer.getChildren().clear();
            showError("Could not load coordinators.");
            if (loadTask.getException() != null) {
                loadTask.getException().printStackTrace();
            }
        });

        Thread thread = new Thread(loadTask);
        thread.setDaemon(true);
        thread.start();
    }

    private List<UserCardData> filterData(String query) {
        if (query == null || query.isBlank()) {
            return new ArrayList<>(loadedCoordinators);
        }

        String q = query.toLowerCase().trim();
        List<UserCardData> filtered = new ArrayList<>();

        for (UserCardData data : loadedCoordinators) {
            EventCoordinator coordinator = data.coordinator();
            if (coordinator.getName().toLowerCase().contains(q)
                    || coordinator.getEmail().toLowerCase().contains(q)
                    || coordinator.getUsername().toLowerCase().contains(q)) {
                filtered.add(data);
            }
        }

        return filtered;
    }

    private void renderCards(List<UserCardData> data) {
        userCardContainer.getChildren().clear();

        if (data.isEmpty()) {
            Label emptyLabel = new Label("No coordinators found.");
            emptyLabel.getStyleClass().add("muted");
            userCardContainer.getChildren().add(emptyLabel);
            return;
        }

        for (UserCardData item : data) {
            userCardContainer.getChildren().add(createCoordinatorCard(item));
        }
    }

    private GridPane createCoordinatorCard(UserCardData data) {
        EventCoordinator coordinator = data.coordinator();

        GridPane row = new GridPane();
        row.getStyleClass().addAll("card", "user-row-grid");

        ColumnConstraints c1 = new ColumnConstraints();
        c1.setHgrow(Priority.ALWAYS);
        c1.setMinWidth(10);

        ColumnConstraints c2 = new ColumnConstraints();
        c2.setPrefWidth(170);

        ColumnConstraints c3 = new ColumnConstraints();
        c3.setPrefWidth(90);

        ColumnConstraints c4 = new ColumnConstraints();
        c4.setPrefWidth(210);

        row.getColumnConstraints().addAll(c1, c2, c3, c4);

        VBox left = new VBox(2);
        Label nameLabel = new Label(coordinator.getName());
        nameLabel.getStyleClass().add("card-title");

        Label emailLabel = new Label(coordinator.getEmail());
        emailLabel.getStyleClass().add("card-subtext");

        left.getChildren().addAll(nameLabel, emailLabel);
        GridPane.setColumnIndex(left, 0);
        GridPane.setValignment(left, VPos.CENTER);

        Label roleLabel = new Label("Coordinator");
        roleLabel.getStyleClass().addAll("tag", "coordinator");
        GridPane.setColumnIndex(roleLabel, 1);
        GridPane.setValignment(roleLabel, VPos.CENTER);

        Label eventCountLabel = new Label(data.eventCount() + " events");
        eventCountLabel.getStyleClass().add("muted");
        GridPane.setColumnIndex(eventCountLabel, 2);
        GridPane.setValignment(eventCountLabel, VPos.CENTER);

        Button removeButton = new Button("Remove Coordinator");
        removeButton.getStyleClass().add("secondary-button");
        removeButton.setOnAction(e -> removeCoordinator(coordinator));
        GridPane.setColumnIndex(removeButton, 3);
        GridPane.setValignment(removeButton, VPos.CENTER);

        row.getChildren().addAll(left, roleLabel, eventCountLabel, removeButton);

        return row;
    }

    private void removeCoordinator(EventCoordinator coordinator) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Remove Coordinator");
        confirm.setHeaderText(null);
        confirm.setContentText("Remove " + coordinator.getName() + " from the database?");

        confirm.showAndWait().ifPresent(response -> {
            String buttonText = response.getText().toLowerCase();
            if (!buttonText.contains("ok")) {
                return;
            }

            Task<Void> deleteTask = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    eventManager.deleteCoordinator(coordinator);
                    return null;
                }
            };

            deleteTask.setOnSucceeded(event -> {
                loadedCoordinators.removeIf(item -> item.coordinator().getId() == coordinator.getId());
                renderCards(filterData(searchField != null ? searchField.getText() : ""));
            });

            deleteTask.setOnFailed(event -> {
                showError("Could not remove coordinator.");
                if (deleteTask.getException() != null) {
                    deleteTask.getException().printStackTrace();
                }
            });

            Thread thread = new Thread(deleteTask);
            thread.setDaemon(true);
            thread.start();
        });
    }

    private void showError(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    private record UserCardData(EventCoordinator coordinator, int eventCount) {}
}
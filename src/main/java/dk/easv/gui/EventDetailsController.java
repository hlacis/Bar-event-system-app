package dk.easv.gui;

import dk.easv.be.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import dk.easv.be.TicketType;
import dk.easv.bll.TicketTypeManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TextField;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.format.DateTimeFormatter;

/**
 * Controller for the Event Details view.
 * Responsible for displaying event information
 * and handling navigation (edit + back).
 */
public class EventDetailsController {

    // UI elements from FXML
    @FXML private Label nameLabel;
    @FXML private Label locationLabel;
    @FXML private Label timeLabel;
    @FXML private Label notesLabel;
    @FXML private TextField txtTicketName;
    @FXML private TextField txtPrice;
    @FXML private TextField txtQuantity;

    @FXML private TableView<TicketType> ticketTable;
    @FXML private TableColumn<TicketType, String> colName;
    @FXML private TableColumn<TicketType, Double> colPrice;
    @FXML private TableColumn<TicketType, Integer> colQuantity;

    // Holds the selected event
    private Event event;
    private TicketTypeManager ticketTypeManager = new TicketTypeManager();
    private ObservableList<TicketType> ticketList = FXCollections.observableArrayList();

    /**
     * Called from EventsController when a user clicks an event.
     * This method sets the data in the UI.
     */
    @FXML
    public void initialize() {
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        ticketTable.setItems(ticketList);
    }

    @FXML
    public void setEvent(Event event) {
        this.event = event;

        // Display event data in labels
        nameLabel.setText(event.getName());
        locationLabel.setText("Location: " + event.getLocation());

        // Handle time safely (avoid null)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        if (event.getStartTime() != null) {
            String start = event.getStartTime().format(formatter);
            String end = event.getEndTime() != null ? event.getEndTime().format(formatter) : "";

            timeLabel.setText(start + " - " + end);
        }

        // Handle notes (empty vs filled)
        if (event.getNotes() == null || event.getNotes().isBlank()) {
            notesLabel.setText("No notes");
        } else {
            notesLabel.setText(event.getNotes());
        }
        loadTicketTypes();
    }

    /**
     * Opens the Event Creator view in EDIT mode.
     * Reuses the existing EventCreatorController.
     */

    private void loadTicketTypes() {
        try {
            ticketList.clear();
            ticketList.addAll(ticketTypeManager.getTicketTypesByEvent(event.getId()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddTicketType() {
        try {
            String name = txtTicketName.getText();
            double price = Double.parseDouble(txtPrice.getText());
            int quantity = Integer.parseInt(txtQuantity.getText());

            TicketType tt = new TicketType(
                    event.getId(),
                    name,
                    price,
                    quantity
            );

            ticketTypeManager.createTicketType(tt);

            // Refresh table
            loadTicketTypes();

            // Clear input fields
            txtTicketName.clear();
            txtPrice.clear();
            txtQuantity.clear();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void handleEdit() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/dk/easv/gui/EventsCreator.fxml")
            );

            Parent view = loader.load();

            // Get controller and pass the selected event
            EventCreatorController controller = loader.getController();
            controller.setEvent(event);

            // Replace current view inside the same content area
            StackPane contentHost = findContentHost(nameLabel);
            if (contentHost != null) {
                contentHost.getChildren().setAll(view);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Navigates back to the Events list view.
     */
    @FXML
    private void handleBack() {
        try {
            Parent view = FXMLLoader.load(
                    getClass().getResource("/dk/easv/gui/EventsView.fxml")
            );

            StackPane contentHost = findContentHost(nameLabel);
            if (contentHost != null) {
                contentHost.getChildren().setAll(view);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handlePrint() {
        System.out.println("Print tickets clicked");
    }

    /**
     * Finds the main content container (StackPane)
     * so we can swap views inside the same window.
     */
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
}
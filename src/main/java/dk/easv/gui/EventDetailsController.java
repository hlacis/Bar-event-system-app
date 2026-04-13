package dk.easv.gui;

import dk.easv.be.Event;
import dk.easv.be.Ticket;
import dk.easv.be.TicketType;
import dk.easv.bll.TicketManager;
import dk.easv.bll.TicketPDFGenerator;
import dk.easv.bll.TicketTypeManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.format.DateTimeFormatter;

public class EventDetailsController {

    @FXML private Label nameLabel;
    @FXML private Label locationLabel;
    @FXML private Label timeLabel;
    @FXML private Label notesLabel;

    @FXML private TextField txtTicketName;
    @FXML private TextField txtPrice;
    @FXML private TextField txtQuantity;
    @FXML private TextField txtNote;

    @FXML private TableView<TicketType> ticketTable;
    @FXML private TableColumn<TicketType, String> colName;
    @FXML private TableColumn<TicketType, Double> colPrice;
    @FXML private TableColumn<TicketType, Integer> colQuantity;

    private Event event;
    private TicketTypeManager ticketTypeManager = new TicketTypeManager();
    private ObservableList<TicketType> ticketList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        ticketTable.setItems(ticketList);

        ticketTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, selected) -> {
            if (selected != null) {
                txtTicketName.setText(selected.getName());
                txtPrice.setText(String.valueOf(selected.getPrice()));
                txtQuantity.setText(String.valueOf(selected.getQuantity()));
                txtNote.setText(selected.getNote());
            }
        });
    }

    @FXML
    public void setEvent(Event event) {
        this.event = event;

        nameLabel.setText(event.getName());
        locationLabel.setText("Location: " + event.getLocation());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        if (event.getStartTime() != null) {
            String start = event.getStartTime().format(formatter);
            String end = event.getEndTime() != null ? event.getEndTime().format(formatter) : "";
            timeLabel.setText(start + " - " + end);
        }

        if (event.getNotes() == null || event.getNotes().isBlank()) {
            notesLabel.setText("No notes");
        } else {
            notesLabel.setText(event.getNotes());
        }

        loadTicketTypes();
    }

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
            TicketType tt = new TicketType(
                    event.getId(),
                    txtTicketName.getText(),
                    Double.parseDouble(txtPrice.getText()),
                    Integer.parseInt(txtQuantity.getText()),
                    txtNote.getText()
            );

            ticketTypeManager.createTicketType(tt);
            loadTicketTypes();

            txtTicketName.clear();
            txtPrice.clear();
            txtQuantity.clear();
            txtNote.clear();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleEditTicketType() {
        try {
            TicketType selected = ticketTable.getSelectionModel().getSelectedItem();

            if (selected == null) {
                showError("No ticket selected");
                return;
            }

            selected.setName(txtTicketName.getText());
            selected.setPrice(Double.parseDouble(txtPrice.getText()));
            selected.setQuantity(Integer.parseInt(txtQuantity.getText()));
            selected.setNote(txtNote.getText());

            ticketTypeManager.updateTicketType(selected);
            loadTicketTypes();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeleteTicketType() {
        try {
            TicketType selected = ticketTable.getSelectionModel().getSelectedItem();

            if (selected == null) {
                showError("No ticket selected");
                return;
            }

            ticketTypeManager.deleteTicketType(selected.getId());
            loadTicketTypes();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handlePrintTickets() {

        TicketType selected = ticketTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showError("Please select a ticket type");
            return;
        }

        try {
            // Create ticket in database
            TicketManager manager = new TicketManager();

            Ticket ticket = manager.createTicket(
                    event.getId(),
                    selected.getId(),
                    "Test Name",
                    "test@mail.com"
            );

            // Generate PDF with QR code
            TicketPDFGenerator pdfGen = new TicketPDFGenerator();
            pdfGen.generatePDF(ticket);

            // Show success message
            showInfo("Success", "Ticket created and PDF generated");

        } catch (Exception e) {
            e.printStackTrace();
            showError("Could not create ticket");
        }
    }

    @FXML
    private void handleEdit() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/dk/easv/gui/EventsCreator.fxml")
            );

            Parent view = loader.load();

            EventCreatorController controller = loader.getController();
            controller.setEvent(event);

            StackPane contentHost = findContentHost(nameLabel);
            if (contentHost != null) {
                contentHost.getChildren().setAll(view);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
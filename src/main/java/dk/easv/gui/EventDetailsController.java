package dk.easv.gui;

import dk.easv.be.Event;
import dk.easv.be.Ticket;
import dk.easv.be.TicketType;
import dk.easv.be.Voucher;
import dk.easv.bll.TicketManager;
import dk.easv.bll.TicketPDFGenerator;
import dk.easv.bll.TicketTypeManager;
import dk.easv.bll.VoucherManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    @FXML private TableColumn<TicketType, Integer> colLeft;
    @FXML private TableColumn<TicketType, String> colNote;

    @FXML private TextField txtVoucherName;
    @FXML private TextField txtVoucherQuantity;
    @FXML private TextField txtVoucherLeft;
    @FXML private TextField txtVoucherNote;

    @FXML private TableView<Voucher> voucherTable;
    @FXML private TableColumn<Voucher, String> colVoucherName;
    @FXML private TableColumn<Voucher, Integer> colVoucherQuantity;
    @FXML private TableColumn<Voucher, Integer> colVoucherLeft;
    @FXML private TableColumn<Voucher, String> colVoucherNote;
    @FXML private Button printButton;

    private Event event;


    private final TicketTypeManager ticketTypeManager = new TicketTypeManager();
    private final VoucherManager voucherManager = new VoucherManager();

    private final ObservableList<Voucher> voucherList = FXCollections.observableArrayList();
    private final ObservableList<TicketType> ticketList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colLeft.setCellValueFactory(new PropertyValueFactory<>("ticketsLeft"));
        colNote.setCellValueFactory(new PropertyValueFactory<>("note"));

        colVoucherName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colVoucherQuantity.setCellValueFactory(new PropertyValueFactory<>("total"));
        colVoucherLeft.setCellValueFactory(new PropertyValueFactory<>("vouchersLeft"));
        colVoucherNote.setCellValueFactory(new PropertyValueFactory<>("note"));

        ticketTable.setItems(ticketList);
        voucherTable.setItems(voucherList);

        ticketTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, selected) -> {
            if (selected != null) {
                txtTicketName.setText(selected.getName());
                txtPrice.setText(String.valueOf(selected.getPrice()));
                txtQuantity.setText(String.valueOf(selected.getQuantity()));
                txtNote.setText(selected.getNote());
            } else {
                txtTicketName.clear();
                txtPrice.clear();
                txtQuantity.clear();
                txtNote.clear();
            }
        });

        voucherTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, selected) -> {
            if (selected != null) {
                txtVoucherName.setText(selected.getName());
                txtVoucherQuantity.setText(String.valueOf(selected.getTotal()));
                txtVoucherLeft.setText(String.valueOf(selected.getVouchersLeft()));
                txtVoucherNote.setText(selected.getNote());
            } else {
                txtVoucherName.clear();
                txtVoucherQuantity.clear();
                txtVoucherLeft.clear();
                txtVoucherNote.clear();
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
        loadVouchers();
    }

    private void loadTicketTypes() {
        try {
            ticketList.clear();
            ticketList.addAll(ticketTypeManager.getTicketTypesByEvent(event.getId()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadVouchers() {
        try {
            voucherList.clear();
            voucherList.addAll(voucherManager.getVouchersByEvent(event.getId()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddTicketType() {
        try {
            int quantity = Integer.parseInt(txtQuantity.getText().trim());

            TicketType tt = new TicketType(
                    event.getId(),
                    txtTicketName.getText().trim(),
                    Double.parseDouble(txtPrice.getText().trim()),
                    quantity,
                    quantity,
                    txtNote.getText().trim()
            );

            ticketTypeManager.createTicketType(tt);
            loadTicketTypes();

            txtTicketName.clear();
            txtPrice.clear();
            txtQuantity.clear();
            txtNote.clear();

        } catch (Exception e) {
            e.printStackTrace();
            showError("Could not add ticket type");
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

            String name = txtTicketName.getText().trim();
            double price = Double.parseDouble(txtPrice.getText().trim());
            int quantity = Integer.parseInt(txtQuantity.getText().trim());
            String note = txtNote.getText().trim();

            int sold = selected.getQuantity() - selected.getTicketsLeft();
            int newLeft = Math.max(0, quantity - sold);

            selected.setName(name);
            selected.setPrice(price);
            selected.setQuantity(quantity);
            selected.setTicketsLeft(newLeft);
            selected.setNote(note);

            ticketTypeManager.updateTicketType(selected);
            loadTicketTypes();

            txtTicketName.clear();
            txtPrice.clear();
            txtQuantity.clear();
            txtNote.clear();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText("Ticket updated successfully!");
            alert.showAndWait();

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

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Delete");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to delete this ticket?");

            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                ticketTypeManager.deleteTicketType(selected.getId());
                loadTicketTypes();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddVoucher() {
        try {
            String name = txtVoucherName.getText().trim();
            int quantity = Integer.parseInt(txtVoucherQuantity.getText().trim());
            String note = txtVoucherNote.getText().trim();

            Voucher voucher = new Voucher(
                    event.getId(),
                    name,
                    quantity,
                    quantity,
                    note
            );

            voucherManager.createVoucher(voucher);
            loadVouchers();

            txtVoucherName.clear();
            txtVoucherQuantity.clear();
            txtVoucherLeft.clear();
            txtVoucherNote.clear();

            showInfo("Success", "Voucher created!");

        } catch (Exception e) {
            e.printStackTrace();
            showError("Could not create voucher");
        }
    }

    @FXML
    private void handleEditVoucher() {
        try {
            Voucher selected = voucherTable.getSelectionModel().getSelectedItem();

            if (selected == null) {
                showError("No voucher selected");
                return;
            }

            String name = txtVoucherName.getText().trim();
            int quantity = Integer.parseInt(txtVoucherQuantity.getText().trim());
            String note = txtVoucherNote.getText().trim();

            int used = selected.getTotal() - selected.getVouchersLeft();
            int newLeft = Math.max(0, quantity - used);

            selected.setName(name);
            selected.setTotal(quantity);
            selected.setVouchersLeft(newLeft);
            selected.setNote(note);

            voucherManager.updateVoucher(selected);
            loadVouchers();

            txtVoucherName.clear();
            txtVoucherQuantity.clear();
            txtVoucherLeft.clear();
            txtVoucherNote.clear();

            showInfo("Success", "Voucher updated!");

        } catch (Exception e) {
            e.printStackTrace();
            showError("Could not update voucher");
        }
    }

    @FXML
    private void handleDeleteVoucher() {
        try {
            Voucher selected = voucherTable.getSelectionModel().getSelectedItem();

            if (selected == null) {
                showError("No voucher selected");
                return;
            }

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Delete");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to delete this voucher?");

            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                voucherManager.deleteVoucher(selected.getId());
                loadVouchers();
            }

        } catch (Exception e) {
            e.printStackTrace();
            showError("Could not delete voucher");
        }
    }

    @FXML
    private void handlePrintTickets() {
        TicketType selected = ticketTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showError("Please select a ticket type");
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Creating Ticket");

        TextField nameField = new TextField();
        nameField.setPromptText("Name");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");

        TextField amountField = new TextField();
        amountField.setPromptText("Amount");

        VBox content = new VBox(10,
                new Label("Enter customer info"),
                nameField,
                emailField,
                amountField
        );
        content.setStyle(
                "-fx-padding: 20;" +
                        "-fx-background-color: white;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-radius: 10;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10,0,0,2);"
        );

        DialogPane pane = dialog.getDialogPane();
        pane.setContent(content);

        ButtonType createButton = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        pane.getButtonTypes().addAll(createButton, cancelButton);

        Node createBtn = pane.lookupButton(createButton);
        createBtn.setDisable(true);
        createBtn.setStyle(
                "-fx-background-color: #ff7a00;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;"
        );

        ButtonBar buttonBar = (ButtonBar) pane.lookup(".button-bar");
        if (buttonBar != null) {
            buttonBar.setButtonOrder(ButtonBar.BUTTON_ORDER_NONE);
            buttonBar.setStyle("-fx-alignment: center; -fx-padding: 10;");
        }

        amountField.textProperty().addListener((obs, oldVal, newVal) ->
                validateForm(nameField, emailField, amountField, selected, createBtn));

        nameField.textProperty().addListener((obs, oldVal, newVal) ->
                validateForm(nameField, emailField, amountField, selected, createBtn));

        emailField.textProperty().addListener((obs, oldVal, newVal) ->
                validateForm(nameField, emailField, amountField, selected, createBtn));

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == createButton) {
            try {
                String name = nameField.getText().trim();
                String email = emailField.getText().trim();
                int amount = Integer.parseInt(amountField.getText().trim());

                if (amount <= 0) {
                    showError("Amount must be at least 1");
                    return;
                }

                if (amount > selected.getTicketsLeft()) {
                    showError("Not enough tickets available");
                    return;
                }

                TicketManager manager = new TicketManager();
                List<Ticket> tickets = new ArrayList<>();

                for (int i = 0; i < amount; i++) {
                    Ticket t = manager.createTicket(
                            event.getId(),
                            selected.getId(),
                            name,
                            email
                    );
                    tickets.add(t);
                }

                selected.setTicketsLeft(selected.getTicketsLeft() - amount);
                ticketTypeManager.updateTicketType(selected);

                TicketPDFGenerator generator = new TicketPDFGenerator();
                String eventName = event.getName();
                String location = event.getLocation();

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");
                String start = event.getStartTime().format(formatter);
                String end = event.getEndTime().format(formatter);
                String time = start + " - " + end;

                generator.generatePDF(tickets, eventName, location, time);
                loadTicketTypes();

            } catch (Exception e) {
                e.printStackTrace();
                showError("Could not create ticket");
            }
        }
    }

    private void validateForm(TextField nameField, TextField emailField,
                              TextField amountField, TicketType selected, Node createBtn) {
        try {
            int amount = Integer.parseInt(amountField.getText());

            boolean invalid =
                    amount <= 0 ||
                            nameField.getText().trim().isEmpty() ||
                            emailField.getText().trim().isEmpty() ||
                            amount > selected.getTicketsLeft();

            createBtn.setDisable(invalid);

        } catch (Exception e) {
            createBtn.setDisable(true);
        }
    }

    @FXML
    private void handlePrintVouchers() {

        Voucher selected = voucherTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showError("Please select a voucher");
            return;
        }

        // Create dialog
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Creating Voucher");

        TextField nameField = new TextField();
        nameField.setPromptText("Name");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");

        TextField amountField = new TextField();
        amountField.setPromptText("Amount");

        VBox content = new VBox(10,
                new Label("Enter customer info"),
                nameField,
                emailField,
                amountField
        );

        content.setStyle(
                "-fx-padding: 20;" +
                        "-fx-background-color: white;" +
                        "-fx-background-radius: 10;"
        );

        dialog.getDialogPane().setContent(content);

        ButtonType createButton = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        dialog.getDialogPane().getButtonTypes().addAll(createButton, cancelButton);

        Node createBtn = dialog.getDialogPane().lookupButton(createButton);
        createBtn.setDisable(true);
        printButton.getStyleClass().add("print-button");

        amountField.textProperty().addListener((obs, oldVal, newVal) -> {
            try {
                int amount = Integer.parseInt(newVal);

                boolean invalid =
                        amount <= 0 ||
                                nameField.getText().isBlank() ||
                                emailField.getText().isBlank() ||
                                amount > selected.getTotal();

                createBtn.setDisable(invalid);

            } catch (Exception e) {
                createBtn.setDisable(true);
            }
        });

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == createButton) {
            try {
                String name = nameField.getText();
                String email = emailField.getText();
                int amount = Integer.parseInt(amountField.getText());
                if (amount > selected.getVouchersLeft()) {
                    showError("Not enough vouchers available");
                    return;
                }

                VoucherManager manager = new VoucherManager();
                List<Voucher> vouchers = new ArrayList<>();

                for (int i = 0; i < amount; i++) {

                    Voucher v = new Voucher(
                            event.getId(),
                            name,
                            selected.getTotal(),
                            selected.getVouchersLeft(),
                            selected.getNote()
                    );

                    manager.createVoucher(v);
                    vouchers.add(v);
                }

                selected.setVouchersLeft(selected.getVouchersLeft() - amount);
                voucherManager.updateVoucher(selected);

                // PDF generation
                TicketPDFGenerator generator = new TicketPDFGenerator();

                String eventName = event.getName();
                String location = event.getLocation();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");

                String start = event.getStartTime().format(formatter);
                String end = event.getEndTime().format(formatter);
                String time = start + " - " + end;

                generator.generateVoucherPDF(vouchers, eventName, location, time);

                //showInfo("Success", amount + " vouchers created");

            } catch (Exception e) {
                e.printStackTrace();
                showError("Could not create vouchers");
            }
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
package dk.easv.be;

public class TicketType {

    private int id;
    private int eventId;
    private String name;
    private double price;
    private int quantity;

    public TicketType(int id, int eventId, String name, double price, int quantity) {
        this.id = id;
        this.eventId = eventId;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public TicketType(int eventId, String name, double price, int quantity) {
        this(0, eventId, name, price, quantity);
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // Getters & setters
}


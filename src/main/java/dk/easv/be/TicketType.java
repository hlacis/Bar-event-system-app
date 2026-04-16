package dk.easv.be;

public class TicketType {
    //Constructors 1

    private int id;
    private int eventId;
    private String name;
    private double price;
    private int quantity;
    private int ticketsLeft;
    private String note;
    //Constructors 2

    public TicketType(int id, int eventId, String name, double price, int quantity, int ticketsLeft, String note) {
        this.id = id;
        this.eventId = eventId;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.ticketsLeft = ticketsLeft;
        this.note = note;
    }


    public TicketType(int eventId, String name, double price, int quantity, int ticketsLeft, String note) {
        this(0, eventId, name, price, quantity, ticketsLeft, note);
    }

    // Getters & setters

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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getTicketsLeft() {
        return ticketsLeft;
    }

    public void setTicketsLeft(int ticketsLeft) {
        this.ticketsLeft = ticketsLeft;
    }


}


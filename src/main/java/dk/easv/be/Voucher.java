package dk.easv.be;

public class Voucher {

    private int id;
    private int eventId;
    private String name;
    private String type;   // FREE, DISCOUNT, EXTRA
    private double value;

    public Voucher(int id, int eventId, String name, String type, double value) {
        this.id = id;
        this.eventId = eventId;
        this.name = name;
        this.type = type;
        this.value = value;
    }

    public Voucher(int eventId, String name, String type, double value) {
        this(0, eventId, name, type, value);
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getEventId() { return eventId; }
    public void setEventId(int eventId) { this.eventId = eventId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public double getValue() { return value; }
    public void setValue(double value) { this.value = value; }
}
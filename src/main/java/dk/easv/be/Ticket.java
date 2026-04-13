package dk.easv.be;

public class Ticket {

    private String id;
    private int eventId;
    private int ticketTypeId;

    private String customerName;
    private String customerEmail;

    public Ticket(String id, int eventId, int ticketTypeId,
                  String customerName, String customerEmail) {
        this.id = id;
        this.eventId = eventId;
        this.ticketTypeId = ticketTypeId;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
    }

    public Ticket() {

    }
    public String getId() {
        return id;
    }

    public int getEventId() {
        return eventId;
    }

    public int getTicketTypeId() {
        return ticketTypeId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }
}
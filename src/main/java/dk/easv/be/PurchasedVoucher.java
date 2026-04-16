package dk.easv.be;

public class PurchasedVoucher {

    private int id;
    private int eventId;
    private int voucherTypeId;
    private String customerName;
    private String customerEmail;

    public PurchasedVoucher() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getEventId() { return eventId; }
    public void setEventId(int eventId) { this.eventId = eventId; }

    public void setVoucherTypeId(int voucherTypeId) { this.voucherTypeId = voucherTypeId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
}
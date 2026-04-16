package dk.easv.be;

public class Voucher {

    private int id;
    private int eventId;
    private String name;
    private int total;
    private int vouchersLeft;
    private String note;

    public Voucher(int id, int eventId, String name, int total, int vouchersLeft, String note) {
        this.id = id;
        this.eventId = eventId;
        this.name = name;
        this.total = total;
        this.vouchersLeft = vouchersLeft;
        this.note = note;
    }

    public Voucher(int eventId, String name, int total, int vouchersLeft, String note) {
        this(0, eventId, name, total, vouchersLeft, note);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getVouchersLeft() {
        return vouchersLeft;
    }

    public void setVouchersLeft(int vouchersLeft) {
        this.vouchersLeft = vouchersLeft;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

}
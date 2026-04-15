package dk.easv.dal.dao;

import dk.easv.be.TicketType;
import dk.easv.dal.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TicketTypeDAO {

    private final ConnectionManager connectionManager = new ConnectionManager();

    public TicketType createTicketType(TicketType ticketType) throws Exception {
        String sql = "INSERT INTO TicketType (EventId, Name, Price, Quantity, Note) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, ticketType.getEventId());
            stmt.setString(2, ticketType.getName());
            stmt.setDouble(3, ticketType.getPrice());
            stmt.setInt(4, ticketType.getQuantity());
            stmt.setString(5, ticketType.getNote());

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                ticketType.setId(rs.getInt(1));
            }

            return ticketType;
        }
    }

    public void updateTicketType(TicketType tt) throws Exception {
        String sql = "UPDATE TicketType SET Name = ?, Price = ?, Quantity = ?, Note = ?, ticketsLeft = ? WHERE Id = ?";

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, tt.getName());
            stmt.setDouble(2, tt.getPrice());
            stmt.setInt(3, tt.getQuantity());
            stmt.setString(4, tt.getNote());
            stmt.setInt(5, tt.getTicketsLeft());
            stmt.setInt(6, tt.getId());

            stmt.executeUpdate();
        }
    }

    public void deleteTicketType(int id) throws Exception {
        String sql = "DELETE FROM TicketType WHERE Id=?";

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public List<TicketType> getTicketTypesByEvent(int eventId) throws Exception {
        List<TicketType> list = new ArrayList<>();

        String sql = "SELECT * FROM TicketType WHERE EventId = ?";

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, eventId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                TicketType tt = new TicketType(
                        rs.getInt("Id"),
                        rs.getInt("EventId"),
                        rs.getString("Name"),
                        rs.getDouble("Price"),
                        rs.getInt("Quantity"),
                        rs.getString("Note")
                );

                tt.setTicketsLeft(rs.getInt("ticketsLeft"));

                list.add(tt);
            }

            return list;
        }
    }
}
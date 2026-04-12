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
        String sql = "INSERT INTO TicketType (EventId, Name, Price, Quantity) VALUES (?, ?, ?, ?)";

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, ticketType.getEventId());
            stmt.setString(2, ticketType.getName());
            stmt.setDouble(3, ticketType.getPrice());
            stmt.setInt(4, ticketType.getQuantity());

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                ticketType.setId(rs.getInt(1));
            }

            return ticketType;
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
                list.add(new TicketType(
                        rs.getInt("Id"),
                        rs.getInt("EventId"),
                        rs.getString("Name"),
                        rs.getDouble("Price"),
                        rs.getInt("Quantity")
                ));
            }
        }

        return list;
    }
}
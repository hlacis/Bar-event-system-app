package dk.easv.dal.dao;

import dk.easv.be.Ticket;
import dk.easv.dal.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class TicketDAO {

    public void saveTicket(Ticket ticket) throws Exception {

        String sql = "INSERT INTO Ticket (id, eventId, ticketTypeId, customerName, customerEmail) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, ticket.getId());
            stmt.setInt(2, ticket.getEventId());
            stmt.setInt(3, ticket.getTicketTypeId());
            stmt.setString(4, ticket.getCustomerName());
            stmt.setString(5, ticket.getCustomerEmail());

            stmt.executeUpdate();
        }
    }
}
package dk.easv.dal.dao;

import dk.easv.be.Event;
import dk.easv.dal.ConnectionManager;

import java.sql.*;

public class EventDAO {

    private ConnectionManager cm = new ConnectionManager();

    public Event createEvent(Event event) throws Exception {
        String sql = "INSERT INTO Event (Name, Location, StartTime, EndTime, Notes) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = cm.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, event.getName());
            stmt.setString(2, event.getLocation());
            stmt.setTimestamp(3, Timestamp.valueOf(event.getStartTime()));

            if (event.getEndTime() != null)
                stmt.setTimestamp(4, Timestamp.valueOf(event.getEndTime()));
            else
                stmt.setNull(4, Types.TIMESTAMP);

            stmt.setString(5, event.getNotes());

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                event.setId(rs.getInt(1));
            }

            return event;
        }
    }
}
package dk.easv.dal.dao;

import dk.easv.be.Event;
import dk.easv.dal.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EventDAO {

    private final ConnectionManager cm = new ConnectionManager();

    public Event createEvent(Event event) throws Exception {
        String sql = "INSERT INTO Event (Name, Location, StartTime, EndTime, Notes) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = cm.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, event.getName());
            stmt.setString(2, event.getLocation());
            stmt.setTimestamp(3, Timestamp.valueOf(event.getStartTime()));

            if (event.getEndTime() != null) {
                stmt.setTimestamp(4, Timestamp.valueOf(event.getEndTime()));
            } else {
                stmt.setNull(4, Types.TIMESTAMP);
            }

            stmt.setString(5, event.getNotes());

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                event.setId(rs.getInt(1));
            }

            return event;
        }
    }

    public List<Event> getAllEvents() throws Exception {
        List<Event> events = new ArrayList<>();

        String sql = "SELECT Id, Name, Location, StartTime, EndTime, Notes FROM Event ORDER BY StartTime";

        try (Connection conn = cm.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("Id");
                String name = rs.getString("Name");
                String location = rs.getString("Location");

                Timestamp startTs = rs.getTimestamp("StartTime");
                Timestamp endTs = rs.getTimestamp("EndTime");

                String notes = rs.getString("Notes");

                Event event = new Event(
                        id,
                        name,
                        location,
                        startTs != null ? startTs.toLocalDateTime() : null,
                        endTs != null ? endTs.toLocalDateTime() : null,
                        notes
                );

                events.add(event);
            }
        }

        return events;
    }

    public void deleteEvent(int eventId) throws Exception {
        String sql = "DELETE FROM Event WHERE Id = ?";

        try (Connection conn = cm.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, eventId);
            stmt.executeUpdate();
        }
    }
    public void updateEvent(Event event) throws Exception {
        String sql = "UPDATE Event SET Name = ?, Location = ?, StartTime = ?, EndTime = ?, Notes = ? WHERE Id = ?";

        try (Connection conn = cm.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, event.getName());
            stmt.setString(2, event.getLocation());

            if (event.getStartTime() != null) {
                stmt.setTimestamp(3, Timestamp.valueOf(event.getStartTime()));
            } else {
                stmt.setNull(3, Types.TIMESTAMP);
            }

            if (event.getEndTime() != null) {
                stmt.setTimestamp(4, Timestamp.valueOf(event.getEndTime()));
            } else {
                stmt.setNull(4, Types.TIMESTAMP);
            }

            stmt.setString(5, event.getNotes());
            stmt.setInt(6, event.getId());

            stmt.executeUpdate();
        }
    }
}
package dk.easv.dal.dao;

import dk.easv.be.EventCoordinator;
import dk.easv.dal.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EventCoordinatorDAO {

    private final ConnectionManager connectionManager;

    public EventCoordinatorDAO() {
        connectionManager = new ConnectionManager();
    }

    public EventCoordinator createCoordinator(EventCoordinator coordinator) throws Exception {
        String sql = "INSERT INTO EventCoordinator (Name, Email, Username, Password) VALUES (?, ?, ?, ?)";

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, coordinator.getName());
            stmt.setString(2, coordinator.getEmail());
            stmt.setString(3, coordinator.getUsername());
            stmt.setString(4, coordinator.getPassword());

            stmt.executeUpdate();

            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                coordinator.setId(keys.getInt(1));
            }

            return coordinator;
        }
    }

    public List<EventCoordinator> getAllCoordinators() throws Exception {
        List<EventCoordinator> coordinators = new ArrayList<>();

        String sql = "SELECT Id, Name, Email, Username, Password FROM EventCoordinator ORDER BY Name";

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                coordinators.add(new EventCoordinator(
                        rs.getInt("Id"),
                        rs.getString("Name"),
                        rs.getString("Email"),
                        rs.getString("Username"),
                        rs.getString("Password")
                ));
            }
        }

        return coordinators;
    }

    public List<Integer> getCoordinatorIdsForEvent(int eventId) throws Exception {
        List<Integer> coordinatorIds = new ArrayList<>();

        String sql = "SELECT CoordinatorId FROM Event_EventCoordinator WHERE EventId = ?";

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, eventId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    coordinatorIds.add(rs.getInt("CoordinatorId"));
                }
            }
        }

        return coordinatorIds;
    }
    public List<String> getCoordinatorNamesForEvent(int eventId) throws Exception {
        List<String> names = new ArrayList<>();

        String sql = """
            SELECT ec.Name
            FROM Event_EventCoordinator eec
            JOIN EventCoordinator ec ON eec.CoordinatorId = ec.Id
            WHERE eec.EventId = ?
            ORDER BY ec.Name
            """;

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, eventId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    names.add(rs.getString("Name"));
                }
            }
        }

        return names;
    }

    public void replaceCoordinatorsForEvent(int eventId, List<Integer> coordinatorIds) throws Exception {
        String deleteSql = "DELETE FROM Event_EventCoordinator WHERE EventId = ?";
        String insertSql = "INSERT INTO Event_EventCoordinator (EventId, CoordinatorId) VALUES (?, ?)";

        try (Connection conn = connectionManager.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                deleteStmt.setInt(1, eventId);
                deleteStmt.executeUpdate();
            }

            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                for (Integer coordinatorId : coordinatorIds) {
                    insertStmt.setInt(1, eventId);
                    insertStmt.setInt(2, coordinatorId);
                    insertStmt.addBatch();
                }
                insertStmt.executeBatch();
            }

            conn.commit();

        } catch (Exception e) {
            throw e;
        }
    }
}
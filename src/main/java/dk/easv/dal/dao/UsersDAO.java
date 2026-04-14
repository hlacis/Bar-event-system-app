package dk.easv.dal.dao;

import dk.easv.be.Users;
import dk.easv.dal.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsersDAO {

    private final ConnectionManager connectionManager;

    public UsersDAO() {
        connectionManager = new ConnectionManager();
    }

    public Users createCoordinator(Users coordinator) throws Exception {
        String sql = "INSERT INTO Users (Name, Email, Username, Password, Role) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, coordinator.getName());
            stmt.setString(2, coordinator.getEmail());
            stmt.setString(3, coordinator.getUsername());
            stmt.setString(4, coordinator.getPassword());
            stmt.setString(5, "EventCoordinator");

            stmt.executeUpdate();

            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                coordinator.setId(keys.getInt(1));
            }

            return coordinator;
        }
    }

    public List<Users> getAllCoordinators() throws Exception {
        List<Users> coordinators = new ArrayList<>();

        String sql = """
                SELECT Id, Name, Email, Username, Password, Role
                FROM Users
                WHERE Role = 'EventCoordinator'
                ORDER BY Name
                """;

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                coordinators.add(new Users(
                        rs.getInt("Id"),
                        rs.getString("Name"),
                        rs.getString("Email"),
                        rs.getString("Username"),
                        rs.getString("Password"),
                        rs.getString("Role")
                ));
            }
        }

        return coordinators;
    }

    public List<Integer> getCoordinatorIdsForEvent(int eventId) throws Exception {
        List<Integer> coordinatorIds = new ArrayList<>();

        String sql = "SELECT UserId FROM Event_User WHERE EventId = ?";

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, eventId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    coordinatorIds.add(rs.getInt("UserId"));
                }
            }
        }

        return coordinatorIds;
    }

    public List<String> getCoordinatorNamesForEvent(int eventId) throws Exception {
        List<String> names = new ArrayList<>();

        String sql = """
                SELECT u.Name
                FROM Event_User eu
                JOIN Users u ON eu.UserId = u.Id
                WHERE eu.EventId = ?
                AND u.Role = 'EventCoordinator'
                ORDER BY u.Name
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
        String deleteSql = "DELETE FROM Event_User WHERE EventId = ?";
        String insertSql = "INSERT INTO Event_User (EventId, UserId) VALUES (?, ?)";

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

    public int getEventCountForCoordinator(int coordinatorId) throws Exception {
        String sql = "SELECT COUNT(*) AS EventCount FROM Event_User WHERE UserId = ?";

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, coordinatorId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("EventCount");
                }
            }
        }

        return 0;
    }

    public void deleteCoordinator(int coordinatorId) throws Exception {
        String deleteAssignmentsSql = "DELETE FROM Event_User WHERE UserId = ?";
        String deleteCoordinatorSql = "DELETE FROM Users WHERE Id = ? AND Role = 'EventCoordinator'";

        try (Connection conn = connectionManager.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement deleteAssignmentsStmt = conn.prepareStatement(deleteAssignmentsSql);
                 PreparedStatement deleteCoordinatorStmt = conn.prepareStatement(deleteCoordinatorSql)) {

                deleteAssignmentsStmt.setInt(1, coordinatorId);
                deleteAssignmentsStmt.executeUpdate();

                deleteCoordinatorStmt.setInt(1, coordinatorId);
                deleteCoordinatorStmt.executeUpdate();

                conn.commit();

            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
        }
    }
}
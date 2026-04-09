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
        String sql = "INSERT INTO EventCoordinator (Name, Email) VALUES (?, ?)";

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, coordinator.getName());
            stmt.setString(2, coordinator.getEmail());

            stmt.executeUpdate();

            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                coordinator.setId(keys.getInt(1));
            }

            return coordinator;
        }
    }
}
package dk.easv.dal.dao;

import dk.easv.be.Voucher;
import dk.easv.dal.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VoucherDAO {

    private ConnectionManager cm = new ConnectionManager();

    public List<Voucher> getVouchersByEvent(int eventId) throws Exception {
        List<Voucher> list = new ArrayList<>();

        String sql = "SELECT * FROM Voucher WHERE eventId = ?";

        try (Connection conn = cm.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, eventId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(new Voucher(
                        rs.getInt("id"),
                        rs.getInt("eventId"),
                        rs.getString("name"),
                        rs.getString("type"),
                        rs.getDouble("value")
                ));
            }
        }

        return list;
    }

    public void createVoucher(Voucher voucher) throws Exception {
        String sql = "INSERT INTO Voucher (eventId, name, type, value) VALUES (?, ?, ?, ?)";

        try (Connection conn = cm.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, voucher.getEventId());
            stmt.setString(2, voucher.getName());
            stmt.setString(3, voucher.getType());
            stmt.setDouble(4, voucher.getValue());

            stmt.executeUpdate();
        }
    }

    public void updateVoucher(Voucher voucher) throws Exception {
        String sql = "UPDATE Voucher SET name = ?, type = ?, value = ? WHERE id = ?";

        try (Connection conn = cm.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, voucher.getName());
            stmt.setString(2, voucher.getType());
            stmt.setDouble(3, voucher.getValue());
            stmt.setInt(4, voucher.getId());

            stmt.executeUpdate();
        }
    }

    public void deleteVoucher(int id) throws Exception {
        String sql = "DELETE FROM Voucher WHERE id = ?";

        try (Connection conn = cm.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}
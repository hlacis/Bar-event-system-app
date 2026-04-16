package dk.easv.dal.dao;

import dk.easv.be.Voucher;
import dk.easv.dal.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VoucherDAO {

    private final ConnectionManager cm = new ConnectionManager();

    public List<Voucher> getVouchersByEvent(int eventId) throws Exception {
        List<Voucher> list = new ArrayList<>();

        String sql = "SELECT * FROM Voucher WHERE eventId = ?";

        try (Connection conn = cm.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, eventId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new Voucher(
                            rs.getInt("id"),
                            rs.getInt("eventId"),
                            rs.getString("name"),
                            rs.getInt("total"),
                            rs.getInt("VouchersLeft"),
                            rs.getString("note")
                    ));
                }
            }
        }

        return list;
    }

    public void createVoucher(Voucher voucher) throws Exception {
        String sql = "INSERT INTO Voucher (eventId, name, total, VouchersLeft, note) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = cm.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, voucher.getEventId());
            stmt.setString(2, voucher.getName());
            stmt.setInt(3, voucher.getTotal());
            stmt.setInt(4, voucher.getVouchersLeft());
            stmt.setString(5, voucher.getNote());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    voucher.setId(rs.getInt(1));
                }
            }
        }
    }

    public void updateVoucher(Voucher voucher) throws Exception {
        String sql = "UPDATE Voucher SET name = ?, total = ?, VouchersLeft = ?, note = ? WHERE id = ?";

        try (Connection conn = cm.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, voucher.getName());
            stmt.setInt(2, voucher.getTotal());
            stmt.setInt(3, voucher.getVouchersLeft());
            stmt.setString(4, voucher.getNote());
            stmt.setInt(5, voucher.getId());

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
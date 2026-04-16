package dk.easv.dal.dao;

import dk.easv.be.PurchasedVoucher;
import dk.easv.dal.ConnectionManager;

import java.sql.*;

public class PurchasedVoucherDAO {

    public PurchasedVoucher createPurchasedVoucher(int eventId, int voucherTypeId, String name, String email) throws Exception {

        String sql = "INSERT INTO PurchasedVoucher (eventId, voucherTypeId, customerName, customerEmail) VALUES (?, ?, ?, ?)";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // Set values
            stmt.setInt(1, eventId);
            stmt.setInt(2, voucherTypeId);
            stmt.setString(3, name);
            stmt.setString(4, email);

            // Execute
            stmt.executeUpdate();

            // Get generated ID
            ResultSet rs = stmt.getGeneratedKeys();
            int id = 0;
            if (rs.next()) {
                id = rs.getInt(1);
            }

            // Create object
            PurchasedVoucher voucher = new PurchasedVoucher();
            voucher.setId(id);
            voucher.setEventId(eventId);
            voucher.setVoucherTypeId(voucherTypeId);
            voucher.setCustomerName(name);
            voucher.setCustomerEmail(email);

            return voucher;
        }
    }
}
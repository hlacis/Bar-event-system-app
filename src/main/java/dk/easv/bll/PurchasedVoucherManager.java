package dk.easv.bll;

import dk.easv.be.PurchasedVoucher;
import dk.easv.dal.dao.PurchasedVoucherDAO;

public class PurchasedVoucherManager {

    private PurchasedVoucherDAO dao = new PurchasedVoucherDAO();

    public PurchasedVoucher createVoucher(int eventId, int voucherTypeId, String name, String email) throws Exception {
        return dao.createPurchasedVoucher(eventId, voucherTypeId, name, email);
    }
}
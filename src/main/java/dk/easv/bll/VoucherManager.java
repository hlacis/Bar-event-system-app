package dk.easv.bll;

import dk.easv.be.Voucher;
import dk.easv.dal.dao.VoucherDAO;

import java.util.List;

public class VoucherManager {

    private VoucherDAO voucherDAO = new VoucherDAO();

    public List<Voucher> getVouchersByEvent(int eventId) throws Exception {
        return voucherDAO.getVouchersByEvent(eventId);
    }

    public void createVoucher(Voucher voucher) throws Exception {
        voucherDAO.createVoucher(voucher);
    }

    public void updateVoucher(Voucher voucher) throws Exception {
        voucherDAO.updateVoucher(voucher);
    }

    public void deleteVoucher(int id) throws Exception {
        voucherDAO.deleteVoucher(id);
    }
}
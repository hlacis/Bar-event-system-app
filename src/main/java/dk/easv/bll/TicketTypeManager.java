package dk.easv.bll;

import dk.easv.be.TicketType;
import dk.easv.dal.dao.TicketTypeDAO;

import java.util.List;

public class TicketTypeManager {

    private final TicketTypeDAO dao = new TicketTypeDAO();

    public TicketType createTicketType(TicketType ticketType) throws Exception {
        return dao.createTicketType(ticketType);
    }

    public List<TicketType> getTicketTypesByEvent(int eventId) throws Exception {
        return dao.getTicketTypesByEvent(eventId);
    }
}
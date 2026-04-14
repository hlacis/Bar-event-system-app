package dk.easv.bll;

import dk.easv.be.Ticket;

import java.util.UUID;

import dk.easv.dal.dao.TicketDAO;

public class TicketManager {

    private TicketDAO ticketDAO;

    public TicketManager() {
        ticketDAO = new TicketDAO();
    }

    public Ticket createTicket(int eventId, int ticketTypeId,
                               String name, String email) throws Exception {

        String id = UUID.randomUUID().toString();

        Ticket ticket = new Ticket(id, eventId, ticketTypeId, name, email);

        ticketDAO.saveTicket(ticket);

        return ticket;
    }

    public void createTicketWithPDF(int eventId, int ticketTypeId, String name, String email) throws Exception {

        Ticket ticket = createTicket(eventId, ticketTypeId, name, email);

        TicketPDFGenerator generator = new TicketPDFGenerator();
        generator.generatePDF(ticket);
    }
}



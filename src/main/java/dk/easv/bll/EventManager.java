package dk.easv.bll;

import dk.easv.be.Event;
import dk.easv.dal.dao.EventDAO;

import java.util.List;

public class EventManager {

    private final EventDAO eventDAO = new EventDAO();
    private final Checker checker = new Checker();

    public Event createEvent(Event event) throws Exception {
        checker.validateEvent(event);
        return eventDAO.createEvent(event);
    }

    public List<Event> getAllEvents() throws Exception {
        return eventDAO.getAllEvents();
    }

    public void deleteEvent(Event event) throws Exception {
        if (event == null) {
            throw new Exception("Event is missing.");
        }

        if (event.getId() <= 0) {
            throw new Exception("Invalid event id.");
        }

        eventDAO.deleteEvent(event.getId());
    }
}
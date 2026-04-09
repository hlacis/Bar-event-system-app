package dk.easv.bll;

import dk.easv.be.Event;
import dk.easv.be.EventCoordinator;
import dk.easv.dal.dao.EventCoordinatorDAO;
import dk.easv.dal.dao.EventDAO;

import java.util.List;

public class EventManager {

    private final EventDAO eventDAO = new EventDAO();
    private final EventCoordinatorDAO eventCoordinatorDAO = new EventCoordinatorDAO();
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

    public void updateEvent(Event event) throws Exception {
        checker.validateEvent(event);
        eventDAO.updateEvent(event);
    }

    public List<EventCoordinator> getAllCoordinators() throws Exception {
        return eventCoordinatorDAO.getAllCoordinators();

    }

    public List<Integer> getCoordinatorIdsForEvent(int eventId) throws Exception {

        return eventCoordinatorDAO.getCoordinatorIdsForEvent(eventId);
    }

    public List<String> getCoordinatorNamesForEvent(int eventId) throws Exception {
        return eventCoordinatorDAO.getCoordinatorNamesForEvent(eventId);
    }

    public void replaceCoordinatorsForEvent(int eventId, List<Integer> coordinatorIds) throws Exception {
        eventCoordinatorDAO.replaceCoordinatorsForEvent(eventId, coordinatorIds);
    }

}
package dk.easv.bll;

import dk.easv.be.Event;
import dk.easv.be.Users;
import dk.easv.dal.dao.UsersDAO;
import dk.easv.dal.dao.EventDAO;

import java.util.List;

public class EventManager {

    private final EventDAO eventDAO = new EventDAO();
    private final UsersDAO usersDAO = new UsersDAO();
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

    public List<Users> getAllCoordinators() throws Exception {
        return usersDAO.getAllCoordinators();

    }

    public List<Integer> getCoordinatorIdsForEvent(int eventId) throws Exception {

        return usersDAO.getCoordinatorIdsForEvent(eventId);
    }

    public List<String> getCoordinatorNamesForEvent(int eventId) throws Exception {
        return usersDAO.getCoordinatorNamesForEvent(eventId);
    }

    public void replaceCoordinatorsForEvent(int eventId, List<Integer> coordinatorIds) throws Exception {
        usersDAO.replaceCoordinatorsForEvent(eventId, coordinatorIds);
    }
    public int getEventCountForCoordinator(int coordinatorId) throws Exception {
        return usersDAO.getEventCountForCoordinator(coordinatorId);
    }

    public void deleteCoordinator(Users coordinator) throws Exception {
        if (coordinator == null) {
            throw new Exception("Coordinator is missing.");
        }

        if (coordinator.getId() <= 0) {
            throw new Exception("Invalid coordinator id.");
        }

        usersDAO.deleteCoordinator(coordinator.getId());
    }
    public Users createCoordinator(Users coordinator) throws Exception {
        if (coordinator == null) {
            throw new Exception("Coordinator is missing.");
        }

        if (coordinator.getName() == null || coordinator.getName().isBlank()) {
            throw new Exception("Coordinator name is required.");
        }

        if (coordinator.getEmail() == null || coordinator.getEmail().isBlank()) {
            throw new Exception("Coordinator email is required.");
        }

        if (coordinator.getUsername() == null || coordinator.getUsername().isBlank()) {
            throw new Exception("Coordinator username is required.");
        }

        if (coordinator.getPassword() == null || coordinator.getPassword().isBlank()) {
            throw new Exception("Coordinator password is required.");
        }

        return usersDAO.createCoordinator(coordinator);
    }

}
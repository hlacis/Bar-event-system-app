package dk.easv.bll;

import dk.easv.be.Event;

public class Checker {

    public void validateEvent(Event event) throws Exception {
        if (event == null) {
            throw new Exception("Event is missing.");
        }

        if (event.getName() == null || event.getName().isBlank()) {
            throw new Exception("Event name is required.");
        }

        if (event.getLocation() == null || event.getLocation().isBlank()) {
            throw new Exception("Location is required.");
        }

        if (event.getStartTime() == null) {
            throw new Exception("Start date is required.");
        }

        if (event.getEndTime() == null) {
            throw new Exception("End date is required.");
        }

        if (event.getEndTime().isBefore(event.getStartTime())) {
            throw new Exception("End date cannot be before start date.");
        }
    }
}
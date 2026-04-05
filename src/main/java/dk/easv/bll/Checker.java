package dk.easv.bll;

import dk.easv.be.Event;

public class Checker {

    public static void validateEvent(Event event) throws Exception {

        if (event.getName() == null || event.getName().isEmpty()) {
            throw new Exception("Event must have a name");
        }

        if (event.getStartTime() == null) {
            throw new Exception("Start time is required");
        }

        if (event.getEndTime() != null &&
                event.getEndTime().isBefore(event.getStartTime())) {
            throw new Exception("End time cannot be before start time");
        }
    }
}
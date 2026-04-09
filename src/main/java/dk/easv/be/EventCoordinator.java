package dk.easv.be;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class EventCoordinator {
    private int id;
    private String name;
    private String email;

    public EventCoordinator(int id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public EventCoordinator(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public String getEmail() { return email; }
}


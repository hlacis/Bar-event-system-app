package dk.easv.bll;

import dk.easv.be.Users;
import dk.easv.dal.dao.UsersDAO;

public class UserManager {

    private final UsersDAO usersDAO = new UsersDAO();

    public Users login(String username, String password) throws Exception {
        if (username == null || username.isBlank()) {
            throw new Exception("Username is required.");
        }

        if (password == null || password.isBlank()) {
            throw new Exception("Password is required.");
        }

        return usersDAO.login(username, password);
    }
}
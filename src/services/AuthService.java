package services;

import models.User;
import utils.PasswordUtils;
import java.util.List;

public class AuthService {

    public static User login(String username, String rawPassword) {
        List<User> users = FileHandler.loadUsers();

        // If we were using hashing, we would hash the input password here:
        // String hashedInput = PasswordUtils.hashPassword(rawPassword);
        // But for now, since your text file has plain passwords ('123'),
        // we compare directly. *Later we can upgrade data to hashed.*

        for (User u : users) {
            if (u.getUsername().equals(username) && u.getPassword().equals(rawPassword)) {
                return u; // Return the full User object (Polymorphism!)
            }
        }
        return null; // Login failed
    }
}
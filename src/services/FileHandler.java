package services;

import enums.FileName;
import models.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.FileWriter;
import java.io.IOException;

public class FileHandler {

    //Helper to ensure we always use the correct file path
    private static String getFilePath() {
        return FileName.USERS.getFilename();
    }

    public static List<User> loadUsers() {
        List<User> userList = new ArrayList<>();
        File file = new File(FileName.USERS.getFilename());

        // 1. Create file if missing
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs(); // Use mkdirs() (Plural) it is safer
                file.createNewFile();
            } catch (IOException e) {
                System.out.println("Could not create file: " + e.getMessage());
            }
            return userList; // Return empty list immediately, so app doesn't crash
        }

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.trim().isEmpty()) continue;

                String[] parts = line.split(",");

                // Basic validation : We need at least 6 parts (ID, User, Pass, Name, Email, Role)
                if (parts.length < 6) continue;

                // 1. Extract Common Data (Indices 0 to 4)
                String id = parts[0].trim();
                String username = parts[1].trim();
                String password = parts[2].trim();
                String name = parts[3];
                String email = parts[4];

                // 2. Extract Role from the VERY LAST position
                String role = parts[parts.length - 1];

                // 3. Logic based on Role
                if (role.equalsIgnoreCase("CUSTOMER")) {
                    // Customers must have 7 parts (Phone is at index 5)
                    if (parts.length >= 7) {
                        String phone = parts[5]; // Phone is before the Role
                        userList.add(new Customer(id, username, password, name, email, phone));
                    } else {
                        System.out.println("Skipping invalid Customer line: " + line);
                    }
                }
                else if (role.equalsIgnoreCase("ADMIN")) {
                    userList.add(new Administrator(id, username, password, name, email));
                }
                else if (role.equalsIgnoreCase("SCHEDULER")) {
                    userList.add(new Scheduler(id, username, password, name, email));
                }
                else if (role.equalsIgnoreCase("MANAGER")) {
                    userList.add(new Manager(id, username, password, name, email));
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error reading file."+ e.getMessage());
        }
        return userList;
    }

    public static boolean addUser(User user) {

        try (FileWriter fw = new FileWriter(getFilePath(), true)) {
        // Write the user data followed by a New Line character
        fw.write(user.toFileString() + "\n");

        return true;
        } catch (IOException e) {
            System.out.println("Error saving user: " + e.getMessage());
            return false;
        }
    }
    }


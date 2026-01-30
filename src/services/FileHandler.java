package services;

import enums.FileName;
import models.*;

import javax.xml.transform.Source;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
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
    // --- User Methods ---

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
                    if (parts.length >= 7) {
                        String phone = parts[5];
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
    // --- Hall Method ---

    public static List<Hall> loadHalls(){
        List<Hall> hallList = new ArrayList<>();
        File file = new File(FileName.HALLS.getFilename());

        if(!file.exists()) return hallList;
        try (Scanner scanner = new Scanner(file)){
            while (scanner.hasNextLine()){
                String line = scanner.nextLine();
                if(line.trim().isEmpty()) continue;

                String[] parts = line.split(",");
                if (parts.length <5) continue;

                String id = parts[0];
                String name = parts[1];
                double price = Double.parseDouble(parts[2]);
                int capacity = Integer.parseInt(parts[3]);
                boolean maintenance = Boolean.parseBoolean(parts[4]);

                hallList.add(new Hall(id, name, price , capacity, maintenance));

            }
        } catch (Exception e){
            System.out.println("Error loading halls: "+ e.getMessage());
        }

        return hallList;
    }


    // --- Booking Methods ---
    public static boolean saveBooking(Booking booking) {
        try (FileWriter fw = new FileWriter("data/bookings.txt", true)) {
            fw.write(booking.toFileString() + "\n");
            return true;
        } catch (IOException e) {
            System.out.println("Error saving booking: " + e.getMessage());
            return false;
        }
    }

    private static void updateBookingEmail(String oldEmail, String newEmail) {
        List<Booking> allBookings = loadBookings();
        boolean changed = false;

        for (Booking b : allBookings) {
            // Find bookings with the OLD email
            if (b.getCustomerEmail().equalsIgnoreCase(oldEmail)) {
                b.setCustomerEmail(newEmail); // Update to NEW email
                changed = true;
            }
        }

        if (changed) {
            // Rewrite the bookings file
            try (FileWriter fw = new FileWriter(FileName.BOOKINGS.getFilename(), false)) {
                for (Booking b : allBookings) {
                    fw.write(b.toFileString() + "\n");
                }
                System.out.println("Updated booking records from " + oldEmail + " to " + newEmail);
            } catch (IOException e) {
                System.out.println("Error cascading email update: " + e.getMessage());
            }
        }
    }

    public static List<Booking> loadBookings() {
        List<Booking> list = new ArrayList<>();
        File file = new File("data/bookings.txt");
        if (!file.exists()) return list;

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.trim().isEmpty()) continue;
                String[] p = line.split(",");

                // B001, email, hall, date, start, end, price, status
                if (p.length < 8) continue;

                list.add(new Booking(
                        p[0], p[1], p[2],
                        LocalDate.parse(p[3]), // Standard ISO format (YYYY-MM-DD)
                        LocalTime.parse(p[4]), // HH:MM
                        LocalTime.parse(p[5]),
                        Double.parseDouble(p[6]),
                        p[7]
                ));
            }
        } catch (Exception e) {
            System.out.println("Error loading bookings: " + e.getMessage());
        }
        return list;
    }

    // --- UPDATE  Profile METHOD ---
    public static boolean updateUser(User updatedUser) {
        List<User> allUsers = loadUsers();
        boolean found = false;

        for (int i = 0; i < allUsers.size(); i++) {
            User existingUser = allUsers.get(i);

            if (existingUser.getId().equals(updatedUser.getId())) {
                // --- CHECK IF EMAIL CHANGED ---
                String oldEmail = existingUser.getEmail();
                String newEmail = updatedUser.getEmail();

                if (!oldEmail.equalsIgnoreCase(newEmail)) {
                    // Trigger the cascading update for bookings
                    updateBookingEmail(oldEmail, newEmail);
                }
                // -----------------------------

                allUsers.set(i, updatedUser); // Replace user in memory
                found = true;
                break;
            }
        }

        if (!found) return false;

        // Rewrite User File
        try (FileWriter fw = new FileWriter(FileName.USERS.getFilename(), false)) {
            for (User u : allUsers) {
                fw.write(u.toFileString() + "\n");
            }
            return true;
        } catch (IOException e) {
            System.out.println("Error saving user: " + e.getMessage());
            return false;
        }
    }


    // --- ISSUE METHODS ---

    public static boolean saveIssue(Issue issue) {
        try (FileWriter fw = new FileWriter(FileName.ISSUES.getFilename(), true)) {
            fw.write(issue.toFileString() + "\n");
            return true;
        } catch (IOException e) {
            System.out.println("Error saving issue: " + e.getMessage());
            return false;
        }
    }

    public static List<Issue> loadIssues() {
        List<Issue> list = new ArrayList<>();
        File file = new File(FileName.ISSUES.getFilename());
        if (!file.exists()) return list;

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.trim().isEmpty()) continue;
                String[] p = line.split(",");
                if (p.length < 6) continue;

                // I001, B005, email, desc, status, date
                list.add(new Issue(p[0], p[1], p[2], p[3], p[4], LocalDate.parse(p[5])));
            }
        } catch (Exception e) {
            System.out.println("Error loading issues: " + e.getMessage());
        }
        return list;
    }

    // --- UPDATE ISSUE STATUS ---
    public static boolean updateIssue(Issue updatedIssue) {
        List<Issue> allIssues = loadIssues();
        boolean found = false;

        for (int i = 0; i < allIssues.size(); i++) {
            if (allIssues.get(i).getIssueId().equals(updatedIssue.getIssueId())) {
                allIssues.set(i, updatedIssue); // Replace with new data
                found = true;
                break;
            }
        }

        if (!found) return false;

        // Rewrite File
        try (FileWriter fw = new FileWriter(FileName.ISSUES.getFilename(), false)) {
            for (Issue issue : allIssues) {
                fw.write(issue.toFileString() + "\n");
            }
            return true;
        } catch (IOException e) {
            System.out.println("Error updating issue: " + e.getMessage());
            return false;
        }
    }

    }


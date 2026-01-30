package utils;

import enums.FileName;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class IdGenerator {

    // Call this method like: IdGenerator.generateNextId("CUSTOMER");
    public static String generateNextId(String role) {
        String prefix = getPrefix(role);
        int maxId = 0;

        File file = new File(FileName.USERS.getFilename());

        if (file.exists()) {
            try (Scanner scanner = new Scanner(file)) {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    if (line.trim().isEmpty()) continue;

                    String[] parts = line.split(",");
                    String currentId = parts[0].trim();

                    // Check if this ID starts with our prefix (e.g., "C")
                    if (currentId.startsWith(prefix)) {
                        try {
                            // Extract number (e.g., "C005" -> 5)
                            int numericPart = Integer.parseInt(currentId.substring(1));
                            if (numericPart > maxId) {
                                maxId = numericPart;
                            }
                        } catch (NumberFormatException e) {
                            // Ignore IDs that don't match the format (like old UUIDs)
                        }
                    }
                }
            } catch (FileNotFoundException e) {
                System.out.println("Error reading file for ID generation.");
            }
        }

        // Generate next ID (e.g., Prefix + 001)
        return String.format("%s%03d", prefix, maxId + 1);
    }

    private static String getPrefix(String role) {
        if (role.equalsIgnoreCase("CUSTOMER")) return "C";
        if (role.equalsIgnoreCase("ADMIN")) return "A";
        if (role.equalsIgnoreCase("MANAGER")) return "M";
        if (role.equalsIgnoreCase("SCHEDULER")) return "S";
        return "U"; // Default for Unknown
    }
}
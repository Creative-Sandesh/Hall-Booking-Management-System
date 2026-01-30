package utils;

import enums.FileName;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class IdGenerator {

    // Helper to extract the numeric part of an ID (e.g., "C005" -> 5)
    private static int extractIdNumber(String idString) {
        // Remove the first character (the letter)
        String numberPart = idString.substring(1);
        try {
            return Integer.parseInt(numberPart);
        } catch (NumberFormatException e) {
            return 0; // If ID is weird, treat it as 0
        }
    }

    public static String generateNextId(String role) {
        String prefix = "U"; // Default
        if (role.equalsIgnoreCase("CUSTOMER")) prefix = "C";
        else if (role.equalsIgnoreCase("ADMIN")) prefix = "A";
        else if (role.equalsIgnoreCase("SCHEDULER")) prefix = "S";
        else if (role.equalsIgnoreCase("MANAGER")) prefix = "M";
        else if (role.equalsIgnoreCase("ISSUE")) prefix = "I";   // <--- ADD THIS
        else if (role.equalsIgnoreCase("BOOKING")) prefix = "B";

        int maxId = 0;
        File file = new File(FileName.USERS.getFilename());

        if (file.exists()) {
            try (Scanner scanner = new Scanner(file)) {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    if (line.trim().isEmpty()) continue;

                    String[] parts = line.split(",");
                    String currentId = parts[0].trim();

                    // If we found an ID starting with our letter (e.g., "C")
                    if (currentId.startsWith(prefix)) {
                        int currentNum = extractIdNumber(currentId);
                        if (currentNum > maxId) {
                            maxId = currentNum;
                        }
                    }
                }
            } catch (FileNotFoundException e) {
                System.out.println("Error reading file for IDs");
            }
        }

        // Return next ID (e.g., Prefix + (max + 1) padded with zeros)
        return String.format("%s%03d", prefix, maxId + 1);
    }
}
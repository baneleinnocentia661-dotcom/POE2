/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.quickchat;

import java.util.Scanner;
import java.io.FileWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.UUID;

public class StoreMessage {

    private static ArrayList<String> sentMessages = new ArrayList<>();
    private static ArrayList<String> disregardedMessages = new ArrayList<>();
    private static ArrayList<String> storedMessages = new ArrayList<>();
    private static ArrayList<String> messageHashes = new ArrayList<>();
    private static ArrayList<String> messageIDs = new ArrayList<>();
    private static ArrayList<String> senders = new ArrayList<>();
    private static ArrayList<String> recipients = new ArrayList<>();
    private static ArrayList<String> messageContents = new ArrayList<>();
    
    private static String currentUser = "";
    private static String currentFirstName = "";
    private static String currentLastName = "";

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        
        System.out.println("=== REGISTRATION ===");
        
        System.out.print("Enter Username: ");
        String username = input.nextLine();
        
        boolean isValidUsername = username.contains("_") && username.length() <= 5;
        
        if (isValidUsername) {
            System.out.println("Username successfully captured.");
        } else {
            System.out.println("Username is not correctly formatted; please ensure that your username contains an underscore and is no more than five characters in length.");
            input.close();
            return;
        }
        
        System.out.print("Enter Password: ");
        String password = input.nextLine();
        
        boolean hasCapital = !password.equals(password.toLowerCase());
        boolean hasNumber = password.matches(".*\\d.*");
        boolean hasSpecial = password.matches(".*[^A-Za-z0-9].*");
        boolean isValidPassword = password.length() >= 8 && hasCapital && hasNumber && hasSpecial;
        
        if (isValidPassword) {
            System.out.println("Password successfully captured.");
        } else {
            System.out.println("Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number and a special character.");
            input.close();
            return;
        }
        
        System.out.print("Enter cellphone number: ");
        String cellphone = input.nextLine();
        
        boolean isValidCellphone = cellphone.startsWith("+") && cellphone.length() <= 13;
        
        if (isValidCellphone) {
            System.out.println("cellphone number successfully added.");
        } else {
            System.out.println("cellphone number incorrectly formatted or does not contain international code.");
            input.close();
            return;
        }
        
        System.out.println("\n=== Login ===");
        
        System.out.print("Enter username: ");
        String loginUsername = input.nextLine();
        
        System.out.print("Enter password: ");
        String loginPassword = input.nextLine();
        
        if (loginUsername.equals(username) && loginPassword.equals(password)) {
            System.out.println("The entered username and password are correct, and user is able to log in.");
            System.out.println("Login successfully.");
            currentUser = loginUsername;
            
            if (username.contains("_")) {
                String[] nameParts = username.split("_");
                currentFirstName = nameParts[0];
                currentLastName = nameParts.length > 1 ? nameParts[1] : "";
            }
            
            System.out.println("Welcome " + currentFirstName + " " + currentLastName + ", it is great to see you again.");
            
            loadStoredMessages();
            
            System.out.println("\nWelcome to quickchat.");
            
            System.out.print("How many messages do you want to send? ");
            int maxMessages = input.nextInt();
            input.nextLine();
            
            int sentMessagesCount = 0;
            boolean running = true;
            
            while (running) {
                System.out.println("\n--- MENU ---");
                System.out.println("1. Send messages");
                System.out.println("2. Show recent messages");
                System.out.println("3. Quit");
                System.out.println("4. Stored Messages Management");
                
                System.out.print("Choose an option: ");
                String option = input.nextLine();
                
                switch (option) {
                    case "1":
                        if (sentMessagesCount < maxMessages) {
                            System.out.print("Enter recipient username: ");
                            String recipient = input.nextLine();
                            
                            System.out.print("Enter your message: ");
                            String message = input.nextLine();
                            
                            String messageID = generateMessageID();
                            String messageHash = generateMessageHash(message);
                            
                            messageIDs.add(messageID);
                            messageHashes.add(messageHash);
                            sentMessages.add(message);
                            senders.add(currentUser);
                            recipients.add(recipient);
                            messageContents.add(message);
                            
                            try {
                                String jsonMessage = "{\n" +
                                        "  \"messageID\": \"" + messageID + "\",\n" +
                                        "  \"sender\": \"" + currentUser + "\",\n" +
                                        "  \"recipient\": \"" + recipient + "\",\n" +
                                        "  \"message\": \"" + escapeJson(message) + "\",\n" +
                                        "  \"hash\": \"" + messageHash + "\"\n" +
                                        "}\n";
                                
                                FileWriter file = new FileWriter("messages.json", true);
                                file.write(jsonMessage);
                                file.close();
                                
                                System.out.println("Message stored successfully.");
                                System.out.println("Message ID: " + messageID);
                                System.out.println("Message Hash: " + messageHash);
                                sentMessagesCount++;
                            } catch (IOException e) {
                                System.out.println("Error writing to file.");
                            }
                        } else {
                            System.out.println("You have reached your message limit.");
                            disregardedMessages.add("Message limit reached - message not sent");
                        }
                        break;
                        
                    case "2":
                        displayRecentMessages();
                        break;
                        
                    case "3":
                        System.out.println("Exiting quickchat.");
                        running = false;
                        break;
                        
                    case "4":
                        storedMessagesMenu(input);
                        break;
                        
                    default:
                        System.out.println("Invalid option.");
                }
            }
        } else {
            System.out.println("Username or password incorrect. Please try again.");
        }
        
        input.close();
    }
    
    private static void loadStoredMessages() {
        try {
            File file = new File("messages.json");
            if (file.exists()) {
                Scanner fileReader = new Scanner(file);
                while (fileReader.hasNextLine()) {
                    String line = fileReader.nextLine();
                    if (line.trim().startsWith("{")) {
                        storedMessages.add(line);
                        String messageID = extractValue(line, "messageID");
                        String sender = extractValue(line, "sender");
                        String recipient = extractValue(line, "recipient");
                        String message = extractValue(line, "message");
                        String hash = extractValue(line, "hash");
                        
                        if (messageID != null && !messageIDs.contains(messageID)) messageIDs.add(messageID);
                        if (sender != null && !senders.contains(sender)) senders.add(sender);
                        if (recipient != null && !recipients.contains(recipient)) recipients.add(recipient);
                        if (message != null && !messageContents.contains(message)) messageContents.add(message);
                        if (hash != null && !messageHashes.contains(hash)) messageHashes.add(hash);
                    }
                }
                fileReader.close();
                System.out.println("Loaded " + storedMessages.size() + " stored messages.");
            }
        } catch (FileNotFoundException e) {
            System.out.println("No existing messages file found.");
        }
    }
    
    private static String extractValue(String jsonLine, String key) {
        String searchKey = "\"" + key + "\": \"";
        int startIndex = jsonLine.indexOf(searchKey);
        if (startIndex != -1) {
            startIndex += searchKey.length();
            int endIndex = jsonLine.indexOf("\"", startIndex);
            if (endIndex != -1) {
                return jsonLine.substring(startIndex, endIndex);
            }
        }
        return null;
    }
    
    private static String generateMessageID() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
    
    private static String generateMessageHash(String message) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(message.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString().substring(0, 16);
        } catch (NoSuchAlgorithmException e) {
            return String.valueOf(message.hashCode());
        }
    }
    
    private static String escapeJson(String text) {
        return text.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }
    
    private static void displayRecentMessages() {
        System.out.println("\n=== Recent Messages ===");
        if (sentMessages.isEmpty()) {
            System.out.println("No messages sent yet.");
        } else {
            for (int i = 0; i < sentMessages.size(); i++) {
                System.out.println((i + 1) + ". " + sentMessages.get(i));
                System.out.println("   To: " + recipients.get(i));
                System.out.println("   ID: " + messageIDs.get(i));
                System.out.println();
            }
        }
    }
    
    private static void storedMessagesMenu(Scanner input) {
        boolean backToMain = false;
        
        while (!backToMain) {
            System.out.println("\n=== STORED MESSAGES MANAGEMENT ===");
            System.out.println("a. Display sender and recipient of all stored messages");
            System.out.println("b. Display the longest stored message");
            System.out.println("c. Search for a message ID and display recipient and message");
            System.out.println("d. Search for all messages stored for a particular recipient");
            System.out.println("e. Delete a message using the message hash");
            System.out.println("f. Display report of all stored messages");
            System.out.println("g. Back to Main Menu");
            
            System.out.print("Choose an option: ");
            String choice = input.nextLine().toLowerCase();
            
            switch (choice) {
                case "a":
                    displaySendersAndRecipients();
                    break;
                case "b":
                    displayLongestMessage();
                    break;
                case "c":
                    searchByMessageID(input);
                    break;
                case "d":
                    searchByRecipient(input);
                    break;
                case "e":
                    deleteByMessageHash(input);
                    break;
                case "f":
                    displayFullReport();
                    break;
                case "g":
                    backToMain = true;
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }
    
    private static void displaySendersAndRecipients() {
        System.out.println("\n=== SENDERS AND RECIPIENTS ===");
        if (senders.isEmpty()) {
            System.out.println("No messages stored.");
            return;
        }
        for (int i = 0; i < senders.size(); i++) {
            System.out.println("Message " + (i + 1) + ":");
            System.out.println("  Sender: " + senders.get(i));
            System.out.println("  Recipient: " + recipients.get(i));
            System.out.println();
        }
    }
    
    private static void displayLongestMessage() {
        System.out.println("\n=== LONGEST STORED MESSAGE ===");
        if (messageContents.isEmpty()) {
            System.out.println("No messages stored.");
            return;
        }
        String longestMessage = "";
        int longestIndex = -1;
        for (int i = 0; i < messageContents.size(); i++) {
            if (messageContents.get(i).length() > longestMessage.length()) {
                longestMessage = messageContents.get(i);
                longestIndex = i;
            }
        }
        if (longestIndex != -1) {
            System.out.println("Message: " + longestMessage);
            System.out.println("Length: " + longestMessage.length() + " characters");
            System.out.println("From: " + senders.get(longestIndex));
            System.out.println("To: " + recipients.get(longestIndex));
        }
    }
    
    private static void searchByMessageID(Scanner input) {
        System.out.print("\nEnter Message ID to search: ");
        String searchID = input.nextLine();
        boolean found = false;
        for (int i = 0; i < messageIDs.size(); i++) {
            if (messageIDs.get(i).equals(searchID)) {
                System.out.println("\n=== MESSAGE FOUND ===");
                System.out.println("Message ID: " + messageIDs.get(i));
                System.out.println("Recipient: " + recipients.get(i));
                System.out.println("Message: " + messageContents.get(i));
                System.out.println("Sender: " + senders.get(i));
                found = true;
                break;
            }
        }
        if (!found) {
            System.out.println("No message found with ID: " + searchID);
        }
    }
    
    private static void searchByRecipient(Scanner input) {
        System.out.print("\nEnter recipient username to search: ");
        String searchRecipient = input.nextLine();
        boolean found = false;
        System.out.println("\n=== MESSAGES FOR RECIPIENT: " + searchRecipient + " ===");
        for (int i = 0; i < recipients.size(); i++) {
            if (recipients.get(i).equalsIgnoreCase(searchRecipient)) {
                System.out.println("\nMessage " + (i + 1) + ":");
                System.out.println("  Message ID: " + messageIDs.get(i));
                System.out.println("  From: " + senders.get(i));
                System.out.println("  Message: " + messageContents.get(i));
                System.out.println("  Hash: " + messageHashes.get(i));
                found = true;
            }
        }
        if (!found) {
            System.out.println("No messages found for recipient: " + searchRecipient);
        }
    }
    
    private static void deleteByMessageHash(Scanner input) {
        System.out.print("\nEnter Message Hash to delete: ");
        String searchHash = input.nextLine();
        boolean found = false;
        for (int i = 0; i < messageHashes.size(); i++) {
            if (messageHashes.get(i).equals(searchHash)) {
                System.out.println("\n=== MESSAGE TO DELETE ===");
                System.out.println("Message ID: " + messageIDs.get(i));
                System.out.println("Message: " + messageContents.get(i));
                System.out.println("From: " + senders.get(i));
                System.out.println("To: " + recipients.get(i));
                System.out.print("Are you sure you want to delete this message? (yes/no): ");
                String confirm = input.nextLine().toLowerCase();
                if (confirm.equals("yes")) {
                    messageIDs.remove(i);
                    messageHashes.remove(i);
                    messageContents.remove(i);
                    senders.remove(i);
                    recipients.remove(i);
                    if (i < sentMessages.size()) sentMessages.remove(i);
                    if (i < storedMessages.size()) storedMessages.remove(i);
                    System.out.println("Message deleted successfully.");
                } else {
                    System.out.println("Deletion cancelled.");
                }
                found = true;
                break;
            }
        }
        if (!found) {
            System.out.println("No message found with hash: " + searchHash);
        }
    }
    
    private static void displayFullReport() {
        System.out.println("\n=== FULL STORED MESSAGES REPORT ===");
        System.out.println("Total messages: " + messageContents.size());
        System.out.println("--------------------------------------------------");
        if (messageContents.isEmpty()) {
            System.out.println("No messages to display.");
            return;
        }
        for (int i = 0; i < messageContents.size(); i++) {
            System.out.println("\nMessage #" + (i + 1));
            System.out.println("  Message ID: " + (i < messageIDs.size() ? messageIDs.get(i) : "N/A"));
            System.out.println("  Sender: " + (i < senders.size() ? senders.get(i) : "N/A"));
            System.out.println("  Recipient: " + (i < recipients.size() ? recipients.get(i) : "N/A"));
            System.out.println("  Message: " + (i < messageContents.size() ? messageContents.get(i) : "N/A"));
            System.out.println("  Hash: " + (i < messageHashes.size() ? messageHashes.get(i) : "N/A"));
            System.out.println("  Message Length: " + (i < messageContents.size() ? messageContents.get(i).length() : 0) + " characters");
            System.out.println("--------------------------------------------------");
        }
    }
}
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.storemessage;
import java.util.Scanner;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author banel
 */
public class StoreMessage {

    private static ArrayList<String> sentMessages = new ArrayList<>();
    private static ArrayList<String> disregardedMessages = new ArrayList<>();
    private static ArrayList<String> storedMessages = new ArrayList<>();
    private static ArrayList<Integer> messageHashes = new ArrayList<>();
    private static ArrayList<Integer> messageIDs = new ArrayList<>();
    
    private static String currentUsername = "";
    private static String currentPassword = "";
    private static int nextMessageID = 1;
    
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        
        System.out.println("=== REGISTRATION ===");
        System.out.print("Enter Username: ");
        String username = input.nextLine();
        
        boolean isValidUsername = username.contains("_") && username.length() <= 5;
        
        if (isValidUsername) {
            System.out.println("Username successfully captured.");
            currentUsername = username;
        } else {
            System.out.println("Username is not correctly formatted");
            input.close();
            return;
        }
        
        System.out.print("Enter Password: ");
        String password = input.nextLine();
        
        boolean hasCapital = !password.equals(password.toLowerCase());
        boolean hasNumber = password.matches(".*\\d.*");
        boolean hasSpecial = !password.matches("[A-Za-z0-9]*");
        boolean isValidPassword = password.length() >= 8 && hasCapital && hasNumber && hasSpecial;
        
        if (isValidPassword) {
            System.out.println("Password successfully captured.");
            currentPassword = password;
        } else {
            System.out.println("Password is not correctly formatted");
            input.close();
            return;
        }
        
        System.out.print("Enter cellphone number: ");
        String cellphone = input.nextLine();
        
        boolean isValidCellphone = cellphone.startsWith("+") && cellphone.length() <= 13;
        
        if (isValidCellphone) {
            System.out.println("cellphone number successfully added.");
        } else {
            System.out.println("cellphone number incorrectly formatted");
            input.close();
            return;
        }
        
        System.out.println("\n=== Login ===");
        System.out.print("Enter username: ");
        String loginUsername = input.nextLine();
        System.out.print("Enter password: ");
        String loginPassword = input.nextLine();
        
        if (loginUsername.equals(currentUsername) && loginPassword.equals(currentPassword)) {
            System.out.println("Login successfully.");
            
            String firstName = "";
            String lastName = "";
            if (currentUsername.contains("_")) {
                String[] nameParts = currentUsername.split("_");
                firstName = nameParts[0];
                lastName = nameParts.length > 1 ? nameParts[1] : "";
            }
            System.out.println("Welcome " + firstName + " " + lastName);
            
            loadMessagesFromJSON();
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
                System.out.println("4. Stored Messages");
                System.out.print("Choose an option: ");
                String option = input.nextLine();
                
                switch (option) {
                    case "1":
                        if (sentMessagesCount < maxMessages) {
                            System.out.print("Enter your message: ");
                            String message = input.nextLine();
                            if (!message.trim().isEmpty()) {
                                int messageID = nextMessageID++;
                                int messageHash = message.hashCode();
                                sentMessages.add(message);
                                messageIDs.add(messageID);
                                messageHashes.add(messageHash);
                                saveMessageToJSON(currentUsername, message, messageID, messageHash);
                                System.out.println("Message stored. ID: " + messageID + ", Hash: " + messageHash);
                                sentMessagesCount++;
                            } else {
                                System.out.println("Empty message disregarded.");
                                disregardedMessages.add("Empty message");
                            }
                        } else {
                            System.out.println("Message limit reached.");
                        }
                        break;
                    case "2":
                        displayRecentMessages();
                        break;
                    case "3":
                        System.out.println("Goodbye!");
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
            System.out.println("Login failed.");
        }
        input.close();
    }
    
    private static void saveMessageToJSON(String username, String message, int id, int hash) {
        try (FileWriter file = new FileWriter("messages.json", true)) {
            String json = "{\"id\":" + id + ",\"username\":\"" + username + "\",\"message\":\"" + message.replace("\"", "\\\"") + "\",\"hash\":" + hash + "}\n";
            file.write(json);
            storedMessages.add(username + ": " + message);
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    private static void loadMessagesFromJSON() {
        try {
            File file = new File("messages.json");
            if (!file.exists()) return;
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("\"id\"")) {
                    try {
                        int id = Integer.parseInt(line.split("\"id\":")[1].split(",")[0]);
                        String user = line.split("\"username\":\"")[1].split("\"")[0];
                        String msg = line.split("\"message\":\"")[1].split("\"")[0];
                        int hash = Integer.parseInt(line.split("\"hash\":")[1].split("}")[0]);
                        messageIDs.add(id);
                        messageHashes.add(hash);
                        sentMessages.add(msg);
                        storedMessages.add(user + ": " + msg);
                        if (id >= nextMessageID) nextMessageID = id + 1;
                    } catch (Exception e) {}
                }
            }
            reader.close();
        } catch (IOException e) {}
    }
    
    private static void displayRecentMessages() {
        if (sentMessages.isEmpty()) {
            System.out.println("No messages.");
            return;
        }
        System.out.println("\n=== Recent Messages ===");
        int start = Math.max(0, sentMessages.size() - 5);
        for (int i = start; i < sentMessages.size(); i++) {
            System.out.println("- " + sentMessages.get(i));
        }
    }
    
    private static void storedMessagesMenu(Scanner input) {
        boolean back = false;
        while (!back) {
            System.out.println("\n=== STORED MESSAGES MENU ===");
            System.out.println("a. Display sender and recipient");
            System.out.println("b. Display longest message");
            System.out.println("c. Search by ID");
            System.out.println("d. Search by recipient");
            System.out.println("e. Delete by hash");
            System.out.println("f. Full report");
            System.out.println("g. Return");
            System.out.print("Choice: ");
            String choice = input.nextLine().toLowerCase();
            switch (choice) {
                case "a": displaySendersAndRecipients(); break;
                case "b": displayLongestMessage(); break;
                case "c": searchByID(input); break;
                case "d": searchByRecipient(input); break;
                case "e": deleteByHash(input); break;
                case "f": displayFullReport(); break;
                case "g": back = true; break;
                default: System.out.println("Invalid");
            }
        }
    }
    
    private static void displaySendersAndRecipients() {
        System.out.println("\n=== SENDERS & RECIPIENTS ===");
        for (int i = 0; i < storedMessages.size(); i++) {
            String sender = storedMessages.get(i).split(":")[0];
            System.out.println((i+1) + ". Sender: " + sender + ", Recipient: " + currentUsername);
        }
    }
    
    private static void displayLongestMessage() {
        if (sentMessages.isEmpty()) {
            System.out.println("No messages.");
            return;
        }
        String longest = "";
        int index = -1;
        for (int i = 0; i < sentMessages.size(); i++) {
            if (sentMessages.get(i).length() > longest.length()) {
                longest = sentMessages.get(i);
                index = i;
            }
        }
        System.out.println("\nLongest message (" + longest.length() + " chars): " + longest);
        if (index >= 0) System.out.println("ID: " + messageIDs.get(index));
    }
    
    private static void searchByID(Scanner input) {
        System.out.print("Enter Message ID: ");
        int id = input.nextInt();
        input.nextLine();
        for (int i = 0; i < messageIDs.size(); i++) {
            if (messageIDs.get(i) == id) {
                System.out.println("Recipient: " + currentUsername);
                System.out.println("Message: " + sentMessages.get(i));
                return;
            }
        }
        System.out.println("Not found.");
    }
    
    private static void searchByRecipient(Scanner input) {
        System.out.print("Enter recipient: ");
        String recipient = input.nextLine();
        System.out.println("\nMessages for " + recipient + ":");
        for (int i = 0; i < storedMessages.size(); i++) {
            if (storedMessages.get(i).toLowerCase().contains(recipient.toLowerCase())) {
                System.out.println("- " + storedMessages.get(i));
            }
        }
    }
    
    private static void deleteByHash(Scanner input) {
        System.out.print("Enter hash to delete: ");
        int hash = input.nextInt();
        input.nextLine();
        for (int i = 0; i < messageHashes.size(); i++) {
            if (messageHashes.get(i) == hash) {
                System.out.println("Deleting: " + sentMessages.get(i));
                messageIDs.remove(i);
                messageHashes.remove(i);
                sentMessages.remove(i);
                storedMessages.remove(i);
                System.out.println("Deleted.");
                return;
            }
        }
        System.out.println("Not found.");
    }
    
    private static void displayFullReport() {
        System.out.println("\n=== FULL REPORT ===");
        System.out.println("Total messages: " + sentMessages.size());
        System.out.println("Disregarded: " + disregardedMessages.size());
        System.out.println("\nDetails:");
        for (int i = 0; i < sentMessages.size(); i++) {
            System.out.println("--- Message " + (i+1) + " ---");
            System.out.println("ID: " + messageIDs.get(i));
            System.out.println("Hash: " + messageHashes.get(i));
            System.out.println("Sender: " + currentUsername);
            System.out.println("Recipient: " + currentUsername);
            System.out.println("Message: " + sentMessages.get(i));
            System.out.println("Length: " + sentMessages.get(i).length());
        }
    }
}
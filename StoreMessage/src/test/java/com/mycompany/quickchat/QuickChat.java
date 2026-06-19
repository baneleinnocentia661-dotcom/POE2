/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */


                    package com.mycompany.quickchat;

import java.util.Scanner;
import java.io.*;
import java.util.ArrayList;

public class QuickChat {
    private static ArrayList<String> sentMessages = new ArrayList<>();
    private static ArrayList<String> disregardedMessages = new ArrayList<>();
    private static ArrayList<Integer> messageHashes = new ArrayList<>();
    private static ArrayList<Integer> messageIDs = new ArrayList<>();
    private static String currentUsername = "";
    private static int nextMessageID = 1;
    
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        
        System.out.println("=== REGISTRATION ===");
        System.out.print("Enter Username (must contain _ and max 5 chars): ");
        String username = input.nextLine();
        if (!username.contains("_") || username.length() > 5) {
            System.out.println("ERROR: Username must contain '_' and be <= 5 characters");
            return;
        }
        currentUsername = username;
        System.out.println("✓ Username accepted");
        
        System.out.print("Enter Password (8+ chars, capital, number, special): ");
        String password = input.nextLine();
        boolean validPass = password.length() >= 8 && 
                           !password.equals(password.toLowerCase()) &&
                           password.matches(".*\\d.*") &&
                           !password.matches("[A-Za-z0-9]*");
        if (!validPass) {
            System.out.println("ERROR: Password must have 8+ chars, capital, number, and special character");
            return;
        }
        System.out.println("✓ Password accepted");
        
        System.out.print("Enter Cellphone (must start with +, max 13 chars): ");
        String cellphone = input.nextLine();
        if (!cellphone.startsWith("+") || cellphone.length() > 13) {
            System.out.println("ERROR: Cellphone must start with '+' and be <= 13 characters");
            return;
        }
        System.out.println("✓ Cellphone accepted\n");
        
        System.out.println("=== LOGIN ===");
        System.out.print("Username: ");
        String loginUser = input.nextLine();
        System.out.print("Password: ");
        String loginPass = input.nextLine();
        
        if (!loginUser.equals(currentUsername) || !loginPass.equals(password)) {
            System.out.println("ERROR: Invalid username or password");
            return;
        }
        
        System.out.println("✓ Login successful!\n");
        String[] names = currentUsername.split("_");
        System.out.println("Welcome " + names[0] + " " + (names.length > 1 ? names[1] : ""));
        
        loadMessages();
        
        System.out.print("\nHow many messages can you send? ");
        int maxMessages = input.nextInt();
        input.nextLine();
        int sentCount = 0;
        
        while (true) {
            System.out.println("\n╔══════════════════════════╗");
            System.out.println("║         M E N U          ║");
            System.out.println("╠══════════════════════════╣");
            System.out.println("║ 1. Send message          ║");
            System.out.println("║ 2. Show recent messages  ║");
            System.out.println("║ 3. Stored messages menu  ║");
            System.out.println("║ 4. Exit                  ║");
            System.out.println("╚══════════════════════════╝");
            System.out.print("Choice: ");
            String choice = input.nextLine();
            
            switch (choice) {
                case "1":
                    if (sentCount < maxMessages) {
                        System.out.print("Enter your message: ");
                        String msg = input.nextLine();
                        if (msg.trim().isEmpty()) {
                            System.out.println("⚠ Empty message disregarded");
                            disregardedMessages.add("Empty message from " + currentUsername);
                        } else if (msg.length() > 500) {
                            System.out.println("⚠ Message exceeds 500 characters - disregarded");
                            disregardedMessages.add("Oversized message (" + msg.length() + " chars)");
                        } else {
                            int id = nextMessageID++;
                            int hash = msg.hashCode();
                            sentMessages.add(msg);
                            messageIDs.add(id);
                            messageHashes.add(hash);
                            saveMessage(msg, id, hash);
                            System.out.println("✓ Message stored!");
                            System.out.println("  Message ID: " + id);
                            System.out.println("  Message Hash: " + hash);
                            sentCount++;
                        }
                    } else {
                        System.out.println("⚠ You have reached your message limit!");
                    }
                    break;
                    
                case "2":
                    System.out.println("\n=== RECENT MESSAGES ===");
                    if (sentMessages.isEmpty()) {
                        System.out.println("No messages sent yet.");
                    } else {
                        int start = Math.max(0, sentMessages.size() - 5);
                        for (int i = start; i < sentMessages.size(); i++) {
                            System.out.println((i+1) + ". " + sentMessages.get(i));
                        }
                    }
                    break;
                    
                case "3":
                    storedMessagesMenu(input);
                    break;
                    
                case "4":
                    System.out.println("Goodbye!");
                    input.close();
                    return;
                    
                default:
                    System.out.println("Invalid option. Please choose 1-4.");
            }
        }
    }
    
    private static void saveMessage(String msg, int id, int hash) {
        try (FileWriter fw = new FileWriter("messages.json", true)) {
            String json = String.format("{\"id\":%d,\"user\":\"%s\",\"msg\":\"%s\",\"hash\":%d}\n",
                id, currentUsername, msg.replace("\"", "\\\""), hash);
            fw.write(json);
        } catch (IOException e) {
            System.out.println("Error saving message: " + e.getMessage());
        }
    }
    
    private static void loadMessages() {
        try {
            File f = new File("messages.json");
            if (!f.exists()) return;
            BufferedReader br = new BufferedReader(new FileReader(f));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("\"id\"")) {
                    try {
                        int id = Integer.parseInt(extractValue(line, "id"));
                        String msg = extractStringValue(line, "msg");
                        int hash = Integer.parseInt(extractValue(line, "hash"));
                        messageIDs.add(id);
                        messageHashes.add(hash);
                        sentMessages.add(msg);
                        if (id >= nextMessageID) nextMessageID = id + 1;
                    } catch (Exception e) {}
                }
            }
            br.close();
        } catch (IOException e) {}
    }
    
    private static String extractValue(String json, String key) {
        String search = "\"" + key + "\":";
        int start = json.indexOf(search);
        if (start == -1) return "0";
        start += search.length();
        int end = json.indexOf(",", start);
        if (end == -1) end = json.indexOf("}", start);
        return json.substring(start, end).trim();
    }
    
    private static String extractStringValue(String json, String key) {
        String search = "\"" + key + "\":\"";
        int start = json.indexOf(search);
        if (start == -1) return "";
        start += search.length();
        int end = json.indexOf("\"", start);
        return json.substring(start, end);
    }
    
    private static void storedMessagesMenu(Scanner input) {
        while (true) {
            System.out.println("\n╔═══════════════════════════════════════╗");
            System.out.println("║       STORED MESSAGES MENU            ║");
            System.out.println("╠═══════════════════════════════════════╣");
            System.out.println("║ a. Show all senders & recipients      ║");
            System.out.println("║ b. Show longest message               ║");
            System.out.println("║ c. Search by message ID               ║");
            System.out.println("║ d. Search by recipient                ║");
            System.out.println("║ e. Delete message by hash             ║");
            System.out.println("║ f. Display full report                ║");
            System.out.println("║ g. Return to main menu                ║");
            System.out.println("╚═══════════════════════════════════════╝");
            System.out.print("Choice: ");
            String ch = input.nextLine().toLowerCase();
            
            switch (ch) {
                case "a":
                    System.out.println("\n=== SENDERS & RECIPIENTS ===");
                    for (int i = 0; i < sentMessages.size(); i++) {
                        System.out.printf("%d. Sender: %s | Recipient: %s\n", 
                            i+1, currentUsername, currentUsername);
                    }
                    break;
                    
                case "b":
                    System.out.println("\n=== LONGEST MESSAGE ===");
                    if (sentMessages.isEmpty()) {
                        System.out.println("No messages stored.");
                    } else {
                        String longest = "";
                        int longestIndex = -1;
                        for (int i = 0; i < sentMessages.size(); i++) {
                            if (sentMessages.get(i).length() > longest.length()) {
                                longest = sentMessages.get(i);
                                longestIndex = i;
                            }
                        }
                        System.out.println("Length: " + longest.length() + " characters");
                        System.out.println("Message: " + longest);
                        if (longestIndex >= 0) {
                            System.out.println("Message ID: " + messageIDs.get(longestIndex));
                        }
                    }
                    break;
                    
                case "c":
                    System.out.print("Enter Message ID: ");
                    int id = input.nextInt();
                    input.nextLine();
                    boolean found = false;
                    for (int i = 0; i < messageIDs.size(); i++) {
                        if (messageIDs.get(i) == id) {
                            System.out.println("\n=== MESSAGE FOUND ===");
                            System.out.println("Recipient: " + currentUsername);
                            System.out.println("Message: " + sentMessages.get(i));
                            System.out.println("Hash: " + messageHashes.get(i));
                            found = true;
                            break;
                        }
                    }
                    if (!found) System.out.println("Message ID " + id + " not found.");
                    break;
                    
                case "d":
                    System.out.print("Enter recipient username: ");
                    String recip = input.nextLine();
                    System.out.println("\n=== MESSAGES FOR " + recip + " ===");
                    int count = 0;
                    for (int i = 0; i < sentMessages.size(); i++) {
                        System.out.println((i+1) + ". " + sentMessages.get(i));
                        count++;
                    }
                    if (count == 0) System.out.println("No messages found.");
                    break;
                    
                case "e":
                    System.out.print("Enter Message Hash to delete: ");
                    int hash = input.nextInt();
                    input.nextLine();
                    for (int i = 0; i < messageHashes.size(); i++) {
                        if (messageHashes.get(i) == hash) {
                            System.out.println("Message to delete: " + sentMessages.get(i));
                            System.out.print("Confirm delete? (yes/no): ");
                            String confirm = input.nextLine();
                            if (confirm.equalsIgnoreCase("yes")) {
                                messageIDs.remove(i);
                                messageHashes.remove(i);
                                sentMessages.remove(i);
                                System.out.println("✓ Message deleted successfully.");
                            } else {
                                System.out.println("Deletion cancelled.");
                            }
                            break;
                        }
                    }
                    break;
                    
                case "f":
                    System.out.println("\n╔═══════════════════════════════════════╗");
                    System.out.println("║           FULL MESSAGE REPORT          ║");
                    System.out.println("╚═══════════════════════════════════════╝");
                    System.out.println("Total Messages: " + sentMessages.size());
                    System.out.println("Disregarded Messages: " + disregardedMessages.size());
                    System.out.println("\n--- DETAILS ---");
                    for (int i = 0; i < sentMessages.size(); i++) {
                        System.out.println("\nMessage #" + (i+1));
                        System.out.println("  ID: " + messageIDs.get(i));
                        System.out.println("  Hash: " + messageHashes.get(i));
                        System.out.println("  Sender: " + currentUsername);
                        System.out.println("  Recipient: " + currentUsername);
                        System.out.println("  Message: " + sentMessages.get(i));
                        System.out.println("  Length: " + sentMessages.get(i).length() + " chars");
                    }
                    break;
                    
                case "g":
                    return;
                    
                default:
                    System.out.println("Invalid option. Please choose a-g.");
            }
        }
    }
}
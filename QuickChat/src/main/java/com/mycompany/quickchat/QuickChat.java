package com.mycompany.quickchat;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

/**
 *
 * @author banel
 */

import java.util.Scanner;
import java.io.FileWriter;
import java.io.IOException;

public class QuickChat {

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        
        // Registration Section
        System.out.println("=== REGISTRATION ===");
        
        System.out.print("Enter Username: ");
        String username = input.nextLine();
        
        // Validate username (contains underscore and max 5 characters)
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
        
        // Validate password (at least 8 chars, contains capital letter, number, special character)
        boolean hasCapital = !password.equals(password.toLowerCase());
        boolean hasNumber = password.matches(".*\\d.*");
        boolean hasSpecial = !password.matches("[A-Za-z0-9]*");
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
        
        // Validate cellphone (contains international code + and max 13 chars after code)
        boolean isValidCellphone = cellphone.startsWith("+") && cellphone.length() <= 13;
        
        if (isValidCellphone) {
            System.out.println("cellphone number successfully added.");
        } else {
            System.out.println("cellphone number incorrectly formatted or does not contain international code.");
            input.close();
            return;
        }
        
        System.out.println("\n=== Login ===");
        
        // Login attempt
        System.out.print("Enter username: ");
        String loginUsername = input.nextLine();
        
        System.out.print("Enter password: ");
        String loginPassword = input.nextLine();
        
        // Check credentials
        if (loginUsername.equals(username) && loginPassword.equals(password)) {
            System.out.println("The entered username and password are correct, and user is able to log in.");
            System.out.println("Login successfully.");
            
            // and last name from username (assuming format: first_last)
            String firstName = "";
            String lastName = "";
            if (username.contains("_")) {
                String[] nameParts = username.split("_");
                firstName = nameParts[0];
                lastName = nameParts.length > 1 ? nameParts[1] : "";
            }
            
            System.out.println("Welcome " + firstName + " " + lastName + ", it is great to see you again.");
            
            // Message System
            System.out.println("\nWelcome to quickchat.");
            
            System.out.print("How many messages do you want to send? ");
            int maxMessages = input.nextInt();
            input.nextLine();
            
            int sentMessages = 0;
            boolean running = true;
            
            while (running) {
                System.out.println("\n--- menu ---");
                System.out.println("1. Send messages");
                System.out.println("2. Show recent messages");
                System.out.println("3. Quit");
                
                System.out.print("Choose an option: ");
                String option = input.nextLine();
                
                switch (option) {
                    case "1":
                        if (sentMessages < maxMessages) {
                            System.out.print("Enter your message: ");
                            String message = input.nextLine();
                            
                            // Save message to JSON file
                            try {
                                String jsonMessage = "{\n" +
                                        "  \"username\": \"" + loginUsername + "\",\n" +
                                        "  \"message\": \"" + message + "\"\n" +
                                        "}\n";
                                
                                FileWriter file = new FileWriter("messages.json", true);
                                file.write(jsonMessage);
                                file.close();
                                
                                System.out.println("Message stored in JSON file.");
                                sentMessages++;
                            } catch (IOException e) {
                                System.out.println("Error writing to file.");
                            }
                        } else {
                            System.out.println("You have reached your message limit.");
                        }
                        break;
                        
                    case "2":
                        System.out.println("Coming soon.");
                        break;
                        
                    case "3":
                        System.out.println("Exiting quickchat.");
                        running = false;
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
}
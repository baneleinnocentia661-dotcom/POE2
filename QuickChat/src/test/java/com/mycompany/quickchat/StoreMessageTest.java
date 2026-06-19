/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package com.mycompany.quickchat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.util.ArrayList;

import java.nio.file.*;


class StoreMessageTest {
    
    
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final InputStream originalIn = System.in;
    
    
    
    
    private void deleteTestFiles() {
        try {
            Files.deleteIfExists(Paths.get("messages.json"));
        } catch (IOException e) {
            // Ignore
        }
    }
    
    // ==================== REGISTRATION TESTS ====================
    
    @Test
    @DisplayName("Username validation - valid username")
    void testValidUsername() {
        String username = "john_doe";
        assertTrue(username.contains("_"), "Username should contain underscore");
        assertTrue(username.length() <= 5, "Username should be max 5 characters");
    }
    
    @Test
    @DisplayName("Username validation - invalid username (no underscore)")
    void testInvalidUsernameNoUnderscore() {
        String username = "johndoe";
        assertFalse(username.contains("_"), "Username without underscore should be invalid");
    }
    
    @Test
    @DisplayName("Username validation - invalid username (too long)")
    void testInvalidUsernameTooLong() {
        String username = "john_doe_long";
        assertFalse(username.length() <= 5, "Username longer than 5 chars should be invalid");
    }
    
    @Test
    @DisplayName("Password validation - valid password")
    void testValidPassword() {
        String password = "Password1!";
        assertTrue(password.length() >= 8, "Password should be at least 8 characters");
        assertTrue(!password.equals(password.toLowerCase()), "Password should have uppercase letter");
        assertTrue(password.matches(".*\\d.*"), "Password should have a number");
        assertTrue(password.matches(".*[^A-Za-z0-9].*"), "Password should have special character");
    }
    
    @Test
    @DisplayName("Password validation - too short")
    void testInvalidPasswordTooShort() {
        String password = "Pass1!";
        assertFalse(password.length() >= 8, "Password less than 8 chars should be invalid");
    }
    
    @Test
    @DisplayName("Password validation - no uppercase")
    void testInvalidPasswordNoUppercase() {
        String password = "password1!";
        assertFalse(!password.equals(password.toLowerCase()), "Password without uppercase should be invalid");
    }
    
    @Test
    @DisplayName("Password validation - no number")
    void testInvalidPasswordNoNumber() {
        String password = "Password!";
        assertFalse(password.matches(".*\\d.*"), "Password without number should be invalid");
    }
    
    @Test
    @DisplayName("Password validation - no special character")
    void testInvalidPasswordNoSpecial() {
        String password = "Password1";
        assertFalse(password.matches(".*[^A-Za-z0-9].*"), "Password without special char should be invalid");
    }
    
    @Test
    @DisplayName("Cellphone validation - valid international number")
    void testValidCellphone() {
        String cellphone = "+1234567890";
        assertTrue(cellphone.startsWith("+"), "Cellphone should start with +");
        assertTrue(cellphone.length() <= 13, "Cellphone should be max 13 characters");
    }
    
    @Test
    @DisplayName("Cellphone validation - invalid (no international code)")
    void testInvalidCellphoneNoPlus() {
        String cellphone = "1234567890";
        assertFalse(cellphone.startsWith("+"), "Cellphone without + should be invalid");
    }
    
    @Test
    @DisplayName("Cellphone validation - too long")
    void testInvalidCellphoneTooLong() {
        String cellphone = "+123456789012345";
        assertFalse(cellphone.length() <= 13, "Cellphone longer than 13 chars should be invalid");
    }
    
    // ==================== MESSAGE GENERATION TESTS ====================
    
    @Test
    @DisplayName("Generate message ID - format and uniqueness")
    void testGenerateMessageID() {
        // Access private method via reflection
        String id1 = invokeGenerateMessageID();
        String id2 = invokeGenerateMessageID();
        
        assertNotNull(id1, "Message ID should not be null");
        assertEquals(8, id1.length(), "Message ID should be 8 characters");
        assertNotEquals(id1, id2, "Message IDs should be unique");
    }
    
    @Test
    @DisplayName("Generate message hash - consistency and uniqueness")
    void testGenerateMessageHash() {
        String hash1 = invokeGenerateMessageHash("Hello World");
        String hash2 = invokeGenerateMessageHash("Hello World");
        String hash3 = invokeGenerateMessageHash("Different Message");
        
        assertNotNull(hash1, "Message hash should not be null");
        assertEquals(16, hash1.length(), "Message hash should be 16 characters");
        assertEquals(hash1, hash2, "Same message should produce same hash");
        assertNotEquals(hash1, hash3, "Different messages should produce different hashes");
    }
    
    @Test
    @DisplayName("Escape JSON - special characters")
    void testEscapeJson() {
        String input = "Hello \"World\"\nNew Line";
        String escaped = invokeEscapeJson(input);
        
        assertFalse.contains("\""), "Quotes should be escaped");
        assertTrue(escaped.contains("\\\""), "Should contain escaped quotesTrue(escaped.contains("\\n"), "Should contain escaped newline");
    }
    
    // ==================== FILE OPERATIONS TESTS ====================
Test
    @DisplayName("Load stored messages - no existing file")
    void testLoadStoredMessagesNoFile() {
        // Ensure no messages file exists
        File file = new File("messages.json");
        if (file.exists()) {
            file.delete();
        }
        
        // This should not throw an exception
        invokeLoadStoredMessages();
        assertTrue(outContent.toString().contains("No existing messages file found"));
    }
    
    @Test
    @DisplayName("Extract value from JSON - valid fields")
    void testExtractValue() {
        String json = "{\"messageID\": \"abc123\", \"sender\": \"john_doe\", \"message\": \"Hello\"}";
        
        assertEquals("abc123", invokeExtractValue(json, "messageID"));
        assertEquals("john_doe", invokeExtractValue(json, "sender"));
        assertEquals("Hello", invokeExtractValue(json, "message"));
    }
    
    @Test
    @DisplayName("Extract value from JSON - nonexistent field")
    void testExtractValueNonexistent() {
        String json = "{\"messageID\": \"abc123\"}";
        assertNull(invokeExtractValue(json, "nonex }
    
    // ==================== MESSAGE MANAGEMENT TESTS ====================
    
    @Test
    @DisplayName("Display recent messages - no messages")
    void testDisplayRecentMessagesEmpty() {
        invokeDisplayRecentMessages();
        String output = outContent.toString();
        assertTrue(output.contains("No messages sent yet"));
    }
    
    @Test
    @DisplayName("Display longest message - no messages")
    void testDisplayLongestMessageEmpty() {
        invokeDisplayLongestMessage();
        String output = outContent.toStringTrue(output.contains("No messages stored"));
    }
    
    @Test
    @DisplayName("Display full report - no messages")
    void testDisplayFullReportEmpty invokeDisplayFullReport();
        String output = outContent.toString();
        assertTrue(output.contains("No messages to display"));
    }
    
    @Test
    @DisplayName("Search by message ID - not found")
    void testSearchByMessageIDNotFound() {
        invokeSearchByMessageID("nonexistent123");
        String output = outContent.toString();
        assertTrue(output.contains("No message found with ID"));
    }
    
    @Test
    @DisplayName("Search by recipient - not found")
    void testSearchByRecipient

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author banel
 */

public class MessageSystemTest {
    private static int passed = 0;
    private static int failed = 0;
    
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("     QUICKCHAT UNIT TESTS");
        System.out.println("========================================\n");
        
        // Run all tests based on the test data provided
        runTest("Test Data Message 1: Valid message 'Did you get the cake?'", 
                () -> testMessage1());
        
        runTest("Test Data Message 2: Long message 'Where are you? You are late...'", 
                () -> testMessage2());
        
        runTest("Test Data Message 3: Message 'Yohooo, I am at your gate.'", 
                () -> testMessage3());
        
        runTest("Test Data Message 4: Invalid cellphone number (missing +)", 
                () -> testMessage4());
        
        runTest("Test Data Message 5: Stored message 'Ok, I am leaving without you.'", 
                () -> testMessage5());
        
        runTest("Username validation (underscore and max 5 chars)", 
                () -> testUsernameValidation());
        
        runTest("Password validation (8+ chars, capital, number, special)", 
                () -> testPasswordValidation());
        
        runTest("Cellphone validation (starts with +)", 
                () -> testCellphoneValidation());
        
        runTest("Empty message is disregarded", 
                () -> testEmptyMessage());
        
        runTest("Message limit enforcement", 
                () -> testMessageLimit());
        
        runTest("Login with wrong credentials fails", 
                () -> testWrongLogin());
        
        runTest("JSON file persistence", 
                () -> testJSONPersistence());
        
        System.out.println("\n========================================");
        System.out.println("              TEST RESULTS");
        System.out.println("========================================");
        System.out.println("✅ PASSED: " + passed);
        System.out.println("❌ FAILED: " + failed);
        System.out.println("📊 TOTAL:  " + (passed + failed));
        System.out.println("========================================");
        
        if (failed == 0) {
            System.out.println("\n🎉 CONGRATULATIONS! ALL TESTS PASSED! 🎉");
        } else {
            System.out.println("\n⚠️ " + failed + " TEST(S) FAILED. Please check the output above.");
        }
    }
    
    private static void runTest(String testName, TestExecutor test) {
        System.out.print("▶ " + testName + " ... ");
        try {
            // Clean up before each test
            Files.deleteIfExists(Paths.get("messages.json"));
            
            if (test.execute()) {
                System.out.println("PASSED ✓");
                passed++;
            } else {
                System.out.println("FAILED ✗");
                failed++;
            }
        } catch (Exception e) {
            System.out.println("ERROR ✗ - " + e.getMessage());
            failed++;
        } finally {
            // Clean up after test
            Files.deleteIfExists(Paths.get("messages.json"));
        }
    }
    
    // Test Data Message 1: Should be stored successfully
    private static boolean testMessage1() {
        String input = "john_doe\nPassword1!\n+27834557896\njohn_doe\nPassword1!\n5\n1\nDid you get the cake?\n3\n";
        String output = runProgram(input);
        return output.contains("Message stored") && new File("messages.json").exists();
    }
    
    // Test Data Message 2: Long message should be stored
    private static boolean testMessage2() {
        String input = "jane_doe\nPassword1!\n+27838884567\njane_doe\nPassword1!\n5\n1\nWhere are you? You are late! I have asked you to be on time.\n3\n";
        String output = runProgram(input);
        return output.contains("Message stored") || output.contains("successfully");
    }
    
    // Test Data Message 3: Special characters message
    private static boolean testMessage3() {
        String input = "yoh_user\nPassword1!\n+27834484567\nyoh_user\nPassword1!\n5\n1\nYohooo, I am at your gate.\n3\n";
        String output = runProgram(input);
        return output.contains("Message stored") || output.contains("successfully");
    }
    
    // Test Data Message 4: Invalid cellphone (no + prefix) - Should be rejected
    private static boolean testMessage4() {
        String input = "dev_user\nPassword1!\n0838884567\n";
        String output = runProgram(input);
        return output.contains("incorrectly formatted") || output.contains("cellphone");
    }
    
    // Test Data Message 5: Another stored message
    private static boolean testMessage5() {
        String input = "ok_user\nPassword1!\n+27838884567\nok_user\nPassword1!\n5\n1\nOk, I am leaving without you.\n3\n";
        String output = runProgram(input);
        return output.contains("Message stored") || output.contains("successfully");
    }
    
    // Username validation test
    private static boolean testUsernameValidation() {
        // Valid username
        String validInput = "jo_n\nPassword1!\n+27831234567\n";
        String validOutput = runProgram(validInput);
        boolean validPasses = validOutput.contains("Password");
        
        // Invalid username (no underscore, too long)
        String invalidInput = "invalidusername\nPassword1!\n+27831234567\n";
        String invalidOutput = runProgram(invalidInput);
        boolean invalidFails = invalidOutput.contains("not correctly formatted");
        
        return validPasses && invalidFails;
    }
    
    // Password validation test
    private static boolean testPasswordValidation() {
        // Valid password
        String validInput = "test_user\nPassword1!\n+27831234567\n";
        String validOutput = runProgram(validInput);
        boolean validPasses = validOutput.contains("cellphone");
        
        // Invalid password (weak)
        String invalidInput = "test_user\nweak\n+27831234567\n";
        String invalidOutput = runProgram(invalidInput);
        boolean invalidFails = invalidOutput.contains("not correctly formatted");
        
        return validPasses && invalidFails;
    }
    
    // Cellphone validation test
    private static boolean testCellphoneValidation() {
        // Valid cellphone (starts with +)
        String validInput = "test_user\nPassword1!\n+27831234567\n";
        String validOutput = runProgram(validInput);
        boolean validPasses = validOutput.contains("Login");
        
        // Invalid cellphone (no +)
        String invalidInput = "test_user\nPassword1!\n0831234567\n";
        String invalidOutput = runProgram(invalidInput);
        boolean invalidFails = invalidOutput.contains("incorrectly formatted");
        
        return validPasses && invalidFails;
    }
    
    // Empty message should be disregarded
    private static boolean testEmptyMessage() {
        String input = "test_user\nPassword1!\n+27831234567\ntest_user\nPassword1!\n5\n1\n\n3\n";
        String output = runProgram(input);
        return output.toLowerCase().contains("empty") || output.toLowerCase().contains("disregard");
    }
    
    // Message limit enforcement
    private static boolean testMessageLimit() {
        String input = "limit_user\nPassword1!\n+27831234567\nlimit_user\nPassword1!\n2\n1\nMessage 1\n1\nMessage 2\n1\nMessage 3\n3\n";
        String output = runProgram(input);
        return output.toLowerCase().contains("limit") || output.toLowerCase().contains("reached");
    }
    
    // Wrong login credentials should fail
    private static boolean testWrongLogin() {
        String input = "test_user\nPassword1!\n+27831234567\nwrong_user\nWrongPass!\n";
        String output = runProgram(input);
        return output.toLowerCase().contains("incorrect") || output.toLowerCase().contains("try again");
    }
    
    // JSON file persistence
    private static boolean testJSONPersistence() throws Exception {
        Files.deleteIfExists(Paths.get("messages.json"));
        
        String input = "persist_user\nPassword1!\n+27831234567\npersist_user\nPassword1!\n5\n1\nTest persistence message\n3\n";
        runProgram(input);
        
        File jsonFile = new File("messages.json");
        boolean fileExists = jsonFile.exists() && jsonFile.length() > 0;
        
        if (fileExists) {
            String content = new String(Files.readAllBytes(Paths.get("messages.json")));
            return content.contains("Test persistence message") || content.contains("persist_user");
        }
        return false;
    }
    
    // Helper method to run the program with simulated input
    private static String runProgram(String input) {
        try {
            // Save original System.in and System.out
            InputStream originalIn = System.in;
            java.io.PrintStream originalOut = System.out;
            
            // Redirect input and output
            ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
            System.setIn(in);
            
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            System.setOut(new PrintStream(out));
            
            // Run the program
            StoreMessage.main(new String[]{});
            
            // Restore System.in and System.out
            System.setIn(originalIn);
            System.setOut(originalOut);
            
            return out.toString();
        } catch (Exception e) {
            return "";
        }
    }
    
    @FunctionalInterface
    interface TestExecutor {
        boolean execute() throws Exception;
    }
}
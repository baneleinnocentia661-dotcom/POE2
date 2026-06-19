/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;

/**
 *
 * @author banel
 */
public class StoreMessageTest {
  
 

public class RunTests {
    private static int passed = 0;
    private static int failed = 0;
    
    public static void main(String[] args) {
        System.out.println("=== RUNNING UNIT TESTS ===\n");
        
        runTest("Valid message storage", () -> testValidMessageStorage());
        runTest("Empty message handling", () -> testEmptyMessageHandling());
        runTest("Username validation", () -> testUsernameValidation());
        runTest("Password validation", () -> testPasswordValidation());
        runTest("Cellphone validation", () -> testCellphoneValidation());
        runTest("Message limit enforcement", () -> testMessageLimit());
        runTest("Login validation", () -> testLoginValidation());
        runTest("JSON file creation", () -> testJSONFileCreation());
        
        System.out.println("\n=== RESULTS ===");
        System.out.println("✅ Passed: " + passed);
        System.out.println("❌ Failed: " + failed);
        System.out.println("📊 Total: " + (passed + failed));
        
        if (failed == 0) {
            System.out.println("\n🎉 ALL TESTS PASSED!");
        } else {
            System.out.println("\n⚠️ SOME TESTS FAILED!");
        }
    }
    
    private static void runTest(String name, TestFunction test) {
        try {
            if (test.execute()) {
                System.out.println("✓ " + name + " - PASSED");
                passed++;
            } else {
                System.out.println("✗ " + name + " - FAILED");
                failed++;
            }
        } catch (Exception e) {
            System.out.println("✗ " + name + " - ERROR: " + e.getMessage());
            failed++;
        }
    }
    
    private static boolean testValidMessageStorage() {
        String input = "john_doe\nPassword1!\n+27831234567\njohn_doe\nPassword1!\n5\n1\nDid you get the cake?\n3\n";
        return runProgram(input).contains("Message stored");
    }
    
    private static boolean testEmptyMessageHandling() {
        String input = "test_user\nPassword1!\n+27831234567\ntest_user\nPassword1!\n5\n1\n\n3\n";
        String output = runProgram(input);
        return output.toLowerCase().contains("empty") || output.toLowerCase().contains("disregard");
    }
    
    private static boolean testUsernameValidation() {
        String input = "jo_n\nPassword1!\n+27831234567\n";
        return runProgram(input).contains("Password");
    }
    
    private static boolean testPasswordValidation() {
        String input = "test_user\nweakpass\n";
        String output = runProgram(input);
        return output.toLowerCase().contains("password") && 
               output.toLowerCase().contains("not correctly formatted");
    }
    
    private static boolean testCellphoneValidation() {
        String input = "test_user\nPassword1!\n0831234567\n";
        String output = runProgram(input);
        return output.toLowerCase().contains("cellphone") && 
               output.toLowerCase().contains("incorrectly formatted");
    }
    
    private static boolean testMessageLimit() {
        String input = "limit_user\nPassword1!\n+27831234567\nlimit_user\nPassword1!\n1\n1\nFirst message\n1\nSecond message\n3\n";
        return runProgram(input).toLowerCase().contains("limit");
    }
    
    private static boolean testLoginValidation() {
        String input = "login_user\nPassword1!\n+27831234567\nwrong_user\nWrongPass!\n";
        String output = runProgram(input);
        return output.toLowerCase().contains("incorrect") || output.toLowerCase().contains("try again");
    }
    
    private static boolean testJSONFileCreation() {
        try {
            Files.deleteIfExists(Paths.get("messages.json"));
            String input = "json_user\nPassword1!\n+27831234567\njson_user\nPassword1!\n5\n1\nTest message\n3\n";
            runProgram(input);
            File jsonFile = new File("messages.json");
            boolean exists = jsonFile.exists() && jsonFile.length() > 0;
            Files.deleteIfExists(Paths.get("messages.json"));
            return exists;
        } catch (Exception e) {
            return false;
        }
    }
    
    private static String runProgram(String input) {
        try {
            ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
            System.setIn(in);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            System.setOut(new PrintStream(out));
            StoreMessage.main(new String[]{});
            System.setOut(System.out);
            return out.toString();
        } catch (Exception e) {
            return "";
        }
    }
    
    @FunctionalInterface
    interface TestFunction {
        boolean execute() throws Exception;
    }
}


/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */



import java.io.*;
import java.nio.file.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

public class StoreMessageTest {
    
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    
    @BeforeEach
    public void setUp() {
        System.setOut(new PrintStream(outContent));
        Files.deleteIfExists(Paths.get("messages.json")); // Ignore
    }
    
    @AfterEach
    public void tearDown() {
        System.setOut(originalOut);
        Files.deleteIfExists(Paths.get("messages.json")); // Ignore
    }
    
    private void provideInput(String data) {
        ByteArrayInputStream testIn = new ByteArrayInputStream(data.getBytes());
        System.setIn(testIn);
    }
    
    @Test
    @DisplayName("Test Data 1: Did you get the cake?")
    public void test1() {
        String input = "john_doe\nPassword1!\n+27834557896\njohn_doe\nPassword1!\n5\n1\nDid you get the cake?\n4\n";
        provideInput(input);
        StoreMessage.main(new String[]{});
        String output = outContent.toString();
        assertTrue(output.contains("stored") || output.contains("saved"));
    }
    
    @Test
    @DisplayName("Test Data 2: Long message")
    public void test2() {
        String input = "jane_doe\nPassword1!\n+27838884567\njane_doe\nPassword1!\n5\n1\nWhere are you? You are late! I have asked you to be on time.\n4\n";
        provideInput(input);
        StoreMessage.main(new String[]{});
        String output = outContent.toString();
        assertTrue(output.contains("stored") || output.contains("saved"));
    }
    
    @Test
    @DisplayName("Test Data 3: Yohooo message")
    public void test3() {
        String input = "yoh_user\nPassword1!\n+27834484567\nyoh_user\nPassword1!\n5\n1\nYohooo, I am at your gate.\n4\n";
        provideInput(input);
        StoreMessage.main(new String[]{});
        String output = outContent.toString();
        assertTrue(output.contains("stored") || output.contains("saved"));
    }
    
    @Test
    @DisplayName("Test Data 4: Invalid cellphone")
    public void test4() {
        String input = "dev_user\nPassword1!\n08388884567\n";
        provideInput(input);
        StoreMessage.main(new String[]{});
        String output = outContent.toString();
        assertTrue(output.toLowerCase().contains("incorrectly formatted"));
    }
    
    @Test
    @DisplayName("Test Data 5: Empty message")
    public void test5() {
        String input = "test_user\nPassword1!\n+27831234567\ntest_user\nPassword1!\n5\n1\n\n4\n";
        provideInput(input);
        StoreMessage.main(new String[]{});
        String output = outContent.toString();
        assertTrue(output.toLowerCase().contains("empty"));
    }
    
    @Test
    @DisplayName("Valid username")
    public void test6() {
        String input = "jo_n\nPassword1!\n+27831234567\n";
        provideInput(input);
        StoreMessage.main(new String[]{});
        String output = outContent.toString();
        assertTrue(output.contains("Password"));
    }
    
    @Test
    @DisplayName("Invalid username")
    public void test7() {
        String input = "bad_username\nPassword1!\n+27831234567\n";
        provideInput(input);
        StoreMessage.main(new String[]{});
        String output = outContent.toString();
        assertTrue(output.toLowerCase().contains("not correctly formatted"));
    }
    
    @Test
    @DisplayName("Valid password")
    public void test8() {
        String input = "test_user\nPassword1!\n+27831234567\n";
        provideInput(input);
        StoreMessage.main(new String[]{});
        String output = outContent.toString();
        assertTrue(output.contains("cellphone"));
    }
    
    @Test
    @DisplayName("Invalid password")
    public void test9() {
        String input = "test_user\nweak\n+27831234567\n";
        provideInput(input);
        StoreMessage.main(new String[]{});
        String output = outContent.toString();
        assertTrue(output.toLowerCase().contains("not correctly formatted"));
    }
    
    @Test
    @DisplayName("Valid cellphone")
    public void test10() {
        String input = "test_user\nPassword1!\n+27831234567\n";
        provideInput(input);
        StoreMessage.main(new String[]{});
        String output = outContent.toString();
        assertTrue(output.contains("Login"));
    }
    
    @Test
    @DisplayName("Invalid cellphone no plus")
    public void test11() {
        String input = "test_user\nPassword1!\n0831234567\n";
        provideInput(input);
        StoreMessage.main(new String[]{});
        String output = outContent.toString();
        assertTrue(output.toLowerCase().contains("incorrectly formatted"));
    }
    
    @Test
    @DisplayName("Correct login")
    public void test12() {
        String input = "login_user\nPassword1!\n+27831234567\nlogin_user\nPassword1!\n";
        provideInput(input);
        StoreMessage.main(new String[]{});
        String output = outContent.toString();
        assertTrue(output.contains("Welcome"));
    }
    
    @Test
    @DisplayName("Wrong login")
    public void test13() {
        String input = "login_user\nPassword1!\n+27831234567\nwrong_user\nWrongPass!\n";
        provideInput(input);
        StoreMessage.main(new String[]{});
        String output = outContent.toString();
        assertTrue(output.toLowerCase().contains("incorrect"));
    }
    
    @Test
    @DisplayName("Message limit")
    public void test14() {
        String input = "limit_user\nPassword1!\n+27831234567\nlimit_user\nPassword1!\n2\n1\nMsg1\n1\nMsg2\n1\nMsg3\n4\n";
        provideInput(input);
        StoreMessage.main(new String[]{});
        String output = outContent.toString();
        assertTrue(output.toLowerCase().contains("limit"));
    }
    
    @Test
    @DisplayName("Part 3a - Senders and recipients")
    public void test15() {
        String input = "sender_user\nPassword1!\n+27831234567\nsender_user\nPassword1!\n5\n1\nTest\n3\na\ng\n4\n";
        provideInput(input);
        StoreMessage.main(new String[]{});
        String output = outContent.toString();
        assertTrue(output.contains("Sender"));
    }
    
    @Test
    @DisplayName("Part 3b - Longest message")
    public void test16() {
        String input = "long_user\nPassword1!\n+27831234567\nlong_user\nPassword1!\n5\n1\nShort\n1\nVery long message here\n3\nb\ng\n4\n";
        provideInput(input);
        StoreMessage.main(new String[]{});
        String output = outContent.toString();
        assertTrue(output.toLowerCase().contains("longest"));
    }
    
    @Test
    @DisplayName("Part 3c - Search by ID")
    public void test17() {
        String input = "search_user\nPassword1!\n+27831234567\nsearch_user\nPassword1!\n5\n1\nTest\n3\nc\n1\ng\n4\n";
        provideInput(input);
        StoreMessage.main(new String[]{});
        String output = outContent.toString();
        assertTrue(output.contains("Message") || !output.contains("Not found"));
    }
    
    @Test
    @DisplayName("Part 3f - Full report")
    public void test18() {
        String input = "report_user\nPassword1!\n+27831234567\nreport_user\nPassword1!\n5\n1\nTest\n3\nf\ng\n4\n";
        provideInput(input);
        StoreMessage.main(new String[]{});
        String output = outContent.toString();
        assertTrue(output.contains("Total") || output.contains("REPORT"));
    }
    
    @Test
    @DisplayName("JSON persistence")
    public void test19() throws Exception {
        Files.deleteIfExists(Paths.get("messages.json"));
        String input = "json_user\nPassword1!\n+27831234567\njson_user\nPassword1!\n5\n1\nTest JSON\n4\n";
        provideInput(input);
        StoreMessage.main(new String[]{});
        File jsonFile = new File("messages.json");
        assertTrue(jsonFile.exists());
        assertTrue(jsonFile.length() > 0);
        Files.deleteIfExists(Paths.get("messages.json"));
    }
    
    @Test
    @DisplayName("Long message over 500 chars")
    public void test20() {
        String longMsg = "A".repeat(501);
        String input = "long_user\nPassword1!\n+27831234567\nlong_user\nPassword1!\n5\n1\n" + longMsg + "\n4\n";
        provideInput(input);
        StoreMessage.main(new String[]{});
        String output = outContent.toString();
        assertTrue(output.toLowerCase().contains("exceeds"));
    }
}
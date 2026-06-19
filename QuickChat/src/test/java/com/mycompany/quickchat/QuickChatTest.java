/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 package*/
package com.mycompany.quickchat;

import org.junit.jupiter.api.io.TempDir;
import java.io.*;
import java.nio.file.*;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
/**
 *
 * @author banel
 */
public class QuickChatTest {
    
private final InputStream originalSystemIn = System.in;
    private final PrintStream originalSystemOut = System.out;

    private ByteArrayOutputStream outputStream;

    @TempDir
    Path tempDir;

    private String messagesFilePath;

    @BeforeEach
    void setUp() {

        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        messagesFilePath =
                tempDir.resolve("messages.json").toString();

        System.setProperty(
                "messages.file.path",
                messagesFilePath
        );
    }

    @AfterEach
    void tearDown() {

        System.setIn(originalSystemIn);
        System.setOut(originalSystemOut);
    }

    private void provideInput(String data) {

        ByteArrayInputStream testInput =
                new ByteArrayInputStream(data.getBytes());

        System.setIn(testInput);
    }

    // =========================
    // TEST 1 - Login Success
    // =========================
    @Test
    void testLoginSuccess() {

        String input =
                "john\n" +
                "1234\n" +
                "1\n" +
                "3\n";

        provideInput(input);

        QuickChat.main(new String[]{});

        String output = outputStream.toString();

        assertTrue(output.contains("Login successfully"));
    }

    // =========================
    // TEST 2 - Send Message
    // =========================
    @Test
    void testSendMessage() throws IOException {

        String input =
                "john\n" +
                "1234\n" +
                "2\n" +
                "1\n" +
                "Hello World\n" +
                "3\n";

        provideInput(input);

        QuickChat.main(new String[]{});

        String output = outputStream.toString();

        assertTrue(output.contains("Message stored in JSON file"));

        String content =
                Files.readString(Paths.get(messagesFilePath));

        assertTrue(content.contains("Hello World"));
    }

    // =========================
    // TEST 3 - Maximum Messages
    // =========================
    @Test
    void testMaximumMessagesLimit() {

        String input =
                "user\n" +
                "pass\n" +
                "1\n" +
                "1\n" +
                "First Message\n" +
                "1\n" +
                "Second Message\n" +
                "3\n";

        provideInput(input);

        QuickChat.main(new String[]{});

        String output = outputStream.toString();

        assertTrue(output.contains("you have reached your messages"));
    }

    // =========================
    // TEST 4 - Invalid Menu
    // =========================
    @Test
    void testInvalidMenuOption() {

        String input =
                "user\n" +
                "pass\n" +
                "1\n" +
                "9\n" +
                "3\n";

        provideInput(input);

        QuickChat.main(new String[]{});

        String output = outputStream.toString();

        assertTrue(output.contains("Invalid option"));
    }

    // =========================
    // TEST 5 - Quit Program
    // =========================
    @Test
    void testQuitProgram() {

        String input =
                "user\n" +
                "pass\n" +
                "1\n" +
                "3\n";

        provideInput(input);

        QuickChat.main(new String[]{});

        String output = outputStream.toString();

        assertTrue(output.contains("exiting quickchat"));
    }

    // =========================
    // TEST 6 - Empty Message
    // =========================
    @Test
    void testEmptyMessage() throws IOException {

        String input =
                "user\n" +
                "pass\n" +
                "1\n" +
                "1\n" +
                "\n" +
                "3\n";

        provideInput(input);

        QuickChat.main(new String[]{});

        String content =
                Files.readString(Paths.get(messagesFilePath));

        assertTrue(content.contains("\"message\": \"\""));
    }

    // =========================
    // TEST 7 - Special Characters
    // =========================
    @Test
    void testSpecialCharactersMessage() throws IOException {

        String input =
                "user\n" +
                "pass\n" +
                "1\n" +
                "1\n" +
                "@#$%^&*()\n" +
                "3\n";

        provideInput(input);

        QuickChat.main(new String[]{});

        String content =
                Files.readString(Paths.get(messagesFilePath));

        assertTrue(content.contains("@#$%^&*()"));
    }

    // =========================
    // TEST 8 - Show Messages Feature
    // =========================
    @Test
    void testShowMessagesFeature() {

        String input =
                "user\n" +
                "pass\n" +
                "2\n" +
                "2\n" +
                "3\n";

        provideInput(input);

        QuickChat.main(new String[]{});

        String output = outputStream.toString();

    }
}

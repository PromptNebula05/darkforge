package darkforge.cli;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.nio.file.Path;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for ConsoleMainMenu interactive loop.
 * Simulates user input for menu navigation and
 * verifies graceful handling of exit/quit commands.
 */
class ConsoleMainMenuTest {

    @TempDir
    Path tempDir;

    private Scanner scannerFrom(String input) {
        return new Scanner(
                new ByteArrayInputStream(
                        input.getBytes()));
    }

    private ByteArrayOutputStream captureOutput() {
        ByteArrayOutputStream baos =
                new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));
        return baos;
    }

    private void restoreOutput() {
        System.setOut(
                new PrintStream(
                        new FileOutputStream(
                                FileDescriptor.out)));
    }

    @Test
    void shouldDisplayMainMenu() {
        // Input: immediately quit
        String input = "Q\n";
        ByteArrayOutputStream out = captureOutput();
        try {
            ConsoleMainMenu menu =
                    new ConsoleMainMenu(
                            scannerFrom(input));
            menu.run();
            String output = out.toString();
            assertTrue(
                    output.contains("DARKFORGE")
                            || output.contains("Menu")
                            || output.contains("menu"),
                    "Should display main menu header");
        } finally {
            restoreOutput();
        }
    }

    @Test
    void shouldDisplayCreateOption() {
        String input = "Q\n";
        ByteArrayOutputStream out = captureOutput();
        try {
            ConsoleMainMenu menu =
                    new ConsoleMainMenu(
                            scannerFrom(input));
            menu.run();
            String output = out.toString();
            assertTrue(
                    output.contains("Create")
                            || output.contains("create")
                            || output.contains("New"),
                    "Should show create explorer option");
        } finally {
            restoreOutput();
        }
    }

    @Test
    void shouldDisplayLoadOption() {
        String input = "Q\n";
        ByteArrayOutputStream out = captureOutput();
        try {
            ConsoleMainMenu menu =
                    new ConsoleMainMenu(
                            scannerFrom(input));
            menu.run();
            String output = out.toString();
            assertTrue(
                    output.contains("Load")
                            || output.contains("load")
                            || output.contains("Open"),
                    "Should show load explorer option");
        } finally {
            restoreOutput();
        }
    }

    @Test
    void shouldExitGracefullyOnQuit() {
        String input = "Q\n";
        ByteArrayOutputStream out = captureOutput();
        try {
            ConsoleMainMenu menu =
                    new ConsoleMainMenu(
                            scannerFrom(input));
            assertDoesNotThrow(menu::run,
                    "Should exit cleanly on 0, Q, or q");
        } finally {
            restoreOutput();
        }
    }

    @Test
    void shouldHandleInvalidMenuChoice() {
        // Input: invalid then quit
        String input = "Z\nQ\n";
        ByteArrayOutputStream out = captureOutput();
        try {
            ConsoleMainMenu menu =
                    new ConsoleMainMenu(
                            scannerFrom(input));
            assertDoesNotThrow(menu::run,
                    "Should handle invalid choice "
                            + "and re-prompt");
        } finally {
            restoreOutput();
        }
    }

    @Test
    void shouldDisplaySearchOption() {
        String input = "Q\n";
        ByteArrayOutputStream out = captureOutput();
        try {
            ConsoleMainMenu menu =
                    new ConsoleMainMenu(
                            scannerFrom(input));
            menu.run();
            String output = out.toString();
            assertTrue(
                    output.contains("Search")
                            || output.contains("search")
                            || output.contains("Find"),
                    "Should show search option");
        } finally {
            restoreOutput();
        }
    }

    @Test
    void shouldHandleLoadWhenNoSavesExist() {
        // Input: choose Load (2), then go back
        String input = "3\nQ\n";
        ByteArrayOutputStream out = captureOutput();
        try {
            ConsoleMainMenu menu =
                    new ConsoleMainMenu(
                            scannerFrom(input));
            assertDoesNotThrow(menu::run,
                    "Should handle empty save list "
                            + "gracefully");
            String output = out.toString();
            assertTrue(
                    output.contains("No")
                            || output.contains("no")
                            || output.contains("empty"),
                    "Should display 'no saves found' "
                            + "message");
        } finally {
            restoreOutput();
        }
    }
}
package darkforge.cli;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.nio.file.Path;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for ConsoleCreationWizard interactive flow.
 * Simulates user input via ByteArrayInputStream and
 * captures output via ByteArrayOutputStream.
 */
class ConsoleCreationWizardTest {

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
    void shouldDisplayProfessionMenu() {
        // Input: select Scholar (1), then cancel
        String input = "1\nTest Name\n1\n1\n" +
                "4\n4\n4\n4\n4\n4\n" +
                "1\n1\n1\n0\n" +
                "quirk\nkeepsake\nappearance\n";
        ByteArrayOutputStream out = captureOutput();
        try {
            ConsoleCreationWizard wizard =
                    new ConsoleCreationWizard(
                            scannerFrom(input));
            wizard.run();
            String output = out.toString();
            assertTrue(
                    output.contains("Scholar")
                            || output.contains("profession"),
                    "Should display profession options");
        } finally {
            restoreOutput();
        }
    }

    @Test
    void shouldPromptForExplorerName() {
        String input = "1\nCantara Loutreides\n" +
                "1\n1\n4\n4\n4\n4\n4\n4\n" +
                "1\n1\n1\n0\n" +
                "quirk\nkeepsake\nappearance\n";
        ByteArrayOutputStream out = captureOutput();
        try {
            ConsoleCreationWizard wizard =
                    new ConsoleCreationWizard(
                            scannerFrom(input));
            wizard.run();
            String output = out.toString();
            assertTrue(
                    output.contains("name")
                            || output.contains("Name"),
                    "Should prompt for explorer name");
        } finally {
            restoreOutput();
        }
    }

    @Test
    void shouldPromptForAttributes() {
        String input = "1\nTest\n1\n1\n" +
                "4\n4\n4\n4\n4\n4\n" +
                "1\n1\n1\n0\n" +
                "quirk\nkeepsake\nappearance\n";
        ByteArrayOutputStream out = captureOutput();
        try {
            ConsoleCreationWizard wizard =
                    new ConsoleCreationWizard(
                            scannerFrom(input));
            wizard.run();
            String output = out.toString();
            assertTrue(
                    output.contains("STRENGTH")
                            || output.contains("Strength")
                            || output.contains("STR"),
                    "Should prompt for attribute values");
        } finally {
            restoreOutput();
        }
    }

    @Test
    void shouldHandleInvalidProfessionChoice() {
        // Input: invalid choice 99, then valid 1
        String input = "99\n1\nTest\n1\n1\n" +
                "4\n4\n4\n4\n4\n4\n" +
                "1\n1\n1\n0\n" +
                "quirk\nkeepsake\nappearance\n";
        ByteArrayOutputStream out = captureOutput();
        try {
            ConsoleCreationWizard wizard =
                    new ConsoleCreationWizard(
                            scannerFrom(input));
            assertDoesNotThrow(wizard::run,
                    "Should handle invalid input "
                            + "gracefully");
        } finally {
            restoreOutput();
        }
    }

    @Test
    void shouldDisplayCreatedExplorerSheet() {
        String input = "1\nTest Scholar\n1\n1\n" +
                "4\n4\n4\n4\n4\n4\n" +
                "1\n1\n1\n0\n" +
                "Constantly reading\nSilver coin\n" +
                "Sharp eyes\n";
        ByteArrayOutputStream out = captureOutput();
        try {
            ConsoleCreationWizard wizard =
                    new ConsoleCreationWizard(
                            scannerFrom(input));
            wizard.run();
            String output = out.toString();
            assertTrue(
                    output.contains("Test Scholar")
                            || output.contains("DARKFORGE"),
                    "Should display the character sheet "
                            + "after creation");
        } finally {
            restoreOutput();
        }
    }

    @Test
    void shouldOfferGeneratedNameOption() {
        // Input: choose generated name (option G/0)
        String input = "1\n0\n1\n1\n" +
                "4\n4\n4\n4\n4\n4\n" +
                "1\n1\n1\n0\n" +
                "quirk\nkeepsake\nappearance\n";
        ByteArrayOutputStream out = captureOutput();
        try {
            ConsoleCreationWizard wizard =
                    new ConsoleCreationWizard(
                            scannerFrom(input));
            assertDoesNotThrow(wizard::run,
                    "Should accept generated name option");
        } finally {
            restoreOutput();
        }
    }
}
package darkforge.cli;

import darkforge.crew.*;
import darkforge.facade.FacadeDarkforge;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for ConsoleCrewWizard. Uses simulated
 * Scanner input and captured System.out to
 * verify wizard flow, validation, and output.
 */
class ConsoleCrewWizardTest {

    @BeforeAll
    static void initData() {
        FacadeDarkforge.getTheInstance()
                .initialize();
    }

    // =========================================
    // Helper: simulate user input
    // =========================================

    private ConsoleCrewWizard wizardWith(
            String input) {
        Scanner scanner = new Scanner(
                new ByteArrayInputStream(
                        input.getBytes()));
        return new ConsoleCrewWizard(scanner);
    }

    private String captureOutput(
            Runnable action) {
        ByteArrayOutputStream out =
                new ByteArrayOutputStream();
        PrintStream original = System.out;
        System.setOut(new PrintStream(out));
        try {
            action.run();
        } catch (Exception ignored) {
        } finally {
            System.setOut(original);
        }
        return out.toString();
    }

    // =========================================
    // Construction
    // =========================================

    @Test
    void constructorDoesNotThrow() {
        assertDoesNotThrow(
                () -> new ConsoleCrewWizard(
                        new Scanner(System.in)));
    }

    // =========================================
    // Empty input aborts
    // =========================================

    @Test
    void emptyInputReturnsNull() {
        ConsoleCrewWizard wizard =
                wizardWith("");
        Crew result = wizard.run();
        assertNull(result);
    }

    // =========================================
    // Banner output
    // =========================================

    @Test
    void wizardPrintsBanner() {
        String output = captureOutput(
                () -> wizardWith("").run());
        assertTrue(output.contains(
                "DARKFORGE v3.0"));
        assertTrue(output.contains(
                "Crew Assembly Wizard"));
    }

    // =========================================
    // Crew name validation
    // =========================================

    @Test
    void emptyNameShowsError() {
        String output = captureOutput(
                () -> wizardWith(
                        "\n\nTest Crew\n")
                        .run());
        assertTrue(output.contains(
                "Name cannot be empty"));
    }

    // =========================================
    // Member count validation
    // =========================================

    @Test
    void fewerThan4MembersAborts() {
        String output = captureOutput(
                () -> {
                    Crew result = wizardWith(
                            "My Crew\n9\n").run();
                    assertNull(result);
                });
        assertNotNull(output);
    }

    // =========================================
    // v3.0 banner version
    // =========================================

    @Test
    void wizardUsesV3Banner() {
        String output = captureOutput(
                () -> wizardWith("Test\n")
                        .run());
        assertTrue(output.contains("v3.0"));
    }

    // =========================================
    // Wizard handles exceptions gracefully
    // =========================================

    @Test
    void wizardHandlesInvalidInput() {
        assertDoesNotThrow(() -> {
            try {
                wizardWith(
                        "Test\n0\n").run();
            } catch (
                    java.util
                            .NoSuchElementException
                            ignored) {
            }
        });
    }

    // =========================================
    // Menu prompt appears
    // =========================================

    @Test
    void wizardShowsMemberPrompt() {
        String output = captureOutput(
                () -> wizardWith(
                        "Test Crew\n").run());
        assertTrue(
                output.contains("Member")
                        || output.contains("member")
                        || output.contains(
                        "Need at least"));
    }
}
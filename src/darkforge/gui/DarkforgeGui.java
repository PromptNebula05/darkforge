package darkforge.gui;

import darkforge.concurrency.BackgroundTask;
import darkforge.crew.Crew;
import darkforge.exception
        .CharacterCorruptionException;
import darkforge.facade.FacadeDarkforge;
import darkforge.persistence
        .BenchmarkResult;
import darkforge.persistence
        .SerializationBenchmark;

import javax.swing.*;
import javax.swing.filechooser
        .FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Main GUI frame for DARKFORGE. Hosts a
 * tabbed interface with the catalog
 * browser, inventory manager, vehicle
 * upgrades, and dice simulator, and a
 * menu bar with File (Open Crew...,
 * Exit) and Tools (Run Serialization
 * Benchmark...).
 *
 * The serialization benchmark runs off
 * the EDT through a
 * BackgroundTask<BenchmarkResult> and
 * renders its summary in a JOptionPane
 * when complete.
 */
public class DarkforgeGui extends JFrame {

    // =========================================
    // Fields
    // =========================================

    private final InventoryPanel
            inventoryPanel;
    private final VehicleUpgradePanel
            vehicleUpgradePanel;

    // =========================================
    // Constructor
    // =========================================

    public DarkforgeGui() {
        super("DARKFORGE v5.0 — Equipment"
                + " Manager");
        setDefaultCloseOperation(
                EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        // Initialize data
        FacadeDarkforge.getTheInstance()
                .initialize();

        // Construct wired panels as fields
        // so the menu handler can push the
        // loaded Crew into them.
        inventoryPanel = new InventoryPanel();
        vehicleUpgradePanel =
                new VehicleUpgradePanel();

        // Tabbed pane
        JTabbedPane tabs =
                new JTabbedPane();
        tabs.addTab("🔍 Catalog",
                new CatalogBrowserPanel());
        tabs.addTab("🎒 Inventory",
                inventoryPanel);
        tabs.addTab("⚙ Vehicle Upgrades",
                vehicleUpgradePanel);
        tabs.addTab("🎲 Dice Simulator",
                new DiceSimulatorPanel());
        add(tabs);

        // Menu bar
        setJMenuBar(buildMenuBar());
    }

    // =========================================
    // Menu bar
    // =========================================

    /**
     * Build the application menu bar.
     * File → Open Crew... loads a saved
     * crew file and pushes the resulting
     * Crew into both wired panels.
     * Tools → Run Serialization
     * Benchmark... runs the persistence
     * benchmark off the EDT and shows
     * the result in a dialog.
     */
    private JMenuBar buildMenuBar() {
        JMenuBar bar = new JMenuBar();

        // File menu
        JMenu fileMenu = new JMenu("File");
        JMenuItem openCrew =
                new JMenuItem("Open Crew...");
        openCrew.addActionListener(
                e -> openCrewDialog());
        fileMenu.add(openCrew);
        fileMenu.addSeparator();
        JMenuItem exit =
                new JMenuItem("Exit");
        exit.addActionListener(
                e -> dispose());
        fileMenu.add(exit);
        bar.add(fileMenu);

        // Tools menu
        JMenu toolsMenu = new JMenu("Tools");
        JMenuItem benchmarkItem =
                new JMenuItem(
                        "Run Serialization"
                                + " Benchmark...");
        benchmarkItem.addActionListener(
                e -> runBenchmarkAsync(
                        benchmarkItem));
        toolsMenu.add(benchmarkItem);
        bar.add(toolsMenu);

        return bar;
    }

    // =========================================
    // Open Crew dialog
    // =========================================

    /**
     * Show a JFileChooser over the user's
     * saved-crew directory, load the chosen
     * file via FacadeCrew.loadCrew(Path),
     * and push the resulting Crew into the
     * InventoryPanel and VehicleUpgradePanel.
     *
     * INTENT:        Provide a GUI-side
     *                analog to the CLI's
     *                crew-load workflow, so
     *                the wired tabs always
     *                operate on a real Crew.
     * EXAMPLE:       File → Open Crew… →
     *                select sirocco_vanguard
     *                .json → panels show
     *                that crew's explorers
     *                and vehicles.
     * DEFINITIONS:   savesDir — the same
     *                  directory the CLI uses
     *                  (~/.darkforge/saves).
     * PRECONDITIONS: User has at least one
     *                crew save file on disk
     *                (otherwise the chooser
     *                will appear empty).
     * POSTCONDITIONS: On success, both wired
     *                 panels hold the loaded
     *                 Crew and the window
     *                 title reflects its
     *                 name; on failure, an
     *                 error dialog is shown
     *                 and panel state is
     *                 unchanged.
     */
    private void openCrewDialog() {
        Path savesDir = Path.of(
                System.getProperty("user.home"),
                ".darkforge", "saves");
        JFileChooser chooser =
                new JFileChooser(
                        savesDir.toFile());
        chooser.setDialogTitle("Open Crew");
        chooser.setFileFilter(
                new FileNameExtensionFilter(
                        "Crew save files"
                                + " (*.bin, *.json)",
                        "bin", "json"));
        int result =
                chooser.showOpenDialog(this);
        if (result
                != JFileChooser
                .APPROVE_OPTION) {
            return;
        }
        File selected =
                chooser.getSelectedFile();
        try {
            Crew loaded = FacadeDarkforge
                    .getTheInstance()
                    .crewAccess()
                    .loadCrew(
                            selected.toPath());
            inventoryPanel.setCrew(loaded);
            vehicleUpgradePanel
                    .setCrew(loaded);
            setTitle(
                    "DARKFORGE v5.0 —"
                            + " Equipment Manager"
                            + " — "
                            + loaded.getName());
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Load failed: "
                            + ex.getMessage(),
                    "Open Crew",
                    JOptionPane.ERROR_MESSAGE);
        } catch (CharacterCorruptionException
                ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "⚠ " + ex.getUserMessage()
                            + "\n\nChoose a"
                            + " different file.",
                    "Open Crew",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    // =========================================
    // Serialization benchmark runner
    // =========================================

    /**
     * Run the persistence benchmark off
     * the EDT and render the resulting
     * BenchmarkResult summary in a dialog.
     * The triggering menu item is disabled
     * while the task runs and re-enabled
     * from onResult / onError on the EDT.
     */
    private void runBenchmarkAsync(
            JMenuItem trigger) {
        trigger.setEnabled(false);

        new BackgroundTask<BenchmarkResult>() {
            @Override
            protected BenchmarkResult compute()
                    throws Exception {
                return new SerializationBenchmark()
                        .run();
            }
        }
                .onResult(result -> {
                    JOptionPane.showMessageDialog(
                            DarkforgeGui.this,
                            result.summary(),
                            "Serialization Benchmark",
                            JOptionPane
                                    .INFORMATION_MESSAGE);
                    trigger.setEnabled(true);
                })
                .onError(err -> {
                    JOptionPane.showMessageDialog(
                            DarkforgeGui.this,
                            "Benchmark failed: "
                                    + err.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    trigger.setEnabled(true);
                })
                .execute();
    }

    // =========================================
    // Main
    // =========================================

    public static void main(
            String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager
                        .setLookAndFeel(
                                UIManager
                                        .getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            new DarkforgeGui()
                    .setVisible(true);
        });
    }
}
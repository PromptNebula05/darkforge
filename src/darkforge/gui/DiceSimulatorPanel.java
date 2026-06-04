package darkforge.gui;

import darkforge.concurrency.BackgroundTask;
import darkforge.concurrency
        .ProbabilityTable;
import darkforge.facade.FacadeConcurrency;
import darkforge.facade.FacadeDarkforge;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

/**
 * GUI surface for the parallel Coriolis
 * dice-pool simulator.
 *
 * UI: pool-size combo (1-10), push checkbox,
 * total-rolls field, Run button, indeterminate
 * progress bar, append-only results table.
 *
 * The Run handler submits a
 * BackgroundTask<ProbabilityTable>:
 *   - compute()  runs on a SwingWorker thread
 *                and calls
 *                FacadeConcurrency.runParallel.
 *   - onResult / onError run on the EDT via
 *                SwingWorker.done().
 *
 * EDT discipline: compute() never touches
 * Swing components.
 */
public class DiceSimulatorPanel
        extends JPanel {

    private static final long
            serialVersionUID = 1L;

    private static final long
            DEFAULT_TOTAL_ROLLS = 1_000_000L;

    // =========================================
    // UI components
    // =========================================

    private final JComboBox<Integer>
            poolSizeBox;
    private final JCheckBox pushedBox;
    private final JTextField rollsField;
    private final JButton runButton;
    private final JProgressBar progressBar;
    private final DefaultTableModel
            resultsModel;

    // =========================================
    // Constructor
    // =========================================

    public DiceSimulatorPanel() {
        super(new BorderLayout(8, 8));

        // Controls
        JPanel controls = new JPanel(
                new GridLayout(2, 4, 8, 4));
        controls.setBorder(BorderFactory
                .createTitledBorder(
                        "Simulation"));

        poolSizeBox = new JComboBox<>();
        for (int i = 1; i <= 10; i++) {
            poolSizeBox.addItem(i);
        }
        poolSizeBox.setSelectedItem(3);

        pushedBox = new JCheckBox(
                "Pushed", false);
        rollsField = new JTextField(
                String.valueOf(
                        DEFAULT_TOTAL_ROLLS));

        runButton = new JButton("Run");
        runButton.addActionListener(
                e -> runSimulation());

        controls.add(new JLabel(
                "Pool size:"));
        controls.add(poolSizeBox);
        controls.add(new JLabel(
                "Total rolls:"));
        controls.add(rollsField);
        controls.add(pushedBox);
        controls.add(new JLabel());
        controls.add(new JLabel());
        controls.add(runButton);

        // Progress bar
        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setString("Idle");

        // Results table
        resultsModel = new DefaultTableModel(
                new Object[] {
                        "Pool", "Pushed", "Rolls",
                        "Success %", "Avg Sixes",
                        "Banes", "Threads", "ms" },
                0);
        JTable resultsTable = new JTable(
                resultsModel);
        resultsTable.setFillsViewportHeight(
                true);
        JScrollPane scroll = new JScrollPane(
                resultsTable);
        scroll.setBorder(BorderFactory
                .createTitledBorder("Results"));

        // Layout
        JPanel north = new JPanel(
                new BorderLayout(0, 4));
        north.add(controls,
                BorderLayout.CENTER);
        north.add(progressBar,
                BorderLayout.SOUTH);
        add(north, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
    }

    // =========================================
    // Run handler (EDT entry point)
    // =========================================

    private void runSimulation() {
        final int poolSize = (Integer)
                poolSizeBox.getSelectedItem();
        final boolean pushed =
                pushedBox.isSelected();
        final long totalRolls = parseRolls(
                rollsField.getText());
        final long seed = System.nanoTime();
        final long t0 = System.nanoTime();

        runButton.setEnabled(false);
        progressBar.setIndeterminate(true);
        progressBar.setString("Running...");

        new BackgroundTask<ProbabilityTable>(){
            @Override
            protected ProbabilityTable
            compute() {
                FacadeConcurrency facade =
                        FacadeDarkforge
                                .getTheInstance()
                                .concurrencyAccess();
                return facade.runParallel(
                        poolSize, pushed,
                        totalRolls, seed);
            }
        }
                .onResult(table -> {
                    long ms = (System.nanoTime() - t0)
                            / 1_000_000L;
                    int threads = FacadeDarkforge
                            .getTheInstance()
                            .concurrencyAccess()
                            .getThreadCount();
                    appendResult(table, threads, ms);
                    finishRun("Done");
                })
                .onError(err -> {
                    showError(err);
                    finishRun("Error");
                })
                .execute();
    }

    // =========================================
    // EDT-only helpers (called from done())
    // =========================================

    private void appendResult(
            ProbabilityTable table,
            int threads, long ms) {
        resultsModel.addRow(new Object[] {
                table.getPoolSize(),
                table.isPushed() ? "yes" : "no",
                table.getRollsCompleted(),
                String.format("%.2f%%",
                        table.getSuccessRate()
                                * 100.0),
                String.format("%.3f",
                        table.getAverageSixes()),
                table.snapshot().getBaneOnes(),
                threads, ms });
    }

    private void finishRun(String status) {
        progressBar.setIndeterminate(false);
        progressBar.setValue(0);
        progressBar.setString(status);
        runButton.setEnabled(true);
    }

    private long parseRolls(String text) {
        try {
            long n = Long.parseLong(
                    text.trim());
            return Math.max(1L, n);
        } catch (NumberFormatException e) {
            return DEFAULT_TOTAL_ROLLS;
        }
    }

    private void showError(Throwable err) {
        JOptionPane.showMessageDialog(this,
                "Simulation failed: "
                        + err.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }
}
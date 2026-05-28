package darkforge.gui;

import darkforge.facade.FacadeDarkforge;
import javax.swing.*;
import java.awt.*;

/**
 * Main GUI frame for DARKFORGE.
 * Tabbed interface: Catalog Browser,
 * Inventory Manager, Vehicle Upgrades.
 */
public class DarkforgeGui extends JFrame {

    public DarkforgeGui() {
        super("DARKFORGE v4.0 — Equipment"
                + " Manager");
        setDefaultCloseOperation(
                EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        // Initialize data
        FacadeDarkforge.getTheInstance()
                .initialize();

        // Tabbed pane
        JTabbedPane tabs =
                new JTabbedPane();
        tabs.addTab("🔍 Catalog",
                new CatalogBrowserPanel());
        tabs.addTab("🎒 Inventory",
                new InventoryPanel());
        tabs.addTab("⚙ Vehicle Upgrades",
                new VehicleUpgradePanel());

        add(tabs);
    }

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
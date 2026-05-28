package darkforge.gui;

import darkforge.model.*;
import darkforge.collection.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

/**
 * Inventory management panel.
 * Displays an entity's current inventory
 * with weight/slot usage. Supports equip,
 * unequip, remove, and sort operations.
 */
public class InventoryPanel
        extends JPanel {

    private final DefaultTableModel
            inventoryModel;
    private final JLabel statusLabel;
    private final JComboBox<String>
            sortBox;

    public InventoryPanel() {
        setLayout(
                new BorderLayout(8, 8));
        setBorder(BorderFactory
                .createEmptyBorder(
                        10, 10, 10, 10));

        // ---- Top: Status ----
        JPanel top = new JPanel(
                new FlowLayout(
                        FlowLayout.LEFT));
        statusLabel = new JLabel(
                "No entity selected");
        top.add(statusLabel);

        top.add(new JLabel("Sort:"));
        sortBox = new JComboBox<>(
                new String[] {
                        "Name", "Weight",
                        "Cost", "Type"
                });
        top.add(sortBox);

        JButton equipBtn =
                new JButton("Equip");
        JButton unequipBtn =
                new JButton("Unequip");
        JButton removeBtn =
                new JButton("Remove");
        top.add(equipBtn);
        top.add(unequipBtn);
        top.add(removeBtn);
        add(top, BorderLayout.NORTH);

        // ---- Center: Table ----
        String[] cols = {
                "Item", "Type", "Weight",
                "Cost", "Equipped"
        };
        inventoryModel =
                new DefaultTableModel(
                        cols, 0);
        JTable table =
                new JTable(inventoryModel);
        add(new JScrollPane(table),
                BorderLayout.CENTER);

        // Lambda event handlers
        sortBox.addActionListener(
                e -> refreshInventory());
        equipBtn.addActionListener(
                e -> JOptionPane
                        .showMessageDialog(this,
                                "Select an explorer"
                                        + " first."));
        unequipBtn.addActionListener(
                e -> JOptionPane
                        .showMessageDialog(this,
                                "Select an explorer"
                                        + " first."));
        removeBtn.addActionListener(
                e -> JOptionPane
                        .showMessageDialog(this,
                                "Select an explorer"
                                        + " first."));
    }

    private void refreshInventory() {
        // Placeholder — populated when
        // an explorer is selected
        inventoryModel.setRowCount(0);
    }
}
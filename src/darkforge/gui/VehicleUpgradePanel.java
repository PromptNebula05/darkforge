package darkforge.gui;

import darkforge.crew.VehicleType;
import darkforge.data.*;
import darkforge.model.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

/**
 * Vehicle module installation panel.
 * Shows vehicle stat block, available modules
 * filtered by compatibility, and visual slot
 * usage indicator.
 */
public class VehicleUpgradePanel
        extends JPanel {

    private final ItemCatalog catalog;
    private final JComboBox<VehicleType>
            vehicleSelector;
    private final DefaultTableModel
            moduleModel;
    private final JProgressBar slotBar;
    private final JLabel cpLabel;

    public VehicleUpgradePanel() {
        this.catalog =
                GameDataProvider
                        .getTheInstance()
                        .getItemCatalog();
        setLayout(
                new BorderLayout(8, 8));
        setBorder(BorderFactory
                .createEmptyBorder(
                        10, 10, 10, 10));

        // ---- Top: Vehicle selector ----
        JPanel top = new JPanel(
                new FlowLayout(
                        FlowLayout.LEFT));
        top.add(
                new JLabel("Vehicle:"));
        vehicleSelector =
                new JComboBox<>(
                        VehicleType.values());
        top.add(vehicleSelector);

        slotBar = new JProgressBar(
                0, 7);
        slotBar.setStringPainted(true);
        slotBar.setString(
                "0/7 slots used");
        top.add(slotBar);

        cpLabel = new JLabel(
                "CP: 0");
        top.add(cpLabel);

        JButton installBtn =
                new JButton("Install");
        top.add(installBtn);
        add(top, BorderLayout.NORTH);

        // ---- Center: Module table ----
        String[] cols = {
                "Module", "Slot", "CP",
                "Type", "Effect", "Tech"
        };
        moduleModel =
                new DefaultTableModel(
                        cols, 0);
        JTable table =
                new JTable(moduleModel);
        add(new JScrollPane(table),
                BorderLayout.CENTER);

        // Lambda event handler
        vehicleSelector
                .addActionListener(
                        e -> refreshModules());
        installBtn.addActionListener(
                e -> JOptionPane
                        .showMessageDialog(
                                this,
                                "Create a crew"
                                        + " first."));

        refreshModules();
    }

    private void refreshModules() {
        VehicleType selected =
                (VehicleType)
                        vehicleSelector
                                .getSelectedItem();
        if (selected == null) return;

        // Update slot bar
        slotBar.setMaximum(
                selected.getSlots());
        slotBar.setValue(0);
        slotBar.setString(
                "0/" + selected.getSlots()
                        + " slots used");

        // Load compatible modules
        List<VehicleModule> modules =
                catalog
                        .getCompatibleModules(
                                selected);

        moduleModel.setRowCount(0);
        for (VehicleModule m : modules) {
            moduleModel.addRow(
                    new Object[] {
                            m.getName(),
                            m.isAddon()
                                    ? "Add-on"
                                    : m.getSlotCost(),
                            m.getCpCost(),
                            m.getModuleType(),
                            m.getEffect(),
                            m.getTechString()
                    });
        }
    }
}
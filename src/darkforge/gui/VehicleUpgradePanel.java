package darkforge.gui;

import darkforge.crew.Crew;
import darkforge.crew.Vehicle;
import darkforge.crew.VehicleType;
import darkforge.data.GameDataProvider;
import darkforge.data.ItemCatalog;
import darkforge.model.VehicleModule;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Vehicle module installation panel.
 * Iteration 4 — Phase 9 wiring.
 *
 * Holds a reference to the active Crew
 * supplied by DarkforgeGui via setCrew(...).
 * For the selected vehicle (rover or
 * shuttle), lists every compatible module
 * from the ItemCatalog with an "Installed"
 * status column derived from the vehicle's
 * live EquipmentLoadout<VehicleModule>.
 *
 * Install and Uninstall buttons dispatch
 * to Vehicle.equip(module) / unequip(module)
 * via the existing Equippable<VehicleModule>
 * contract — no new model logic introduced.
 */
public class VehicleUpgradePanel
        extends JPanel {

    private Crew currentCrew;

    private final ItemCatalog catalog;
    private final JComboBox<Vehicle>
            vehicleSelector;
    private final DefaultTableModel
            moduleModel;
    private final JTable moduleTable;
    private final JProgressBar slotBar;
    private final JLabel cpLabel;
    private final JLabel hullLabel;
    private final JButton installBtn;
    private final JButton uninstallBtn;
    private final JButton refreshBtn;

    private final List<VehicleModule>
            currentRows = new ArrayList<>();

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

        // ---- Top: Vehicle + status ----
        JPanel top = new JPanel(
                new FlowLayout(
                        FlowLayout.LEFT));

        top.add(new JLabel("Vehicle:"));
        vehicleSelector =
                new JComboBox<>();
        vehicleSelector.setRenderer(
                new DefaultListCellRenderer() {
                    @Override
                    public Component
                    getListCellRendererComponent(
                            JList<?> list,
                            Object value,
                            int index,
                            boolean isSelected,
                            boolean cellHasFocus) {
                        super.getListCellRendererComponent(
                                list, value, index,
                                isSelected,
                                cellHasFocus);
                        if (value instanceof Vehicle v) {
                            setText(
                                    v.getName()
                                            + " ("
                                            + v.getType()
                                            .getDisplayName()
                                            + ")");
                        } else if (value == null) {
                            setText("(no vehicles)");
                        }
                        return this;
                    }
                });
        top.add(vehicleSelector);

        slotBar = new JProgressBar(0, 1);
        slotBar.setStringPainted(true);
        slotBar.setPreferredSize(
                new Dimension(180, 20));
        top.add(slotBar);

        hullLabel = new JLabel("Hull: —");
        top.add(hullLabel);

        cpLabel =
                new JLabel("CP installed: 0");
        top.add(cpLabel);

        installBtn = new JButton("Install");
        uninstallBtn =
                new JButton("Uninstall");
        refreshBtn = new JButton("Refresh");
        top.add(installBtn);
        top.add(uninstallBtn);
        top.add(refreshBtn);
        add(top, BorderLayout.NORTH);

        // ---- Center: Module table ----
        String[] cols = {
                "Module", "Slot", "CP",
                "Type", "Effect", "Tech",
                "Installed"
        };
        moduleModel =
                new DefaultTableModel(
                        cols, 0) {
                    @Override
                    public boolean isCellEditable(
                            int r, int c) {
                        return false;
                    }
                };
        moduleTable =
                new JTable(moduleModel);
        moduleTable
                .setAutoCreateRowSorter(true);
        add(new JScrollPane(moduleTable),
                BorderLayout.CENTER);

        // ---- Lambda event handlers ----
        vehicleSelector.addActionListener(
                e -> refreshModules());
        installBtn.addActionListener(
                e -> handleInstall());
        uninstallBtn.addActionListener(
                e -> handleUninstall());
        refreshBtn.addActionListener(
                e -> reloadCrew());

        vehicleSelector.setEnabled(false);
        installBtn.setEnabled(false);
        uninstallBtn.setEnabled(false);
        hullLabel.setText(
                "No crew loaded — use File"
                        + " → Open Crew to load one.");
    }

    /**
     * Set the active Crew for this panel.
     * Called by DarkforgeGui when the user
     * picks a crew via File → Open Crew.
     */
    public void setCrew(Crew crew) {
        this.currentCrew = crew;
        reloadCrew();
    }

    /**
     * Reload the vehicle dropdown from the
     * active Crew (or show the no-crew state
     * if no crew is set). Also called when
     * the user clicks Refresh.
     */
    private void reloadCrew() {
        Vehicle previouslySelected =
                (Vehicle)
                        vehicleSelector
                                .getSelectedItem();
        vehicleSelector.removeAllItems();
        if (currentCrew == null) {
            vehicleSelector.setEnabled(false);
            hullLabel.setText(
                    "No crew loaded — use File"
                            + " → Open Crew to load"
                            + " one.");
            cpLabel.setText(
                    "CP installed: 0");
            slotBar.setMaximum(1);
            slotBar.setValue(0);
            slotBar.setString("0 / 0 slots");
            moduleModel.setRowCount(0);
            currentRows.clear();
            updateButtonState();
            return;
        }
        Vehicle rover = currentCrew.getRover();
        Vehicle shuttle =
                currentCrew.getShuttle();
        if (rover == null
                && shuttle == null) {
            vehicleSelector.setEnabled(false);
            hullLabel.setText(
                    currentCrew.getName()
                            + " has no vehicles.");
            cpLabel.setText(
                    "CP installed: 0");
            slotBar.setMaximum(1);
            slotBar.setValue(0);
            slotBar.setString("0 / 0 slots");
            moduleModel.setRowCount(0);
            currentRows.clear();
            updateButtonState();
            return;
        }
        vehicleSelector.setEnabled(true);
        if (rover != null) {
            vehicleSelector.addItem(rover);
        }
        if (shuttle != null) {
            vehicleSelector.addItem(shuttle);
        }
        if (previouslySelected != null) {
            vehicleSelector
                    .setSelectedItem(
                            previouslySelected);
        }
        refreshModules();
    }

    /**
     * Repopulate the module table from
     * the currently selected vehicle and
     * the catalog's compatible modules.
     */
    private void refreshModules() {
        moduleModel.setRowCount(0);
        currentRows.clear();

        Vehicle vehicle = (Vehicle)
                vehicleSelector
                        .getSelectedItem();
        if (vehicle == null) {
            updateButtonState();
            return;
        }

        VehicleType type = vehicle.getType();
        List<VehicleModule> compatible =
                catalog
                        .getCompatibleModules(type);
        List<VehicleModule> installed =
                vehicle.getEquipped();

        int used = installed.size();
        int max = type.getSlots();
        slotBar.setMaximum(
                Math.max(max, 1));
        slotBar.setValue(used);
        slotBar.setString(
                used + " / " + max
                        + " slots");

        hullLabel.setText(
                String.format(
                        "Hull: %d / %d",
                        vehicle.getCurrentHull(),
                        vehicle.getMaxHull()));
        cpLabel.setText(
                "CP installed: "
                        + vehicle.getTotalCpCost());

        for (VehicleModule m : compatible) {
            boolean isInstalled =
                    installed.contains(m);
            moduleModel.addRow(
                    new Object[] {
                            m.getName(),
                            m.isAddon()
                                    ? "Add-on"
                                    : Integer.toString(
                                    m.getSlotCost()),
                            m.getCpCost(),
                            m.getModuleType(),
                            m.getEffect(),
                            m.getTechString(),
                            isInstalled ? "\u2713" : ""
                    });
            currentRows.add(m);
        }

        updateButtonState();
    }

    private void updateButtonState() {
        boolean hasVehicle =
                vehicleSelector
                        .getSelectedItem() != null;
        installBtn.setEnabled(hasVehicle);
        uninstallBtn.setEnabled(hasVehicle);
    }

    private VehicleModule
    getSelectedRowModule() {
        int viewRow =
                moduleTable.getSelectedRow();
        if (viewRow < 0) return null;
        int modelRow =
                moduleTable
                        .convertRowIndexToModel(
                                viewRow);
        if (modelRow < 0
                || modelRow
                >= currentRows.size()) {
            return null;
        }
        return currentRows.get(modelRow);
    }

    private void handleInstall() {
        Vehicle vehicle = (Vehicle)
                vehicleSelector
                        .getSelectedItem();
        VehicleModule module =
                getSelectedRowModule();
        if (vehicle == null
                || module == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Select a module first.");
            return;
        }
        if (vehicle.isEquipped(module)) {
            JOptionPane.showMessageDialog(
                    this,
                    "That module is already"
                            + " installed.");
            return;
        }
        boolean ok = vehicle.equip(module);
        if (!ok) {
            JOptionPane.showMessageDialog(
                    this,
                    "Could not install (no"
                            + " slots, or"
                            + " incompatible"
                            + " vehicle type).");
        }
        refreshModules();
    }

    private void handleUninstall() {
        Vehicle vehicle = (Vehicle)
                vehicleSelector
                        .getSelectedItem();
        VehicleModule module =
                getSelectedRowModule();
        if (vehicle == null
                || module == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Select a module first.");
            return;
        }
        if (!vehicle.isEquipped(module)) {
            JOptionPane.showMessageDialog(
                    this,
                    "That module is not"
                            + " currently installed.");
            return;
        }
        boolean ok =
                vehicle.unequip(module);
        if (!ok) {
            JOptionPane.showMessageDialog(
                    this,
                    "Could not uninstall.");
        }
        refreshModules();
    }
}

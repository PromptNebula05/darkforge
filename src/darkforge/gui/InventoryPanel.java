package darkforge.gui;

import darkforge.crew.Crew;
import darkforge.model.CharacterItem;
import darkforge.model.Explorer;
import darkforge.model.Item;
import darkforge.model.Weapon;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Inventory management panel. The active Crew is supplied by
 * DarkforgeGui via setCrew(...). The user picks an Explorer in that
 * crew and sees their inventory; the Equipped column is driven by
 * Explorer.getEquipped(). All actions dispatch through Explorer /
 * EquipmentLoadout. The "Add Item…" button opens a modal
 * CatalogBrowserPanel in picker mode, restricted to CharacterItem
 * rows, and calls Explorer.addItem(...) on the chosen catalog row.
 */
public class InventoryPanel extends JPanel {

    private Crew currentCrew;

    private final JComboBox<Explorer> explorerSelector;
    private final DefaultTableModel inventoryModel;
    private final JTable inventoryTable;
    private final JLabel statusLabel;
    private final JComboBox<String> sortBox;
    private final JButton addBtn;
    private final JButton equipBtn;
    private final JButton unequipBtn;
    private final JButton removeBtn;
    private final JButton refreshBtn;

    private final List<CharacterItem> currentRows = new ArrayList<>();

    public InventoryPanel() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ---- Top: Explorer + actions ----
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Explorer:"));
        explorerSelector = new JComboBox<>();
        explorerSelector.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);
                if (value instanceof Explorer e) {
                    setText(e.getName() + " — " + e.getProfessionName());
                } else if (value == null) {
                    setText("(no crew)");
                }
                return this;
            }
        });
        top.add(explorerSelector);

        top.add(new JLabel("Sort:"));
        sortBox = new JComboBox<>(new String[] {"Name", "Weight", "Cost", "Type"});
        top.add(sortBox);

        addBtn      = new JButton("Add Item…");
        equipBtn    = new JButton("Equip");
        unequipBtn  = new JButton("Unequip");
        removeBtn   = new JButton("Remove");
        refreshBtn  = new JButton("Refresh");
        top.add(addBtn);
        top.add(equipBtn);
        top.add(unequipBtn);
        top.add(removeBtn);
        top.add(refreshBtn);
        add(top, BorderLayout.NORTH);

        // ---- Center: Inventory table ----
        String[] cols = {"Item", "Type", "Weight", "Cost", "Equipped"};
        inventoryModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        inventoryTable = new JTable(inventoryModel);
        inventoryTable.setAutoCreateRowSorter(true);
        add(new JScrollPane(inventoryTable), BorderLayout.CENTER);

        // ---- Bottom: status bar ----
        statusLabel = new JLabel(
                "No crew loaded — use File → Open Crew to load one.");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        add(statusLabel, BorderLayout.SOUTH);

        // ---- Lambda event handlers ----
        explorerSelector.addActionListener(e -> refreshInventory());
        sortBox.addActionListener(e -> refreshInventory());
        addBtn.addActionListener(e -> handleAdd());
        equipBtn.addActionListener(e -> handleEquip());
        unequipBtn.addActionListener(e -> handleUnequip());
        removeBtn.addActionListener(e -> handleRemove());
        refreshBtn.addActionListener(e -> reloadCrew());

        explorerSelector.setEnabled(false);
        updateButtonState();
    }

    public void setCrew(Crew crew) {
        this.currentCrew = crew;
        reloadCrew();
    }

    private void reloadCrew() {
        Explorer previouslySelected =
                (Explorer) explorerSelector.getSelectedItem();
        explorerSelector.removeAllItems();
        if (currentCrew == null) {
            explorerSelector.setEnabled(false);
            statusLabel.setText(
                    "No crew loaded — use File → Open Crew to load one.");
            inventoryModel.setRowCount(0);
            currentRows.clear();
            updateButtonState();
            return;
        }
        List<Explorer> members = currentCrew.getMembers();
        if (members.isEmpty()) {
            explorerSelector.setEnabled(false);
            statusLabel.setText(currentCrew.getName() + " has no members.");
            inventoryModel.setRowCount(0);
            currentRows.clear();
            updateButtonState();
            return;
        }
        explorerSelector.setEnabled(true);
        for (Explorer e : members) explorerSelector.addItem(e);
        if (previouslySelected != null && members.contains(previouslySelected)) {
            explorerSelector.setSelectedItem(previouslySelected);
        }
        refreshInventory();
    }

    private void refreshInventory() {
        inventoryModel.setRowCount(0);
        currentRows.clear();

        Explorer selected = (Explorer) explorerSelector.getSelectedItem();
        if (selected == null) {
            statusLabel.setText("No explorer selected");
            updateButtonState();
            return;
        }

        List<CharacterItem> items = new ArrayList<>(selected.getAllItems());
        sortItems(items);
        List<Weapon> equipped = selected.getEquipped();

        for (CharacterItem item : items) {
            boolean isEquipped = (item instanceof Weapon w) && equipped.contains(w);
            inventoryModel.addRow(new Object[] {
                    item.getName(),
                    item.getItemType(),
                    item.getWeightClass().getDisplayName(),
                    item.getCost(),
                    isEquipped ? "\u2713" : ""
            });
            currentRows.add(item);
        }

        statusLabel.setText(String.format(
                "%s: %d item(s) | %d equipped | load %.1f / %.1f",
                selected.getName(),
                items.size(), equipped.size(),
                selected.getCurrentCarryWeight(),
                selected.getMaxCarryWeight()));
        updateButtonState();
    }

    private void sortItems(List<CharacterItem> items) {
        String mode = (String) sortBox.getSelectedItem();
        Comparator<CharacterItem> cmp = switch (mode == null ? "Name" : mode) {
            case "Weight" -> Comparator.comparingDouble(CharacterItem::getWeight);
            case "Cost"   -> Comparator.comparingInt(CharacterItem::getCost);
            case "Type"   -> Comparator.comparing(CharacterItem::getItemType);
            default       -> Comparator.comparing(CharacterItem::getName);
        };
        items.sort(cmp);
    }

    private void updateButtonState() {
        Explorer selected = (Explorer) explorerSelector.getSelectedItem();
        boolean hasExplorer = selected != null;
        boolean hasRows = hasExplorer && !currentRows.isEmpty();
        addBtn.setEnabled(hasExplorer);
        equipBtn.setEnabled(hasRows);
        unequipBtn.setEnabled(hasRows);
        removeBtn.setEnabled(hasRows);
    }

    private CharacterItem getSelectedRowItem() {
        int viewRow = inventoryTable.getSelectedRow();
        if (viewRow < 0) return null;
        int modelRow = inventoryTable.convertRowIndexToModel(viewRow);
        if (modelRow < 0 || modelRow >= currentRows.size()) return null;
        return currentRows.get(modelRow);
    }

    // =========================================
    // Add Item…  (re-uses CatalogBrowserPanel)
    // =========================================

    private void handleAdd() {
        Explorer explorer = (Explorer) explorerSelector.getSelectedItem();
        if (explorer == null) {
            JOptionPane.showMessageDialog(this,
                    "Select an explorer first.");
            return;
        }

        // Build a modal dialog hosting a CatalogBrowserPanel in picker mode.
        Window owner = SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(
                owner instanceof Frame f ? f : null,
                "Add item to " + explorer.getName(),
                Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setLayout(new BorderLayout());

        CatalogBrowserPanel picker = new CatalogBrowserPanel();
        picker.enablePickerMode(
                "Add to " + explorer.getName(),
                item -> item instanceof CharacterItem,
                item -> {
                    if (!(item instanceof CharacterItem ci)) return;
                    boolean ok = explorer.addItem(ci);
                    if (ok) {
                        statusLabel.setText(
                                "Added " + ci.getName()
                                        + " to " + explorer.getName());
                        refreshInventory();
                    } else {
                        JOptionPane.showMessageDialog(dialog,
                                "Could not add '" + ci.getName()
                                        + "' (inventory full or duplicate).");
                    }
                });
        dialog.add(picker, BorderLayout.CENTER);

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> dialog.dispose());
        south.add(closeBtn);
        dialog.add(south, BorderLayout.SOUTH);

        dialog.setSize(900, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void handleEquip() {
        Explorer explorer = (Explorer) explorerSelector.getSelectedItem();
        CharacterItem item = getSelectedRowItem();
        if (explorer == null || item == null) {
            JOptionPane.showMessageDialog(this, "Select a row first.");
            return;
        }
        if (!(item instanceof Weapon weapon)) {
            JOptionPane.showMessageDialog(this, "Only weapons can be equipped.");
            return;
        }
        boolean ok = explorer.equip(weapon);
        if (!ok) {
            JOptionPane.showMessageDialog(this,
                    "Could not equip (slots full or already equipped).");
        }
        refreshInventory();
    }

    private void handleUnequip() {
        Explorer explorer = (Explorer) explorerSelector.getSelectedItem();
        CharacterItem item = getSelectedRowItem();
        if (explorer == null || item == null) {
            JOptionPane.showMessageDialog(this, "Select a row first.");
            return;
        }
        if (!(item instanceof Weapon weapon)) {
            JOptionPane.showMessageDialog(this, "Only weapons can be unequipped.");
            return;
        }
        boolean ok = explorer.unequip(weapon);
        if (!ok) {
            JOptionPane.showMessageDialog(this,
                    "That weapon is not currently equipped.");
        }
        refreshInventory();
    }

    private void handleRemove() {
        Explorer explorer = (Explorer) explorerSelector.getSelectedItem();
        CharacterItem item = getSelectedRowItem();
        if (explorer == null || item == null) {
            JOptionPane.showMessageDialog(this, "Select a row first.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Remove '" + item.getName() + "' from "
                        + explorer.getName() + "?",
                "Confirm Remove",
                JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        if (item instanceof Weapon w && explorer.isEquipped(w)) {
            explorer.unequip(w);
        }
        boolean ok = explorer.removeItem(item);
        if (!ok) {
            JOptionPane.showMessageDialog(this, "Could not remove item.");
        }
        refreshInventory();
    }
}

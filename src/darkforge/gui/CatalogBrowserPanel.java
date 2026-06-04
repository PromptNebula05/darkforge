package darkforge.gui;

import darkforge.concurrency.BackgroundTask;
import darkforge.data.*;
import darkforge.facade.FacadeDarkforge;
import darkforge.model.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Catalog browser with real-time search, category dropdown, and cost
 * range slider. All event handlers use lambda listeners.
 *
 * Reload: a Reload Catalog button beside the existing search controls
 * re-reads the JSON catalog resources via FacadeCatalog.reload(). The
 * JSON I/O runs off the EDT through BackgroundTask&lt;ItemCatalog&gt;;
 * the new catalog is rebound and the results table is refreshed on
 * the EDT through onResult.
 *
 * Picker mode: when {@link #enablePickerMode} is called, an extra
 * action button appears at the bottom that fires a Consumer with the
 * selected catalog Item. Used by InventoryPanel's "Add Item…" dialog.
 */
public class CatalogBrowserPanel extends JPanel {

    // =========================================
    // Fields
    // =========================================

    private ItemCatalog catalog;
    private final JLabel costValueLabel;
    private final DefaultTableModel tableModel;
    private final JTable table;
    private final JTextField searchField;
    private final JComboBox<String> categoryBox;
    private final JSlider costSlider;
    private final JLabel resultCount;
    private final JButton reloadButton;
    private final JLabel statusLabel;
    private final List<Item> currentRows = new ArrayList<>();

    // Picker mode state
    private Predicate<Item> rowFilter = item -> true;
    private Consumer<Item> onAddRequested;
    private JButton addBtn;
    private JPanel pickerBar;

    // =========================================
    // Constructor
    // =========================================

    public CatalogBrowserPanel() {
        this.catalog = GameDataProvider.getTheInstance().getItemCatalog();
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ---- Top: Search controls ----
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controls.add(new JLabel("Search:"));
        searchField = new JTextField(20);
        controls.add(searchField);

        controls.add(new JLabel("Category:"));
        categoryBox = new JComboBox<>();
        rebuildCategoryBox();
        controls.add(categoryBox);

        // ---- Max cost (slider + live readout) ----
        controls.add(new JLabel("Max cost:"));
        costSlider = new JSlider(0, 5000, 5000);          // tighter, realistic range
        costSlider.setPreferredSize(new Dimension(160, 22));
        costSlider.setPaintTicks(false);
        costSlider.setPaintLabels(false);                 // <-- kills the inline labels
        costSlider.setFocusable(false);
        controls.add(costSlider);

        costValueLabel = new JLabel("≤ 5,000 rukh");
        costValueLabel.setPreferredSize(new Dimension(110, 22));
        controls.add(costValueLabel);

        resultCount = new JLabel("0 items");
        controls.add(resultCount);

        // ---- Reload (off-EDT via BackgroundTask) ----
        reloadButton = new JButton("Reload Catalog");
        reloadButton.addActionListener(e -> reloadCatalogAsync());
        controls.add(reloadButton);

        statusLabel = new JLabel(" ");
        controls.add(statusLabel);

        add(controls, BorderLayout.NORTH);

        // ---- Center: Results table ----
        String[] columns = {"Name", "Type", "Category", "Cost", "Tech", "Weight"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setAutoCreateRowSorter(true);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // ---- Event handlers (lambdas) ----
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { refreshResults(); }
            @Override public void removeUpdate(DocumentEvent e) { refreshResults(); }
            @Override public void changedUpdate(DocumentEvent e) { refreshResults(); }
        });
        categoryBox.addActionListener(e -> refreshResults());
        costSlider.addChangeListener(e -> {
            costValueLabel.setText(
                    "≤ " + String.format("%,d", costSlider.getValue()) + " rukh");
            refreshResults();
        });

        refreshResults();
    }

    // =========================================
    // Picker mode
    // =========================================

    /**
     * Switch this panel into picker mode. Shows an extra button beneath
     * the results table; clicking it invokes {@code onAdd} with the
     * currently selected {@link Item}. The {@code rowFilter} is applied
     * on top of the existing search/category/cost filters so callers
     * can restrict the catalog (e.g. only {@link CharacterItem}).
     *
     * @param buttonLabel  text on the action button (e.g. "Add to Explorer")
     * @param rowFilter    extra predicate; pass {@code null} for no extra filter
     * @param onAdd        invoked with the selected Item when the button is clicked
     */
    public void enablePickerMode(String buttonLabel,
                                 Predicate<Item> rowFilter,
                                 Consumer<Item> onAdd) {
        this.rowFilter = (rowFilter != null) ? rowFilter : (item -> true);
        this.onAddRequested = onAdd;
        if (pickerBar == null) {
            pickerBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            addBtn = new JButton(buttonLabel != null ? buttonLabel : "Add");
            addBtn.addActionListener(e -> handleAdd());
            pickerBar.add(addBtn);
            add(pickerBar, BorderLayout.SOUTH);
            revalidate();
        } else {
            addBtn.setText(buttonLabel != null ? buttonLabel : "Add");
        }
        refreshResults();
    }

    private void handleAdd() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            JOptionPane.showMessageDialog(this,
                    "Select a catalog row first.");
            return;
        }
        int modelRow = table.convertRowIndexToModel(viewRow);
        if (modelRow < 0 || modelRow >= currentRows.size()) return;
        if (onAddRequested != null) {
            onAddRequested.accept(currentRows.get(modelRow));
        }
    }

    // =========================================
    // Results table
    // =========================================

    private void refreshResults() {
        String query = searchField.getText().trim().toLowerCase();
        String category = (String) categoryBox.getSelectedItem();
        int maxCost = costSlider.getValue();

        List<Item> results = catalog.filter(item -> {
            boolean matchesSearch = query.isEmpty()
                    || item.getName().toLowerCase().contains(query)
                    || item.getDescription().toLowerCase().contains(query);
            boolean matchesCategory = category == null
                    || "All".equals(category)
                    || item.getCategory().equals(category);
            boolean matchesCost = item.getCost() <= maxCost;
            boolean matchesPicker = rowFilter.test(item);
            return matchesSearch && matchesCategory && matchesCost && matchesPicker;
        });

        tableModel.setRowCount(0);
        currentRows.clear();
        for (Item item : results) {
            tableModel.addRow(new Object[] {
                    item.getName(),
                    item.getItemType(),
                    item.getCategory(),
                    item.getCost(),
                    item.getTechString(),
                    item.getWeight()
            });
            currentRows.add(item);
        }
        resultCount.setText(results.size() + " items");
    }

    // =========================================
    // Reload
    // =========================================

    /**
     * Rebuild the category dropdown from the current catalog. The
     * previously-selected category is preserved when it still exists;
     * otherwise the selection falls back to "All".
     */
    private void rebuildCategoryBox() {
        String previous = (String) categoryBox.getSelectedItem();
        categoryBox.removeAllItems();
        categoryBox.addItem("All");
        catalog.getCategories().stream().sorted().forEach(categoryBox::addItem);
        if (previous != null) {
            for (int i = 0; i < categoryBox.getItemCount(); i++) {
                if (previous.equals(categoryBox.getItemAt(i))) {
                    categoryBox.setSelectedIndex(i);
                    return;
                }
            }
        }
        categoryBox.setSelectedItem("All");
    }

    /**
     * Rebind this panel to a freshly-loaded ItemCatalog and refresh
     * the visible results. Must run on the EDT (called only from the
     * reload BackgroundTask's onResult).
     */
    private void rebindTableModel(ItemCatalog freshCatalog) {
        this.catalog = freshCatalog;
        rebuildCategoryBox();
        refreshResults();
    }

    /**
     * Re-read the JSON catalog resources off the EDT and rebind this
     * panel when the worker completes. JSON I/O happens in compute();
     * the rebind, status update, and button re-enable happen on the
     * EDT through SwingWorker.done().
     */
    private void reloadCatalogAsync() {
        reloadButton.setEnabled(false);
        statusLabel.setText("Reloading catalog...");

        new BackgroundTask<ItemCatalog>() {
            @Override
            protected ItemCatalog compute() {
                return FacadeDarkforge.getTheInstance()
                        .catalogAccess()
                        .reload();
            }
        }
                .onResult(freshCatalog -> {
                    rebindTableModel(freshCatalog);
                    statusLabel.setText(
                            "Catalog reloaded (" + freshCatalog.size() + " items)");
                    reloadButton.setEnabled(true);
                })
                .onError(err -> {
                    statusLabel.setText("Reload failed: " + err.getMessage());
                    reloadButton.setEnabled(true);
                })
                .execute();
    }
}
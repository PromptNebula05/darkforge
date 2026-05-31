package darkforge.gui;

import darkforge.data.*;
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
 * Picker mode: when {@link #enablePickerMode} is called, an extra
 * action button appears at the bottom that fires a Consumer with the
 * selected catalog Item. Used by InventoryPanel's "Add Item…" dialog.
 */
public class CatalogBrowserPanel extends JPanel {

    private final ItemCatalog catalog;

    private final JLabel costValueLabel;
    private final DefaultTableModel tableModel;
    private final JTable table;
    private final JTextField searchField;
    private final JComboBox<String> categoryBox;
    private final JSlider costSlider;
    private final JLabel resultCount;

    private final List<Item> currentRows = new ArrayList<>();

    // Picker mode state
    private Predicate<Item> rowFilter = item -> true;
    private Consumer<Item> onAddRequested;
    private JButton addBtn;
    private JPanel pickerBar;

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
        categoryBox.addItem("All");
        catalog.getCategories().stream().sorted().forEach(categoryBox::addItem);
        controls.add(categoryBox);

        // ---- Max cost (slider + live readout) ----
        controls.add(new JLabel("Max cost:"));
        costSlider = new JSlider(0, 5000, 5000);          // tighter, realistic range
        costSlider.setPreferredSize(new Dimension(160, 22));
        costSlider.setPaintTicks(false);
        costSlider.setPaintLabels(false);                 // <-- kills the inline labels
        costSlider.setFocusable(false);
        controls.add(costSlider);

        costValueLabel = new JLabel("≤ 5,000 rukh");       // new field
        costValueLabel.setPreferredSize(new Dimension(110, 22));
        controls.add(costValueLabel);

        resultCount = new JLabel("0 items");
        controls.add(resultCount);

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

    private void refreshResults() {
        String query = searchField.getText().trim().toLowerCase();
        String category = (String) categoryBox.getSelectedItem();
        int maxCost = costSlider.getValue();

        List<Item> results = catalog.filter(item -> {
            boolean matchesSearch = query.isEmpty()
                    || item.getName().toLowerCase().contains(query)
                    || item.getDescription().toLowerCase().contains(query);
            boolean matchesCategory = "All".equals(category)
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
}

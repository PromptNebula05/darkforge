package darkforge.gui;

import darkforge.data.*;
import darkforge.model.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

/**
 * Catalog browser with real-time search,
 * category dropdown, and cost range slider.
 * All event handlers use lambda listeners.
 */
public class CatalogBrowserPanel
        extends JPanel {

    private final ItemCatalog catalog;
    private final DefaultTableModel
            tableModel;
    private final JTextField searchField;
    private final JComboBox<String>
            categoryBox;
    private final JSlider costSlider;
    private final JLabel resultCount;

    public CatalogBrowserPanel() {
        this.catalog =
                GameDataProvider
                        .getTheInstance()
                        .getItemCatalog();
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory
                .createEmptyBorder(
                        10, 10, 10, 10));

        // ---- Top: Search controls ----
        JPanel controls = new JPanel(
                new FlowLayout(FlowLayout.LEFT));

        controls.add(
                new JLabel("Search:"));
        searchField =
                new JTextField(20);
        controls.add(searchField);

        controls.add(
                new JLabel("Category:"));
        categoryBox =
                new JComboBox<>();
        categoryBox.addItem("All");
        catalog.getCategories()
                .stream().sorted()
                .forEach(
                        categoryBox::addItem);
        controls.add(categoryBox);

        controls.add(
                new JLabel("Max cost:"));
        costSlider =
                new JSlider(0, 25000, 25000);
        costSlider.setMajorTickSpacing(
                5000);
        costSlider.setPaintLabels(true);
        controls.add(costSlider);

        resultCount =
                new JLabel("0 items");
        controls.add(resultCount);

        add(controls, BorderLayout.NORTH);

        // ---- Center: Results table ----
        String[] columns = {
                "Name", "Type", "Category",
                "Cost", "Tech", "Weight"
        };
        tableModel =
                new DefaultTableModel(
                        columns, 0) {
                    @Override
                    public boolean isCellEditable(
                            int r, int c) {
                        return false;
                    }
                };
        JTable table =
                new JTable(tableModel);
        table.setAutoCreateRowSorter(
                true);
        add(new JScrollPane(table),
                BorderLayout.CENTER);

        // ---- Event handlers (lambdas) ----

        // Real-time search on keystroke
        searchField.getDocument()
                .addDocumentListener(
                        new DocumentListener() {
                            @Override
                            public void insertUpdate(
                                    DocumentEvent e) {
                                refreshResults();
                            }
                            @Override
                            public void removeUpdate(
                                    DocumentEvent e) {
                                refreshResults();
                            }
                            @Override
                            public void changedUpdate(
                                    DocumentEvent e) {
                                refreshResults();
                            }
                        });

        // Category filter
        categoryBox.addActionListener(
                e -> refreshResults());

        // Cost slider
        costSlider.addChangeListener(
                e -> refreshResults());

        // Initial load
        refreshResults();
    }

    private void refreshResults() {
        String query = searchField
                .getText().trim().toLowerCase();
        String category = (String)
                categoryBox.getSelectedItem();
        int maxCost =
                costSlider.getValue();

        List<Item> results =
                catalog.filter(item -> {
                    boolean matchesSearch =
                            query.isEmpty()
                                    || item.getName()
                                    .toLowerCase()
                                    .contains(query)
                                    || item.getDescription()
                                    .toLowerCase()
                                    .contains(query);
                    boolean matchesCategory =
                            "All".equals(category)
                                    || item.getCategory()
                                    .equals(category);
                    boolean matchesCost =
                            item.getCost()
                                    <= maxCost;
                    return matchesSearch
                            && matchesCategory
                            && matchesCost;
                });

        tableModel.setRowCount(0);
        for (Item item : results) {
            tableModel.addRow(
                    new Object[] {
                            item.getName(),
                            item.getItemType(),
                            item.getCategory(),
                            item.getCost(),
                            item.getTechString(),
                            item.getWeight()
                    });
        }
        resultCount.setText(
                results.size() + " items");
    }
}
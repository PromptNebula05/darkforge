package darkforge.facade;

import darkforge.data.*;
import darkforge.model.*;

import java.util.*;
import java.util.function.Predicate;

/**
 * Simplified facade for the equipment
 * catalog. Follows the existing
 * FacadeCrew pattern. Accessed via
 * FacadeDarkforge.catalogAccess().
 *
 * Exposes the catalog's search, filter,
 * analytics, and info surface to the
 * GUI and CLI, plus a reload() entry
 * point that re-reads the JSON catalog
 * files via GameDataProvider and
 * rebinds both cached references in one
 * synchronized step.
 */
public class FacadeCatalog {

    private ItemCatalog catalog;
    private CatalogAnalytics analytics;

    FacadeCatalog(ItemCatalog catalog) {
        this.catalog = catalog;
        this.analytics =
                new CatalogAnalytics(catalog);
    }

    // =========================================
    // Search & Filter
    // =========================================

    public List<Item> search(
            String keyword) {
        return catalog.search(keyword);
    }

    public List<Item> filter(
            Predicate<Item> predicate) {
        return catalog.filter(predicate);
    }

    public <T extends Item> List<T>
    filterByType(Class<T> type) {
        return catalog
                .filterByType(type);
    }

    public List<Item> filterByCategory(
            String category) {
        return catalog
                .filterByCategory(category);
    }

    public List<Item> filterByCostRange(
            int min, int max) {
        return catalog
                .filterByCostRange(min, max);
    }

    public List<Item> filterByTechLevel(
            TechLevel level) {
        return catalog
                .filterByTechLevel(level);
    }

    // =========================================
    // Analytics
    // =========================================

    public String getCatalogReport() {
        return analytics
                .generateCatalogReport();
    }

    public List<Item> getTopByCost(
            int n) {
        return analytics
                .getTopNByCost(n);
    }

    public Map<Boolean, List<Item>>
    partitionByBudget(
            int budget) {
        return analytics
                .partitionByAffordability(
                        budget);
    }

    // =========================================
    // Info
    // =========================================

    public int getCatalogSize() {
        return catalog.size();
    }

    public Set<String> getCategories() {
        return catalog.getCategories();
    }

    public ItemCatalog getRawCatalog() {
        return catalog;
    }

    // =========================================
    // Reload
    // =========================================

    /**
     * Re-reads the JSON catalog files via
     * GameDataProvider and rebinds this
     * facade's cached ItemCatalog and
     * CatalogAnalytics. Returns the
     * freshly-loaded catalog so callers
     * can swap their own references in
     * one step.
     *
     * Safe to call from a worker thread —
     * no Swing components are touched.
     * Callers that hold the previous
     * ItemCatalog reference (for example,
     * a GUI table model) must replace it
     * with the value returned here.
     */
    public synchronized ItemCatalog reload() {
        ItemCatalog fresh = GameDataProvider
                .getTheInstance()
                .reload();
        this.catalog = fresh;
        this.analytics =
                new CatalogAnalytics(fresh);
        return fresh;
    }
}
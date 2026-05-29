package darkforge.facade;

import darkforge.data.GameDataProvider;

/**
 * Root Façade singleton — provides unified access
 * to all DARKFORGE subsystems via sub-Façade
 * accessors. Call initialize() once at startup
 * before any other operations.
 */
public class FacadeDarkforge {

    private static final FacadeDarkforge INSTANCE =
            new FacadeDarkforge();

    private final FacadeModel model;
    private final FacadeMechanics mechanics;
    private final FacadeCreation creation;
    private final FacadeDisplay display;
    private final FacadePersistence persistence;
    private final FacadeCrew crew;

    private FacadeCatalog catalog;

    private FacadeDarkforge() {
        this.model = FacadeModel.getTheInstance();
        this.mechanics = FacadeMechanics.getTheInstance();
        this.creation = FacadeCreation.getTheInstance();
        this.display = FacadeDisplay.getTheInstance();
        this.persistence = FacadePersistence.getTheInstance();
        this.crew = FacadeCrew.getTheInstance();
    }

    public static FacadeDarkforge getTheInstance() {
        return INSTANCE;
    }

    public void initialize() {
        GameDataProvider.getTheInstance().initialize();

        catalog = new FacadeCatalog(
                GameDataProvider.getTheInstance().getItemCatalog());
    }

    public String getVersion() {
        return "DARKFORGE v4.0";
    }

    public FacadeModel modelAccess() {
        return model;
    }

    public FacadeMechanics mechanicsAccess() {
        return mechanics;
    }

    public FacadeCreation creationAccess() {
        return creation;
    }

    public FacadeDisplay displayAccess() {
        return display;
    }

    public FacadePersistence persistenceAccess() {
        return persistence;
    }

    public FacadeCrew crewAccess() {
        return crew;
    }

    // NEW
    public FacadeCatalog catalogAccess() {
        return catalog;
    }
}

package darkforge.facade;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for FacadeDarkforge singleton root facade.
 * Verifies Singleton pattern via getInstance() and
 * sub-facade accessor wiring.
 */
class FacadeDarkforgeTest {

    @Test
    void shouldReturnNonNullInstance() {
        assertNotNull(FacadeDarkforge.getTheInstance());
    }

    @Test
    void shouldReturnSameInstanceOnMultipleCalls() {
        FacadeDarkforge first = FacadeDarkforge.getTheInstance();
        FacadeDarkforge second = FacadeDarkforge.getTheInstance();
        assertSame(first, second,
                "getInstance() should always return the "
                        + "same Singleton");
    }

    @Test
    void shouldProvideModelAccess() {
        assertNotNull(
                FacadeDarkforge.getTheInstance().modelAccess(),
                "modelAccess() should return a non-null "
                        + "FacadeModel");
    }

    @Test
    void shouldProvideMechanicsAccess() {
        assertNotNull(
                FacadeDarkforge.getTheInstance()
                        .mechanicsAccess(),
                "mechanicsAccess() should return a non-null "
                        + "FacadeMechanics");
    }

    @Test
    void shouldProvideCreationAccess() {
        assertNotNull(
                FacadeDarkforge.getTheInstance()
                        .creationAccess(),
                "creationAccess() should return a non-null "
                        + "FacadeCreation");
    }

    @Test
    void shouldProvideDisplayAccess() {
        assertNotNull(
                FacadeDarkforge.getTheInstance()
                        .displayAccess(),
                "displayAccess() should return a non-null "
                        + "FacadeDisplay");
    }

    @Test
    void shouldProvidePersistenceAccess() {
        assertNotNull(
                FacadeDarkforge.getTheInstance()
                        .persistenceAccess(),
                "persistenceAccess() should return a non-null "
                        + "FacadePersistence");
    }

    @Test
    void subFacadesShouldBeSameAcrossCalls() {
        FacadeDarkforge facade =
                FacadeDarkforge.getTheInstance();
        assertSame(
                facade.modelAccess(),
                facade.modelAccess(),
                "modelAccess() should return same instance");
        assertSame(
                facade.mechanicsAccess(),
                facade.mechanicsAccess(),
                "mechanicsAccess() should return same "
                        + "instance");
        assertSame(
                facade.creationAccess(),
                facade.creationAccess(),
                "creationAccess() should return same "
                        + "instance");
        assertSame(
                facade.displayAccess(),
                facade.displayAccess(),
                "displayAccess() should return same instance");
        assertSame(
                facade.persistenceAccess(),
                facade.persistenceAccess(),
                "persistenceAccess() should return same "
                        + "instance");
    }
}
package drinkshop.service.validator;

import drinkshop.domain.IngredientReteta;
import drinkshop.domain.Reteta;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * F02 - White-Box Testing pentru RetetaValidator.validate(Reteta)
 *
 * Metoda testata: RetetaValidator.validate(Reteta reteta)
 *
 * Conditii:
 *   C1: reteta.getId() <= 0
 *   C2: ingrediente == null || ingrediente.isEmpty()
 *   C3: !errors.isEmpty() — dupa blocul null/empty
 *   C4: for — mai are elemente
 *   C5: entry.getCantitate() <= 0
 *   C6: !errors.isEmpty() — final
 *
 * Criterii: SC, DC, CC, DCC, MCC, APC, LC
 */
@DisplayName("F02 - RetetaValidator White-Box Tests")
class RetetaValidatorTest {

    private RetetaValidator validator;

    @BeforeEach
    void setUp() {
        validator = new RetetaValidator();
    }

    // =========================================================
    // Helper methods
    // =========================================================

    private Reteta buildReteta(int id, List<IngredientReteta> ingrediente) {
        return new Reteta(id, ingrediente);
    }

    private IngredientReteta validIngredient(String nume, double cantitate) {
        return new IngredientReteta(nume, cantitate);
    }

    // =========================================================
    // TC01 — SC: Statement Coverage
    // Input: id=1, ing=[{apa, 2.0}]
    // C1=F, C2=F, C4=T→F, C5=F, C6=F
    // Path: P07 — N1→N2(F)→N4→N5(F)→N10(T)→N11(F)→N10(F)→N13(F)→N15
    // Loop: 1 iteratie
    // Expected: no exception
    // =========================================================
    @Test
    @DisplayName("TC01 - SC: id valid, 1 ingredient valid → no exception")
    void tc01_sc_retetaValida_noException() {
        // Arrange
        List<IngredientReteta> ingrediente = List.of(
                validIngredient("apa", 2.0)
        );
        Reteta reteta = buildReteta(1, ingrediente);

        // Act + Assert
        assertDoesNotThrow(() -> validator.validate(reteta));
    }

    // =========================================================
    // TC02 — DC/CC: C1=T (id invalid)
    // Input: id=-1, ing=[{apa, 2.0}]
    // C1=T, C2=F, C4=T→F, C5=F, C6=T
    // Path: P02 — N1→N2(T)→N3→N4→N5(F)→N10(T)→N11(F)→N10(F)→N13(T)→N14→N15
    // Loop: 1 iteratie
    // Expected: ValidationException cu "ID invalid"
    // =========================================================
    @Test
    @DisplayName("TC02 - DC/CC C1=T: id=-1 → ValidationException cu mesaj ID invalid")
    void tc02_dc_idInvalid_throwsException() {
        // Arrange
        List<IngredientReteta> ingrediente = List.of(
                validIngredient("apa", 2.0)
        );
        Reteta reteta = buildReteta(-1, ingrediente);

        // Act
        ValidationException ex = assertThrows(ValidationException.class,
                () -> validator.validate(reteta));

        // Assert
        assertTrue(ex.getMessage().contains("Product ID invalid!"));
    }

    // =========================================================
    // TC03 — DC/CC: C2=T, C3=T (ingrediente empty)
    // Input: id=1, ing=[]
    // C1=F, C2=T, C3=T
    // Path: P03 — N1→N2(F)→N4→N5(T)→N6→N7(T)→N8→N15
    // Loop: 0 iteratii
    // Expected: ValidationException cu "Ingrediente empty"
    // =========================================================
    @Test
    @DisplayName("TC03 - DC/CC C2=T: ingrediente=[] → ValidationException cu mesaj Ingrediente empty")
    void tc03_dc_ingredienteEmpty_throwsException() {
        // Arrange
        Reteta reteta = buildReteta(1, new ArrayList<>());

        // Act
        ValidationException ex = assertThrows(ValidationException.class,
                () -> validator.validate(reteta));

        // Assert
        assertTrue(ex.getMessage().contains("Ingrediente empty!"));
    }

    // =========================================================
    // TC04 — MCC: C1=T, C2=T simultan (id invalid + ingrediente empty)
    // Input: id=-1, ing=[]
    // C1=T, C2=T, C3=T
    // Path: P04 — N1→N2(T)→N3→N4→N5(T)→N6→N7(T)→N8→N15
    // Loop: 0 iteratii
    // Expected: ValidationException cu ambele mesaje
    // =========================================================
    @Test
    @DisplayName("TC04 - MCC: id invalid + ingrediente empty → ambele mesaje de eroare")
    void tc04_mcc_idInvalidSiEmpty_throwsException() {
        // Arrange
        Reteta reteta = buildReteta(-1, new ArrayList<>());

        // Act
        ValidationException ex = assertThrows(ValidationException.class,
                () -> validator.validate(reteta));

        // Assert
        assertTrue(ex.getMessage().contains("Product ID invalid!"));
        assertTrue(ex.getMessage().contains("Ingrediente empty!"));
    }

    // =========================================================
    // TC05 — DC/CC: C5=T (cantitate invalida)
    // Input: id=1, ing=[{apa, -1.0}]
    // C1=F, C2=F, C4=T→F, C5=T, C6=T
    // Path: P06 — N1→N2(F)→N4→N5(F)→N10(T)→N11(T)→N12→N10(F)→N13(T)→N14→N15
    // Loop: 1 iteratie
    // Expected: ValidationException cu "cantitate negativa"
    // =========================================================
    @Test
    @DisplayName("TC05 - DC/CC C5=T: cantitate=-1 → ValidationException cu mesaj cantitate negativa")
    void tc05_dc_cantitateInvalida_throwsException() {
        // Arrange
        List<IngredientReteta> ingrediente = List.of(
                validIngredient("apa", -1.0)
        );
        Reteta reteta = buildReteta(1, ingrediente);

        // Act
        ValidationException ex = assertThrows(ValidationException.class,
                () -> validator.validate(reteta));

        // Assert
        assertTrue(ex.getMessage().contains("cantitate negativa sau zero"));
    }

    // =========================================================
    // TC06 — MCC: C2=T cu null (ingrediente null)
    // Input: id=-1, ing=null
    // C1=T, C2=T (null), C3=T
    // Path: P04
    // Loop: 0 iteratii
    // Expected: ValidationException cu ambele mesaje
    // =========================================================
    @Test
    @DisplayName("TC06 - MCC: id invalid + ingrediente=null → ambele mesaje de eroare")
    void tc06_mcc_idInvalidSiNull_throwsException() {
        // Arrange
        Reteta reteta = buildReteta(-1, null);

        // Act
        ValidationException ex = assertThrows(ValidationException.class,
                () -> validator.validate(reteta));

        // Assert
        assertTrue(ex.getMessage().contains("Product ID invalid!"));
        assertTrue(ex.getMessage().contains("Ingrediente empty!"));
    }

    // =========================================================
    // TC07 — LC: loop n=2 iteratii, toate valide
    // Input: id=1, ing=[{apa,2.0}, {suc,3.0}]
    // C1=F, C2=F, C4=T→T→F, C5=F ambele, C6=F
    // Path: P07
    // Loop: 2 iteratii
    // Expected: no exception
    // =========================================================
    @Test
    @DisplayName("TC07 - LC (n=2): 2 ingrediente valide → loop 2 iteratii, no exception")
    void tc07_lc_loop2Iteratii_toateValide_noException() {
        // Arrange
        List<IngredientReteta> ingrediente = new ArrayList<>();
        ingrediente.add(validIngredient("apa", 2.0));
        ingrediente.add(validIngredient("suc", 3.0));
        Reteta reteta = buildReteta(1, ingrediente);

        // Act + Assert
        assertDoesNotThrow(() -> validator.validate(reteta));
    }

    // =========================================================
    // TC08 — LC: loop n=2, ambele cantitati invalide
    // Input: id=1, ing=[{apa,-1.0}, {suc,-2.0}]
    // C1=F, C2=F, C4=T→T→F, C5=T ambele, C6=T
    // Path: P06
    // Loop: 2 iteratii
    // Expected: ValidationException cu 2 mesaje de cantitate
    // =========================================================
    @Test
    @DisplayName("TC08 - LC (n=2): 2 cantitati invalide → 2 mesaje de eroare")
    void tc08_lc_loop2Iteratii_ambeleInvalide_throwsException() {
        // Arrange
        List<IngredientReteta> ingrediente = new ArrayList<>();
        ingrediente.add(validIngredient("apa", -1.0));
        ingrediente.add(validIngredient("suc", -2.0));
        Reteta reteta = buildReteta(1, ingrediente);

        // Act
        ValidationException ex = assertThrows(ValidationException.class,
                () -> validator.validate(reteta));

        // Assert
        assertTrue(ex.getMessage().contains("[apa] cantitate negativa sau zero"));
        assertTrue(ex.getMessage().contains("[suc] cantitate negativa sau zero"));
    }

    // =========================================================
    // TC09 — LC: loop n=2, primul valid al doilea invalid
    // Input: id=1, ing=[{apa,2.0}, {suc,-1.0}]
    // C4=T→T→F, C5=F→T
    // Loop: 2 iteratii
    // Expected: ValidationException cu mesaj pentru suc
    // =========================================================
    @Test
    @DisplayName("TC09 - LC (n=2): primul valid, al doilea invalid → eroare la al 2-lea")
    void tc09_lc_loop2Iteratii_ulTimulInvalid_throwsException() {
        // Arrange
        List<IngredientReteta> ingrediente = new ArrayList<>();
        ingrediente.add(validIngredient("apa", 2.0));
        ingrediente.add(validIngredient("suc", -1.0));
        Reteta reteta = buildReteta(1, ingrediente);

        // Act
        ValidationException ex = assertThrows(ValidationException.class,
                () -> validator.validate(reteta));

        // Assert
        assertTrue(ex.getMessage().contains("[suc] cantitate negativa sau zero"));
        assertFalse(ex.getMessage().contains("[apa]"));
    }
}
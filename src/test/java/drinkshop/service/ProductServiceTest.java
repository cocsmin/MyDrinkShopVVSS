package drinkshop.service;

import static org.junit.jupiter.api.Assertions.*;
import drinkshop.domain.CategorieBautura;
import drinkshop.domain.Product;
import drinkshop.domain.TipBautura;
import drinkshop.repository.Repository;
import drinkshop.service.validator.ProductValidator;
import drinkshop.service.validator.ValidationException;
import org.junit.jupiter.api.*;
        import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Teste Black-Box pentru metoda addProduct (F01).
 *
 * Parametrii investigati: pret si nume
 *   - pret valid: pret > 0
 *   - nume valid: String nenul si nevid
 *
 * Tehnici aplicate: ECP (Equivalence Class Partitioning) + BVA (Boundary Value Analysis)
 * Pattern: AAA (Arrange - Act - Assert)
 */
@DisplayName("F01 - Adaugare Produs: ECP + BVA Tests")
class ProductServiceTest {
    static class InMemoryProductRepository implements Repository<Integer, Product> {
        private final Map<Integer, Product> store = new HashMap<>();

        @Override public Product findOne(Integer id)       { return store.get(id); }
        @Override public List<Product> findAll()           { return new ArrayList<>(store.values()); }
        @Override public Product save(Product p)           { store.put(p.getId(), p); return p; }
        @Override public Product delete(Integer id)        { return store.remove(id); }
        @Override public Product update(Product p)         { store.put(p.getId(), p); return p; }
    }

    private static final int    DUMMY_ID        = 1;
    private static final String DUMMY_NUME      = "Borsec";
    private static final double DUMMY_PRET      = 15.0;
    private static final CategorieBautura DUMMY_CATEGORIE = CategorieBautura.JUICE;
    private static final TipBautura       DUMMY_TIP       = TipBautura.WATER_BASED;

    private ProductValidator validator;
    private ProductService service;

    @BeforeEach
    void setUp() {
        validator = new ProductValidator();
        InMemoryProductRepository repo = new InMemoryProductRepository();
        service = new ProductService(repo);
    }

    @Nested
    @DisplayName("ECP Tests - Equivalence Class Partitioning")
    class ECPTests {

        /**
         * TC1_ECP - Valid
         * EC: pret > 0 (clasa valida), nume este String nevid (clasa valida)
         * Input: id=1, nume="Borsec", pret=15, categorie=Apa, tip=Minerala
         * Expected: produs adaugat cu succes (fara exceptie)
         */
        @Test
        @DisplayName("TC1_ECP - pret valid (>0) si nume valid -> succes")
        void tc1_ecp_pretSiNumeValide_addProduct_success() {
            // Arrange
            Product p = new Product(DUMMY_ID, "Borsec", 15.0, DUMMY_CATEGORIE, DUMMY_TIP);

            // Act + Assert
            assertDoesNotThrow(() -> service.addProduct(p));
            assertNotNull(service.findById(DUMMY_ID));
        }

        /**
         * TC2_ECP - Non-valid
         * EC: pret < 0 (clasa non-valida)
         * Input: id=2, nume="Sprite", pret=-10, categorie=Suc, tip=Carbogazos
         * Expected: ValidationException cu mesaj despre pret invalid
         */
        @Test
        @DisplayName("TC2_ECP - pret negativ -> ValidationException")
        void tc2_ecp_pretNegativ_addProduct_throwsException() {
            // Arrange
            Product p = new Product(2, "Sprite", -10.0, CategorieBautura.JUICE, TipBautura.WATER_BASED);

            // Act
            ValidationException ex = assertThrows(ValidationException.class,
                    () -> service.addProduct(p));

            // Assert
            assertTrue(ex.getMessage().contains("Pret invalid"));
        }

        /**
         * TC3_ECP - Valid
         * EC: pret > 0 (clasa valida), valoare mica de granita
         * Input: id=3, nume="Fanta", pret=1, categorie=Suc, tip=Carbogazos
         * Expected: produs adaugat cu succes
         */
        @Test
        @DisplayName("TC3_ECP - pret=1 (valid, clasa pozitiva) -> succes")
        void tc3_ecp_pretUnu_addProduct_success() {
            // Arrange
            Product p = new Product(3, "Fanta", 1.0, CategorieBautura.JUICE, TipBautura.WATER_BASED);

            // Act + Assert
            assertDoesNotThrow(() -> service.addProduct(p));
            assertEquals(1.0, service.findById(3).getPret());
        }

        /**
         * TC4_ECP - Non-valid
         * EC: pret = 0 (clasa non-valida, limita inferioara exclusa)
         * Input: id=4, nume="Cola", pret=0, categorie=Suc, tip=Carbogazos
         * Expected: ValidationException
         */
        @Test
        @DisplayName("TC4_ECP - pret=0 -> ValidationException")
        void tc4_ecp_pretZero_addProduct_throwsException() {
            // Arrange
            Product p = new Product(4, "Cola", 0.0, CategorieBautura.JUICE, TipBautura.WATER_BASED);

            // Act
            ValidationException ex = assertThrows(ValidationException.class,
                    () -> service.addProduct(p));

            // Assert
            assertTrue(ex.getMessage().contains("Pret invalid"));
        }
    }

    @Nested
    @DisplayName("BVA Tests - Boundary Value Analysis (parametru: pret)")
    class BVATests {

        /**
         * TC1_BVA - Non-valid
         * BVA: pret = -10 (valoare sub limita minima, non-valid)
         * Corelat cu TC2_ECP
         * Expected: ValidationException
         */
        @Test
        @DisplayName("TC1_BVA - pret=-10 (sub limita) -> ValidationException")
        void tc1_bva_pretMinus10_throwsException() {
            // Arrange
            Product p = new Product(2, "Sprite", -10.0, CategorieBautura.JUICE, TipBautura.WATER_BASED);

            // Act
            ValidationException ex = assertThrows(ValidationException.class,
                    () -> service.addProduct(p));

            // Assert
            assertTrue(ex.getMessage().contains("Pret invalid"));
        }

        /**
         * TC2_BVA - Non-valid
         * BVA: pret = 0 (exact la limita inferioara, exclus)
         * Corelat cu TC4_ECP
         * Expected: ValidationException
         */
        @Test
        @DisplayName("TC2_BVA - pret=0 (exact limita inferioara, exclusa) -> ValidationException")
        void tc2_bva_pretZero_throwsException() {
            // Arrange
            Product p = new Product(4, "Cola", 0.0, CategorieBautura.JUICE, TipBautura.WATER_BASED);

            // Act
            ValidationException ex = assertThrows(ValidationException.class,
                    () -> service.addProduct(p));

            // Assert
            assertTrue(ex.getMessage().contains("Pret invalid"));
        }

        /**
         * TC3_BVA - Valid
         * BVA: pret = 1 (prima valoare valida peste limita inferioara)
         * Corelat cu TC3_ECP
         * Expected: succes
         */
        @Test
        @DisplayName("TC3_BVA - pret=1 (prima valoare valida) -> succes")
        void tc3_bva_pretUnu_success() {
            // Arrange
            Product p = new Product(3, "Fanta", 1.0, CategorieBautura.JUICE, TipBautura.WATER_BASED);

            // Act + Assert
            assertDoesNotThrow(() -> service.addProduct(p));
            assertEquals(1.0, service.findById(3).getPret());
        }

        /**
         * TC4_BVA - Valid
         * BVA: pret = Double.MAX_VALUE - 1 (valoare aproape de maxim, valida)
         * Corelat cu TC1_ECP (clasa valida pret > 0)
         * Expected: succes
         */
        @Test
        @DisplayName("TC4_BVA - pret=MAX_VALUE-1 (aproape de maxim) -> succes")
        void tc4_bva_pretMaxMinusUnu_success() {
            // Arrange
            double pretMaxMinus1 = Double.MAX_VALUE - 1;
            Product p = new Product(6, "Cappy", pretMaxMinus1, CategorieBautura.JUICE, TipBautura.WATER_BASED);

            // Act + Assert
            assertDoesNotThrow(() -> service.addProduct(p));
            assertEquals(pretMaxMinus1, service.findById(6).getPret());
        }

        /**
         * TC5_BVA - Valid
         * BVA: pret = Double.MAX_VALUE (valoarea maxima posibila, valida)
         * Expected: succes
         */
        @Test
        @DisplayName("TC5_BVA - pret=MAX_VALUE (valoarea maxima) -> succes")
        void tc5_bva_pretMaxValue_success() {
            // Arrange
            Product p = new Product(7, "Aqua Carpatica", Double.MAX_VALUE, CategorieBautura.JUICE, TipBautura.WATER_BASED);

            // Act + Assert
            assertDoesNotThrow(() -> service.addProduct(p));
            assertEquals(Double.MAX_VALUE, service.findById(7).getPret());
        }
    }

    @ParameterizedTest(name = "pret={0} trebuie sa fie invalid")
    @ValueSource(doubles = {-100.0, -1.0, -0.01, 0.0})
    @DisplayName("ECP/BVA - orice pret <= 0 arunca ValidationException")
    void pretInvalidMultiple_throwsException(double pretInvalid) {
        // Arrange
        Product p = new Product(DUMMY_ID, DUMMY_NUME, pretInvalid, DUMMY_CATEGORIE, DUMMY_TIP);

        // Act + Assert
        assertThrows(ValidationException.class, () -> service.addProduct(p));
    }
}
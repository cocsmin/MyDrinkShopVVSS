package drinkshop.it.service.td.depthfirst;

import drinkshop.domain.*;
import drinkshop.repository.file.FileProductRepository;
import drinkshop.service.ProductService;
import drinkshop.service.validator.ValidationException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.extension.ExtendWith; // ADAUGĂ ASTA
import org.mockito.junit.jupiter.MockitoExtension; // ADAUGĂ ASTA
import java.io.File;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class) // FOARTE IMPORTANT
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProductServiceLevel1RepoIntTest {

    private ProductService productService;
    private FileProductRepository repository;
    private Product productMock;
    private final String testFile = "data/products_it.txt";

    @BeforeEach
    void setUp() throws Exception {
        // 1. Asigurăm existența folderului și fișierului pentru a evita FileNotFoundException
        File folder = new File("data");
        if (!folder.exists()) folder.mkdir();

        File file = new File(testFile);
        if (!file.exists()) file.createNewFile();

        // 2. Inițializăm componentele
        repository = new FileProductRepository(testFile);
        productService = new ProductService(repository);

        // 3. Creăm mock-ul manual aici ca să fim siguri că nu e null
        productMock = mock(Product.class);
    }

    @Test
    @Order(1)
    void addProduct_Level1_Integration_Success() {
        // Antrenăm mock-ul
        when(productMock.getId()).thenReturn(101);
        when(productMock.getNume()).thenReturn("Mock Drink");
        when(productMock.getPret()).thenReturn(15.0);
        when(productMock.getCategorie()).thenReturn(CategorieBautura.JUICE);
        when(productMock.getTip()).thenReturn(TipBautura.WATER_BASED);

        assertDoesNotThrow(() -> productService.addProduct(productMock));

        // Verificăm integrarea cu Repository-ul real
        assertNotNull(repository.findOne(101));
    }

    @Test
    @Order(2)
    void addProduct_Level1_Integration_ValidationFail() {
        // Setăm mock-ul să returneze un nume invalid (gol)
        when(productMock.getNume()).thenReturn("");

        assertThrows(ValidationException.class, () -> productService.addProduct(productMock));
    }
}
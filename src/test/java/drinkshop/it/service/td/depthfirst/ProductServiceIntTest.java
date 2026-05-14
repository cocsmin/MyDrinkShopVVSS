package drinkshop.it.service.td.depthfirst;

import drinkshop.domain.*;
import drinkshop.repository.file.FileProductRepository;
import drinkshop.service.ProductService;
import org.junit.jupiter.api.*;
import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ProductServiceIntTest {

    private ProductService productService;
    private FileProductRepository repository;
    private final String testFile = "data/products_test.txt";

    @BeforeEach
    void setUp() throws java.io.IOException {
        File folder = new File("data");
        if (!folder.exists()) folder.mkdir();

        File file = new File(testFile);
        if (!file.exists()) {
            file.createNewFile();
        } else {
            new java.io.PrintWriter(file).close();
        }

        repository = new FileProductRepository(testFile);
        productService = new ProductService(repository);
    }

    @Test
    void fullIntegration_AddAndRetrieve() {
        Product p = new Product(1, "Pepsi", 7.0, CategorieBautura.JUICE, TipBautura.WATER_BASED);

        productService.addProduct(p);

        // Verificăm integrarea cu fișierul prin findById
        Product found = productService.findById(1);
        assertNotNull(found);
        assertEquals("Pepsi", found.getNume());

        // Verificăm getAll
        List<Product> all = productService.getAllProducts();
        assertEquals(1, all.size());
    }

    @Test
    void fullIntegration_UpdateAndDelete() {
        Product p = new Product(2, "Cola", 5.0, CategorieBautura.JUICE, TipBautura.WATER_BASED);
        productService.addProduct(p);

        productService.updateProduct(2, "Cola Zero", 6.0, CategorieBautura.JUICE, TipBautura.WATER_BASED);
        assertEquals("Cola Zero", productService.findById(2).getNume());

        productService.deleteProduct(2);
        assertNull(productService.findById(2));
    }
}
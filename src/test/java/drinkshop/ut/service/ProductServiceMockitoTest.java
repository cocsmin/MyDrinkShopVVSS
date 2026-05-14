package drinkshop.ut.service;

import drinkshop.domain.*;
import drinkshop.repository.Repository;
import drinkshop.service.ProductService;
import drinkshop.service.validator.ValidationException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceMockitoTest {

    @Mock
    private Repository<Integer, Product> repository;

    @Mock
    private Product product;

    @InjectMocks
    private ProductService productService;

    // --- TESTE ADD / VALIDATION ---

    @Test
    void addProduct_Success() {
        when(product.getId()).thenReturn(1);
        when(product.getNume()).thenReturn("Valid");
        when(product.getPret()).thenReturn(10.0);

        productService.addProduct(product);
        verify(repository, times(1)).save(product);
    }

    @Test
    void addProduct_ThrowsException_WhenInvalid() {
        // Trimitem un produs cu date invalide (nume gol)
        Product invalidP = new Product(1, "", -5, CategorieBautura.JUICE, TipBautura.WATER_BASED);

        assertThrows(ValidationException.class, () -> productService.addProduct(invalidP));
        verify(repository, never()).save(any());
    }

    // --- TESTE FIND / DELETE / UPDATE ---

    @Test
    void findById_CallsRepo() {
        productService.findById(1);
        verify(repository).findOne(1);
    }

    @Test
    void deleteProduct_CallsRepo() {
        productService.deleteProduct(1);
        verify(repository).delete(1);
    }

    @Test
    void updateProduct_Success() {
        productService.updateProduct(1, "New Name", 15.0, CategorieBautura.JUICE, TipBautura.WATER_BASED);
        verify(repository).update(any(Product.class));
    }

    // --- TESTE FILTRARE (Aici atingem ramurile IF pentru coverage) ---

    @Test
    void filterByCategorie_All_ReturnsEverything() {
        productService.filterByCategorie(CategorieBautura.ALL);
        verify(repository).findAll(); // Ramura if (categorie == ALL)
    }

    @Test
    void filterByCategorie_Specific_FiltersList() {
        Product p1 = new Product(1, "A", 10, CategorieBautura.JUICE, TipBautura.WATER_BASED);
        Product p2 = new Product(2, "B", 10, CategorieBautura.TEA, TipBautura.WATER_BASED);
        when(repository.findAll()).thenReturn(Arrays.asList(p1, p2));

        List<Product> result = productService.filterByCategorie(CategorieBautura.JUICE);

        assertEquals(1, result.size());
        assertEquals(CategorieBautura.JUICE, result.get(0).getCategorie());
    }

    @Test
    void filterByTip_All_ReturnsEverything() {
        productService.filterByTip(TipBautura.ALL);
        verify(repository).findAll(); // Ramura if (tip == ALL)
    }

    @Test
    void filterByTip_Specific_FiltersList() {
        Product p1 = new Product(1, "A", 10, CategorieBautura.JUICE, TipBautura.WATER_BASED);
        when(repository.findAll()).thenReturn(Arrays.asList(p1));

        List<Product> result = productService.filterByTip(TipBautura.WATER_BASED);
        assertEquals(1, result.size());
    }
}
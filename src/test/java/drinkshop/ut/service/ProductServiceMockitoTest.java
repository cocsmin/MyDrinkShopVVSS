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

    @Test
    void addProduct_Success() {
        when(product.getId()).thenReturn(1);
        when(product.getNume()).thenReturn("Valid");
        when(product.getPret()).thenReturn(10.0);

        assertDoesNotThrow(() -> productService.addProduct(product));

        verify(repository, times(1)).save(product);
    }

    @Test
    void addProduct_ThrowsException_WhenInvalid() {
        Product mockInvalidP = mock(Product.class);
        when(mockInvalidP.getNume()).thenReturn("");

        ValidationException exception = assertThrows(ValidationException.class, () -> productService.addProduct(mockInvalidP));
        assertNotNull(exception);

        verify(repository, never()).save(any());
    }


    @Test
    void findById_CallsRepo() {
        when(repository.findOne(1)).thenReturn(product);

        Product found = productService.findById(1);

        assertNotNull(found);
        assertSame(product, found);

        verify(repository, times(1)).findOne(1);
    }

    @Test
    void deleteProduct_CallsRepo() {
        when(repository.delete(1)).thenReturn(product);

        productService.deleteProduct(1);

        verify(repository, times(1)).delete(1);
    }

    @Test
    void updateProduct_Success() {
        when(repository.update(any(Product.class))).thenReturn(product);

        productService.updateProduct(1, "New Name", 15.0, CategorieBautura.JUICE, TipBautura.WATER_BASED);

        verify(repository, times(1)).update(any(Product.class));
    }

    @Test
    void filterByCategorie_All_ReturnsEverything() {
        List<Product> mockList = Arrays.asList(product);
        when(repository.findAll()).thenReturn(mockList);

        List<Product> result = productService.filterByCategorie(CategorieBautura.ALL);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(repository, times(1)).findAll();
    }

    @Test
    void filterByCategorie_Specific_FiltersList() {
        Product mockP1 = mock(Product.class);
        when(mockP1.getCategorie()).thenReturn(CategorieBautura.JUICE);

        Product mockP2 = mock(Product.class);
        when(mockP2.getCategorie()).thenReturn(CategorieBautura.TEA);

        when(repository.findAll()).thenReturn(Arrays.asList(mockP1, mockP2));

        List<Product> result = productService.filterByCategorie(CategorieBautura.JUICE);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(CategorieBautura.JUICE, result.get(0).getCategorie());

        verify(repository, times(1)).findAll();
    }

    @Test
    void filterByTip_All_ReturnsEverything() {
        List<Product> mockList = Arrays.asList(product);
        when(repository.findAll()).thenReturn(mockList);

        List<Product> result = productService.filterByTip(TipBautura.ALL);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(repository, times(1)).findAll();
    }

    @Test
    void filterByTip_Specific_FiltersList() {
        Product mockP1 = mock(Product.class);
        when(mockP1.getTip()).thenReturn(TipBautura.WATER_BASED);

        when(repository.findAll()).thenReturn(Arrays.asList(mockP1));

        List<Product> result = productService.filterByTip(TipBautura.WATER_BASED);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(TipBautura.WATER_BASED, result.get(0).getTip());

        verify(repository, times(1)).findAll();
    }
}
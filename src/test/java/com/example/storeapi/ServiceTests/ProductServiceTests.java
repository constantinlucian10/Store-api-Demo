package com.example.storeapi.ServiceTests;

import com.example.storeapi.Model.Order;
import com.example.storeapi.Model.Product;
import com.example.storeapi.Repository.OrderRepository;
import com.example.storeapi.Repository.ProductRepository;
import com.example.storeapi.Service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
public class ProductServiceTests {

    @Mock
    ProductRepository productRepository;

    @Mock
    OrderRepository orderRepository;

    @InjectMocks
    ProductService productService;

    @Test
    public void changePrice_updates_whenExists() {

        Product product = new Product("Phone", 2500);
        product.setProductId(1L);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        when(productRepository.save(any(Product.class))).thenAnswer(i -> i.getArgument(0));

        Product updatedProduct = productService.changePrice(1L, 1500);
        assertEquals(1500, updatedProduct.getPrice());
        verify(productRepository).save(product);
    }

    @Test
    public void orderProductHavingDiscount_saveOrder() {
        Product product = new Product("Phone", 1200);
        product.setProductId(3L);

        when(productRepository.findById(3L)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        Order order = productService.orderWithDiscount(3L, 10);

        assertEquals(10200.0, order.getFinalPrice(), 0.0001);
        assertEquals(10, order.getQuantity());
        assertEquals(3L, order.getProductId());
        verify(orderRepository, times(1)).save(any(Order.class));
    }
}

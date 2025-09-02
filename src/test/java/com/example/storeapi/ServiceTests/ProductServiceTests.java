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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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
    public void changePrice_throws_whenProductNotFound() {

        when(productRepository.findById(19L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> productService.changePrice(19L, 20.0));

        assertTrue(exception.getMessage().toLowerCase().contains("does not exist"));
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    public void changePrice_throws_whenNegativePrice() {

        Product product = new Product("Phone", 2500);
        product.setProductId(3L);

        when(productRepository.findById(3L)).thenReturn(Optional.of(product));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> productService.changePrice(3L, -100.0));
        assertTrue(exception.getMessage().toLowerCase().contains("invalid price"));
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    public void orderProductHavingDiscount_saveOrder() {

        Product product = new Product("Phone", 1200);
        product.setProductId(3L);

        when(productRepository.findById(3L)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        Order order = productService.orderWithDiscount(3L, 10);
        assertEquals(10200.0, order.getFinalPrice());
        assertEquals(10, order.getQuantity());
        assertEquals(3L, order.getProductId());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    public void orderProductWithDiscount_throws_whenQuantityZero() {

        RuntimeException exception = assertThrows(RuntimeException.class, () -> productService.orderWithDiscount(1L, 0));

        assertTrue(exception.getMessage().toLowerCase().contains("invalid quantity"));
        verify(productRepository, never()).findById(anyLong());
        verify(orderRepository, never()).save(any());
    }

    @Test
    public void orderWithDiscount_whenQuantityDiscount() {

        Product product = new Product("Mini car", 700);
        product.setProductId(4L);

        when(productRepository.findById(4L)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        Order order = productService.orderWithDiscount(4L, 10);
        double expected = 700.0 * 10 * (1 - 0.10);
        assertEquals(10, order.getQuantity());
        assertEquals(4L, order.getProductId());
        assertEquals(expected, order.getFinalPrice());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    public void orderWithDiscount_whenPriceDiscount() {

        Product product = new Product("Mini car", 1700);
        product.setProductId(5L);

        when(productRepository.findById(5L)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        Order order = productService.orderWithDiscount(5L, 5);
        double expected = 1700.0 * 5 * (1- 0.05);
        assertEquals(expected, order.getFinalPrice());
        assertEquals(5, order.getQuantity());
        assertEquals(5L, order.getProductId());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    public void orderProduct_whenNoDiscount() {

        Product product = new Product("Mini car", 300);
        product.setProductId(6L);

        when(productRepository.findById(6L)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        Order order = productService.orderWithDiscount(6L, 5);
        double expected  = 300.0 * 5;
        assertEquals(expected, order.getFinalPrice());
        assertEquals(5, order.getQuantity());
        assertEquals(6L, order.getProductId());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    public void findAllProducts_whenReturnsList() {
        Product p1 = new Product("P1", 100);
        Product p2 = new Product("P2", 200);
        List<Product> list = new ArrayList<>();
        list.add(p1);
        list.add(p2);

        when(productRepository.findAll()).thenReturn(list);

        List<Product> result = productService.findAllProducts();
        assertEquals(2, result.size());
        assertEquals(list, result);
        verify(productRepository, times(1)).findAll();
    }
}

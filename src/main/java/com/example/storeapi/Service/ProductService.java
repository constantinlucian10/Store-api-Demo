package com.example.storeapi.Service;

import com.example.storeapi.Model.Order;
import com.example.storeapi.Model.Product;
import com.example.storeapi.Repository.OrderRepository;
import com.example.storeapi.Repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    private final OrderRepository orderRepository;

    public ProductService(ProductRepository productRepository, OrderRepository orderRepository) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    public Product addProduct(Product product) {
        return productRepository.save(product);
    }

    public Optional<Product> findProduct(Long productId) {
        return productRepository.findById(productId);
    }

    public Product changePrice(Long productId, double newPrice) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("The product does not exist!"));
        if (newPrice < 0) throw new RuntimeException("Invalid price!");
        product.setPrice(newPrice);
        return productRepository.save(product);
    }

    public List<Product> findAllProducts() {
        return productRepository.findAll();
    }

    public void deleteProductById(Long id) {
        if(!productRepository.existsById(id)) {
            throw new RuntimeException("The product does not exist, so it cannot be deleted !");
        }
        productRepository.deleteById(id);
    }

    @Transactional
    public Order orderWithDiscount(Long productId, int quantity) {
        if(quantity <= 0) throw new RuntimeException("Invalid quantity !");

        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("The product does not exist !"));

        double totalPrice = product.getPrice() * quantity;

        double discountPercent = 0.0;
        if (quantity >= 10) discountPercent += 0.1;
        if (product.getPrice() > 1000) discountPercent += 0.05;

        double finalPrice = totalPrice * (1 - discountPercent);

        Order order = new Order(product.getProductId(), product.getName(), quantity, finalPrice);

        return orderRepository.save(order);
    }

}

package com.example.storeapi.Controller;

import com.example.storeapi.Model.Order;
import com.example.storeapi.Model.Product;
import com.example.storeapi.Service.ProductService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    private final Logger logger = LoggerFactory.getLogger(ProductController.class);

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/{id}")
    public Product findProduct(@PathVariable Long id) {
        return productService.findProduct(id).orElseThrow(() -> new RuntimeException("Product not found!"));
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping
    public List<Product> listAllProducts() {
        return productService.findAllProducts();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Product> addProduct(@RequestBody @Valid Product product) {
        logger.info("Add product: {}", product.getName());

        Product savedProduct = productService.addProduct(product);

        return ResponseEntity.created(URI.create("/api/products/" + savedProduct.getProductId())).body(savedProduct);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/price")
    public Product changePrice(@PathVariable Long id, @RequestBody UpdatedProduct updatedPoduct){
        return productService.changePrice(id, updatedPoduct.getPrice());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id){
        productService.deleteProductById(id);

        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PostMapping("/{id}/order")
    public Order orderProduct(@PathVariable Long id, @RequestParam(name = "quantity") int quantity) {
        return productService.orderWithDiscount(id, quantity);
    }

    public static class UpdatedProduct {

        private double price;

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

    }

}

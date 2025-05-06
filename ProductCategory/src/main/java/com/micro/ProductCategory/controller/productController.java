package com.micro.ProductCategory.controller;

import java.util.List;
import java.util.Map;

import com.micro.ProductCategory.service.productsServiceImpli;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.micro.ProductCategory.model.Product;
import com.micro.ProductCategory.repository.productsRepository;

@RestController
public class productController {

    @Autowired
    private productsServiceImpli productService;

    @Autowired
    private productsRepository productsRepository;

    @GetMapping("/getallproducts")
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/getproducts")
    public Page<Product> getProducts(@RequestParam int page, @RequestParam int size) {
        return productService.retrivePoducts(page, size);
    }

    // get category details by product id
    @GetMapping("/getproductdetailsbyproid/{productId}")
    public Product getProductDetailsByProId(@PathVariable Long productId) {
        return productsRepository.findCatDtlByProId(productId);
    }

    @GetMapping("/getproducts/{categoryId}/{purchase_sale}")
    public List<Product> getProductsWithCate(@PathVariable Long categoryId, @PathVariable String purchase_sale,
            HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        return productService.retrivePoductsWithCat(categoryId, purchase_sale, authHeader);
    }

    @PostMapping("/saveproduct")
    public String saveProducts(@RequestBody Product product) {

        return productService.saveProduct(product);

    }

    @PutMapping("/deleteproduct/{productId}")
    public String deleteProduct(@PathVariable Long productId) {

        return productService.deletePro(productId);
    }

    @GetMapping("/getproductbyid")
    public Product getProById(@RequestParam Long productId) {
        return productService.getProudctsById(productId);
    }

    @PutMapping("/updateproducts/{productId}")
    public String updateProduct(@PathVariable Long productId, @RequestBody Product product) {
        return productService.updatePro(productId, product);
    }

    @GetMapping("/gettotalproducts")
    public Long getTotalProducts() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Check if authentication is null or the user is not authenticated
        if (authentication == null || !authentication.isAuthenticated()) {
            // return "User is not authenticated!";
        }

        String email = authentication.getName();
        return productService.getProCount(email);
    }
}

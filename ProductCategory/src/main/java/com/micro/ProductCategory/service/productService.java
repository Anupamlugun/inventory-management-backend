package com.micro.ProductCategory.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.micro.ProductCategory.model.Product;

public interface productService {

    String saveProduct(Product product);

    Page<Product> retrivePoducts(int page, int size);

    String deletePro(Long productId);

    String updatePro(Long productId, Product product);

    List<Product> retrivePoductsWithCat(Long categoryId, String purchase_sale, String authHeader);

    Product getProudctsById(Long productId);

    Long getProCount(String email);

    List<Product> getAllProducts();
}

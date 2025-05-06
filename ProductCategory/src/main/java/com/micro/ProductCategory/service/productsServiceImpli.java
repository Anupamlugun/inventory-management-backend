package com.micro.ProductCategory.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.micro.ProductCategory.entity.CategoryEntity;

import com.micro.ProductCategory.entity.productsEntity;
import com.micro.ProductCategory.model.Product;
import com.micro.ProductCategory.model.Stock;
import com.micro.ProductCategory.repository.CategoryRepository;

import com.micro.ProductCategory.repository.productsRepository;

@Service
public class productsServiceImpli implements productService {
    @Autowired
    private productsRepository productsRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private final RestTemplate restTemplate;
    private final SimpMessagingTemplate messagingTemplate;

    productsServiceImpli(RestTemplate restTemplate, SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
        this.restTemplate = restTemplate;
    }

    @Override
    public String saveProduct(Product product) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Check if authentication is null or the user is not authenticated
        if (authentication == null || !authentication.isAuthenticated()) {
            return "User is not authenticated!";
        }

        String email = authentication.getName();

        // Convert product to productsEntity
        productsEntity productsEntity = new productsEntity();
        BeanUtils.copyProperties(product, productsEntity);

        // Find the category by ID, safely extract the value from Optional
        // Optional<CategoryEntity> optionalCategory =
        // categoryRepository.findById(product.getCategoryId());

        Optional<CategoryEntity> optionalCategory = categoryRepository.findByCategoryIdAndEmail(
                product.getCategoryId(),
                email);

        if (!optionalCategory.isPresent()) {
            return "Category not found"; // Early return if category doesn't exist
        }

        // Get all products and check for duplicates based on category and product name
        // List<productsEntity> productsEntities = productsRepository.findAll();
        List<productsEntity> productsEntities = productsRepository.findAllByEmail(email);

        for (productsEntity existingProduct : productsEntities) {
            if (existingProduct.getCategory() != null &&
                    product.getCategoryId().equals(existingProduct.getCategory().getCategoryId()) &&
                    product.getProduct_name().equals(existingProduct.getProduct_name())) {
                return "Products already exist"; // Early return if duplicate is found
            }
        }

        // Set the category and save the product if no duplicates are found
        productsEntity.setCategory(optionalCategory.get());
        productsEntity.setEmail(email); // Set the email from authentication

        // saving to database
        productsRepository.save(productsEntity);

        getProCount(email); // Update the total product count
        return "Product saved successfully"; // Return success message
    }

    @Override
    public Page<Product> retrivePoducts(int page, int size) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Check if authentication is null or the user is not authenticated
        if (authentication == null || !authentication.isAuthenticated()) {
            // return "User is not authenticated!";
        }

        String email = authentication.getName();

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt"));
        // Page<productsEntity> productsPage =
        // productsRepository.findByStatusTrue(pageable);

        Page<productsEntity> productsPage = productsRepository.findByStatusTrueAndEmail(email, pageable);

        return productsPage.map(productsEntity -> {
            Product pro = new Product();
            BeanUtils.copyProperties(productsEntity, pro);

            if (productsEntity.getCategory() != null) {
                pro.setCategoryId(productsEntity.getCategory().getCategoryId());
                pro.setCategory_name(productsEntity.getCategory().getCategory_name());
            }

            return pro;
        });
    }

    @Override
    public List<Product> retrivePoductsWithCat(Long category_id, String purchase_sale, String authHeader) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Check if authentication is null or the user is not authenticated
        if (authentication == null || !authentication.isAuthenticated()) {
            // return "User is not authenticated!";
        }

        String email = authentication.getName();

        String jwt = authHeader.substring(7);
        List<productsEntity> productsList = productsRepository.findByStatusTrue();

        // List<productsEntity> productsList =
        // productsRepository.findByStatusTrueAndEmail(email);

        List<Product> products = new ArrayList<>();

        for (productsEntity productsentity : productsList) {
            if (category_id.equals(productsentity.getCategory().getCategoryId())) {
                Product pro = new Product();

                // for stock availability
                // Optional<StockEntity> sOptional = stockRepository
                // .findByProductIdStockAvailable(productsentity.getProductId());
                // StockEntity stockEntity = sOptional.orElse(null);

                // Stock stockavailablity = restTemplate.getForObject(
                // "http://SUPPLIERSTOCK/getstockavailablity/" + productsentity.getProductId(),
                // Stock.class);

                HttpHeaders headers = new HttpHeaders();
                headers.setBearerAuth(jwt);
                HttpEntity<String> entity = new HttpEntity<>(headers);

                ResponseEntity<Stock> stockAvailabilityResponse = restTemplate.exchange(
                        "http://SUPPLIERSTOCK/getstockavailablity/" + productsentity.getProductId(),
                        HttpMethod.GET,
                        entity,
                        Stock.class);

                Stock stockAvailability = stockAvailabilityResponse.getBody();

                if (stockAvailability != null) {
                    pro.setAvailable(stockAvailability.getAvailable());
                } else {
                    pro.setAvailable(0L);
                }

                pro.setCategoryId(productsentity.getCategory().getCategoryId());
                pro.setCategory_name(productsentity.getCategory().getCategory_name());
                BeanUtils.copyProperties(productsentity, pro);

                // Debugging output
                System.out.println("Product ID: " + productsentity.getProductId() +
                        " Available: " + pro.getAvailable() +
                        " Purchase/Sale: " + purchase_sale);

                // Fixed condition
                if (pro.getAvailable() != null && pro.getAvailable() > 0 &&
                        "sale".equals(purchase_sale)) {

                    products.add(pro);
                    System.out.println(purchase_sale + " this is sale");
                } else if ("purchase".equals(purchase_sale)) {
                    products.add(pro);
                    System.out.println(purchase_sale + " this is purchase");
                }
            }
        }

        return products;

    }

    @Override
    public String deletePro(Long productId) {
        if (!productsRepository.existsById(productId)) {

            return "Product not found";
        }

        productsRepository.updateProductStatus(productId, false);

        return "Products delete";
    }

    @Override
    public String updatePro(Long product_Id, Product product) {

        // Get all products and check for duplicates based on category and product name
        List<productsEntity> productsEntities = productsRepository.findAll();
        for (productsEntity existingProduct : productsEntities) {
            if (product.getCategoryId().equals(existingProduct.getCategory().getCategoryId())
                    && product.getProduct_name().equals(existingProduct.getProduct_name())) {
                return "Products already exist"; // Early return if duplicate is found
            }
        }

        // esle update if product of same category and name not found

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Check if authentication is null or the user is not authenticated
        if (authentication == null || !authentication.isAuthenticated()) {
            return "User is not authenticated!";
        }

        String email = authentication.getName();

        if (productsRepository.existsById(product_Id)) {
            productsEntity productsEntity = new productsEntity();
            BeanUtils.copyProperties(product, productsEntity);
            productsEntity.setProductId(product_Id);

            Optional<CategoryEntity> category = categoryRepository.findById(product.getCategoryId());

            productsEntity.setCategory(category.get());
            productsEntity.setEmail(email); // Set the email from authentication
            productsEntity.setProductId(product_Id);
            productsEntity.setStatus(true); // Set status to true
            productsRepository.save(productsEntity);
            return "Product updated successfully";
        }
        return "Product not found";
    }

    @Override
    public Product getProudctsById(Long product_Id) {
        // Use the repository to find the product by its ID
        Optional<productsEntity> product = productsRepository.findById(product_Id);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Check if authentication is null or the user is not authenticated
        if (authentication == null || !authentication.isAuthenticated()) {
            // return "User is not authenticated!";
        }

        String email = authentication.getName();

        // Optional<productsEntity> product =
        // productsRepository.findByIdAndEmail(product_Id, email);

        if (product.isPresent()) {
            productsEntity productsEntity = product.get();

            Product product2 = new Product();

            product2.setCategoryId(productsEntity.getCategory().getCategoryId());
            product2.setCategory_name(productsEntity.getProduct_name());

            BeanUtils.copyProperties(productsEntity, product2);

            return product2;

        }
        return null;

    }

    @Override
    public Long getProCount(String email) {

        Long count = productsRepository.countByStatusTrueAndEmail(email);
        messagingTemplate.convertAndSend("/topic/totalProducts", count);
        return count;
    }

    @Override
    public List<Product> getAllProducts() {

        List<productsEntity> productsEntities = productsRepository.findByStatusTrue();
        List<Product> products = new ArrayList<>();

        for (productsEntity productsEntity : productsEntities) {
            Product pro = new Product();
            BeanUtils.copyProperties(productsEntity, pro);

            if (productsEntity.getCategory() != null) {
                pro.setCategoryId(productsEntity.getCategory().getCategoryId());
                pro.setCategory_name(productsEntity.getCategory().getCategory_name());
            }

            products.add(pro);
        }

        return products;
    }

}

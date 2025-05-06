package com.micro.ProductCategory.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    private Long productId;
    private Long categoryId;
    private String category_name;
    private String product_name;
    private int product_price;
    private Long available;

    // Constructor that matches the query result
    public Product(String category_name, String product_name, int product_price) {
        this.category_name = category_name;
        this.product_name = product_name;
        this.product_price = product_price;
    }
}

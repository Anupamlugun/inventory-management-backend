package com.micro.SupplierStock.model;

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
public class Stock {
    private Long product_Id;
    private String category_name;
    private String product_name;
    private int product_price;
    private Long purchase;
    private Long sale;
    private Long available;
}

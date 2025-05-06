package com.micro.SupplierStock.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StockSaleUpdateRequest {

    private Long product_Id;
    private Long sale;
    private String Token;
    private String email;
}

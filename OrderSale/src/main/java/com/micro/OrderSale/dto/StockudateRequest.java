package com.micro.OrderSale.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StockudateRequest {

    private Long product_Id;
    private Long purchase;
    private String Token;
    private String email;
}

package com.micro.OrderSale.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StockSaleUdateRequest {

    private Long product_Id;
    private Long sale;
    private String Token;
    private String email;
}

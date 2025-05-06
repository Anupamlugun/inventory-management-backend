package com.micro.OrderSale.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
public class Sale {
    private Long id;
    private String bill;
    private String customer_name;
    private String phone_number;
    private Double sale_grand_total;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDate curren_date;
    private List<SaleItems> items;
}

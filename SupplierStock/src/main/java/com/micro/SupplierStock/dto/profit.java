package com.micro.SupplierStock.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class profit {
    private Double total_purchase_amount;
    private Double total_sale_amount;
    private Double total_profit;
    private Double gst_deduction;
    private Double total_profit_after_gst;
}

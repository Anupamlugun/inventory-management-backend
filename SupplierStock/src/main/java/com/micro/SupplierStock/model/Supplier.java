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
public class Supplier {
    private Long supplier_id;
    private String supplier_name;
    private String supplier_phone;
    private String supplier_email;
    private String supplier_address;
}

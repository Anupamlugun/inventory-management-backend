package com.micro.OrderSale.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestParam;

import com.micro.OrderSale.model.Sale;
import com.micro.OrderSale.model.SaleItems;

public interface SaleService {
    String saveSale(Sale sale, String authHeader);

    // Page<Sale> getSaleReport(@RequestParam int page, @RequestParam int size);

    Page<Sale> getSaleReport(@RequestParam LocalDate startDate, @RequestParam LocalDate endDate,
            @RequestParam int page, @RequestParam int size);

    List<SaleItems> getSaleDetail(Long sale);

    Sale getSaleByBill(String bill);
}

package com.micro.OrderSale.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.micro.OrderSale.model.Sale;
import com.micro.OrderSale.model.SaleItems;
import com.micro.OrderSale.service.SaleServiceImpli;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class SaleController {

    @Autowired
    private SaleServiceImpli saleServiceImpli;

    @PostMapping("/sale")
    public String saveSl(@RequestBody Sale sale, HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        return saleServiceImpli.saveSale(sale, authHeader);
    }

    @GetMapping("getsalesreport")
    public Page<Sale> getSaleRpt(@RequestParam LocalDate startDate, @RequestParam LocalDate endDate,
            @RequestParam int page, @RequestParam int size) {
        return saleServiceImpli.getSaleReport(startDate, endDate, page, size);
    }

    @GetMapping("getsalesreport/{sale}")
    public List<SaleItems> getSaleDtl(@PathVariable Long sale) {
        return saleServiceImpli.getSaleDetail(sale);
    }

    @GetMapping("/sale")
    public Sale getSlbill(@RequestParam String bill) {
        return saleServiceImpli.getSaleByBill(bill);
    }
}

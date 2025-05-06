package com.micro.SupplierStock.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.micro.SupplierStock.dto.StockSaleUpdateRequest;
import com.micro.SupplierStock.dto.StockUpdateRequest;
import com.micro.SupplierStock.dto.profit;
import com.micro.SupplierStock.entity.StockEntity;
import com.micro.SupplierStock.model.Stock;
import com.micro.SupplierStock.repository.StockRepository;
import com.micro.SupplierStock.service.StockService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class StockController {
    @Autowired
    private StockService stockservice;

    @Autowired
    private StockRepository stockRepository;

    @GetMapping("stock")
    public List<Stock> getStk(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        return stockservice.getStock(authHeader);
    }

    @GetMapping("getstockavailablity/{productId}")
    public Optional<StockEntity> getStockAvailablity(@PathVariable Long productId) {

        return stockRepository.findByProductIdStockAvailable(productId);

    }

    @GetMapping("getproductsbystockavailability")
    public List<StockEntity> getProductsByStockAvailability() {
        return stockRepository.findListByAvailableStock();
    }

    @PostMapping("/updatestock")
    public String updateStock(@RequestBody StockUpdateRequest stock) {
        return stockservice.updateStock(stock);
    }

    @PostMapping("/updatesalestock")
    public String updateSaleStock(@RequestBody StockSaleUpdateRequest stock) {
        return stockservice.udateForSaleStock(stock);
    }

    @GetMapping("/updatestockstatus")
    public Map<String, Long> updateStockStatus() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Check if authentication is null or the user is not authenticated
        if (authentication == null || !authentication.isAuthenticated()) {
            // return "User is not authenticated!";
        }

        String email = authentication.getName();

        return stockservice.sendmessagewithwebsocket(email);
    }

    @GetMapping("gettotalprofit")
    public profit getTotalProfit(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Check if authentication is null or the user is not authenticated
        if (authentication == null || !authentication.isAuthenticated()) {
            // return "User is not authenticated!";
        }

        String email = authentication.getName();

        return stockservice.getTotalProfit(authHeader, email);
    }

    @GetMapping("gettopandleastproduct")
    public Map<String, List<Stock>> getTopAndLeastProduct(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Check if authentication is null or the user is not authenticated
        if (authentication == null || !authentication.isAuthenticated()) {
            // return "User is not authenticated!";
        }

        String email = authentication.getName();
        return stockservice.getTopAndLeastProduct(authHeader, email);
    }

    @GetMapping("getlistoflowstock")
    public List<Stock> getListOfLowStock(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        return stockservice.getListofLowStock(authHeader);
    }

    @GetMapping("getListofoutofstock")
    public List<Stock> getListofOutofStock(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        return stockservice.getListofOutofStock(authHeader);
    }
}

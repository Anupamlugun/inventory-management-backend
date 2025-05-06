package com.micro.SupplierStock.service;

import java.util.List;
import java.util.Map;

import com.micro.SupplierStock.dto.StockSaleUpdateRequest;
import com.micro.SupplierStock.dto.StockUpdateRequest;
import com.micro.SupplierStock.dto.profit;
import com.micro.SupplierStock.model.Stock;

public interface StockService {
    List<Stock> getStock(String authHeader);

    String updateStock(StockUpdateRequest stock);

    String udateForSaleStock(StockSaleUpdateRequest stock);

    Map<String, Long> sendmessagewithwebsocket(String email);

    profit getTotalProfit(String authHeader, String email);

    Map<String, List<Stock>> getTopAndLeastProduct(String authHeader, String email);

    List<Stock> getListofLowStock(String authHeader);

    List<Stock> getListofOutofStock(String authHeader);
}

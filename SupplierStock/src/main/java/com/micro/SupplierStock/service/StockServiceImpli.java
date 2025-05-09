package com.micro.SupplierStock.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.micro.SupplierStock.dto.StockSaleUpdateRequest;
import com.micro.SupplierStock.dto.StockUpdateRequest;
import com.micro.SupplierStock.dto.profit;
import com.micro.SupplierStock.entity.StockEntity;
import com.micro.SupplierStock.model.ProCatDtls;
import com.micro.SupplierStock.model.Stock;
import com.micro.SupplierStock.repository.StockRepository;

@Service
public class StockServiceImpli implements StockService {
    @Autowired
    private StockRepository stockRepository;

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;
    private final SimpMessagingTemplate messagingTemplate;

    StockServiceImpli(RestTemplate restTemplate, ObjectMapper mapper, SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
        this.mapper = mapper;
        this.restTemplate = restTemplate;
    }

    @Override
    public List<Stock> getStock(String authHeader) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Check if authentication is null or the user is not authenticated
        if (authentication == null || !authentication.isAuthenticated()) {
            // return "User is not authenticated!";
        }

        String email = authentication.getName();

        String jwt = authHeader.substring(7);
        List<StockEntity> stockEntities = stockRepository.findAllByEmail(email);

        List<Stock> stocks = new ArrayList<>();
        for (StockEntity stockEntity : stockEntities) {
            Stock stock = new Stock();
            BeanUtils.copyProperties(stockEntity, stock);

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(jwt);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<ProCatDtls> proCatDtls = restTemplate.exchange(
                    "http://PRODUCTCATEGORY/getproductdetailsbyproid/" + stockEntity.getProduct_Id(),
                    HttpMethod.GET,
                    entity,
                    ProCatDtls.class);

            stock.setCategory_name(proCatDtls.getBody().getCategory_name());
            stock.setProduct_name(proCatDtls.getBody().getProduct_name());
            stock.setProduct_price(proCatDtls.getBody().getProduct_price());
            stock.setSale(stockEntity.getSaleId());
            stock.setPurchase(stockEntity.getPurchaseId());
            stocks.add(stock);

        }
        return stocks;
    }

    @Override
    public String updateStock(StockUpdateRequest stock) {
        if (stock.getProduct_Id() == null || stock.getPurchase() == null || stock.getEmail() == null) {
            return "Product ID, Purchase quantity and Update time cannot be null";
        }

        Optional<StockEntity> existingStock = stockRepository.findByProductId(stock.getProduct_Id());

        System.out.println("existingStock: " + existingStock);

        if (existingStock.isPresent()) {
            // If product exists, update stock quantity
            StockEntity stockEntityExist = existingStock.get();
            stockEntityExist.purchaseStock(stock.getPurchase());

            stockRepository.save(stockEntityExist);
        } else {
            // If product does not exist, create a new stock entry
            StockEntity newStockEntity = new StockEntity();
            newStockEntity.setProduct_Id(stock.getProduct_Id());
            newStockEntity.purchaseStock(stock.getPurchase());
            newStockEntity.setEmail(stock.getEmail());
            stockRepository.save(newStockEntity);
        }

        return "Purchase stock updated";
    }

    @Override
    public String udateForSaleStock(StockSaleUpdateRequest stock) {
        if (stock.getProduct_Id() == null || stock.getSale() == null || stock.getEmail() == null) {
            return "Product ID and Purchase quantity cannot be null";
        }

        Optional<StockEntity> existingStock = stockRepository.findByProductId(stock.getProduct_Id());

        if (existingStock.isPresent()) {
            // If product exists, update stock quantity
            StockEntity stockEntityExist = existingStock.get();
            stockEntityExist.saleStock(stock.getSale());

            stockRepository.save(stockEntityExist);
        } else {
            // If product does not exist, create a new stock entry
            StockEntity newStockEntity = new StockEntity();
            newStockEntity.setProduct_Id(stock.getProduct_Id());
            newStockEntity.saleStock(stock.getSale());
            newStockEntity.setEmail(stock.getEmail());
            stockRepository.save(newStockEntity);
        }

        return "Sale stock updated";
    }

    @KafkaListener(topics = "purchaseupdatestock", groupId = "supplier-stock")
    public void consume(String record) {
        System.out.println("RAW: " + record);
        try {
            String json = mapper.readValue(record, String.class);
            StockUpdateRequest stockUpdateRequest = mapper.readValue(json,
                    StockUpdateRequest.class);
            System.out.println("JSON: " + json);
            System.out.println("stockUpdateRequest: " + stockUpdateRequest.getEmail());

            String updateResult = updateStock(stockUpdateRequest);
            System.out.println("Stock update message: {}" + updateResult);

            System.out.println("stockUpdateRequest: " + stockUpdateRequest.getProduct_Id());

            // Send message to WebSocket
            sendmessagewithwebsocket(stockUpdateRequest.getEmail());

        } catch (Exception e) {
            // System.err.println("Error processing Kafka message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @KafkaListener(topics = "saleupdatestock", groupId = "supplier-stock")
    public void consumeSale(String record) {
        System.out.println("RAW: " + record);
        try {
            String json = mapper.readValue(record, String.class);
            StockSaleUpdateRequest stockSaleUpdateRequest = mapper.readValue(json, StockSaleUpdateRequest.class);
            System.out.println("JSON: " + json);
            System.out.println("stockSaleUpdateRequest: " + stockSaleUpdateRequest);
            String udateforstock = udateForSaleStock(stockSaleUpdateRequest);
            System.out.println("udateforstock: " + udateforstock);
            // Send message to WebSocket
            sendmessagewithwebsocket(stockSaleUpdateRequest.getEmail());
            getTotalProfit(stockSaleUpdateRequest.getToken(), stockSaleUpdateRequest.getEmail());
        } catch (Exception e) {
            System.err.println("Error processing Kafka message: " + e.getMessage());
        }
    }

    @KafkaListener(topics = "deleteproductfromstock", groupId = "supplier-stock")
    public void consumeDelete(String record) {
        System.out.println("RAW: " + record);
        try {
            String json = mapper.readValue(record, String.class);
            StockUpdateRequest stockUpdateRequest = mapper.readValue(json,
                    StockUpdateRequest.class);
            System.out.println("JSON: " + json);
            System.out.println("stockUpdateRequest: " +
                    stockUpdateRequest.getProduct_Id() + " " +
                    stockUpdateRequest.getEmail());

            // Delete product from stock
            stockRepository.deleteByProductId(stockUpdateRequest.getProduct_Id());

            // Send message to WebSocket
            sendmessagewithwebsocket(stockUpdateRequest.getEmail());

        } catch (Exception e) {
            System.err.println("Error processing Kafka message: " +
                    e.getMessage());

        }
    }

    @Override
    public Map<String, Long> sendmessagewithwebsocket(String email) {

        Long outOfStock = stockRepository.findByOutOfStock(email);
        Long lowOfStock = stockRepository.findByLowOfStock(email);

        Map<String, Long> stockStatus = Map.of("outOfStock", outOfStock,
                "lowOfStock", lowOfStock);

        messagingTemplate.convertAndSend("/topic/stockStatus", stockStatus);

        return stockStatus;
    }

    @Override
    public profit getTotalProfit(String authHeader, String email) {
        String jwt = authHeader.substring(7);
        Double total_purchase_amount = 0.0;
        Double total_sale_amount = 0.0;

        // profit and tax calculation
        Double MIN_GST = 0.05;
        Double MAX_GST = 0.1;
        Double MAX_PROFIT = 0.1;
        Double MIN_PROFIT = 0.05;
        Double GST = 0.1;

        List<StockEntity> stockEntities = stockRepository.findAllByEmail(email);
        for (StockEntity stockEntity : stockEntities) {

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(jwt);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<ProCatDtls> proCatDtls = restTemplate.exchange(
                    "http://PRODUCTCATEGORY/getproductdetailsbyproid/" +
                            stockEntity.getProduct_Id(),
                    HttpMethod.GET,
                    entity,
                    ProCatDtls.class);

            total_purchase_amount += proCatDtls.getBody().getProduct_price() *
                    stockEntity.getSaleId();
            total_sale_amount += proCatDtls.getBody().getProduct_price() > 1000
                    ? (proCatDtls.getBody().getProduct_price() * MAX_GST)
                            + (proCatDtls.getBody().getProduct_price() * MAX_PROFIT)
                            + proCatDtls.getBody().getProduct_price() * stockEntity.getSaleId()
                    : (proCatDtls.getBody().getProduct_price() * MIN_GST)
                            + (proCatDtls.getBody().getProduct_price() * MIN_PROFIT)
                            + proCatDtls.getBody().getProduct_price() * stockEntity.getSaleId();

        }

        profit profit = new profit();
        profit.setTotal_purchase_amount(total_purchase_amount);
        profit.setTotal_sale_amount(total_sale_amount);
        profit.setTotal_profit(total_sale_amount - total_purchase_amount);
        profit.setGst_deduction(GST * profit.getTotal_profit());
        profit.setTotal_profit_after_gst(total_sale_amount - total_purchase_amount -
                profit.getGst_deduction());

        messagingTemplate.convertAndSend("/topic/totalProfit", profit);
        return profit;
    }

    @Override
    public Map<String, List<Stock>> getTopAndLeastProduct(String authHeader, String email) {
        String jwt = authHeader.substring(7);
        List<StockEntity> topproducts = stockRepository.findTop5Products(email);
        List<StockEntity> leastproducts = stockRepository.findLeast5Products(email);

        List<Stock> topProducts = new ArrayList<>();
        List<Stock> leastProducts = new ArrayList<>();

        for (StockEntity top : topproducts) {
            Stock stock = new Stock();
            BeanUtils.copyProperties(top, stock);

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(jwt);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<ProCatDtls> proCatDtls = restTemplate.exchange(
                    "http://PRODUCTCATEGORY/getproductdetailsbyproid/" + top.getProduct_Id(),
                    HttpMethod.GET,
                    entity,
                    ProCatDtls.class);

            stock.setCategory_name(proCatDtls.getBody().getCategory_name());
            stock.setProduct_name(proCatDtls.getBody().getProduct_name());
            stock.setProduct_price(proCatDtls.getBody().getProduct_price());
            stock.setSale(top.getSaleId());
            stock.setPurchase(top.getPurchaseId());
            topProducts.add(stock);

        }
        for (StockEntity least : leastproducts) {
            Stock stock = new Stock();
            BeanUtils.copyProperties(least, stock);

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(jwt);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<ProCatDtls> proCatDtls = restTemplate.exchange(
                    "http://PRODUCTCATEGORY/getproductdetailsbyproid/" + least.getProduct_Id(),
                    HttpMethod.GET,
                    entity,
                    ProCatDtls.class);

            stock.setCategory_name(proCatDtls.getBody().getCategory_name());
            stock.setProduct_name(proCatDtls.getBody().getProduct_name());
            stock.setProduct_price(proCatDtls.getBody().getProduct_price());
            stock.setSale(least.getSaleId());
            stock.setPurchase(least.getPurchaseId());
            leastProducts.add(stock);

        }

        Map<String, List<Stock>> topLeastProducts = Map.of("topProducts", topProducts,
                "leastProducts", leastProducts);

        messagingTemplate.convertAndSend("/topic/topLeastProducts", topLeastProducts);

        return topLeastProducts;
    }

    @Override
    public List<Stock> getListofLowStock(String authHeader) {
        String jwt = authHeader.substring(7);
        List<StockEntity> stockEntities = stockRepository.findListByLowOfStock();

        List<Stock> stocks = new ArrayList<>();
        for (StockEntity stockEntity : stockEntities) {
            Stock stock = new Stock();
            BeanUtils.copyProperties(stockEntity, stock);

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(jwt);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<ProCatDtls> proCatDtls = restTemplate.exchange(
                    "http://PRODUCTCATEGORY/getproductdetailsbyproid/" + stockEntity.getProduct_Id(),
                    HttpMethod.GET,
                    entity,
                    ProCatDtls.class);

            stock.setCategory_name(proCatDtls.getBody().getCategory_name());
            stock.setProduct_name(proCatDtls.getBody().getProduct_name());
            stock.setProduct_price(proCatDtls.getBody().getProduct_price());
            stock.setSale(stockEntity.getSaleId());
            stock.setPurchase(stockEntity.getPurchaseId());
            stocks.add(stock);

        }
        return stocks;
    }

    @Override
    public List<Stock> getListofOutofStock(String authHeader) {
        String jwt = authHeader.substring(7);
        List<StockEntity> stockEntities = stockRepository.findListByOutOfStock();

        List<Stock> stocks = new ArrayList<>();
        for (StockEntity stockEntity : stockEntities) {
            Stock stock = new Stock();
            BeanUtils.copyProperties(stockEntity, stock);

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(jwt);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<ProCatDtls> proCatDtls = restTemplate.exchange(
                    "http://PRODUCTCATEGORY/getproductdetailsbyproid/" + stockEntity.getProduct_Id(),
                    HttpMethod.GET,
                    entity,
                    ProCatDtls.class);

            stock.setCategory_name(proCatDtls.getBody().getCategory_name());
            stock.setProduct_name(proCatDtls.getBody().getProduct_name());
            stock.setProduct_price(proCatDtls.getBody().getProduct_price());
            stock.setSale(stockEntity.getSaleId());
            stock.setPurchase(stockEntity.getPurchaseId());
            stocks.add(stock);

        }
        return stocks;
    }

}

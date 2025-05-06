package com.micro.OrderSale.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.micro.OrderSale.dto.StockudateRequest;
import com.micro.OrderSale.entity.PurchaseOrderEnitiy;
import com.micro.OrderSale.entity.PurchaseOrderItemsEntity;
import com.micro.OrderSale.model.PurchaseOrder;
import com.micro.OrderSale.model.PurchaseOrderItem;

import com.micro.OrderSale.model.Supplier;
import com.micro.OrderSale.repository.PurchaseOrderItemsRepository;
import com.micro.OrderSale.repository.PurchaseOrderRepository;

@Service
public class PurchaseOrderServiceImpli implements PurchaseOrderService {

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private PurchaseOrderItemsRepository purchaseOrderItemsRepository;

    private final RestTemplate restTemplate;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper mapper;

    PurchaseOrderServiceImpli(RestTemplate restTemplate, KafkaTemplate<String, Object> kafkaTemplate,
            ObjectMapper mapper) {
        this.mapper = mapper;
        this.kafkaTemplate = kafkaTemplate;
        this.restTemplate = restTemplate;
    }

    @Override
    public String savePurchaseOrder(PurchaseOrder purchaseOrder, String authHeader) {
        String jwt = authHeader.substring(7);
        PurchaseOrderEnitiy purchaseOrderEnitiy = new PurchaseOrderEnitiy();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwt);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Supplier> supplier = restTemplate.exchange(
                "http://SUPPLIERSTOCK/getsupplierbyid/" + purchaseOrder.getSupplier_id(),
                HttpMethod.GET, entity, Supplier.class);

        if (supplier != null) {
            purchaseOrderEnitiy.setSupplierId(supplier.getBody().getSupplier_id());

        } else {
            return "Supplier not found!";
        }

        // Set the other fields
        purchaseOrderEnitiy.setGrandTotal(purchaseOrder.getGrand_total());

        purchaseOrderEnitiy.setPurchaseInvoice(purchaseOrder.getPurchaseInvoice());

        purchaseOrderEnitiy.setPurchaseDate(purchaseOrder.getPurchaseDate());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Check if authentication is null or the user is not authenticated
        if (authentication == null || !authentication.isAuthenticated()) {
            return "User is not authenticated!";
        }

        String email = authentication.getName();

        purchaseOrderEnitiy.setEmail(email);

        // Save the purchase order FIRST
        purchaseOrderEnitiy = purchaseOrderRepository.save(purchaseOrderEnitiy);

        if (purchaseOrder.getItems() != null) {
            for (PurchaseOrderItem purchaseOrderItem : purchaseOrder.getItems()) {

                PurchaseOrderItemsEntity purchaseOrderItemsEntity = new PurchaseOrderItemsEntity();

                // stock updation

                StockudateRequest stockudateRequest = new StockudateRequest();

                purchaseOrderItemsEntity.setProductId(purchaseOrderItem.getProduct_Id());// purchase order item entity

                stockudateRequest.setProduct_Id(purchaseOrderItem.getProduct_Id());
                stockudateRequest.setPurchase(purchaseOrderItem.getItem_qty());
                stockudateRequest.setToken(authHeader);
                stockudateRequest.setEmail(email);

                // String url = "http://SUPPLIERSTOCK/updatestock";

                // // Set Headers
                // HttpHeaders headers = new HttpHeaders();
                // headers.setContentType(MediaType.APPLICATION_JSON);

                // // Wrap DTO in HttpEntity
                // HttpEntity<StockudateRequest> requestEntity = new
                // HttpEntity<>(stockudateRequest, headers);

                // // Send POST request
                // String response = restTemplate.postForObject(url, requestEntity,
                // String.class);

                // System.out.println(response);
                //////////////////////////////////
                /// now with kafka

                try {
                    String json = mapper.writeValueAsString(stockudateRequest);
                    kafkaTemplate.send("purchaseupdatestock", json);

                    System.out.println("Kafka message sent: " + json);

                } catch (Exception e) {
                    System.out.println("Kafka  and object mapper error" + e.getMessage());
                }

                /// //////////////////////////////

                purchaseOrderItemsEntity.setPurchaseOrder(purchaseOrderEnitiy);

                // Copy other properties
                BeanUtils.copyProperties(purchaseOrderItem, purchaseOrderItemsEntity);
                purchaseOrderItemsEntity.setItemQty(purchaseOrderItem.getItem_qty());
                purchaseOrderItemsEntity.setItemTotalPrice(purchaseOrderItem.getItem_total_price());

                // Save Item
                purchaseOrderItemsRepository.save(purchaseOrderItemsEntity);

            }
        }

        return "Purchased order saved with invoice no. " + purchaseOrder.getPurchaseInvoice();

    }

    @Override
    public Long getPurchaseCount() {
        return purchaseOrderRepository.count();
    }

    @Override
    public Page<PurchaseOrder> getPurchaseReport(LocalDate startDate, LocalDate endDate, int page, int size,
            String authHeader) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Check if authentication is null or the user is not authenticated
        if (authentication == null || !authentication.isAuthenticated()) {
            // return "User is not authenticated!";
        }

        String email = authentication.getName();

        String jwt = authHeader.substring(7);
        Pageable pageable;
        Page<PurchaseOrderEnitiy> purchaseOrderEnitiy;

        pageable = PageRequest.of(page, size);
        purchaseOrderEnitiy = purchaseOrderRepository.findAllByUpdatedAtBetween(startDate, endDate, email,
                pageable);

        List<PurchaseOrder> purchaseOrders = new ArrayList<>();
        for (PurchaseOrderEnitiy purchaseOrderEnitiy2 : purchaseOrderEnitiy.getContent()) {
            PurchaseOrder purchaseOrder = new PurchaseOrder();

            purchaseOrder.setSupplier_id(purchaseOrderEnitiy2.getSupplierId());
            purchaseOrder.setGrand_total(purchaseOrderEnitiy2.getGrandTotal());

            // get supplier name

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(jwt);
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<Supplier> supplier = restTemplate.exchange(
                    "http://SUPPLIERSTOCK/getsupplierbyid/" + purchaseOrderEnitiy2.getSupplierId(),
                    HttpMethod.GET, entity, Supplier.class);

            purchaseOrder.setSupplier_name(supplier.getBody().getSupplier_name());

            BeanUtils.copyProperties(purchaseOrderEnitiy2, purchaseOrder);
            purchaseOrders.add(purchaseOrder);

        }
        return new PageImpl<>(purchaseOrders, pageable,
                purchaseOrderEnitiy.getTotalElements());

    }

    @Override
    public List<PurchaseOrderItem> getPurchaseDetail(Long purchase_id) {

        List<PurchaseOrderItemsEntity> purchaseOrderItemsEntities = purchaseOrderItemsRepository
                .findByPurchase_order(purchase_id);

        List<PurchaseOrderItem> purchaseOrderItems = new ArrayList<>();

        for (PurchaseOrderItemsEntity purchaseOrderItemsEntity : purchaseOrderItemsEntities) {
            PurchaseOrderItem purchaseOrderItem = new PurchaseOrderItem();

            purchaseOrderItem.setProduct_Id(purchaseOrderItemsEntity.getProductId());

            purchaseOrderItem.setItem_qty(purchaseOrderItemsEntity.getItemQty());
            purchaseOrderItem.setItem_total_price(purchaseOrderItemsEntity.getItemTotalPrice());

            BeanUtils.copyProperties(purchaseOrderItemsEntity, purchaseOrderItem);
            purchaseOrderItems.add(purchaseOrderItem);

        }
        return purchaseOrderItems;

    }

    @Override
    public PurchaseOrder getPurchaseByInvoice(String purchase_order, String authHeader) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Check if authentication is null or the user is not authenticated
        if (authentication == null || !authentication.isAuthenticated()) {
            // return "User is not authenticated!";
        }

        String email = authentication.getName();

        String jwt = authHeader.substring(7);

        Optional<PurchaseOrderEnitiy> pOptional = purchaseOrderRepository.findPurchaseByInvoice(purchase_order);

        if (pOptional.isPresent()) {
            PurchaseOrder purchaseOrder = new PurchaseOrder();

            BeanUtils.copyProperties(pOptional.get(), purchaseOrder);
            purchaseOrder.setSupplier_id(pOptional.get().getSupplierId());

            // get supplier name
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(jwt);
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<Supplier> supplier = restTemplate.exchange(
                    "http://SUPPLIERSTOCK/getsupplierbyid/" + pOptional.get().getSupplierId(),
                    HttpMethod.GET, entity, Supplier.class);

            purchaseOrder.setSupplier_name(supplier.getBody().getSupplier_name());
            purchaseOrder.setGrand_total(pOptional.get().getGrandTotal());
            return purchaseOrder;
        }
        return null;

    }

}

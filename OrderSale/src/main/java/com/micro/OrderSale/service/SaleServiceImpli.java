package com.micro.OrderSale.service;

import java.time.LocalDate;
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
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.micro.OrderSale.dto.StockSaleUdateRequest;
import com.micro.OrderSale.entity.SaleEntity;
import com.micro.OrderSale.entity.SaleItemsEntity;
import com.micro.OrderSale.model.Sale;
import com.micro.OrderSale.model.SaleItems;
import com.micro.OrderSale.repository.SaleItemsRepository;
import com.micro.OrderSale.repository.SaleRepository;

@Service
public class SaleServiceImpli implements SaleService {

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    SaleServiceImpli(RestTemplate restTemplate, KafkaTemplate<String, Object> kafkaTemplate, ObjectMapper mapper) {
        this.mapper = mapper;
        this.kafkaTemplate = kafkaTemplate;
        this.restTemplate = restTemplate;
    }

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private SaleItemsRepository saleItemsRepository;

    @Override
    public String saveSale(Sale sale, String authHeader) {

        SaleEntity saleEntity = new SaleEntity();

        long salecount = saleRepository.count();

        LocalDate currentDate = LocalDate.now();
        String formattedDate = currentDate.toString().replace("-", "");

        String bill = "BILL" + formattedDate + salecount;

        BeanUtils.copyProperties(sale, saleEntity);
        saleEntity.setBill(bill);
        saleEntity.setCustomerName(sale.getCustomer_name());
        saleEntity.setPhoneNumber(sale.getPhone_number());
        saleEntity.setSaleGrandTotal(sale.getSale_grand_total());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Check if authentication is null or the user is not authenticated
        if (authentication == null || !authentication.isAuthenticated()) {
            return "User is not authenticated!";
        }

        String email = authentication.getName();

        saleEntity.setEmail(email);
        saleRepository.save(saleEntity);

        if (sale.getItems() != null) {
            for (SaleItems sale2 : sale.getItems()) {
                SaleItemsEntity saleItemsEntity = new SaleItemsEntity();

                // Optional<productsEntity> optProductEntity = productsRepository
                // .findById(sale2.getProductId());
                // optProductEntity.ifPresent(saleItemsEntity::setProductId);
                saleItemsEntity.setProductId(sale2.getProduct_Id());
                saleItemsEntity.setSale(saleEntity);
                saleItemsEntity.setItemQty(sale2.getItem_qty());
                saleItemsEntity.setItemTotalPrice(sale2.getItem_total_price());

                // for stock need kafka

                StockSaleUdateRequest saleUdateRequest = new StockSaleUdateRequest();

                saleUdateRequest.setProduct_Id(saleItemsEntity.getProductId());
                saleUdateRequest.setSale(saleItemsEntity.getItemQty());
                saleUdateRequest.setToken(authHeader);
                saleUdateRequest.setEmail(email);
                // String url = "http://SUPPLIERSTOCK/updatesalestock";

                // // Set Header
                // HttpHeaders headers = new HttpHeaders();
                // headers.setContentType(MediaType.APPLICATION_JSON);

                // // Wrap DTO in HttpEntity
                // HttpEntity<StockSaleUdateRequest> rHttpEntity = new
                // HttpEntity<>(saleUdateRequest, headers);

                // // Send POST request
                // String response = restTemplate.postForObject(url, rHttpEntity, String.class);
                // System.out.println(response);

                /////////////////////////

                /// with kafka
                try {
                    String json = mapper.writeValueAsString(saleUdateRequest);
                    kafkaTemplate.send("saleupdatestock", json);
                } catch (Exception e) {
                    System.out.println("Kafka  and object mapper error" + e.getMessage());
                }
                ///

                BeanUtils.copyProperties(sale2, saleItemsEntity);
                saleItemsRepository.save(saleItemsEntity);

            }
        }

        return "Sale Submitted with invoice no. " + bill;
    }

    @Override
    public Page<Sale> getSaleReport(LocalDate startDate, LocalDate endDate, int page, int size) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Check if authentication is null or the user is not authenticated
        if (authentication == null || !authentication.isAuthenticated()) {
            // return "User is not authenticated!";
        }

        String email = authentication.getName();

        Pageable pageable;
        Page<SaleEntity> saleOrderEntity;

        pageable = PageRequest.of(page, size);
        saleOrderEntity = saleRepository.findAllByUpdatedAtBetween(startDate,
                endDate, email, pageable);

        List<Sale> saleOrders = new ArrayList<>();
        for (SaleEntity saleOrderEntity2 : saleOrderEntity.getContent()) {
            Sale saleOrder = new Sale();

            saleOrder.setCurren_date(saleOrderEntity2.getSaleDate());
            saleOrder.setSale_grand_total(saleOrderEntity2.getSaleGrandTotal());
            saleOrder.setCustomer_name(saleOrderEntity2.getCustomerName());
            saleOrder.setPhone_number(saleOrderEntity2.getPhoneNumber());

            BeanUtils.copyProperties(saleOrderEntity2, saleOrder);
            saleOrders.add(saleOrder);
        }
        return new PageImpl<>(saleOrders, pageable,
                saleOrderEntity.getTotalElements());

    }

    @Override
    public List<SaleItems> getSaleDetail(Long sale) {
        List<SaleItemsEntity> saleOrderItemsEntities = saleItemsRepository.findBySale_order(sale);

        List<SaleItems> saleOrderItems = new ArrayList<>();

        for (SaleItemsEntity saleOrderItemsEntity : saleOrderItemsEntities) {
            SaleItems saleOrderItem = new SaleItems();

            saleOrderItem.setProduct_Id(saleOrderItemsEntity.getProductId());
            saleOrderItem.setItem_qty(saleOrderItemsEntity.getItemQty());
            saleOrderItem.setItem_total_price(saleOrderItemsEntity.getItemTotalPrice());
            saleOrderItem.setSale_id(saleOrderItemsEntity.getId());
            BeanUtils.copyProperties(saleOrderItemsEntity, saleOrderItem);
            saleOrderItems.add(saleOrderItem);
        }
        return saleOrderItems;

    }

    @Override
    public Sale getSaleByBill(String bill) {

        Optional<SaleEntity> saleEntity = saleRepository.findSaleByInvoice(bill);

        if (saleEntity.isPresent()) {

            Sale sale = new Sale();
            BeanUtils.copyProperties(saleEntity.get(), sale);
            sale.setCurren_date(saleEntity.get().getSaleDate());
            sale.setPhone_number(saleEntity.get().getPhoneNumber());
            sale.setSale_grand_total(saleEntity.get().getSaleGrandTotal());
            sale.setCustomer_name(saleEntity.get().getCustomerName());
            return sale;
        }
        return null;

    }
}

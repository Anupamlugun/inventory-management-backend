package com.micro.OrderSale.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestParam;

import com.micro.OrderSale.model.PurchaseOrder;
import com.micro.OrderSale.model.PurchaseOrderItem;

public interface PurchaseOrderService {
    Long getPurchaseCount();

    String savePurchaseOrder(PurchaseOrder purchaseOrder, String authHeader);

    Page<PurchaseOrder> getPurchaseReport(@RequestParam LocalDate startDate, @RequestParam LocalDate endDate,
            @RequestParam int page, @RequestParam int size, String authHeader);

    List<PurchaseOrderItem> getPurchaseDetail(Long id);

    PurchaseOrder getPurchaseByInvoice(String purchase_order, String authHeader);
}

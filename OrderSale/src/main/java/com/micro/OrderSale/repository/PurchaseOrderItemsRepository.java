package com.micro.OrderSale.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.micro.OrderSale.entity.PurchaseOrderItemsEntity;

@Repository
public interface PurchaseOrderItemsRepository extends JpaRepository<PurchaseOrderItemsEntity, Long> {

    @Query(value = "SELECT * FROM purchase_order_items WHERE purchase_order_id = :purchaseId", nativeQuery = true)
    List<PurchaseOrderItemsEntity> findByPurchase_order(@Param("purchaseId") Long purchase_id);
}
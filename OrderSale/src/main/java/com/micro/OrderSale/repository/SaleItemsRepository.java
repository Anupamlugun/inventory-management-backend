package com.micro.OrderSale.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.micro.OrderSale.entity.SaleItemsEntity;

public interface SaleItemsRepository extends JpaRepository<SaleItemsEntity, Long> {
    @Query(value = "SELECT * FROM sale_items WHERE sale_id = :sale", nativeQuery = true)
    List<SaleItemsEntity> findBySale_order(@Param("sale") Long sale);
}

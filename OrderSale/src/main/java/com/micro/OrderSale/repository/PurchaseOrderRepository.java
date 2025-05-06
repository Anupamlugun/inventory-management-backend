package com.micro.OrderSale.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.micro.OrderSale.entity.PurchaseOrderEnitiy;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrderEnitiy, Long> {

    @Query(value = "SELECT * FROM purchase_order  WHERE purchase_date BETWEEN :startDate AND :endDate AND email = :email", nativeQuery = true)
    Page<PurchaseOrderEnitiy> findAllByUpdatedAtBetween(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("email") String email,
            Pageable pageable);

    @Query(value = "SELECT * FROM purchase_order WHERE purchase_invoice = :purchase_invoice", nativeQuery = true)
    Optional<PurchaseOrderEnitiy> findPurchaseByInvoice(@Param("purchase_invoice") String purchase_invoice);
}
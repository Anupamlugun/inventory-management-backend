package com.micro.SupplierStock.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "stock")
public class StockEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_Id", nullable = false)
    private Long product_Id;

    @Column(name = "purchase_id", nullable = false)
    private Long purchaseId = 0L;

    @Column(name = "sale_id", nullable = false)
    private Long saleId = 0L;

    @Column(name = "available", nullable = false)
    private Long available = 0L;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "email", nullable = false)
    private String email;

    // Method to update stock after purchase
    public void purchaseStock(Long quantity) {
        this.purchaseId += quantity;
        this.available += quantity;
    }

    // Method to update stock after sale
    public void saleStock(Long quantity) {
        this.saleId += quantity;
        this.available -= quantity;
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

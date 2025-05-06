package com.micro.SupplierStock.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.micro.SupplierStock.entity.SupplierEntity;

import jakarta.transaction.Transactional;

@Repository
public interface SupplierRepository extends JpaRepository<SupplierEntity, Long> {

    @Modifying
    @Transactional
    @Query(value = "UPDATE supplier SET status = :status WHERE supplier_id = :supplierId", nativeQuery = true)
    void updateSupplierStatus(@Param("supplierId") Long supplierId, @Param("status") Boolean status);

    List<SupplierEntity> findByStatusTrueAndEmail(String email);

    Collection<SupplierEntity> findByStatusTrue();

    List<SupplierEntity> findAllByEmail(String email);
}
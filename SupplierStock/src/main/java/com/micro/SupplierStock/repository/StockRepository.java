package com.micro.SupplierStock.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.micro.SupplierStock.entity.StockEntity;

@Repository
public interface StockRepository extends JpaRepository<StockEntity, Long> {
    @Query(value = "SELECT * FROM stock WHERE product_id = :product_Id LIMIT 1", nativeQuery = true)
    Optional<StockEntity> findByProductId(@Param("product_Id") Long product_Id);

    @Query(value = "SELECT * FROM stock WHERE product_id = :product_Id LIMIT 1", nativeQuery = true)
    Optional<StockEntity> findByProductIdStockAvailable(@Param("product_Id") Long product_Id);

    @Query(value = "SELECT COUNT(*) FROM stock  WHERE available = 0 AND email = :email", nativeQuery = true)
    Long findByOutOfStock(@Param("email") String email);

    @Query(value = "SELECT COUNT(*) FROM stock  WHERE available < 5 AND email = :email", nativeQuery = true)
    Long findByLowOfStock(@Param("email") String email);

    @Query(value = "SELECT * FROM stock WHERE email = :email ORDER BY sale_id DESC LIMIT 5  ", nativeQuery = true)
    List<StockEntity> findTop5Products(@Param("email") String email);

    @Query(value = "SELECT * FROM stock WHERE email = :email  ORDER BY sale_id ASC LIMIT 5 ", nativeQuery = true)
    List<StockEntity> findLeast5Products(@Param("email") String email);

    @Query(value = "SELECT * FROM stock  WHERE available = 0", nativeQuery = true)
    List<StockEntity> findListByOutOfStock();

    @Query(value = "SELECT * FROM stock  WHERE available < 5", nativeQuery = true)
    List<StockEntity> findListByLowOfStock();

    @Query(value = "SELECT * FROM stock  WHERE available > 0", nativeQuery = true)
    List<StockEntity> findListByAvailableStock();

    List<StockEntity> findAllByEmail(String string);

}

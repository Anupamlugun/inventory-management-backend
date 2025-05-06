package com.micro.ProductCategory.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.micro.ProductCategory.entity.productsEntity;
import com.micro.ProductCategory.model.Product;

import jakarta.transaction.Transactional;

@Repository
public interface productsRepository extends JpaRepository<productsEntity, Long> {

        public void save(Product product);

        @Modifying
        @Transactional
        @Query(value = "UPDATE products SET status = :status WHERE product_id = :productId", nativeQuery = true)
        void updateProductStatus(@Param("productId") Long productId, @Param("status") Boolean status);

        Page<productsEntity> findByStatusTrue(Pageable pageable);

        List<productsEntity> findByStatusTrue();

        @Query(value = "SELECT c.category_name, p.product_name, p.product_price " +
                        "FROM products p " +
                        "JOIN category c ON p.category_id = c.category_id " +
                        "WHERE p.product_id = :productId", nativeQuery = true)
        Product findCatDtlByProId(@Param("productId") Long productId);

        @Query(value = " select count(*) from products where status = true", nativeQuery = true)
        Long countByStatusTrue();

        Page<productsEntity> findByStatusTrueAndEmail(
                        String email,
                        Pageable pageable);

        List<productsEntity> findByStatusTrueAndEmail(String email);

        public Optional<productsEntity> findByProductIdAndEmail(Long productId, String email);

        public List<productsEntity> findAllByEmail(String email);

        public Long countByStatusTrueAndEmail(String email);

}

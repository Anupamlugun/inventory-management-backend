package com.micro.ProductCategory.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.micro.ProductCategory.entity.CategoryEntity;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
    Page<CategoryEntity> findByEmail(String email, Pageable pageable);

    List<CategoryEntity> findByEmailOrderByUpdatedAtDesc(String email);

    List<CategoryEntity> findAllByEmailOrderByUpdatedAtDesc(String email);

    Optional<CategoryEntity> findByCategoryIdAndEmail(Long categoryId, String email);
}
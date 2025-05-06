package com.micro.ProductCategory.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestParam;

import com.micro.ProductCategory.model.Category;

public interface CategoryService {
    String saveCategory(Category category);

    List<Category> getCategories();

    String updateCategory(Long categoryId, Category category);

    Page<Category> getCategorypage(@RequestParam int page, @RequestParam int size);

}

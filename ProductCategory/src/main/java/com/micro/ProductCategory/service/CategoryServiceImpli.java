package com.micro.ProductCategory.service;

import java.io.Console;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.micro.ProductCategory.entity.CategoryEntity;
import com.micro.ProductCategory.model.Category;
import com.micro.ProductCategory.repository.CategoryRepository;

@Service
public class CategoryServiceImpli implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public String saveCategory(Category category) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Check if authentication is null or the user is not authenticated
        if (authentication == null || !authentication.isAuthenticated()) {
            return "User is not authenticated!";
        }

        String email = authentication.getName();

        List<CategoryEntity> categoryEntities = categoryRepository.findAllByEmailOrderByUpdatedAtDesc(email);

        // Check if category with the same name already exists
        for (CategoryEntity categoryEnt : categoryEntities) {
            if (categoryEnt.getCategory_name().equals(category.getCategory_name())) {
                return "Category already exists"; // Category exists, no need to save
            }
        }

        System.out.println("This is Email: " + email);

        // If not found, save the new category
        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setEmail(email);
        BeanUtils.copyProperties(category, categoryEntity);
        categoryRepository.save(categoryEntity);

        return "Category saved";

    }

    @Override
    public List<Category> getCategories() {
        // List<CategoryEntity> categoryEntities =
        // categoryRepository.findAll(Sort.by(Sort.Order.desc("updatedAt")));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Check if authentication is null or the user is not authenticated
        if (authentication == null || !authentication.isAuthenticated()) {
            // return "User is not authenticated!";
        }

        String email = authentication.getName();

        List<CategoryEntity> categoryEntities = categoryRepository.findByEmailOrderByUpdatedAtDesc(email);

        List<Category> category = new ArrayList<>();

        for (CategoryEntity categories : categoryEntities) {
            Category cate = new Category();
            BeanUtils.copyProperties(categories, cate);
            category.add(cate);

        }
        return category;

    }

    @Override
    public Page<Category> getCategorypage(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("updatedAt")));

        // Page<CategoryEntity> cPage = categoryRepository.findAll(pageable);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Check if authentication is null or the user is not authenticated
        if (authentication == null || !authentication.isAuthenticated()) {
            // return "User is not authenticated!";
        }

        String email = authentication.getName();

        Page<CategoryEntity> cPage = categoryRepository.findByEmail(email, pageable);

        List<Category> category = new ArrayList<>();

        for (CategoryEntity cEntity : cPage.getContent()) {
            Category cate = new Category();
            BeanUtils.copyProperties(cEntity, cate);
            category.add(cate);
        }
        // Return the paginated result wrapped in a Page object
        return new PageImpl<>(category, pageable, cPage.getTotalElements());
    }

    @Override
    public String updateCategory(Long categoryId, Category category) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Check if authentication is null or the user is not authenticated
        if (authentication == null || !authentication.isAuthenticated()) {
            return "User is not authenticated!";
        }

        String email = authentication.getName();

        List<CategoryEntity> categoryEntities = categoryRepository.findAllByEmailOrderByUpdatedAtDesc(email);

        // Check if category with the same name already exists
        for (CategoryEntity categoryEnt : categoryEntities) {
            if (categoryEnt.getCategory_name().equals(category.getCategory_name())) {
                return "Category already exists"; // Category exists, no need to save
            }
        }

        // if not update
        if (categoryRepository.existsById(categoryId)) {
            CategoryEntity categoryEntity = new CategoryEntity();
            BeanUtils.copyProperties(category, categoryEntity);
            categoryEntity.setCategoryId(categoryId);
            categoryEntity.setEmail(email);
            categoryRepository.save(categoryEntity);
            return "Category updated successfully";
        }
        return "category not found";
    }

}

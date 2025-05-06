package com.micro.ProductCategory.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.micro.ProductCategory.model.Category;
import com.micro.ProductCategory.service.CategoryServiceImpli;

@RestController
public class CategoryController {

    @Autowired
    private CategoryServiceImpli categoryServiceImpli;

    @PostMapping("/savecategory")
    public String saveCate(@RequestBody Category category) {
        System.out.println("This is category: " + category);

        return categoryServiceImpli.saveCategory(category);

    }

    @GetMapping("/getcategories")
    public List<Category> getCate() {

        return categoryServiceImpli.getCategories();

    }

    @GetMapping("/getcategoriespage")
    public Page<Category> getCate(@RequestParam int page, @RequestParam int size) {

        return categoryServiceImpli.getCategorypage(page, size);

    }

    @PutMapping("updatecategory/{categoryId}")
    public String updateCate(@PathVariable Long categoryId, @RequestBody Category category) {
        return categoryServiceImpli.updateCategory(categoryId, category);
    }
}

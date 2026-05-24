package com.ecommerce.controller;


import com.ecommerce.model.Category;
import com.ecommerce.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/public/categories")
    public ResponseEntity<List<Category>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        return new ResponseEntity<>(categories , HttpStatus.OK);
    }

    @PostMapping("/admin/categories")
    public ResponseEntity<String> addCategory(@Valid @RequestBody Category category) {
        categoryService.createCategory(category);
        String status = "Category created: " + category.getCategoryName();
        return new ResponseEntity<>( status , HttpStatus.CREATED);

    }

    @DeleteMapping("/admin/categories/{categoryId}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long categoryId) {
        try {
            String status = categoryService.deleteCategory(categoryId);
            return new ResponseEntity<>(status , HttpStatus.OK);
            //return ResponseEntity.ok(status);
            //return ResponseEntity.status(HttpStatus.OK).body(status);
        }catch (ResponseStatusException e) {
            return new ResponseEntity<>(e.getReason() , e.getStatusCode());
        }
    }

    @PutMapping("/admin/categories/{categoryId}")
    public ResponseEntity<String> updateCategory(@RequestBody Category category , @PathVariable Long categoryId) {
        try {
            Category savedCategory = categoryService.updateCategory(category , categoryId);
            String status = "Category updated: " + savedCategory.getCategoryId();
            return new ResponseEntity<>( status , HttpStatus.OK);

        }catch (ResponseStatusException e) {
            return new ResponseEntity<>(e.getReason() , e.getStatusCode());
        }
    }
}

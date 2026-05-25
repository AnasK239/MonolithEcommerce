package com.ecommerce.controller;


import com.ecommerce.model.Category;
import com.ecommerce.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/public/categories")
    public ResponseEntity<List<Category>> getAllCategories() {
        return new ResponseEntity<>(
                categoryService.getAllCategories() , HttpStatus.OK
        );
    }

    @PostMapping("/admin/categories")
    public ResponseEntity<String> addCategory(@Valid @RequestBody Category category) {
        categoryService.createCategory(category);
        String status = "Category created: " + category.getCategoryName();
        return new ResponseEntity<>( status , HttpStatus.CREATED);

    }

    @DeleteMapping("/admin/categories/{categoryId}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long categoryId) {
            String status = categoryService.deleteCategory(categoryId);
            return new ResponseEntity<>(status , HttpStatus.OK);
    }

    @PutMapping("/admin/categories/{categoryId}")
    public ResponseEntity<String> updateCategory(
            @Valid @RequestBody Category category ,
            @PathVariable Long categoryId
    ) {
            Category savedCategory = categoryService.updateCategory(category , categoryId);
            String status = "Category updated: " + savedCategory.getCategoryId();
            return new ResponseEntity<>( status , HttpStatus.OK);
    }
}

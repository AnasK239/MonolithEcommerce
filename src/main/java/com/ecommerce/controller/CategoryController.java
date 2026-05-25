package com.ecommerce.controller;


import com.ecommerce.model.Category;
import com.ecommerce.payload.CategoryDTO;
import com.ecommerce.payload.CategoryResponse;
import com.ecommerce.service.CategoryService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("/public/categories")
    public ResponseEntity<CategoryResponse> getAllCategories() {
        return new ResponseEntity<>(
                categoryService.getAllCategories() , HttpStatus.OK
        );
    }

    @PostMapping("/admin/categories")
    public ResponseEntity<CategoryDTO> addCategory(@Valid @RequestBody CategoryDTO categoryDTO) {
        CategoryDTO savedCategory = categoryService.createCategory(categoryDTO);
        return new ResponseEntity<>( savedCategory , HttpStatus.CREATED);
    }

    @DeleteMapping("/admin/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> deleteCategory(@PathVariable Long categoryId) {
            CategoryDTO deletedCategory = categoryService.deleteCategory(categoryId);
            return new ResponseEntity<>(deletedCategory, HttpStatus.OK);
    }

    @PutMapping("/admin/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> updateCategory(
            @Valid @RequestBody CategoryDTO categoryDTO ,
            @PathVariable Long categoryId
    ) {
            CategoryDTO savedCategory = categoryService.updateCategory(categoryDTO , categoryId);
            return new ResponseEntity<>( savedCategory , HttpStatus.OK);
    }
}

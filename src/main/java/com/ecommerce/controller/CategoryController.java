package com.ecommerce.controller;


import com.ecommerce.config.AppConstants;
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
    public ResponseEntity<CategoryResponse> getAllCategories(
            @RequestParam(name = "pageNumber" ,
                    defaultValue = AppConstants.PAGE_NUMBER,
                    required = false) Integer pageNo,

            @RequestParam(name = "pageSize" ,
                    defaultValue = AppConstants.PAGE_SIZE,
                    required = false) Integer pageSize,

            @RequestParam(name = "sortBy" ,
                    defaultValue = AppConstants.SORT_CATEGORIES_BY,
                    required = false) String sortBy,

            @RequestParam(name = "sortOrder" ,
                    defaultValue = AppConstants.SORT_DIR,
                    required = false) String sortOrder
    ) {
        return new ResponseEntity<>(
                categoryService.getAllCategories(pageNo , pageSize , sortBy , sortOrder) , HttpStatus.OK
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

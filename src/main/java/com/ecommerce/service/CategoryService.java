package com.ecommerce.service;

import com.ecommerce.dto.CategoryDTO;
import com.ecommerce.dto.CategoryResponse;

public interface CategoryService {

    CategoryResponse getAllCategories(Integer pageNo , Integer pageSize , String sortBy , String sortOrder);
    CategoryDTO createCategory(CategoryDTO category);
    CategoryDTO deleteCategory(Long categoryId);
    CategoryDTO updateCategory(CategoryDTO categoryDTO , Long categoryId);
}

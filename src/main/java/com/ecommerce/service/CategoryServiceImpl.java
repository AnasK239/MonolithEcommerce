package com.ecommerce.service;

import com.ecommerce.exceptions.ResourceNotFoundException;
import com.ecommerce.model.Category;
import com.ecommerce.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;


@Service
public class CategoryServiceImpl implements CategoryService {
    //private final List<Category> categories = new ArrayList<>();

    @Autowired
    private  CategoryRepository categoryRepository;

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public void createCategory(Category category) {
        categoryRepository.save(category);
    }

    @Override
    public String deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category",
                        "categoryId" ,
                        categoryId
                ));

        categoryRepository.delete(category);
        return "Category deleted With Id: " + categoryId;
    }

    @Override
    public Category updateCategory(Category updateCategory ,Long categoryId) {

        Category existingCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category",
                        "categoryId" ,
                        categoryId
                ));

        existingCategory.setCategoryName(updateCategory.getCategoryName());

        return  categoryRepository.save(existingCategory);
    }
}

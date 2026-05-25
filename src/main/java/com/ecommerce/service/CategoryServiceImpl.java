package com.ecommerce.service;

import com.ecommerce.exceptions.APIException;
import com.ecommerce.exceptions.ResourceNotFoundException;
import com.ecommerce.model.Category;
import com.ecommerce.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;


@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private  CategoryRepository categoryRepository;

    @Override
    public List<Category> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        if(categories.isEmpty())
            throw new ResourceNotFoundException("No Categories found");
        return categoryRepository.findAll();
    }

    @Override
    public void createCategory(Category category) {
        Optional<Category> existingCategory
                = categoryRepository.findByCategoryName(category.getCategoryName());

        if(existingCategory.isPresent()) {
            throw new APIException(
              "Category " + category.getCategoryName() + " Already exists"
            );
        }
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

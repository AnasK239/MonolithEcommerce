package com.ecommerce.service;

import com.ecommerce.exceptions.APIException;
import com.ecommerce.exceptions.ResourceNotFoundException;
import com.ecommerce.model.Category;
import com.ecommerce.payload.CategoryDTO;
import com.ecommerce.payload.CategoryResponse;
import com.ecommerce.repository.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private  CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CategoryResponse getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        if(categories.isEmpty())
            throw new ResourceNotFoundException("No Categories found");

        List<CategoryDTO> categoryDTOS = categories.stream()
                .map(category -> modelMapper.map(category, CategoryDTO.class))
                .toList();

        return new CategoryResponse(categoryDTOS);
    }

    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        Category category = modelMapper.map(categoryDTO, Category.class);
        Optional<Category> existingCategory
                = categoryRepository.findByCategoryName(category.getCategoryName());

        if(existingCategory.isPresent()) {
            throw new APIException(
              "Category " + category.getCategoryName() + " Already exists"
            );
        }
        Category savedCategory = categoryRepository.save(category);
        return modelMapper.map(savedCategory, CategoryDTO.class);
    }

    @Override
    public CategoryDTO deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category",
                        "categoryId" ,
                        categoryId
                ));

        CategoryDTO deletedCategoryDTO = modelMapper.map(category, CategoryDTO.class);
        categoryRepository.delete(category);
        return deletedCategoryDTO;
    }

    @Override
    public CategoryDTO updateCategory(CategoryDTO updateCategoryDTO ,Long categoryId) {

        Category existingCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category",
                        "categoryId" ,
                        categoryId
                ));

        modelMapper.map(updateCategoryDTO, existingCategory);
        existingCategory.setCategoryId(categoryId);

        Category savedCategory = categoryRepository.save(existingCategory);
        return modelMapper.map(savedCategory, CategoryDTO.class);
    }
}

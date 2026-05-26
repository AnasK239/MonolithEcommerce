package com.ecommerce.service;


import com.ecommerce.payload.ProductDTO;
import com.ecommerce.payload.ProductResponse;
import org.springframework.stereotype.Service;

@Service
public interface ProductService {
    ProductDTO addProduct(ProductDTO productDTO, Long categoryId);

    ProductResponse getAllProducts();

    ProductResponse searchByCategory(Long categoryId);

    ProductResponse searchProductByKeyword(String keyword);
}

package com.ecommerce.controller;


import com.ecommerce.config.AppConstants;
import com.ecommerce.dto.ProductDTO;
import com.ecommerce.dto.ProductResponse;
import com.ecommerce.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping("/admin/categories/{categoryId}/product")
    public ResponseEntity<ProductDTO> addProduct(
            @Valid @RequestBody  ProductDTO productDTO,
            @PathVariable Long categoryId
    ) {
        ProductDTO responseProductDTO =
                productService.addProduct(productDTO,categoryId);

        return new ResponseEntity<>(responseProductDTO, HttpStatus.CREATED);
    }

    @GetMapping("/public/products")
    public ResponseEntity<ProductResponse> getAllProducts(
            @RequestParam(name = "pageNumber",
                    defaultValue = AppConstants.PAGE_NUMBER ,
                    required = false) Integer pageNumber,

            @RequestParam(name = "pageSize",
                    defaultValue = AppConstants.PAGE_SIZE ,
                    required = false) Integer pageSize,

            @RequestParam(name = "sortBy",
                    defaultValue = AppConstants.SORT_PRODUCTS_BY ,
                    required = false) String sortBy,

            @RequestParam(name = "sortOrder",
                    defaultValue = AppConstants.SORT_DIR ,
                    required = false) String sortOrder
    ) {

        return new ResponseEntity<>(
                productService.getAllProducts(pageNumber,pageSize,sortBy,sortOrder),
                HttpStatus.OK
        );
    }

    @GetMapping("/public/categories/{categoryId}/products")
    public ResponseEntity<ProductResponse> getProductsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(name = "pageNumber",
                    defaultValue = AppConstants.PAGE_NUMBER ,
                    required = false) Integer pageNumber,

            @RequestParam(name = "pageSize",
                    defaultValue = AppConstants.PAGE_SIZE ,
                    required = false) Integer pageSize,

            @RequestParam(name = "sortBy",
                    defaultValue = AppConstants.SORT_PRODUCTS_BY ,
                    required = false) String sortBy,

            @RequestParam(name = "sortOrder",
                    defaultValue = AppConstants.SORT_DIR ,
                    required = false) String sortOrder
    )
    {
        ProductResponse productResponse
                = productService.searchByCategory(categoryId ,pageNumber,pageSize,sortBy,sortOrder);

        return new ResponseEntity<>(productResponse, HttpStatus.OK);
    }

    @GetMapping("/public/products/keyword/{keyword}")
    public ResponseEntity<ProductResponse> getProductByKeyWord(
            @PathVariable String keyword,
            @RequestParam(name = "pageNumber",
                    defaultValue = AppConstants.PAGE_NUMBER ,
                    required = false) Integer pageNumber,

            @RequestParam(name = "pageSize",
                    defaultValue = AppConstants.PAGE_SIZE ,
                    required = false) Integer pageSize,

            @RequestParam(name = "sortBy",
                    defaultValue = AppConstants.SORT_PRODUCTS_BY ,
                    required = false) String sortBy,

            @RequestParam(name = "sortOrder",
                    defaultValue = AppConstants.SORT_DIR ,
                    required = false) String sortOrder
    ){
       ProductResponse productResponse =
               productService.searchProductByKeyword(keyword,pageNumber, pageSize,sortBy,sortOrder);
       return new ResponseEntity<>(productResponse, HttpStatus.FOUND);
    }

    @PutMapping("/admin/products/{productId}")
    public ResponseEntity<ProductDTO> updateProduct(
            @Valid @RequestBody ProductDTO productDTO,
            @PathVariable Long productId
    ){
        ProductDTO productResponse =
                productService.updateProduct(productId , productDTO);
        return new ResponseEntity<>(productResponse, HttpStatus.OK);
    }

    @DeleteMapping("/admin/products/{productId}")
    public ResponseEntity<String> deleteProduct(
            @PathVariable Long productId
    ){
        String productDeletionResponse
                = productService.deleteProduct(productId);

        return new ResponseEntity<>(productDeletionResponse, HttpStatus.OK);
    }

    @PutMapping("/products/{productId}/image")
    public ResponseEntity<ProductDTO> updateProductImage(
            @PathVariable Long productId,
            @RequestParam("image") MultipartFile image
    ) throws IOException {
        ProductDTO updatedProduct =
                productService.updateProductImage(productId , image);

        return new ResponseEntity<>(updatedProduct , HttpStatus.OK);
    }
}

package com.ecommerce.service;

import com.ecommerce.dto.CartDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public interface CartService {
    CartDTO addProductToCart(Long productId, Integer quantity);

    List<CartDTO> getAllCarts();

    CartDTO getUserCart(Long cartId);

    @Transactional
    CartDTO updateCartProductQuantity(Long productId, Integer deltaQuantity);

    String deleteProduct(Long cartId, Long productId);

    void updateProductInCarts(Long cartId, Long productId);
}

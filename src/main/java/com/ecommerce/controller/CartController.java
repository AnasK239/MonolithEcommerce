package com.ecommerce.controller;

import com.ecommerce.dto.CartDTO;
import com.ecommerce.model.Cart;
import com.ecommerce.repository.CartRepository;
import com.ecommerce.service.CartService;
import com.ecommerce.util.AuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CartController {

    @Autowired
    private CartService cartService;
    @Autowired
    private AuthUtil authUtil;
    @Autowired
    private CartRepository cartRepository;


    @PostMapping("/carts/products/{productId}/quantity/{quantity}")
    public ResponseEntity<CartDTO> addProduct(
            @PathVariable Long productId ,
            @PathVariable Integer quantity
    ) {
        CartDTO cartDTO = cartService.addProductToCart(productId , quantity);
        return new ResponseEntity<>(cartDTO, HttpStatus.CREATED);
    }


    @GetMapping("/carts")
    public ResponseEntity<List<CartDTO>> getAllCarts() {
        List<CartDTO> cartDTOS = cartService.getAllCarts();
        return new ResponseEntity<>(cartDTOS, HttpStatus.FOUND);
    }

    @GetMapping("/carts/user/cart")
    public ResponseEntity<CartDTO> getCart() {
        CartDTO cartDTO = cartService.getUserCart();
       return new ResponseEntity<>(cartDTO, HttpStatus.OK);
    }
}

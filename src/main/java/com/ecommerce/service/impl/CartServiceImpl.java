package com.ecommerce.service.impl;

import com.ecommerce.dto.CartDTO;
import com.ecommerce.dto.ProductDTO;
import com.ecommerce.exceptions.ApiException;
import com.ecommerce.exceptions.ResourceNotFoundException;
import com.ecommerce.model.Cart;
import com.ecommerce.model.CartItem;
import com.ecommerce.model.Product;
import com.ecommerce.repository.CartItemRepository;
import com.ecommerce.repository.CartRepository;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.util.AuthUtil;
import com.ecommerce.service.CartService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class CartServiceImpl implements CartService {


    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private AuthUtil authUtil;

    @Transactional
    @Override
    public CartDTO addProductToCart(Long productId, Integer quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (quantity == null || quantity <= 0) {
            throw new ApiException("Quantity must be greater than zero");
        }

        if (product.getQuantity() < quantity) {
            throw new ApiException("Product doesn't have enough stock");
        }

        Cart cart = findOrCreateCart();
        CartItem cartItem = cartItemRepository.findByProductIdAndCartId(
                cart.getCartId(),
                productId
        );
        if(cartItem != null) {
            throw new ApiException("Product already in Cart");
        }


        CartItem cartItemToAdd = new CartItem();
        cartItemToAdd.setProduct(product);
        cartItemToAdd.setQuantity(quantity);
        cartItemToAdd.setCart(cart);
        cartItemToAdd.setProductPrice(product.getSpecialPrice());
        cartItemToAdd.setDiscount(product.getDiscount());
        cart.setTotalPrice(cart.getTotalPrice() + cartItemToAdd.getProductPrice() * quantity);

        try {
            cartItemRepository.save(cartItemToAdd);
        }
        catch (DataIntegrityViolationException e) {
            throw new ApiException("Product already in cart");
        }

        cart.getCartItems().add(cartItemToAdd);

        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
        List<CartItem> cartItems = cart.getCartItems();

        List<ProductDTO> productDTOs = cartItems.stream().map(item ->{
                ProductDTO map = modelMapper.map(item.getProduct(), ProductDTO.class);
                map.setQuantity(item.getQuantity());
                return map;
        }).toList();

        cartDTO.setProducts(productDTOs);
        return cartDTO;
    }

    @Override
    public List<CartDTO> getAllCarts() {
        List<Cart> carts = cartRepository.findAll();
        if(carts.isEmpty()) {
            throw new ApiException("No carts found");
        }

        return carts.stream()
                .map(this::getCartDTO)
                .toList();
    }



    @Override
    //** PROBLEM
    public CartDTO getUserCart(){
        Cart cart = cartRepository.findCartByEmailAndCartId(
                authUtil.loggedInEmail(),
                authUtil.loggedInUserId()  // <---
        );
        if(cart == null) {
            throw new ResourceNotFoundException("Cart not found");
        }
        return getCartDTO(cart);
    }


    private CartDTO getCartDTO(Cart cart) {
        List<ProductDTO> cartProducts = cart.getCartItems().stream()
                .map(item -> {
                    ProductDTO productDTO = modelMapper.map(item.getProduct(), ProductDTO.class);
                    productDTO.setQuantity(item.getQuantity());
                    return productDTO;
                })
                .toList();

        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
        cartDTO.setProducts(cartProducts);
        return cartDTO;
    }

    // **Problem
    private Cart findOrCreateCart(){
        Cart cart = cartRepository.findCartByEmailAndCartId(
                authUtil.loggedInEmail(),
                authUtil.loggedInUserId() // <----
        );
        if(cart != null) {
            return cart;
        }

        Cart newCart = new Cart();
        newCart.setTotalPrice(0.0);
        newCart.setUser(authUtil.loggedInUser());
        return cartRepository.save(newCart);
    }
}

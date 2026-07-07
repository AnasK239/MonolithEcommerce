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

        var cart = findOrCreateCart();
        CartItem cartItem = cartItemRepository.findByProductIdAndCartId(
                cart.getCartId(),
                productId
        );
        if(cartItem != null) {
            throw new ApiException("Product already in Cart");
        }


        var cartItemToAdd = new CartItem();
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

        var cartDTO = modelMapper.map(cart, CartDTO.class);
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

        /*
        Interface:
            @Query("""
        select distinct c
        from Cart c
        left join fetch c.cartItems ci
        left join fetch ci.product
    `   """)
        List<Cart> findAllWithItemsAndProducts();

        Change inside method to:
        List<Cart> carts = cartRepository.findAllWithItemsAndProducts();
         */
        if(carts.isEmpty()) {
            throw new ApiException("No carts found");
        }

        return carts.stream()
                .map(this::getCartDTO)
                .toList();
    }



    @Override
    //** PROBLEM
    public CartDTO getUserCart(Long cartId){
        var cart = cartRepository.findCartByEmailAndCartId(
                authUtil.loggedInEmail(),
                cartId  // <---
        );
        if(cart == null) {
            throw new ResourceNotFoundException("Cart not found");
        }
        return getCartDTO(cart);
    }

    @Override
    @Transactional
    public CartDTO updateCartProductQuantity(Long productId, Integer deltaQuantity) {
        Cart userCart = cartRepository.findCartByEmail(authUtil.loggedInEmail());
        if(userCart == null) {
            throw new ResourceNotFoundException("Cart not found");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));


        Integer stockQuantity = product.getQuantity();
        if (stockQuantity == 0) {
            throw new ApiException("Product doesn't have enough stock");
        }
        if (stockQuantity < deltaQuantity) {
            throw new ApiException("Quantity must be greater than zero");
        }
        CartItem cartItem = cartItemRepository.findByProductIdAndCartId(
                productId,
                userCart.getCartId()
        );
        if(cartItem == null) {
            throw new ApiException("Product not found in the cart");
        }

        int newQuantity = cartItem.getQuantity() + deltaQuantity;
        if (newQuantity < 0) {
            throw new ApiException("Quantity must be greater than zero");
        }
        if(newQuantity == 0) {
            deleteProduct(userCart.getCartId(), productId);
        }
        else {
            cartItem.setProductPrice(product.getSpecialPrice());
            cartItem.setQuantity(cartItem.getQuantity() + deltaQuantity);
            cartItem.setDiscount(product.getDiscount());
            userCart.setTotalPrice(userCart.getTotalPrice() + cartItem.getProductPrice() * deltaQuantity);
            cartRepository.save(userCart);
        }

        CartItem updatedItem = cartItemRepository.save(cartItem);
        if(updatedItem.getQuantity() == 0) {
            cartItemRepository.deleteById(updatedItem.getCartItemId());
        }
        return getCartDTO(userCart);
    }

    @Transactional
    @Override
    public String deleteProduct(Long cartId, Long productId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(()->new ResourceNotFoundException("Cart not found"));

        CartItem cartItem = cartItemRepository.findByProductIdAndCartId(productId, cart.getCartId());
        if(cartItem == null) {
            throw new ResourceNotFoundException("Item not found");
        }
        cart.setTotalPrice(cart.getTotalPrice() - cartItem.getProductPrice() * cartItem.getQuantity());
        cartItemRepository.deleteCartItemByProductIdAndCartId(productId , cartId);

        return "CartItem deleted";
    }

    @Override
    public void updateProductInCarts(Long cartId, Long productId) {
        var userCart = cartRepository.findById(cartId)
                .orElseThrow(()->new ResourceNotFoundException("Cart not found"));

        var product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        var cartItem = cartItemRepository.findByProductIdAndCartId(productId , cartId);
        if(cartItem == null) {
            throw new ResourceNotFoundException("No cart with specified product was found");
        }

        double cartPrice = userCart.getTotalPrice() - (cartItem.getProductPrice() * cartItem.getQuantity());
        cartItem.setProductPrice(product.getSpecialPrice());
        userCart.setTotalPrice(cartPrice + (cartItem.getProductPrice() * cartItem.getQuantity()));
        cartItemRepository.save(cartItem);
    }


    private CartDTO getCartDTO(Cart cart) {
        List<ProductDTO> cartProducts = cart.getCartItems().stream()
                .map(item -> {
                    var productDTO = modelMapper.map(item.getProduct(), ProductDTO.class);
                    productDTO.setQuantity(item.getQuantity());
                    return productDTO;
                })
                .toList();

        var cartDTO = modelMapper.map(cart, CartDTO.class);
        cartDTO.setProducts(cartProducts);
        return cartDTO;
    }

    // **Problem
    private Cart findOrCreateCart(){
        var cart = cartRepository.findCartByEmail(authUtil.loggedInEmail());
        if(cart != null) {
            return cart;
        }

        Cart newCart = new Cart();
        newCart.setTotalPrice(0.0);
        newCart.setUser(authUtil.loggedInUser());
        return cartRepository.save(newCart);
    }
}

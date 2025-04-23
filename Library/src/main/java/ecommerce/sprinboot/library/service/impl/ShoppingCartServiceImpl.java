package ecommerce.sprinboot.library.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ecommerce.sprinboot.library.model.*;
import ecommerce.sprinboot.library.repository.*;
import ecommerce.sprinboot.library.service.ShoppingCartService;

import java.util.HashSet;
import java.util.Set;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
    @Autowired
    private CartItemRepository itemRepository;

    @Autowired
    private ShoppingCartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public ShoppingCart addItemToCart(Product product, int quantity, Customer customer) {
        if (quantity > product.getCurrentQuantity()) {
            throw new RuntimeException("Only " + product.getCurrentQuantity() + " items available in stock.");
        }

        ShoppingCart cart = customer.getShoppingCart();
        if (cart == null) {
            cart = new ShoppingCart();
        }

        Set<CartItem> cartItems = cart.getCartItem();
        CartItem cartItem = findCartItem(cartItems, product.getId());

        if (cartItem == null) {
            cartItem = new CartItem();
            cartItem.setProduct(product);
            cartItem.setTotalPrice(quantity * product.getSalePrice());
            cartItem.setQuantity(quantity);
            cartItem.setCart(cart);
            cartItems.add(cartItem);
            itemRepository.save(cartItem);
        } else {
            int newQuantity = cartItem.getQuantity() + quantity;
            if (newQuantity > product.getCurrentQuantity()) {
                throw new RuntimeException("Only " + product.getCurrentQuantity() + " items available in stock.");
            }
            cartItem.setQuantity(newQuantity);
            cartItem.setTotalPrice(cartItem.getTotalPrice() + (quantity * product.getSalePrice()));
            itemRepository.save(cartItem);
        }

        cart.setCartItem(cartItems);
        cart.setTotalPrices(totalPrice(cart.getCartItem()));
        cart.setTotalItems(totalItems(cart.getCartItem()));
        cart.setCustomer(customer);

        return cartRepository.save(cart);
    }

    @Override
    public ShoppingCart updateItemInCart(Product product, int quantity, Customer customer) {
        if (quantity > product.getCurrentQuantity()) {
            throw new RuntimeException("Only " + product.getCurrentQuantity() + " items available in stock.");
        }

        ShoppingCart cart = customer.getShoppingCart();
        Set<CartItem> cartItems = cart.getCartItem();
        CartItem item = findCartItem(cartItems, product.getId());

        if (item != null) {
            item.setQuantity(quantity);
            item.setTotalPrice(quantity * product.getSalePrice());
            itemRepository.save(item);
        }

        cart.setTotalItems(totalItems(cartItems));
        cart.setTotalPrices(totalPrice(cartItems));
        return cartRepository.save(cart);
    }

    @Override
    public ShoppingCart deleteItemFromCart(Product product, Customer customer) {
        ShoppingCart cart = customer.getShoppingCart();
        Set<CartItem> cartItems = cart.getCartItem();
        CartItem item = findCartItem(cartItems, product.getId());

        if (item != null) {
            cartItems.remove(item);
            itemRepository.delete(item);
        }

        cart.setCartItem(cartItems);
        cart.setTotalItems(totalItems(cartItems));
        cart.setTotalPrices(totalPrice(cartItems));
        return cartRepository.save(cart);
    }

    private CartItem findCartItem(Set<CartItem> cartItems, Long productId) {
        return cartItems.stream().filter(item -> item.getProduct().getId().equals(productId)).findFirst().orElse(null);
    }

    private int totalItems(Set<CartItem> cartItems){
        return cartItems.stream().mapToInt(CartItem::getQuantity).sum();
    }

    private double totalPrice(Set<CartItem> cartItems){
        return cartItems.stream().mapToDouble(CartItem::getTotalPrice).sum();
    }
}
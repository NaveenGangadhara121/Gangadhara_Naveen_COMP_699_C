package ecommerce.sprinboot.customer.controller;

import ecommerce.sprinboot.library.model.Customer;
import ecommerce.sprinboot.library.model.Product;
import ecommerce.sprinboot.library.model.ShoppingCart;
import ecommerce.sprinboot.library.service.CustomerService;
import ecommerce.sprinboot.library.service.ProductService;
import ecommerce.sprinboot.library.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.Principal;

@Controller
public class CartController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private ShoppingCartService cartService;

    @Autowired
    private ProductService productService;


    @GetMapping("/cart")
    public String cart(Model model, Principal principal, HttpSession session) {
        if (principal == null) {
            return "redirect:/login";
        }

        String username = principal.getName();
        Customer customer = customerService.findByUsername(username);
        ShoppingCart shoppingCart = customer.getShoppingCart();

        if (shoppingCart == null || shoppingCart.getCartItem().isEmpty()) {
            model.addAttribute("check", "No items in your cart");
            return "redirect:/products";
        }

        session.setAttribute("totalItems", shoppingCart.getTotalItems());
        model.addAttribute("subTotal", shoppingCart.getTotalPrices());
        model.addAttribute("discountPrice", customerService.calculateDiscount(shoppingCart.getTotalPrices()));
        model.addAttribute("shoppingCart", shoppingCart);
        model.addAttribute("title", "Liquor Shop - Cart");

        return "shoping-cart";
    }

    @PostMapping("/add-to-cart")
    public String addItemToCart(@RequestParam("id") Long productId,
                                @RequestParam(value = "quantity", required = false, defaultValue = "1") int quantity,
                                Principal principal,
                                HttpServletRequest request) {

        if (principal == null) {
            return "redirect:/login";
        }

        Product product = productService.getProductById(productId);
        String username = principal.getName();
        Customer customer = customerService.findByUsername(username);

        // 🔹 Stock validation before adding item
        if (quantity > product.getCurrentQuantity()) {
            request.getSession().setAttribute("error", "Only " + product.getCurrentQuantity() + " items available in stock!");
            return "redirect:" + request.getHeader("Referer");
        }

        cartService.addItemToCart(product, quantity, customer);
        return "redirect:" + request.getHeader("Referer");
    }

    @RequestMapping(value = "/update-cart", method = RequestMethod.POST)
    public String updateCart(
            @RequestParam("id") Long productId,
            @RequestParam("quantity") int quantity,
            @RequestParam("action") String action,
            RedirectAttributes attributes,
            Principal principal) {

        if (principal == null) {
            return "redirect:/login";
        }

        String username = principal.getName();
        Customer customer = customerService.findByUsername(username);
        Product product = productService.getProductById(productId);

        // ✅ Handle "Remove from Cart"
        if ("delete".equals(action)) {
            cartService.deleteItemFromCart(product, customer);
            attributes.addFlashAttribute("success", "Item removed from cart successfully!");
            return "redirect:/cart";
        }

        // ✅ Handle "Update Cart" (Stock validation)
        if ("update".equals(action)) {
            if (quantity > product.getCurrentQuantity()) {
                attributes.addFlashAttribute("error", "Only " + product.getCurrentQuantity() + " items available in stock!");
                return "redirect:/cart";
            }

            cartService.updateItemInCart(product, quantity, customer);
            attributes.addFlashAttribute("success", "Cart updated successfully!");
            return "redirect:/cart";
        }

        attributes.addFlashAttribute("error", "Invalid action!");
        return "redirect:/cart";
    }

}



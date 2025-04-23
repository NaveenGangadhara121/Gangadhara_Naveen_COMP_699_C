package ecommerce.sprinboot.customer.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import ecommerce.sprinboot.library.dto.CategoryDto;
import ecommerce.sprinboot.library.model.Category;
import ecommerce.sprinboot.library.model.Product;
import ecommerce.sprinboot.library.service.CategoryService;
import ecommerce.sprinboot.library.service.ProductService;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/products")
    public String products(Model model){
        List<CategoryDto> categoryDtoList = categoryService.getCategoryAndProduct();
        List<Product> products = productService.getAllProducts();
        List<Product> listViewProducts = productService.listViewProducts();
        model.addAttribute("categories", categoryDtoList);
        model.addAttribute("viewProducts", listViewProducts);

        model.addAttribute("title", "Liquor Shop - products");
        return "shop";
    }

    @GetMapping("/find-product/{id}")
    public String findProductById(@PathVariable("id") Long id, Model model){
        Product product = productService.getProductById(id);
        Long categoryId = product.getCategory().getId();
        List<Product>  products = productService.getRelatedProducts(categoryId);
        model.addAttribute("product", product);
        model.addAttribute("products", products);
        model.addAttribute("title", product.getName());
        return "product-detail";
    }

    @GetMapping("/products-in-category/{id}")
    public String getProductsInCategory(@PathVariable("id") Long categoryId ,Model model){
        Category category = categoryService.findById(categoryId);
        List<CategoryDto> categories = categoryService.getCategoryAndProduct();
        List<Product> products = productService.getProductsInCategory(categoryId);
        model.addAttribute("category",category);
        model.addAttribute("categories", categories);
        model.addAttribute("products", products);
        model.addAttribute("title", "Liquor Shop - Category Products");
        return "products-in-category";
    }



    @GetMapping("/high-price")
    public String filterHighPrice(Model model){
        List<Category> categories = categoryService.findAllByActivated();
        List<CategoryDto> categoryDtoList = categoryService.getCategoryAndProduct();
        List<Product> products = productService.filterHighPrice();
        model.addAttribute("categoryDtoList", categoryDtoList);
        model.addAttribute("products", products);
        model.addAttribute("categories", categories);
        model.addAttribute("title", "Liquor Shop - High Price");
        return "filter-high-price";
    }


    @GetMapping("/low-price")
    public String filterLowPrice(Model model){
        List<Category> categories = categoryService.findAllByActivated();
        List<CategoryDto> categoryDtoList = categoryService.getCategoryAndProduct();
        List<Product> products = productService.filterLowPrice();
        model.addAttribute("categoryDtoList", categoryDtoList);
        model.addAttribute("products", products);
        model.addAttribute("categories", categories);
        model.addAttribute("title", "Liquor Shop - Low Price");
        return "filter-low-price";
    }

    @GetMapping("/search")
    public String searchProducts(@RequestParam("query") String query, Model model) {
        List<Product> products = productService.searchProducts(query);

        if (products.isEmpty()) {
            System.out.println("No products found for query: " + query);
        } else {
            System.out.println("Found " + products.size() + " products for query: " + query);
        }

        model.addAttribute("products", products);
        model.addAttribute("title", "Search Results");
        return "search-results";  // Ensure this file exists in templates folder
    }



}

package com.hackaton.website.Controller;


import com.hackaton.website.Entity.Product;
import com.hackaton.website.Service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ShopController {

    @Autowired
    private ProductService productService;

    @GetMapping("/shop")
    public String shop(Model model) {
        // Ajusta os caminhos conforme onde estiverem os arquivos CSV
        Product product1 = productService.loadProductFromCSV("Products_Under_5_Euros.csv");
        Product product2 = productService.loadProductFromCSV("Products_5_to_10_Euros.csv");
        Product product3 = productService.loadProductFromCSV("Products_10_to_15_Euros.csv");

        model.addAttribute("product1", product1);
        model.addAttribute("product2", product2);
        model.addAttribute("product3", product3);

        return "shop"; // Renderiza o template shop.html
    }
}
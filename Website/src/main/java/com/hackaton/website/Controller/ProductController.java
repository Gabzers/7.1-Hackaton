package com.hackaton.website.Controller;

import com.hackaton.website.Entity.Product;
import com.hackaton.website.Entity.User; // Added import for User
import com.hackaton.website.Service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class ProductController {

    @Autowired
    private ProductService productService;

 
@GetMapping("/shop")
    public String serveShopPage(HttpSession session, Model model) {
    if (session.getAttribute("loggedUser") == null) {
        return "redirect:/login";
    }

    // Diretórios dos três conjuntos de CSVs
    String directoryPath1 = "Website/src/main/resources/csv/CostBenefit_Results/Products_Under_5_Euros.csv";
    String directoryPath2 = "Website/src/main/resources/csv/CostBenefit_Results/Products_5_To_10_Euros.csv";
    String directoryPath3 = "Website/src/main/resources/csv/CostBenefit_Results/Products_10_To_15_Euros.csv";

    // Obter produtos de cada categoria (limitar a 10 produtos)
    List<Product> products1 = productService.getProductsFromCSV(directoryPath1).stream().limit(10).toList();
    List<Product> products2 = productService.getProductsFromCSV(directoryPath2).stream().limit(10).toList();
    List<Product> products3 = productService.getProductsFromCSV(directoryPath3).stream().limit(10).toList();

    // Adicionar ao modelo para serem usados no Thymeleaf
    model.addAttribute("products1", products1);
    model.addAttribute("products2", products2);
    model.addAttribute("products3", products3);

    return "shop";
}

@PostMapping("/completeMission")
public String completeMission(HttpSession session, @RequestParam String missionName) {
    User loggedUser = (User) session.getAttribute("loggedUser");
    if (loggedUser == null) {
        return "redirect:/login";
    }

    int pointsEarned = productService.completeMission(loggedUser, missionName); // Ensure this method exists in ProductService
    session.setAttribute("loggedUser", loggedUser); // Update session with new points
    return "redirect:/battlepass?pointsEarned=" + pointsEarned;
}

}
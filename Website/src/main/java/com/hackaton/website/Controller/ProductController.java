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
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

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
@ResponseBody
public Map<String, Integer> completeMission(HttpSession session, @RequestParam String missionName) {
    User loggedUser = (User) session.getAttribute("loggedUser");
    if (loggedUser == null) {
        System.out.println("User not logged in. Returning 0 points.");
        return Map.of("pointsEarned", 0); // Retorna 0 pontos se o usuário não estiver logado
    }

    System.out.println("Mission name received: " + missionName);
    System.out.println("Logged user: " + loggedUser.getName());

    int pointsEarned = 0;
    try {
        pointsEarned = productService.completeMission(loggedUser, missionName);
        session.setAttribute("loggedUser", loggedUser); // Atualiza a sessão com os novos pontos
        System.out.println("Points earned: " + pointsEarned);
    } catch (Exception e) {
        System.err.println("Error completing mission: " + e.getMessage());
        e.printStackTrace();
    }

    return Map.of("pointsEarned", pointsEarned);
}

}
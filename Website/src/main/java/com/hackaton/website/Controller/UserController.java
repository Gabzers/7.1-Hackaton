
package com.hackaton.website.Controller;

import com.hackaton.website.Entity.User;
import com.hackaton.website.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;

@Controller
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/register")
    public String registerUser(@RequestParam("name") String name,
                               @RequestParam("email") String email,
                               @RequestParam("password") String password) {
        // Criar um novo objeto User com os dados recebidos
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);

        // Salvar o usuário na base de dados
        userRepository.save(user);

        // Redirecionar para uma página de sucesso
        return "home"; // Certifique-se de que a página success.html existe
    }

    @PostMapping("/login")
    public String loginUser(@RequestParam("email") String email,
                            @RequestParam("password") String password,
                            Model model) {
        // Verificar se o usuário existe na base de dados
        User user = userRepository.findByEmailAndPassword(email, password);

        if (user != null) {
            // Login bem-sucedido, redirecionar para a página inicial ou dashboard
            return "home"; // Certifique-se de que a página dashboard.html existe
        } else {
            // Login falhou, adicionar mensagem de erro e redirecionar para a página de login
            model.addAttribute("error", "Email ou senha incorretos. Tente novamente.");
            return "login"; // Certifique-se de que a página login.html existe
        }
    }
}
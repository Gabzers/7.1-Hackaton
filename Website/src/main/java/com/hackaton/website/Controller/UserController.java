package com.hackaton.website.Controller;

import com.hackaton.website.Entity.User;
import com.hackaton.website.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;
import java.util.ArrayList;
import java.util.List;
import com.hackaton.website.Entity.User.MovieGenre;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/register")
    public String registerUser(@RequestParam("name") String name,
                               @RequestParam("email") String email,
                               @RequestParam("password") String password,
                               Model model) {
        logger.info("Registering user with name: {}, email: {}", name, email);

        // Check if email already exists
        if (userRepository.findByEmail(email) != null) {
            logger.warn("Email already exists: {}", email);
            model.addAttribute("error", "This email is already in use. Please use another one.");
            return "registration"; // Redirect back to the registration page
        }

        // Criar um novo objeto User com os dados recebidos
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setPoints(0); // Inicializar pontos como 0
        logger.debug("User object created: {}", user);

        // Inicializar os gêneros com score 0
        List<MovieGenre> movieGenres = new ArrayList<>();
        List<String> allGenres = List.of("Action", "Adventure", "Animation", "Biography", "Comedy", "Crime", 
                                         "Documentary", "Drama", "Family", "Fantasy", "Film-Noir", "Game-Show", 
                                         "History", "Horror", "Music", "Musical", "Mystery", "News", "Reality-TV", 
                                         "Romance", "Sci-Fi", "Short", "Sport", "Talk-Show", "Thriller", "War", "Western");

        for (String genre : allGenres) {
            movieGenres.add(new MovieGenre(genre, "0")); // Inicializar todos os gêneros com score 0
        }
        user.setMovieGenres(movieGenres);
        logger.debug("Initialized movie genres for user: {}", movieGenres);

        // Salvar o usuário no banco de dados
        userRepository.save(user);
        logger.info("User saved to database: {}", user);

        // Redirecionar para a página inicial com o ID do usuário
        return "redirect:/home?userId=" + user.getId();
    }

    @PostMapping("/save-genres")
    public String saveGenres(@RequestParam("userId") Long userId,
                             @RequestParam(value = "genres", required = false) List<String> selectedGenres,
                             Model model,
                             HttpSession session) {
        logger.info("Saving genres for user with ID: {}", userId);

        // Buscar o usuário pelo ID
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado com o ID: " + userId));

        // Garantir que a lista de gêneros selecionados não seja nula
        if (selectedGenres == null) {
            selectedGenres = new ArrayList<>();
        }

        // Atualizar os gêneros do usuário
        List<MovieGenre> updatedGenres = new ArrayList<>();
        List<String> allGenres = List.of("Action", "Adventure", "Animation", "Biography", "Comedy", "Crime", 
                                         "Documentary", "Drama", "Family", "Fantasy", "Film-Noir", "Game-Show", 
                                         "History", "Horror", "Music", "Musical", "Mystery", "News", "Reality-TV", 
                                         "Romance", "Sci-Fi", "Short", "Sport", "Talk-Show", "Thriller", "War", "Western");

        for (String genre : allGenres) {
            if (selectedGenres.contains(genre)) {
                updatedGenres.add(new MovieGenre(genre, "10")); // Checkbox selecionado, score = 10
            } else {
                updatedGenres.add(new MovieGenre(genre, "0")); // Checkbox não selecionado, score = 0
            }
        }

        // Atualizar os gêneros do usuário
        user.setMovieGenres(updatedGenres);

        // Adicionar 50 pontos ao usuário
        int currentPoints = user.getPoints() != null ? user.getPoints() : 0;
        user.setPoints(currentPoints + 50);
        logger.info("Added 50 points to user. New total: {}", user.getPoints());

        // Salvar as alterações no banco de dados
        userRepository.save(user);
        logger.info("Updated user saved to database: {}", user);

        // Adicionar o usuário ao modelo e à sessão
        session.setAttribute("loggedUser", user);
        model.addAttribute("loggedUser", user);

        // Redirecionar para /home
        return "redirect:/home";
    }

    @PostMapping("/login")
    public String loginUser(@RequestParam("email") String email,
                            @RequestParam("password") String password,
                            Model model,
                            HttpSession session) {
        logger.info("Attempting to log in user with email: {}", email);

        // Verificar se o usuário existe no banco de dados
        User user = userRepository.findByEmailAndPassword(email, password);
        if (user != null) {
            logger.info("User logged in successfully: {}", user);

            // Adicionar o usuário à sessão
            session.setAttribute("loggedUser", user);

            // Redirecionar para /home
            return "redirect:/home";
        } else {
            logger.error("Login failed for email: {}", email);

            // Adicionar mensagem de erro ao modelo
            model.addAttribute("error", "Email ou senha incorretos. Tente novamente.");

            // Redirecionar para a página de login
            return "login"; // Certifique-se de que a página login.html existe
        }
    }
}
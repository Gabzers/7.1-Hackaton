package com.hackaton.website.Repository;

import com.hackaton.website.Entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email); // Método para buscar usuário apenas pelo email

    User findByEmailAndPassword(String email, String password); // Add this method

    @EntityGraph(attributePaths = "movieGenres") // Eagerly fetch movieGenres
    User findWithMovieGenresById(Long id);
}
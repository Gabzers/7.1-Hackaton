package com.hackaton.website.Repository;

import com.hackaton.website.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Adicione métodos personalizados, se necessário, como:
    // Optional<User> findByEmail(String email);
}
package com.hackaton.website.Repository;

import com.hackaton.website.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing User entities.
 * Provides methods for querying user data.
 * 
 * @author Gabriel Proença
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by ID with their movie genres eagerly loaded.
     *
     * @param id the ID of the user
     * @return the user with their movie genres
     */
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.movieGenres WHERE u.id = :id")
    User findWithMovieGenresById(@Param("id") Long id);

    /**
     * Finds a user by their email.
     *
     * @param email the email of the user
     * @return the user with the specified email
     */
    User findByEmail(String email);

    /**
     * Finds a user by their email and password.
     *
     * @param email    the email of the user
     * @param password the password of the user
     * @return the user with the specified email and password
     */
    User findByEmailAndPassword(String email, String password);
}
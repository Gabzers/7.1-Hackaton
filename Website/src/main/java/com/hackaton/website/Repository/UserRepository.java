package com.hackaton.website.Repository;

import com.hackaton.website.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.movieGenres WHERE u.id = :id")
    User findWithMovieGenresById(@Param("id") Long id);

    User findByEmail(String email);

    User findByEmailAndPassword(String email, String password);
}
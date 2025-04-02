package com.hackaton.website.Repository;

import com.hackaton.website.Entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing Shop entities.
 * Provides methods for querying shop data.
 * 
 * @author Gabriel Proença
 */
@Repository
public interface ShopRepository extends JpaRepository<Shop, Long> {

    /**
     * Finds a shop by the user ID.
     *
     * @param userId the ID of the user
     * @return the shop associated with the specified user
     */
    Shop findByUserId(Long userId);
}

package com.hackaton.website.Service;

import java.util.HashSet;
import java.util.Set;

import com.hackaton.website.Entity.User;

/**
 * Service class responsible for shop-related business logic.
 * Provides methods for redeeming offers and checking user points.
 * 
 * @author Gabriel Proença
 */
public class ShopService {

    /**
     * Checks if the user has sufficient points to redeem a product or offer.
     *
     * @param user The user attempting to redeem.
     * @param cost The cost of the product or offer.
     * @return true if the user has enough points, false otherwise.
     */
    public boolean hasSufficientPoints(User user, int cost) {
        if (user.getPoints() == null || user.getPoints() < cost) {
            return false;
        }
        return true;
    }

    /**
     * Redeems a shop offer for the user.
     *
     * @param user      the user redeeming the offer
     * @param offerName the name of the offer
     * @param offerCost the cost of the offer
     * @return a message indicating the result of the operation
     */
    public String redeemShopOffer(User user, String offerName, int offerCost) {
        // Check if user has enough points
        if (!hasSufficientPoints(user, offerCost)) {
            return "Insufficient points to redeem this offer";
        }
    
        // Check if offer was already redeemed
        Set<String> redeemedOffers = user.getRedeemedOffers();
        if (redeemedOffers == null) {
            redeemedOffers = new HashSet<>();
            user.setRedeemedOffers(redeemedOffers);
        }
    
        if (redeemedOffers.contains(offerName)) {
            return "This offer has already been redeemed";
        }
    
        // Deduct points and mark offer as redeemed
        user.setPoints(user.getPoints() - offerCost);
        redeemedOffers.add(offerName);
        user.setRedeemedOffers(redeemedOffers);
    
        return "Successfully redeemed offer: " + offerName;
    }
}

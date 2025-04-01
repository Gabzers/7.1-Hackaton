package com.hackaton.website.Service;

import java.util.HashSet;
import java.util.Set;

import com.hackaton.website.Entity.User;

public class ShopService {
    public String redeemShopOffer(User user, String offerName, int offerCost) {
        // Check if user has enough points
        if (user.getPoints() == null || user.getPoints() < offerCost) {
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

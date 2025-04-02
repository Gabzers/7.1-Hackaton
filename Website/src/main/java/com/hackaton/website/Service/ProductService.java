package com.hackaton.website.Service;

import com.hackaton.website.Entity.User;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class responsible for product-related business logic.
 * Provides methods for managing products and missions.
 * 
 * @author Gabriel Proença
 */
@Service
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);
    private static final String COST_BENEFIT_CSV_FILE = "Website/src/main/resources/csv/CostBenefit_Results/Products_Under_5_Euros.csv";

    /**
     * Limits the length of a product name to a maximum of 50 characters or 8 words.
     *
     * @param name the original product name
     * @return the limited product name
     */
    private String limitProductName(String name) {
        String[] words = name.split("\\s+");
        String limitedName = String.join(" ", Arrays.copyOfRange(words, 0, Math.min(words.length, 8))); // Limit to 8 words
        return limitedName.length() > 50 ? limitedName.substring(0, 50) + "..." : limitedName; // Limit to 50 characters
    }

    /**
     * Parses a string value into an integer.
     *
     * @param value the string value to parse
     * @return the parsed integer, or 0 if parsing fails
     */
    private int parseInteger(String value) {
        try {
            return (int) Double.parseDouble(value); // Handle both integer and decimal formats
        } catch (NumberFormatException e) {
            logger.warn("Invalid integer value: {}", value);
            return 0; // Default to 0 if parsing fails
        }
    }

    /**
     * Parses a string value into a double.
     *
     * @param value the string value to parse
     * @return the parsed double, or 0.0 if parsing fails
     */
    private double parseDouble(String value) {
        try {
            return Double.parseDouble(value); // Parse double values
        } catch (NumberFormatException e) {
            logger.warn("Invalid double value: {}", value);
            return 0.0; // Default to 0.0 if parsing fails
        }
    }

    /**
     * Completes a mission for the user and awards points.
     *
     * @param user        the user completing the mission
     * @param missionName the name of the mission
     * @return the points earned for completing the mission
     */
    public int completeMission(User user, String missionName) {
        int pointsEarned = 100; // Example points
        user.setPoints(user.getPoints() + pointsEarned); // Assuming User has a setPoints method
        logger.info("Mission '{}' completed by user '{}'. Points earned: {}", missionName, user.getName(), pointsEarned);
        return pointsEarned;
    }

    /**
     * Extracts product names from a CSV file.
     *
     * @return a list of product names
     */
    private List<String> extractProductNamesFromCSV() {
        List<String> productNames = new ArrayList<>();

        try {
            ClassPathResource resource = new ClassPathResource(COST_BENEFIT_CSV_FILE);
            InputStream inputStream = resource.getInputStream();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                 CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build())) {

                if (!parser.getHeaderMap().containsKey("productName")) {
                    logger.warn("CSV file does not contain 'productName' header. Skipping.");
                    return productNames;
                }

                for (CSVRecord record : parser) {
                    productNames.add(record.get("productName"));
                }
            }
        } catch (IOException e) {
            logger.error("Error reading CSV file: {}", COST_BENEFIT_CSV_FILE, e);
            throw new IllegalStateException("Error reading CSV file: " + COST_BENEFIT_CSV_FILE, e);
        }

        return productNames;
    }
}

package com.hackaton.website.Service;

import com.hackaton.website.Entity.Product;
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

@Service
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);
    private static final String COST_BENEFIT_CSV_FILE = "Website/src/main/resources/csv/CostBenefit_Results/Products_Under_5_Euros.csv";

    public List<String> recommendProductNames() {
        return extractProductNamesFromCSV();
    }

    public List<Product> getProductsFromCSV() {
        List<String> productNames = extractProductNamesFromCSV();
        Collections.shuffle(productNames); // Shuffle the list to randomize the order
        return productNames.stream()
                .limit(4) // Limit to 4 random products
                .map(name -> new Product(name, 0, 0)) // Default points and cost to 0
                .collect(Collectors.toList());
    }

    public List<Product> getProductsFromCSV(String filePath) {
        List<Product> products = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            CSVParser parser = new CSVParser(br, CSVFormat.DEFAULT.builder()
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .setIgnoreSurroundingSpaces(true)
                    .setTrim(true)
                    .build());

            for (CSVRecord record : parser) {
                try {
                    String name = record.get("product_name");
                    name = limitProductName(name); // Limit product name
                    int points = parseInteger(record.get("noRatings"));
                    int cost = (int) (parseDouble(record.get("cost")) * 100);
                    Product product = new Product(name, points, cost);
                    products.add(product);
                } catch (Exception e) {
                    logger.warn("Error processing record: {}", record, e);
                }
            }
        } catch (Exception e) {
            logger.error("Error reading CSV file: {}", filePath, e);
        }
        Collections.shuffle(products); // Shuffle the list to randomize the order
        return products.stream()
                .limit(4) // Limit to 4 random products
                .collect(Collectors.toList());
    }

    private String limitProductName(String name) {
        String[] words = name.split("\\s+");
        String limitedName = String.join(" ", Arrays.copyOfRange(words, 0, Math.min(words.length, 8))); // Limit to 8 words
        return limitedName.length() > 50 ? limitedName.substring(0, 50) + "..." : limitedName; // Limit to 50 characters
    }

    private int parseInteger(String value) {
        try {
            return (int) Double.parseDouble(value); // Handle both integer and decimal formats
        } catch (NumberFormatException e) {
            logger.warn("Invalid integer value: {}", value);
            return 0; // Default to 0 if parsing fails
        }
    }

    private double parseDouble(String value) {
        try {
            return Double.parseDouble(value); // Parse double values
        } catch (NumberFormatException e) {
            logger.warn("Invalid double value: {}", value);
            return 0.0; // Default to 0.0 if parsing fails
        }
    }

    public int completeMission(User user, String missionName) {
        // Example logic for completing a mission and awarding points
        int pointsEarned = 100; // Example points
        user.setPoints(user.getPoints() + pointsEarned); // Assuming User has a setPoints method
        logger.info("Mission '{}' completed by user '{}'. Points earned: {}", missionName, user.getName(), pointsEarned);
        return pointsEarned;
    }

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

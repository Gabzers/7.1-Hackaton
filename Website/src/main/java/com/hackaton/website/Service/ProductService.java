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
        return productNames.stream().map(Product::new).collect(Collectors.toList());
    }

    public List<Product> getProductsFromCSV(String filePath) {
        List<Product> products = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                Product product = new Product(values[0]); // Apenas o nome do produto
                products.add(product);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return products;
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

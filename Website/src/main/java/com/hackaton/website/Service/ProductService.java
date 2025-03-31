package com.hackaton.website.Service;

import com.hackaton.website.Entity.Product;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ProductService {
    // Existing methods and fields

    public Product loadProductFromCSV(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line = br.readLine(); // Assuming the first line contains product details
            if (line != null) {
                String[] details = line.split(","); // Assuming CSV is comma-separated
                return new Product(details[0]); // Assuming Product has a constructor that accepts only the name
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

package com.realEstate.repository;

import com.realEstate.model.Property;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class PropertyRepository {
    private static final String FILE_PATH = "data/properties.txt";
    private static final String DELIMITER = "|";
    private static final String TEMP_FILE_PATH = "data/properties_temp.txt";
    private static final String IMAGE_PREFIX = "property_";

    @Value("${file.upload-dir:uploads/properties}") // Default value if not configured
    private String uploadDir;

    @PostConstruct
    public void initializeStorage() {
        try {
            File dataDir = new File("data");
            if (!dataDir.exists() && !dataDir.mkdirs()) {
                throw new IOException("Failed to create data directory");
            }

            File file = new File(FILE_PATH);
            if (!file.exists() && !file.createNewFile()) {
                throw new IOException("Failed to create properties file");
            }

            File uploadDirFile = new File(uploadDir);
            if (!uploadDirFile.exists() && !uploadDirFile.mkdirs()) {
                throw new IOException("Failed to create upload directory");
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize property storage: " + e.getMessage(), e);
        }
    }

    public Property save(Property property) {
        if (property.getPropertyId() == null) {
            property.setPropertyId(generatePropertyId());
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_PATH, true))) {
            writer.println(convertToFileLine(property));
            return property;
        } catch (IOException e) {
            throw new RuntimeException("Failed to save property: " + e.getMessage(), e);
        }
    }

    public String saveImage(MultipartFile imageFile, String propertyId) throws IOException {
        validateImageFile(imageFile);

        String fileExtension = getFileExtension(imageFile.getOriginalFilename());
        String filename = IMAGE_PREFIX + propertyId + fileExtension;
        File dest = new File(uploadDir, filename);

        try {
            imageFile.transferTo(dest);
            return filename;
        } catch (IOException e) {
            throw new IOException("Failed to save image file: " + e.getMessage(), e);
        }
    }

    public List<Property> findAll() {
        List<Property> properties = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                properties.add(convertFromFileLine(line));
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read properties: " + e.getMessage(), e);
        }
        return properties;
    }

    public Optional<Property> findById(String propertyId) {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Property property = convertFromFileLine(line);
                if (property.getPropertyId().equals(propertyId)) {
                    return Optional.of(property);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to find property by ID: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    public List<Property> findBySellerId(String sellerId) {
        List<Property> properties = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Property property = convertFromFileLine(line);
                if (property.getSellerId().equals(sellerId)) {
                    properties.add(property);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to find properties by seller: " + e.getMessage(), e);
        }
        return properties;
    }

    public Property update(Property property) {
        File inputFile = new File(FILE_PATH);
        File tempFile = new File(TEMP_FILE_PATH);
        boolean propertyFound = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             PrintWriter writer = new PrintWriter(new FileWriter(tempFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                Property current = convertFromFileLine(line);
                if (current.getPropertyId().equals(property.getPropertyId())) {
                    writer.println(convertToFileLine(property));
                    propertyFound = true;
                } else {
                    writer.println(line);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to update property: " + e.getMessage(), e);
        }

        if (!propertyFound) {
            throw new IllegalArgumentException("Property not found with ID: " + property.getPropertyId());
        }

        if (!replaceFile(inputFile, tempFile)) {
            throw new RuntimeException("Failed to update properties file");
        }

        return property;
    }

    public void deleteById(String propertyId) {
        File inputFile = new File(FILE_PATH);
        File tempFile = new File(TEMP_FILE_PATH);
        boolean deleted = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             PrintWriter writer = new PrintWriter(new FileWriter(tempFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                Property property = convertFromFileLine(line);
                if (!property.getPropertyId().equals(propertyId)) {
                    writer.println(line);
                } else {
                    deleted = true;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete property: " + e.getMessage(), e);
        }

        if (!deleted) {
            throw new IllegalArgumentException("Property not found with ID: " + propertyId);
        }

        if (!replaceFile(inputFile, tempFile)) {
            throw new RuntimeException("Failed to update properties file after deletion");
        }
    }

    public void deleteImage(String imagePath) {
        if (imagePath != null && !imagePath.isEmpty()) {
            File imageFile = new File(uploadDir, imagePath);
            if (imageFile.exists() && !imageFile.delete()) {
                throw new RuntimeException("Failed to delete image file: " + imagePath);
            }
        }
    }

    private String generatePropertyId() {
        return "PROP-" + UUID.randomUUID().toString().substring(0, 8);
    }

    private String convertToFileLine(Property property) {
        return String.join(DELIMITER,
                property.getPropertyId(),
                property.getTitle(),
                property.getDescription(),
                String.valueOf(property.getPrice()),
                property.getLocation(),
                property.getSellerId(),
                String.valueOf(property.isAvailable()),
                property.getImagePath() != null ? property.getImagePath() : "",
                property.getCategoryId() != null ? property.getCategoryId() : "");
    }

    private Property convertFromFileLine(String line) {
        String[] parts = line.split("\\|", -1);if (parts.length < 9) {
            throw new IllegalArgumentException("Invalid line format: " + line);
        }

        String propertyId = parts[0];
        String title = parts[1];
        String description = parts[2];
        double price = Double.parseDouble(parts[3]);
        String location = parts[4];
        String sellerId = parts[5];
        boolean available = Boolean.parseBoolean(parts[6]);
        String imagePath = parts[7];
        String categoryId = parts[8];

        return new Property(propertyId, title, description, price, location, sellerId, available, imagePath, categoryId);
    }

    private void validateImageFile(MultipartFile imageFile) throws IllegalArgumentException {
        if (imageFile == null || imageFile.isEmpty()) {
            throw new IllegalArgumentException("Image file is required");
        }
        if (imageFile.getOriginalFilename() == null || imageFile.getOriginalFilename().isEmpty()) {
            throw new IllegalArgumentException("Invalid file name");
        }
    }

    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        return lastDotIndex > 0 ? filename.substring(lastDotIndex) : "";
    }

    private boolean replaceFile(File original, File temp) {
        if (!original.delete()) {
            return false;
        }
        return temp.renameTo(original);
    }
}
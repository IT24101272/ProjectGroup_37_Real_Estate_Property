package com.realEstate.service;

import com.realEstate.model.Category;
import com.realEstate.model.Property;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PropertyService {
    private final PropertyRepository propertyRepository;

    @Autowired
    public PropertyService(PropertyRepository propertyRepository) {
        this.propertyRepository = propertyRepository;
    }

    @Autowired
    private PropertyBSTService propertyBSTService;
    @Autowired
    private PropertySortingService propertySortingService;
    @Autowired
    private CategoryService categoryService;

    public Property addProperty(Property property, String sellerId, MultipartFile imageFile) throws IOException {
        if (property.getTitle() == null || property.getTitle().isEmpty()) {
            throw new IllegalArgumentException("Property title is required");
        }
        if (property.getPrice() <= 0) {
            throw new IllegalArgumentException("Property price must be positive");
        }
        if (property.getCategoryId() == null || property.getCategoryId().isEmpty()) {
            throw new IllegalArgumentException("Property category is required");
        }
        Optional<Category> category = categoryService.getCategoryById(property.getCategoryId());
        if (category.isEmpty()) {
            throw new IllegalArgumentException("Invalid category selected");
        }
        property.setPropertyId("PROP-" + UUID.randomUUID().toString().substring(0, 8));
        property.setSellerId(sellerId);
        property.setAvailable(true);
        Property savedProperty = propertyRepository.save(property);
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                String imagePath = propertyRepository.saveImage(imageFile, savedProperty.getPropertyId());
                savedProperty.setImagePath(imagePath);
                savedProperty = propertyRepository.update(savedProperty);
            } catch (IOException e) {
                propertyRepository.deleteById(savedProperty.getPropertyId());
                throw new IOException("Failed to save property image: " + e.getMessage(), e);
            }
        }
        propertyBSTService.insert(savedProperty);
        return savedProperty;
    }

    public List<Property> getAllProperties() {
        return propertyBSTService.inOrderTraversal();
    }

    public Optional<Property> getPropertyById(String propertyId) {
        return propertyRepository.findById(propertyId);
    }

    public List<Property> getPropertiesBySeller(String sellerId) {
        return propertyRepository.findBySellerId(sellerId);
    }

    public List<Property> getPropertiesSortedByPrice() {
        List<Property> properties = propertyBSTService.inOrderTraversal();
        return propertySortingService.quickSortByPrice(properties);
    }

    public Property updateProperty(String propertyId, Property updatedProperty, MultipartFile imageFile) throws IOException {
        Optional<Property> existingOpt = propertyRepository.findById(propertyId);
        if (existingOpt.isPresent()) {
            Property existing = existingOpt.get();
            double oldPrice = existing.getPrice();

            if (imageFile != null && !imageFile.isEmpty()) {
                propertyRepository.deleteImage(existing.getImagePath());
                String newImagePath = propertyRepository.saveImage(imageFile, propertyId);
                existing.setImagePath(newImagePath);
            }

            existing.setTitle(updatedProperty.getTitle());
            existing.setDescription(updatedProperty.getDescription());
            existing.setPrice(updatedProperty.getPrice());
            existing.setLocation(updatedProperty.getLocation());
            existing.setAvailable(updatedProperty.isAvailable());

            Property updated = propertyRepository.update(existing);
            propertyBSTService.rebuildBST();

            return updated;
        }
        throw new IllegalArgumentException("Property not found with ID: " + propertyId);
    }

    public void deleteProperty(String propertyId) {
        Optional<Property> propertyOpt = propertyRepository.findById(propertyId);
        if (propertyOpt.isPresent()) {
            Property property = propertyOpt.get();
            propertyRepository.deleteImage(property.getImagePath());
            propertyBSTService.delete(property);
            propertyRepository.deleteById(propertyId);
        } else {
            throw new IllegalArgumentException("Property not found with ID: " + propertyId);
        }
    }

    public Property markPropertyAsSold(String propertyId) {
        Optional<Property> propertyOpt = propertyRepository.findById(propertyId);
        if (propertyOpt.isPresent()) {
            Property property = propertyOpt.get();
            property.setAvailable(false);
            return propertyRepository.update(property);
        }
        throw new IllegalArgumentException("Property not found with ID: " + propertyId);
    }

    public List<Property> searchPropertiesByPriceRange(double minPrice, double maxPrice) {
        List<Property> allProperties = propertyBSTService.inOrderTraversal();
        List<Property> result = new ArrayList<>();

        for (Property property : allProperties) {
            if (property.getPrice() >= minPrice && property.getPrice() <= maxPrice) {
                result.add(property);
            }
        }

        return result;
    }
}
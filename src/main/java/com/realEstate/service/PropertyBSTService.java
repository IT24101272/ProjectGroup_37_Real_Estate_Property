package com.realEstate.service;

import com.realEstate.model.Property;
import com.realEstate.repository.PropertyRepository;
import com.realEstate.model.PropertyNode;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PropertyBSTService {
    private PropertyNode root;

    @Autowired
    private PropertyRepository propertyRepository;

    @PostConstruct
    public void init() {
        rebuildBST();
    }

    public void rebuildBST() {
        this.root = null;
        List<Property> properties = propertyRepository.findAll();
        for (Property property : properties) {
            insert(property);
        }
    }

    public void insert(Property property) {
        root = insertRec(root, property);
    }

    private PropertyNode insertRec(PropertyNode root, Property property) {
        if (root == null) {
            return new PropertyNode(property);
        }

        if (property.getPrice() < root.property.getPrice()) {
            root.left = insertRec(root.left, property);
        } else {
            root.right = insertRec(root.right, property);
        }

        return root;
    }

    public List<Property> inOrderTraversal() {
        List<Property> result = new ArrayList<>();
        inOrderTraversalRec(root, result);
        return result;
    }

    private void inOrderTraversalRec(PropertyNode root, List<Property> result) {
        if (root != null) {
            inOrderTraversalRec(root.left, result);
            result.add(root.property);
            inOrderTraversalRec(root.right, result);
        }
    }

    private Property searchByPriceRec(PropertyNode root, double price, String propertyId) {
        if (root == null) {
            return null;
        }

        if (root.property.getPrice() == price &&
                (propertyId == null || root.property.getPropertyId().equals(propertyId))) {
            return root.property;
        }

        if (price < root.property.getPrice()) {
            return searchByPriceRec(root.left, price, propertyId);
        }

        return searchByPriceRec(root.right, price, propertyId);
    }

    public void delete(Property property) {
        root = deleteRec(root, property);
    }

    private PropertyNode deleteRec(PropertyNode root, Property property) {
        if (root == null) {
            return null;
        }
        if (property.getPrice() < root.property.getPrice()) {
            root.left = deleteRec(root.left, property);
        } else if (property.getPrice() > root.property.getPrice()) {
            root.right = deleteRec(root.right, property);
        } else {

            if (root.property.getPropertyId().equals(property.getPropertyId())) {

                if (root.left == null) {
                    return root.right;
                } else if (root.right == null) {
                    return root.left;
                }

                root.property = minValue(root.right);
                root.right = deleteRec(root.right, root.property);
            } else {

                root.right = deleteRec(root.right, property);
            }
        }
        return root;
    }

    private Property minValue(PropertyNode root) {
        Property min = root.property;
        while (root.left != null) {
            min = root.left.property;
            root = root.left;
        }
        return min;
    }
}

package com.realEstate.service;

import com.realEstate.model.Property;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class PropertySortingService {
    public List<Property> quickSortByPrice(List<Property> properties) {
        if (properties == null || properties.size() <= 1) {
            return properties;
        }
        List<Property> sorted = new ArrayList<>(properties);
        quickSort(sorted, 0, sorted.size() - 1);
        return sorted;
    }

    private void quickSort(List<Property> properties, int low, int high) {
        if (low < high) {
            int pi = partition(properties, low, high);
            quickSort(properties, low, pi - 1);
            quickSort(properties, pi + 1, high);
        }
    }

    private int partition(List<Property> properties, int low, int high) {
        double pivot = properties.get(high).getPrice();
        int i = low - 1;

        for (int j = low; j < high; j++) {
            if (properties.get(j).getPrice() <= pivot) {
                i++;
                Collections.swap(properties, i, j);
            }
        }

        Collections.swap(properties, i + 1, high);
        return i + 1;
    }
}

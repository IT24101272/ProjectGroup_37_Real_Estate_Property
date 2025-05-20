package com.realEstate.repository;

import com.realEstate.model.Category;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class CategoryRepository {

    private final String FILE_PATH = "data/category.txt";
    private final String DELIMITER = "\\|";

    public CategoryRepository() {
        initializeFile();
    }

    // Ensure folder and file exist
    private void initializeFile() {
        try {
            File file = new File(FILE_PATH);
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs(); // create data/ folder
            }
            if (!file.exists()) {
                file.createNewFile(); // create category.txt
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Category> findAll() {
        List<Category> categories = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(DELIMITER);
                if (parts.length >= 2) {
                    categories.add(new Category(parts[0], parts[1]));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return categories;
    }

    public Optional<Category> findById(String id) {
        return findAll().stream().filter(c -> c.getCategoryId().equals(id)).findFirst();
    }

    public void save(Category category) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(category.getCategoryId() + "|" + category.getName());
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void update(Category category) {
        List<Category> categories = findAll();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Category c : categories) {
                if (c.getCategoryId().equals(category.getCategoryId())) {
                    writer.write(category.getCategoryId() + "|" + category.getName());
                } else {
                    writer.write(c.getCategoryId() + "|" + c.getName());
                }
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteById(String id) {
        List<Category> categories = findAll();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Category c : categories) {
                if (!c.getCategoryId().equals(id)) {
                    writer.write(c.getCategoryId() + "|" + c.getName());
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

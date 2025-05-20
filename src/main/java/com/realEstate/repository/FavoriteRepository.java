package com.realEstate.repository;

import com.realEstate.model.Favorite;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.*;
import java.util.*;

@Repository
public class FavoriteRepository {

    private static final String FILE_PATH = "data/favorites.txt";
    private static final String DELIMITER = "\\|";
    private static final String WRITE_DELIMITER = "|";

    public FavoriteRepository() {
        ensureFileExists();
    }

    private void ensureFileExists() {
        File file = new File(FILE_PATH);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            System.err.println("Could not create file: " + FILE_PATH);
        }
    }

    private String convertToFileLine(Favorite fav) {
        return String.join(WRITE_DELIMITER,
                fav.getFavoriteId(),
                fav.getUserId(),
                fav.getPropertyId(),
                fav.getStatus() != null ? fav.getStatus() : ""
        );
    }

    private Favorite convertFromFileLine(String line) {
        String[] parts = line.split(DELIMITER, -1);
        if (parts.length < 4) {
            throw new IllegalArgumentException("Invalid line format: " + line);
        }

        String favoriteId = parts[0];
        String userId = parts[1];
        String propertyId = parts[2];
        String status = parts[3];

        return new Favorite(favoriteId, userId, propertyId, status);
    }

    public void save(Favorite favorite) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(convertToFileLine(favorite));
            writer.newLine();
        }
    }

    public List<Favorite> findByUserId(String userId) throws IOException {
        List<Favorite> result = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Favorite fav = convertFromFileLine(line);
                if (fav.getUserId().equals(userId)) {
                    result.add(fav);
                }
            }
        }
        return result;
    }

    public void updateStatus(String userId, String propertyId, String newStatus) throws IOException {
        Path tempFile = Files.createTempFile("favorites", ".tmp");
        try (
                BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH));
                BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile.toFile()))
        ) {
            String line;
            while ((line = reader.readLine()) != null) {
                Favorite fav = convertFromFileLine(line);
                if (fav.getUserId().equals(userId) && fav.getPropertyId().equals(propertyId)) {
                    fav.setStatus(newStatus);
                    line = convertToFileLine(fav);
                }
                writer.write(line);
                writer.newLine();
            }
        }
        Files.move(tempFile, Paths.get(FILE_PATH), StandardCopyOption.REPLACE_EXISTING);
    }

    public void delete(String userId, String propertyId) throws IOException {
        Path tempFile = Files.createTempFile("favorites", ".tmp");
        try (
                BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH));
                BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile.toFile()))
        ) {
            String line;
            while ((line = reader.readLine()) != null) {
                Favorite fav = convertFromFileLine(line);
                if (!(fav.getUserId().equals(userId) && fav.getPropertyId().equals(propertyId))) {
                    writer.write(convertToFileLine(fav));
                    writer.newLine();
                }
            }
        }
        Files.move(tempFile, Paths.get(FILE_PATH), StandardCopyOption.REPLACE_EXISTING);
    }
}


package com.realEstate.repository;

import com.realEstate.model.Review;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ReviewRepository {
    private static final String FILE_PATH = "data/reviews.txt";
    private static final String DELIMITER = "|";
    private static final String TEMP_FILE_PATH = "data/reviews_temp.txt";

    @PostConstruct
    public void init() {
        try {
            File dataDir = new File("data");
            if (!dataDir.exists() && !dataDir.mkdirs()) {
                throw new IOException("Failed to create data directory");
            }

            File file = new File(FILE_PATH);
            if (!file.exists() && !file.createNewFile()) {
                throw new IOException("Failed to create reviews file");
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize review storage", e);
        }
    }

    public void save(Review review) {
        if (review.getUserID() == null || review.getUserID().isEmpty()) {
            throw new IllegalArgumentException("User ID is required");
        }
        if (review.getPropertyID() == null || review.getPropertyID().isEmpty()) {
            throw new IllegalArgumentException("Property ID is required");
        }

        if (review.getReviewID() == null) {
            review.setReviewID(generateReviewId());
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_PATH, true))) {
            writer.println(convertToFileLine(review));
        } catch (IOException e) {
            throw new RuntimeException("Failed to save review", e);
        }
    }

    public List<Review> findAll() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            return reader.lines()
                    .map(this::convertFromFileLine)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Failed to retrieve reviews", e);
        }
    }

    public Optional<Review> findById(String reviewId) {
        return findAll().stream()
                .filter(review -> review.getReviewID().equals(reviewId))
                .findFirst();
    }

    public List<Review> findByPropertyId(String propertyId) {
        return findAll().stream()
                .filter(review -> review.getPropertyID().equals(propertyId))
                .collect(Collectors.toList());
    }

    public List<Review> findByUserId(String userId) {
        return findAll().stream()
                .filter(review -> review.getUserID().equals(userId))
                .collect(Collectors.toList());
    }

    public void overwrite(List<Review> reviews) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_PATH))) {
            reviews.stream()
                    .map(this::convertToFileLine)
                    .forEach(writer::println);
        } catch (IOException e) {
            throw new RuntimeException("Failed to overwrite reviews", e);
        }
    }

    public void deleteById(String reviewId) {
        List<Review> filteredReviews = findAll().stream()
                .filter(review -> !review.getReviewID().equals(reviewId))
                .collect(Collectors.toList());

        overwrite(filteredReviews);
    }

    private String generateReviewId() {
        return "REV-" + UUID.randomUUID().toString().substring(0, 8);
    }

    private String convertToFileLine(Review review) {
        return String.join(DELIMITER,
                review.getReviewID(),
                review.getUserID(),
                review.getPropertyID(),
                String.valueOf(review.getRating()),
                review.getComment() != null ? review.getComment() : "");
    }

    private Review convertFromFileLine(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length < 5) {
            throw new IllegalArgumentException("Invalid line format: " + line);
        }

        return new Review(
                parts[0],
                parts[4],
                Integer.parseInt(parts[3]),
                parts[2],
                parts[1]
        );
    }
}
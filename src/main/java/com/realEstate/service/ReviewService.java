package com.realEstate.service;

import com.realEstate.model.Review;
import com.realEstate.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.thymeleaf.util.StringUtils.substring;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    public List<Review> getReviewsForProperty(String propertyID) throws IOException {
        return reviewRepository.findAll().stream()
                .filter(r -> r.getPropertyID().equals(propertyID))
                .collect(Collectors.toList());
    }

    public List<Review> getReviewsByUser(String userID) throws IOException {
        return reviewRepository.findAll().stream()
                .filter(r -> r.getUserID().equals(userID))
                .collect(Collectors.toList());
    }

    public Review getReviewByID(String reviewID) throws IOException {
        return reviewRepository.findAll().stream()
                .filter(r -> r.getReviewID().equals(reviewID))
                .findFirst()
                .orElse(null);
    }

    public void addReview(Review review) throws IOException {
        if (review.getReviewID() == null || review.getReviewID().isEmpty()) {
            review.setReviewID(UUID.randomUUID().toString().substring(0, 8));
        }
        reviewRepository.save(review);
    }

    public void updateReview(String reviewID, Review updatedReview) throws IOException {
        List<Review> reviews = reviewRepository.findAll();
        for (int i = 0; i < reviews.size(); i++) {
            if (reviews.get(i).getReviewID().equals(reviewID)) {
                reviews.set(i, updatedReview);
                break;
            }
        }
        reviewRepository.overwrite(reviews);
    }

    public void deleteReview(String reviewID) throws IOException {
        List<Review> reviews = reviewRepository.findAll();
        reviews.removeIf(r -> r.getReviewID().equals(reviewID));
        reviewRepository.overwrite(reviews);
    }
}

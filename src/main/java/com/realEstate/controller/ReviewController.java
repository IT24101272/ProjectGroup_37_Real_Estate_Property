package com.realEstate.controller;

import com.realEstate.model.Review;
import com.realEstate.service.ReviewService;
import com.realEstate.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import java.util.UUID;

@Controller
@RequestMapping("/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;
    @Autowired
    private UserService userService;

    @GetMapping("/property/{propertyID}")
    public String viewReviews(@PathVariable String propertyID, Model model) throws IOException {
        model.addAttribute("reviews", reviewService.getReviewsForProperty(propertyID));
        model.addAttribute("propertyID", propertyID);
        return "reviewList";
    }

    @GetMapping("/add/{propertyID}")
    public String showAddForm(@PathVariable String propertyID, Model model) {
        Review review = new Review(UUID.randomUUID().toString(), "", 0, propertyID, ""); // userID will be filled later
        model.addAttribute("review", review);
        return "reviews/add";
    }

    @PostMapping("/add")
    public String submitReview(@ModelAttribute Review review,
                               Principal principal) throws IOException {
        if (principal == null) {
            return "redirect:/login";
        }

        String userId = userService.getUserIdByEmail(principal.getName());
        review.setUserID(userId);
        reviewService.addReview(review);
        return "redirect:/properties/" + review.getPropertyID();
    }

    @GetMapping("/edit/{id}")
    public String showEditReviewForm(@PathVariable String id,
                                     Principal principal,
                                     Model model) throws IOException {
        if (principal == null) {
            return "redirect:/login";
        }
        Review review = reviewService.getReviewByID(id);
        if (review == null) {
            return "redirect:/error";
        }

        model.addAttribute("review", review);
        return "redirect:/properties/" + review.getPropertyID() + "?editReviewId=" + id;
    }

    @PostMapping("/edit")
    public String editReview(@ModelAttribute Review review) throws IOException {
        reviewService.updateReview(review.getReviewID(), review);
        return "redirect:/properties/" + review.getPropertyID();
    }

    @GetMapping("/delete/{reviewID}/{propertyID}")
    public String deleteReview(@PathVariable String reviewID, @PathVariable String propertyID) throws IOException {
        reviewService.deleteReview(reviewID);
        return "redirect:/properties/" + propertyID;
    }
}

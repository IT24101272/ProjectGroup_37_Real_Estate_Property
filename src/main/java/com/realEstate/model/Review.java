package com.realEstate.model;

public class Review {
    private String reviewID;
    private String userID;
    private String propertyID;
    private int rating;
    private String comment;

    public Review() {
    }

    public Review(String reviewID, String comment, int rating, String propertyID, String userID) {
        this.reviewID = reviewID;
        this.comment = comment;
        this.rating = rating;
        this.propertyID = propertyID;
        this.userID = userID;
    }

    public String getReviewID() {
        return reviewID;
    }

    public void setReviewID(String reviewID) {
        this.reviewID = reviewID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getPropertyID() {
        return propertyID;
    }

    public void setPropertyID(String propertyID) {
        this.propertyID = propertyID;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}

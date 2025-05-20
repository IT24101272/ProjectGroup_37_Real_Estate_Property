package com.realEstate.model;

public class Favorite {
    private String favoriteId;
    private String userId;
    private String propertyId;
    private String status;

    public Favorite() {
    }

    public Favorite(String favoriteId, String userId, String propertyId, String status) {
        this.favoriteId = favoriteId;
        this.userId = userId;
        this.propertyId = propertyId;
        this.status = status;
    }

    public String getFavoriteId() {
        return favoriteId;
    }

    public void setFavoriteId(String favoriteId) {
        this.favoriteId = favoriteId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

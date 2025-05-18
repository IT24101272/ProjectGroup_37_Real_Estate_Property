package com.realEstate.model;

import java.time.LocalDate;

public class Booking {
    private String requestId;
    private String buyerId;
    private String propertyId;
    private String message;
    private LocalDate scheduledDate;
    private String status;

    public Booking(){
    }

    public Booking(String requestId, String buyerId, String propertyId, String message, LocalDate scheduledDate, String status) {
        this.requestId = requestId;
        this.buyerId = buyerId;
        this.propertyId = propertyId;
        this.message = message;
        this.scheduledDate = scheduledDate;
        this.status = status;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }

    public String getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDate getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(LocalDate scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

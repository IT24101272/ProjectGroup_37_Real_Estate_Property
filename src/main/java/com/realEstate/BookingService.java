package com.realEstate.service;

import com.realEstate.model.Booking;
import com.realEstate.model.Property;
import com.realEstate.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private PropertyService propertyService;

    public void saveBooking(Booking booking) {
        booking.setRequestId(generateRequestId());
        booking.setStatus("Pending");
        bookingRepository.save(booking);
    }

    public void updateBooking(Booking updatedBooking) {
        Booking existingBooking = getBookingById(updatedBooking.getRequestId());
        if (existingBooking != null) {
            existingBooking.setStatus(updatedBooking.getStatus());
            bookingRepository.update(existingBooking);
        }
    }

    public void deleteBooking(String requestId) {
        bookingRepository.deleteById(requestId);
    }

    public Booking getBookingById(String requestId) {
        return bookingRepository.findAll().stream()
                .filter(b -> b.getRequestId().equals(requestId))
                .findFirst()
                .orElse(null);
    }

    public List<Booking> getBookingsByBuyer(String buyerId) {
        return bookingRepository.findAll().stream()
                .filter(b -> b.getBuyerId().equals(buyerId))
                .collect(Collectors.toList());
    }

    public List<Booking> getBookingsForSeller(String sellerId) {
        List<Property> sellerProperties = propertyService.getPropertiesBySeller(sellerId);
        Set<String> propertyIds = sellerProperties.stream()
                .map(Property::getPropertyId)
                .collect(Collectors.toSet());
        return bookingRepository.findAll().stream()
                .filter(b -> propertyIds.contains(b.getPropertyId()))
                .collect(Collectors.toList());
    }

    private String generateRequestId() {
        return "REQ" + System.currentTimeMillis();
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public void updateBookingStatus(String requestId, String status) {
        Booking booking = getBookingById(requestId);
        if (booking != null) {
            booking.setStatus(status);
            bookingRepository.update(booking);
        }
    }
}




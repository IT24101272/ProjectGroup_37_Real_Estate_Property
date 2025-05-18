package com.realEstate.controller;

import com.realEstate.model.Booking;
import com.realEstate.model.User;
import com.realEstate.service.BookingService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @GetMapping("/new/{propertyId}")
    public String showBookingForm(@PathVariable("propertyId") String propertyId,
                                  HttpSession session,
                                  Model model) {
        User user = (User) session.getAttribute("user");
        Booking booking = new Booking();
        booking.setBuyerId(user.getUserId());
        booking.setPropertyId(propertyId);
        model.addAttribute("booking", booking);
        return "bookingForm";
    }

    @PostMapping("/new")
    public String createBooking(@ModelAttribute Booking booking, HttpSession session) {
        User user = (User) session.getAttribute("user");
        booking.setBuyerId(user.getUserId());
        bookingService.saveBooking(booking);
        return "redirect:/bookings/my";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") String requestId, Model model) {
        Booking booking = bookingService.getBookingById(requestId);
        if (booking == null) {
            return "redirect:/bookings";
        }
        model.addAttribute("booking", booking);
        return "updateBookings";
    }

    @PostMapping("/update")
    public String updateBooking(@ModelAttribute Booking updatedBooking, HttpSession session) {
        Booking existingBooking = bookingService.getBookingById(updatedBooking.getRequestId());
        if (existingBooking == null) {
            return "redirect:/bookings";
        }
        existingBooking.setStatus(updatedBooking.getStatus());
        bookingService.updateBooking(existingBooking);
        User user = (User) session.getAttribute("user");
        if (user.getRole().equals("SELLER")) {
            return "redirect:/bookings/seller";
        } else {
            return "redirect:/bookings/my";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteBooking(@PathVariable("id") String requestId) {
        bookingService.deleteBooking(requestId);
        return "redirect:/bookings/my";
    }

    @GetMapping("/my")
    public String viewMyBookings(HttpSession session, Model model) {
        String userId = ((User) session.getAttribute("user")).getUserId();
        List<Booking> bookings = bookingService.getBookingsByBuyer(userId);
        model.addAttribute("bookings", bookings);
        return "myBookings";
    }

    @GetMapping("/seller")
    public String viewSellerBookings(HttpSession session, Model model) {
        String sellerId = ((User) session.getAttribute("user")).getUserId();
        List<Booking> bookings = bookingService.getBookingsForSeller(sellerId);
        model.addAttribute("bookings", bookings);
        return "sellerBookings";
    }
}



package com.realEstate.controller;

import com.realEstate.model.Booking;
import com.realEstate.model.Property;
import com.realEstate.model.User;
import com.realEstate.service.BookingService;
import com.realEstate.service.CategoryService;
import com.realEstate.service.PropertyService;
import com.realEstate.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private UserService userService;
    @Autowired
    private PropertyService propertyService;
    @Autowired
    private BookingService bookingService;
    @Autowired
    private CategoryService categoryService;

    public AdminController(UserService userService, PropertyService propertyService) {
        this.userService = userService;
        this.propertyService = propertyService;
    }

    @GetMapping("/users")
    public String viewAllUsers(Model model) {
        List<User> users = userService.getAllUsers();

        Map<String, Integer> propertyCounts = new HashMap<>();
        for (User user : users) {
            if (user.getRole().equals("SELLER")) {
                propertyCounts.put(user.getUserId(),
                        propertyService.getPropertiesBySeller(user.getUserId()).size());
            }
        }

        model.addAttribute("users", users);
        model.addAttribute("propertyCounts", propertyCounts);
        return "adminUsers";
    }

    @GetMapping("/properties")
    public String viewAllProperties(Model model) {
        model.addAttribute("properties", propertyService.getAllProperties());
        model.addAttribute("userService", userService);
        return "adminProperties";
    }

    @GetMapping("/users/{userId}")
    public String viewUserDetails(@PathVariable String userId, Model model) {
        User user = userService.getUserById(userId);
        List<Property> userProperties = propertyService.getPropertiesBySeller(userId);

        model.addAttribute("user", user);
        model.addAttribute("properties", userProperties);
        return "adminUserDetails";
    }

    @PostMapping("/users/delete/{userId}")
    public String deleteUser(@PathVariable String userId) {
        if ("SELLER".equals(userService.getUserById(userId).getRole())) {
            List<Property> userProperties = propertyService.getPropertiesBySeller(userId);
            for (Property property : userProperties) {
                propertyService.deleteProperty(property.getPropertyId());
            }
        }

        userService.deleteUser(userId);
        return "redirect:/admin/users";
    }

    @PostMapping("/properties/delete/{propertyId}")
    public String deleteProperty(@PathVariable String propertyId) {
        propertyService.deleteProperty(propertyId);
        return "redirect:/admin/properties";
    }

    @PostMapping("/properties/mark-sold/{propertyId}")
    public String markPropertyAsSold(@PathVariable String propertyId) {
        propertyService.markPropertyAsSold(propertyId);
        return "redirect:/admin/properties/" + propertyId;
    }

    @GetMapping("/bookings")
    public String viewAllBookings(Model model) {
        List<Booking> bookings = bookingService.getAllBookings();

        // Create a map of property titles for display
        Map<String, String> propertyTitles = new HashMap<>();
        Map<String, String> buyerNames = new HashMap<>();
        Map<String, String> sellerNames = new HashMap<>();

        for (Booking booking : bookings) {
            Property property = propertyService.getPropertyById(booking.getPropertyId()).orElse(null);
            if (property != null) {
                propertyTitles.put(booking.getPropertyId(), property.getTitle());

                User seller = userService.getUserById(property.getSellerId());
                if (seller != null) {
                    sellerNames.put(property.getSellerId(), seller.getName());
                }
            }

            User buyer = userService.getUserById(booking.getBuyerId());
            if (buyer != null) {
                buyerNames.put(booking.getBuyerId(), buyer.getName());
            }
        }

        model.addAttribute("bookings", bookings);
        model.addAttribute("propertyTitles", propertyTitles);
        model.addAttribute("buyerNames", buyerNames);
        model.addAttribute("sellerNames", sellerNames);
        return "adminBookings";
    }

    @PostMapping("/bookings/update-status/{id}")
    public String updateBookingStatus(@PathVariable("id") String requestId,
                                      @RequestParam String status) {
        bookingService.updateBookingStatus(requestId, status);
        return "redirect:/admin/bookings";
    }

    @PostMapping("/bookings/delete/{id}")
    public String deleteBooking(@PathVariable("id") String requestId) {
        bookingService.deleteBooking(requestId);
        return "redirect:/admin/bookings";
    }

    @GetMapping("/properties/add")
    public String showAddPropertyForm(Model model, HttpServletRequest request) {
        model.addAttribute("property", new Property());
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("isAdmin", true);
        model.addAttribute("sellers", userService.getAllAdmins()); // Explicit flag for admin requests
        return "addProperty";
    }

    @PostMapping("/properties/add")
    public String addPropertyAsAdmin(@ModelAttribute Property property,
                                     @RequestParam("imageFile") MultipartFile imageFile) {
        try {
            // For admin-added properties, use the first available seller
            User seller = userService.getAllUsers().stream()
                    .filter(u -> "SELLER".equals(u.getRole()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("No sellers found"));

            propertyService.addProperty(property, seller.getUserId(), imageFile);
            return "redirect:/admin/properties";
        } catch (IOException e) {
            return "redirect:/admin/properties/add?error=Error+uploading+property";
        }
    }
}
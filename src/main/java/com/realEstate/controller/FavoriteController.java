package com.realEstate.controller;

import com.realEstate.model.Favorite;
import com.realEstate.model.Property;
import com.realEstate.model.User;
import com.realEstate.service.FavoriteService;
import com.realEstate.service.PropertyService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/favorites")
public class FavoriteController {

    @Autowired
    private PropertyService propertyService;
    private final FavoriteService favoriteService = new FavoriteService();

    @GetMapping("/my")
    public String viewFavorites(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        try {
            List<Favorite> favorites = favoriteService.getFavoritesByUser(user.getUserId());
            List<Property> properties = new ArrayList<>();
            for (Favorite favorite : favorites) {
                Optional<Property> optionalProperty = propertyService.getPropertyById(favorite.getPropertyId());
                optionalProperty.ifPresent(properties::add);
            }
            model.addAttribute("favorites", favorites);
            model.addAttribute("properties", properties);
            model.addAttribute("userId", user.getUserId());
        } catch (IOException e) {
            model.addAttribute("error", "Could not load favorites: " + e.getMessage());
        }
        return "favorites";
    }

    @PostMapping("/add")
    public String addFavorite(@RequestParam String userId,
                              @RequestParam String propertyId,
                              @RequestParam String status) throws IOException {
        favoriteService.addFavorite(userId, propertyId, status);
        return "redirect:/favorites/my";
    }

    @PostMapping("/delete")
    public String deleteFavorite(@RequestParam String userId,
                                 @RequestParam String propertyId) {
        try {
            favoriteService.removeFavorite(userId, propertyId);
        } catch (IOException e) {
        }
        return "redirect:/favorites/my" ;
    }
}


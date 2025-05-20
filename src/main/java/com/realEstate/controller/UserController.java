package com.realEstate.controller;

import com.realEstate.model.User;
import com.realEstate.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/default")
    public String defaultAfterLogin(HttpSession session) {
        org.springframework.security.core.Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user = userService.getUserByEmail(email);

        session.setAttribute("user", user);

        if (user.getRole().equalsIgnoreCase(User.ROLE_ADMIN)) {
            return "redirect:/admin/properties";
        } else if (user.getRole().equalsIgnoreCase(User.ROLE_SELLER)) {
            return "redirect:/properties/seller";
        } else {
            return "redirect:/properties/all";
        }
    }


    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user, Model model) {
        try {
            userService.registerUser(user);
            model.addAttribute("message", "Registration successful!");
            return "redirect:/login";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("user", new User());
        return "login";
    }


    @GetMapping("/profile")
    public String showProfile(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        model.addAttribute("user", user);
        return "profile";
    }

    @GetMapping("/user/{userId}")
    public User getUserById(@PathVariable String userId) {
        return userService.getUserById(userId);
    }

    @GetMapping("/email/{email}")
    public User getUserByEmail(@PathVariable String email) {
        return userService.getUserByEmail(email);
    }

    @PostMapping("/change/{userId}")
    public String updateUser(@PathVariable String userId,
                             @ModelAttribute("user") User updatedUser,
                             HttpSession session,
                             Model model) {
        try {
            User updated = userService.updateUser(userId, updatedUser);
            session.setAttribute("user", updated);
            model.addAttribute("message", "Profile updated successfully.");
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
        }
        return "redirect:/profile";
    }

    @DeleteMapping("/{userId}")
    public String deleteUser(@PathVariable String userId, HttpSession session) {
        userService.deleteUser(userId);
        session.invalidate();
        return "redirect:/login?deleted=true";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login?logout=true";
    }

    @GetMapping("/all")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping("/{userId}/change-password")
    public String changePassword(@PathVariable String userId,
                                 @RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 HttpSession session,
                                 Model model) {
        try {
            userService.changePassword(userId, currentPassword, newPassword);
            model.addAttribute("message", "Password changed successfully.");
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
        }
        return "redirect:/profile";
    }

    @PutMapping("/{userId}/role")
    public User updateUserRole(
            @PathVariable String userId,
            @RequestParam String newRole) {
        return userService.updateUserRole(userId, newRole);
    }
}
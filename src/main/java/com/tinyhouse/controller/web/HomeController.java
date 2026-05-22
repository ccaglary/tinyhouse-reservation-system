package com.tinyhouse.controller.web;

import com.tinyhouse.service.TinyHouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final TinyHouseService tinyHouseService;

    @GetMapping({"/", "/home"})
    public String home(Model model) {
        model.addAttribute("topRated", tinyHouseService.getTopRated(6));
        model.addAttribute("cities", tinyHouseService.getAllCities());
        return "home";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/forgot-password")
    public String forgotPassword() {
        return "forgot-password";
    }

    @GetMapping("/reset-password")
    public String resetPassword() {
        return "reset-password";
    }

    @GetMapping("/verify-email")
    public String verifyEmail() {
        return "verify-email";
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "access-denied";
    }

    @GetMapping("/tinyhouses")
    public String tinyHouses(Model model, @PageableDefault(size = 12) Pageable pageable) {
        model.addAttribute("houses", tinyHouseService.getAllActive(pageable));
        model.addAttribute("cities", tinyHouseService.getAllCities());
        return "tinyhouses";
    }

    @GetMapping("/tinyhouses/{id}")
    public String tinyHouseDetail() {
        return "tinyhouse-detail";
    }
}

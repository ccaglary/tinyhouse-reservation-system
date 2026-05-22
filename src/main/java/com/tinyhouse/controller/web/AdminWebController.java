package com.tinyhouse.controller.web;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminWebController {

    @GetMapping({"", "/dashboard"})
    public String dashboard() {
        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String users() {
        return "admin/users";
    }

    @GetMapping("/reservations")
    public String reservations() {
        return "admin/reservations";
    }

    @GetMapping("/tinyhouses")
    public String tinyhouses() {
        return "admin/tinyhouses";
    }

    @GetMapping("/reports")
    public String reports() {
        return "admin/reports";
    }
}

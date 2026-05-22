package com.tinyhouse.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/tenant")
public class TenantWebController {

    @GetMapping({"", "/dashboard"})
    public String dashboard() {
        return "tenant/dashboard";
    }

    @GetMapping("/reservations")
    public String reservations() {
        return "tenant/reservations";
    }

    @GetMapping("/favorites")
    public String favorites() {
        return "tenant/favorites";
    }

    @GetMapping("/profile")
    public String profile() {
        return "tenant/profile";
    }
}

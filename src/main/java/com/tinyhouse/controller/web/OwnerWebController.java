package com.tinyhouse.controller.web;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/owner")
@PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
public class OwnerWebController {

    @GetMapping({"", "/dashboard"})
    public String dashboard() {
        return "owner/dashboard";
    }

    @GetMapping("/my-houses")
    public String myHouses() {
        return "owner/my-houses";
    }

    @GetMapping("/add-house")
    public String addHouse() {
        return "owner/add-house";
    }

    @GetMapping("/reservations")
    public String reservations() {
        return "owner/reservations";
    }
}

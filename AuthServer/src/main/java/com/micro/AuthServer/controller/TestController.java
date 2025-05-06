package com.micro.AuthServer.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String adminEndpoint() {
        return "Admin Board";
    }

    @GetMapping("/user")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public String userEndpoint() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Check if authentication is null or the user is not authenticated
        if (authentication == null || !authentication.isAuthenticated()) {
            return "User is not authenticated!";
        }

        String username = authentication.getName();

        return "Welcome," + username + "  This is the User Board.";
    }

    @GetMapping("/authrization")
    public boolean authrization() {
        return true;
    }

    List<String> list = new ArrayList<>();

    @PostMapping("/list")
    @PreAuthorize("hasRole('USER')")
    public List<String> listEndpoint(@RequestParam String name) {
        list.add(name);
        return list;
    }
}

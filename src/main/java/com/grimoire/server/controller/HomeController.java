package com.grimoire.server.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "Grimoire RPG Manager Server is Running! Use /sheet/{id} or /campaign/notes endpoints.";
    }
}

package com.certicamara.gateway.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FallbackController {

    @Value("${gateway.fallback.message:Servicio no disponible, intenta más tarde}")
    private String fallbackMessage;

    @GetMapping("/fallback")
    public String fallback() {
        return fallbackMessage;
    }
}
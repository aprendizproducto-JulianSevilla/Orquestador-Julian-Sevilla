package com.certicamara.cliente.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Value("${mensaje.test}")
    private String mensaje;

    @GetMapping("/test")
    public String test() {
        return "Mensaje desde Config Server: " + mensaje;
    }
}
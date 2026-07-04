package com.meraki.website.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.meraki.website.dto.LoginRequestDTO;
import com.meraki.website.service.LoginService;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @PostMapping("/admin")
    public ResponseEntity<String> login(@RequestBody LoginRequestDTO loginRequestDto) {

        String response = loginService.login(loginRequestDto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/heartbeat")
    public ResponseEntity<String> login() {
        String response = "hola";
        return ResponseEntity.ok(response);
    }
}


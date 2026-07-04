package com.meraki.website.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.meraki.website.dto.ApiResponse;
import com.meraki.website.dto.AuthLoginRequestDTO;
import com.meraki.website.dto.AuthLoginResponseDTO;
import com.meraki.website.dto.ChangePasswordRequestDTO;
import com.meraki.website.dto.SignupRequestDTO;
import com.meraki.website.dto.UserResponseDTO;
import com.meraki.website.service.AuthService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<UserResponseDTO>> signup(@RequestBody SignupRequestDTO body) {
        try {
            return ResponseEntity.ok(authService.signup(body));
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthLoginResponseDTO>> login(@RequestBody AuthLoginRequestDTO body) {
        try {
            return ResponseEntity.ok(authService.login(body));
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(@RequestBody ChangePasswordRequestDTO body) {
        try {
            return ResponseEntity.ok(authService.changePassword(body));
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponse<>(false, e.getMessage(), null));
        }
    }
}


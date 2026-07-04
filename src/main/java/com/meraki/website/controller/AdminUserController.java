package com.meraki.website.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.meraki.website.dto.ApiResponse;
import com.meraki.website.dto.UserResponseDTO;
import com.meraki.website.service.AdminUserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<Page<UserResponseDTO>>> pending(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        try {
            return ResponseEntity.ok(adminUserService.listPendingUsers(name, email, page, size));
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponse<>(false, e.getMessage(), Page.empty()));
        }
    }

    @GetMapping("/approved")
    public ResponseEntity<ApiResponse<Page<UserResponseDTO>>> approved(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        try {
            return ResponseEntity.ok(adminUserService.listApprovedUsers(name, email, page, size));
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponse<>(false, e.getMessage(), Page.empty()));
        }
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<UserResponseDTO>> approve(@PathVariable("id") String id) {
        try {
            return ResponseEntity.ok(adminUserService.approveUser(id));
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponse<>(false, e.getMessage(), null));
        }
    }
}


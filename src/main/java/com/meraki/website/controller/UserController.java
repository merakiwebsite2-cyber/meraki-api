package com.meraki.website.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.meraki.website.dto.UserResponseDTO;
import com.meraki.website.entity.User;
import com.meraki.website.service.UserService;
import com.meraki.website.dto.ApiResponse;

//todo refactor this controller
import lombok.RequiredArgsConstructor;

@RequestMapping("/user")
@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;

    // CREATE USER
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<UserResponseDTO>> createUser(@RequestBody User user) {
        try {
            ApiResponse<UserResponseDTO> response = userService.createUser(user);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Catch any exception and return as failed ApiResponse
            return ResponseEntity.ok(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // UPDATE USER
    @PostMapping("/update")
    public ResponseEntity<ApiResponse<UserResponseDTO>> updateUser(@RequestBody User user) {
        try {
            ApiResponse<UserResponseDTO> response = userService.updateUser(user.getId(), user);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // PAGINATED GET USERS
    @GetMapping("/paginated")
    public ResponseEntity<ApiResponse<Page<UserResponseDTO>>> getUsersPaginated(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        try {
            // Call service with optional filters
            ApiResponse<Page<UserResponseDTO>> response = userService.getUsersFilteredPaginated(name, email, page, size);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponseDTO>> getUserById(@PathVariable String id) {
        try {
            ApiResponse<UserResponseDTO> response = userService.getUserById(id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponse<>(false, e.getMessage(), null));
        }
    }
}
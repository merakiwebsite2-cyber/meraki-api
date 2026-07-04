package com.meraki.website.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.*;
import org.springframework.stereotype.Service;

import com.meraki.website.dto.ApiResponse;
import com.meraki.website.dto.UserResponseDTO;
import com.meraki.website.entity.User;
import com.meraki.website.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final MongoTemplate mongoTemplate;

    // CREATE USER
    public ApiResponse<UserResponseDTO> createUser(User user) {

        // Check email
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            log.warn("Email already exists: {}", user.getEmail());
            return new ApiResponse<>(false, "Email already exists", null);
        }

        // Check mobile
        if (userRepository.findByMobileNo(user.getMobileNo()).isPresent()) {
            log.warn("Mobile number already exists: {}", user.getMobileNo());
            return new ApiResponse<>(false, "Mobile number already exists", null);
        }

        user.setIsVerified(false);
        user.setRole("USER");
        user.setMustChangePassword(false);
        user.setPassword(null);

        User savedUser = userRepository.save(user);
        log.info("User created: {}", savedUser.getId());

        return new ApiResponse<>(true, "User created successfully", UserResponseDTO.mapToResponse(savedUser));
    }

    // UPDATE USER (partial update using DTO)
    public ApiResponse<UserResponseDTO> updateUser(String id, User updateRequest) {

        User existingUser = userRepository.findById(id).orElse(null);

        if (existingUser == null) {
            log.warn("User not found: {}", id);
            return new ApiResponse<>(false, "User not found", null);
        }

        // Partial updates
        if (updateRequest.getName() != null) existingUser.setName(updateRequest.getName());
        if (updateRequest.getEmail() != null) existingUser.setEmail(updateRequest.getEmail());
        if (updateRequest.getMobileNo() != null) existingUser.setMobileNo(updateRequest.getMobileNo());
        if (updateRequest.getCompanyName() != null) existingUser.setCompanyName(updateRequest.getCompanyName());
        if (updateRequest.getPassword() != null && !updateRequest.getPassword().isEmpty())
            existingUser.setPassword(updateRequest.getPassword());
        if (updateRequest.getIsVerified() != null)
            existingUser.setIsVerified(updateRequest.getIsVerified());

        User savedUser = userRepository.save(existingUser);
        log.info("User updated: {}", savedUser.getId());

        return new ApiResponse<>(true, "User updated successfully", UserResponseDTO.mapToResponse(savedUser));
    }

    // PAGINATED + FILTERED GET USERS
    public ApiResponse<Page<UserResponseDTO>> getUsersFilteredPaginated(
            String name, String email, int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Query query = new Query();
        if (name != null && !name.isEmpty()) {
            query.addCriteria(Criteria.where("name").regex(name, "i"));
        }
        if (email != null && !email.isEmpty()) {
            query.addCriteria(Criteria.where("email").is(email));
        }

        long total = mongoTemplate.count(query, User.class);
        query.with(pageable);

        List<User> users = mongoTemplate.find(query, User.class);
        List<UserResponseDTO> dtoList = users.stream()
                .map(UserResponseDTO::mapToResponse)
                .toList();

        Page<UserResponseDTO> pageResult = new PageImpl<>(dtoList, pageable, total);
        return new ApiResponse<>(true, "Users fetched successfully", pageResult);
    }

    public ApiResponse<UserResponseDTO> getUserById(String id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            log.warn("User not found: {}", id);
            return new ApiResponse<>(false, "User not found", null);
        }

        return new ApiResponse<>(
                true,
                "User fetched successfully",
                UserResponseDTO.mapToResponse(userOpt.get())
        );
    }

}
package com.meraki.website.service;

import java.security.SecureRandom;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.meraki.website.dto.ApiResponse;
import com.meraki.website.dto.UserResponseDTO;
import com.meraki.website.entity.User;
import com.meraki.website.repository.UserRepository;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final UserRepository userRepository;
    private final MongoTemplate mongoTemplate;
    private final EmailService emailService;

    private static final SecureRandom RNG = new SecureRandom();
    private static final char[] PASSWORD_CHARS =
            "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789!@#$%".toCharArray();

    public ApiResponse<Page<UserResponseDTO>> listPendingUsers(String name, String email, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Query query = new Query();
        query.addCriteria(Criteria.where("isVerified").is(false));
        if (StringUtils.hasText(name)) {
            query.addCriteria(Criteria.where("name").regex(name, "i"));
        }
        if (StringUtils.hasText(email)) {
            query.addCriteria(Criteria.where("email").is(email));
        }

        long total = mongoTemplate.count(query, User.class);
        query.with(pageable);
        List<User> users = mongoTemplate.find(query, User.class);
        List<UserResponseDTO> dtos = users.stream().map(UserResponseDTO::mapToResponse).toList();

        return new ApiResponse<>(true, "Pending users fetched successfully", new PageImpl<>(dtos, pageable, total));
    }


    public ApiResponse<Page<UserResponseDTO>> listApprovedUsers(String name, String email, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Query query = new Query();
        query.addCriteria(Criteria.where("isVerified").is(true));
        if (StringUtils.hasText(name)) {
            query.addCriteria(Criteria.where("name").regex(name, "i"));
        }
        if (StringUtils.hasText(email)) {
            query.addCriteria(Criteria.where("email").is(email));
        }

        long total = mongoTemplate.count(query, User.class);
        query.with(pageable);
        List<User> users = mongoTemplate.find(query, User.class);
        List<UserResponseDTO> dtos = users.stream().map(UserResponseDTO::mapToResponse).toList();

        return new ApiResponse<>(true, "Pending users fetched successfully", new PageImpl<>(dtos, pageable, total));
    }

    public ApiResponse<UserResponseDTO> approveUser(String userId) throws MessagingException {
        if (!StringUtils.hasText(userId)) return new ApiResponse<>(false, "Missing userId", null);

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return new ApiResponse<>(false, "User not found", null);
        if (Boolean.TRUE.equals(user.getIsVerified())) {
            return new ApiResponse<>(false, "User already approved", null);
        }
        if (!StringUtils.hasText(user.getEmail())) return new ApiResponse<>(false, "User has no email", null);

        String initialPassword = generatePassword(12);
        user.setPassword(initialPassword); // plain-text as requested (not recommended)
        user.setIsVerified(true);
        user.setRole(StringUtils.hasText(user.getRole()) ? user.getRole() : "USER");
        user.setMustChangePassword(true);

        User saved = userRepository.save(user);
        emailService.sendInitialPasswordV1(saved.getEmail(), initialPassword);

        return new ApiResponse<>(true, "User approved and password emailed", UserResponseDTO.mapToResponse(saved));
    }

    private String generatePassword(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(PASSWORD_CHARS[RNG.nextInt(PASSWORD_CHARS.length)]);
        }
        return sb.toString();
    }
}


package com.meraki.website.service;

import jakarta.mail.MessagingException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.meraki.website.dto.ApiResponse;
import com.meraki.website.dto.AuthLoginRequestDTO;
import com.meraki.website.dto.AuthLoginResponseDTO;
import com.meraki.website.dto.ChangePasswordRequestDTO;
import com.meraki.website.dto.SignupRequestDTO;
import com.meraki.website.dto.UserResponseDTO;
import com.meraki.website.entity.User;
import com.meraki.website.repository.UserRepository;
import com.meraki.website.security.JwtService;
import com.meraki.website.security.RequestUser;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final EmailService emailService;

    public ApiResponse<UserResponseDTO> signup(SignupRequestDTO body)
            throws MessagingException
    {
        if (body == null) return new ApiResponse<>(false, "Missing signup payload", null);
        if (!StringUtils.hasText(body.getEmail())) return new ApiResponse<>(false, "Email is required", null);
        if (!StringUtils.hasText(body.getMobileNo())) return new ApiResponse<>(false, "Mobile number is required", null);

        if (userRepository.findByEmail(body.getEmail()).isPresent()) {
            return new ApiResponse<>(false, "Email already exists", null);
        }
        if (userRepository.findByMobileNo(body.getMobileNo()).isPresent()) {
            return new ApiResponse<>(false, "Mobile number already exists", null);
        }

        User user = User.builder()
                .name(body.getName())
                .email(body.getEmail())
                .mobileNo(body.getMobileNo())
                .companyName(body.getCompanyName())
                .countryCode(body.getCountryCode())
                .country(body.getCountry())
                .role("USER")
                .isVerified(false)
                .mustChangePassword(false)
                .password(null)
                .build();

        User saved = userRepository.save(user);
        emailService.sendCustomerWelcomeEmail(saved.getEmail(),saved.getName());
        emailService.sendNewUserNotificationToAdmin(saved.getEmail(),saved.getName());
        return new ApiResponse<>(true, "Signup successful. Waiting for admin approval.", UserResponseDTO.mapToResponse(saved));
    }

    public ApiResponse<AuthLoginResponseDTO> login(AuthLoginRequestDTO body) {
        if (body == null) return new ApiResponse<>(false, "Missing login payload", null);
        if (!StringUtils.hasText(body.getEmail())) return new ApiResponse<>(false, "Email is required", null);
        if (!StringUtils.hasText(body.getPassword())) return new ApiResponse<>(false, "Password is required", null);

        User user = userRepository.findByEmail(body.getEmail()).orElse(null);
        if (user == null)
            return new ApiResponse<>(false, "Invalid credentials", null);
        if (Boolean.FALSE.equals(user.getIsVerified()))
            return new ApiResponse<>(false, "User not approved by admin yet", null);

        String stored = user.getPassword();
        if (!StringUtils.hasText(stored) || !stored.equals(body.getPassword())) {
            return new ApiResponse<>(false, "Invalid credentials", null);
        }

        String role = StringUtils.hasText(user.getRole()) ? user.getRole() : "USER";
        String token = jwtService.createToken(user.getId(), role);

        return new ApiResponse<>(true, "Login successful", AuthLoginResponseDTO.builder()
                .token(token)
                .userId(user.getId())
                .role(role)
                .mustChangePassword(Boolean.TRUE.equals(user.getMustChangePassword()))
                .build());
    }

    public ApiResponse<Void> changePassword(ChangePasswordRequestDTO body) {
        if (body == null) return new ApiResponse<>(false, "Missing change-password payload", null);
        if (!StringUtils.hasText(body.getNewPassword())) return new ApiResponse<>(false, "New password is required", null);

        String userId = RequestUser.userIdOrNull();
        if (!StringUtils.hasText(userId)) return new ApiResponse<>(false, "Unauthorized", null);

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return new ApiResponse<>(false, "Unauthorized", null);

        user.setPassword(body.getNewPassword());
        user.setMustChangePassword(false);
        userRepository.save(user);

        return new ApiResponse<>(true, "Password changed successfully", null);
    }
}


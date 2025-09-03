package com.femmie.marketplace.service;

import com.femmie.marketplace.dto.*;
import com.femmie.marketplace.model.Role;
import com.femmie.marketplace.model.User;
import com.femmie.marketplace.model.Vendor;
import com.femmie.marketplace.repository.UserRepository;
import com.femmie.marketplace.repository.VendorRepository;
import com.femmie.marketplace.security.JwtService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final VendorRepository vendorRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final ModelMapper modelMapper;
    private final AuthenticationManager authenticationManager;

    public ApiResponse<AuthResponse> register(RegisterRequest request) {
        try {
            if(userRepository.existsByEmail(request.getEmail())) {
                return ApiResponse.error("Email already registered");
            }

            User user = User.builder()
                    .name(request.getName())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .role(Role.USER)
                    .build();

            User savedUser = userRepository.save(user);

            String token = jwtService.generateToken(savedUser);
            String refreshToken = jwtService.generateRefreshToken(savedUser);

            UserProfileResponse userProfile = modelMapper.map(savedUser, UserProfileResponse.class);
            AuthResponse authResponse = AuthResponse.builder()
                    .token(token)
                    .refreshToken(refreshToken)
                    .user(userProfile)
                    .build();

            return ApiResponse.success(authResponse, "User registered successfully");
        } catch (Exception e) {
            return ApiResponse.error("Registration failed: " + e.getMessage());
        }
    }

    public ApiResponse<AuthResponse> login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            String token = jwtService.generateToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);

            UserProfileResponse userProfile = getUserProfileWithVendor(user);
            AuthResponse authResponse = AuthResponse.builder()
                    .token(token)
                    .refreshToken(refreshToken)
                    .user(userProfile)
                    .build();

            return ApiResponse.success(authResponse, "Login successful");

        } catch (Exception e) {
            return ApiResponse.error("Invalid credentials");
        }
    }

    public ApiResponse<UserProfileResponse> getProfile(String email) {
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            UserProfileResponse profile = getUserProfileWithVendor(user);
            return ApiResponse.success(profile, "Profile retrieved successfully");

        } catch (Exception e) {
            return ApiResponse.error("Failed to retrieve profile: " + e.getMessage());
        }
    }

    public ApiResponse<UserProfileResponse> upgradeToVendor(String email, VendorUpgradeRequest request) {
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Check if user is already a vendor
            if (user.getRole() == Role.VENDOR) {
                return ApiResponse.error("User is already a vendor");
            }

            // Check if vendor info already exists
            if (vendorRepository.existsByUserId(user.getId())) {
                return ApiResponse.error("Vendor information already exists for this user");
            }

            // Update user role
            user.setRole(Role.VENDOR);
            User updatedUser = userRepository.save(user);

            // Create vendor record
            Vendor vendor = Vendor.builder()
                    .shopName(request.getShopName())
                    .businessAddress(request.getBusinessAddress())
                    .phoneNumber(request.getPhoneNumber())
                    .user(updatedUser)
                    .build();

            vendorRepository.save(vendor);

            // Return updated profile
            UserProfileResponse profile = getUserProfileWithVendor(updatedUser);
            return ApiResponse.success(profile, "Successfully upgraded to vendor");

        } catch (Exception e) {
            return ApiResponse.error("Failed to upgrade to vendor: " + e.getMessage());
        }
    }

    public ApiResponse<AuthResponse> refreshToken(String refreshToken) {
        try {
            String email = jwtService.extractUsername(refreshToken);
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (jwtService.isTokenValid(refreshToken, user)) {
                String newToken = jwtService.generateToken(user);
                String newRefreshToken = jwtService.generateRefreshToken(user);

                UserProfileResponse userProfile = getUserProfileWithVendor(user);
                AuthResponse authResponse = AuthResponse.builder()
                        .token(newToken)
                        .refreshToken(newRefreshToken)
                        .user(userProfile)
                        .build();

                return ApiResponse.success(authResponse, "Token refreshed successfully");
            } else {
                return ApiResponse.error("Invalid refresh token");
            }
        } catch (Exception e) {
            return ApiResponse.error("Failed to refresh token: " + e.getMessage());
        }
    }

    private UserProfileResponse getUserProfileWithVendor(User user) {
        UserProfileResponse profile = modelMapper.map(user, UserProfileResponse.class);

        // Add vendor information if user is a vendor
        if (user.getRole() == Role.VENDOR) {
            vendorRepository.findByUserId(user.getId())
                    .ifPresent(vendor -> {
                        VendorResponse vendorResponse = modelMapper.map(vendor, VendorResponse.class);
                        profile.setVendor(vendorResponse);
                    });
        }

        return profile;
    }
}

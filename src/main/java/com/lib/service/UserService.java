package com.lib.service;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lib.model.Role;
import com.lib.model.User;
import com.lib.repository.UserRepository;

@Service
public class UserService{

	 private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OTPService otpService;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    private Map<String, User> temporaryUserStore = new HashMap<>();
    public String createUserWithRole(User user) throws UnsupportedEncodingException {
        // Check if the username already exists
        if (userRepository.findByUsername(user.getUsername()) != null) {
            return "User with this username already exists.";
        }
        // Save the user into the database
        userRepository.save(user);
        // Generate and send OTP for email verification
        String otp = otpService.generateOTP(user.getEmail());
        EmailService.sendOTP(user.getEmail(), otp);

        return "User created successfully with role: " + user.getRole() + ". OTP has been sent to the registered email.";
    }

    public String registerUser(User user) throws UnsupportedEncodingException {
        // Check if the user already exists
        if (userRepository.findByUsername(user.getUsername()) != null) {
            return "User already exists.";
        }
        if (temporaryUserStore.containsKey(user.getEmail())) {
            return "A registration is already pending OTP verification for this email.";
        }

        // Log role-specific actions
        if (user.getRole() == Role.ADMIN) {
            logger.info("Admin registration initiated for: {}", user.getUsername());
        }

        // Store user details temporarily
        temporaryUserStore.put(user.getEmail(), user);

        // Generate and send OTP
        String otp = otpService.generateOTP(user.getEmail());
        EmailService.sendOTP(user.getEmail(), otp);

        return "User registered successfully. OTP has been sent to the registered email.";
    }

    public String verifyRegistrationOtp(String email, String otp) {
        boolean isValidOtp = otpService.validateOTP(email, otp);
        if (isValidOtp) {
            User user = temporaryUserStore.get(email);
            if (user != null) {
                // Save user to the database
                userRepository.save(user);
                // Cleanup
                temporaryUserStore.remove(email);
                return "OTP verified successfully. Registration complete.";
            }
            return "No pending registration found for this email.";
        }
        return "Invalid OTP.";
    }

    public Map<String, String> loginUser(String username, String password) throws Exception {
        User user = userRepository.findByUsername(username);
        if (user == null || !password.equals(user.getPassword())) {
            throw new Exception("Invalid username or password.");
        }

        // Generate OTP
        String otp = otpService.generateOTP(user.getEmail());

        try {
            EmailService.sendOTP(user.getEmail(), otp);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send OTP. Please try again.", e);
        }

        // Return structured response
        return Map.of(
            "message", "Login successful. OTP sent to your email for verification.",
            "email", user.getEmail()
        );
    }

    public User findByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("User not found with username: " + username);
        }
        return user;
    }


    public boolean verifyLoginOtp(String email, String otp) {
        boolean isValidOtp = otpService.validateOTP(email, otp);
        if (isValidOtp) {
            return true;
        }
        return false;
    }

    public String initiatePasswordReset(String email) throws UnsupportedEncodingException {
    	email = email.trim().toLowerCase();
//        System.out.println(email);
        User user = userRepository.findByEmail(email);
//        System.out.println("User fetched: " + (user != null ? user.getEmail() : "null"));
        if (user == null) {
            return "Email not registered.";
        }

        // Generate and send OTP
        String otp = otpService.generateOTP(email);
        EmailService.sendOTP(email, otp); // Ensure `sendOTP` returns a boolean indicating success
        userRepository.save(user);
            return "An OTP has been sent to your registered email.";
    }


    public String verifyResetPasswordOtp(String email, String otp) {
        // Log the email and OTP for debugging
        System.out.println("Service: Verifying Email: " + email + ", OTP: " + otp);

        User user = userRepository.findByEmail(email);
        if (user == null || !otpService.validateOTP(email, otp)) {
            throw new IllegalArgumentException("Invalid email or OTP.");
        }

        // Mark OTP as used (if applicable)
        userRepository.save(user);
        return "OTP verified successfully.";
    }

    public String resetPassword(String email, String newPassword) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return "User not found with email: " + email;
        }
        user.setPassword(newPassword); // Encode password
        userRepository.save(user);
        return "Password reset successful";
    }
    public User findByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("User not found with email: " + email);
        }
        return user;
    }


    public User verifySession(String email) {
        // Fetch the user by email
        return userRepository.findByEmail(email);
    }
    public List<User> getUsersByRole(Role role) {
        return userRepository.findByRole(role);
    }



}

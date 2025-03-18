package com.lib.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.springframework.stereotype.Service;

@Service
public class OTPService {
    private final Map<String, String> otpStorage = new HashMap<>();
    private final Random random = new Random();

    public String generateOTP(String email) {
        email = email.trim().toLowerCase(); // Normalize
        String otp = "%04d".formatted(random.nextInt(10000));
        otpStorage.put(email, otp);
        System.out.println("Generated OTP for " + email + ": " + otp);
        return otp;
    }

    public boolean validateOTP(String email, String inputOtp) {
        email = email.trim().toLowerCase(); // Normalize
        String storedOtp = otpStorage.get(email);
        System.out.println("Validating OTP for " + email + ". Stored: " + storedOtp + ", Input: " + inputOtp);
        if (storedOtp != null && storedOtp.equals(inputOtp)) {
            otpStorage.remove(email);
            return true;
        }
        return false;
    }

}

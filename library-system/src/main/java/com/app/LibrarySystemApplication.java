package com.app;

import com.app.entity.User;
import com.app.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@SpringBootApplication
public class LibrarySystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(LibrarySystemApplication.class, args);
    }

    // --- TEMPORARY SIGNUP CODE START ---
    @Bean
    public CommandLineRunner run(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {

            // 1. Mobile Number check karo
            String mobile = "9792079092";

            Optional<User> existing = userRepository.findByMobile(mobile);

            if (existing.isEmpty()) {
                // 2. Agar user nahi hai, to naya banao
                User admin = new User();
                admin.setFullName("Shivam Admin");
                admin.setMobile(mobile);
                admin.setEmail("shivamkumar37je@gmail.com");

                // Password set karo (BCrypt me convert hoke save hoga)
                admin.setPassword(passwordEncoder.encode("shivamking123"));

                admin.setRole("ROLE_ADMIN"); // Isse Admin access mil jayega
                admin.setReferralCode("ADMIN01");
                admin.setWalletBalance(1000.0); // Testing ke liye balance
                admin.setActive(true);

                userRepository.save(admin);
                System.out.println("✅ SUCCESS: Admin User Created Successfully!");
                System.out.println("Login Mobile: " + mobile);
                System.out.println("Login Password: admin123");
            } else {
                System.out.println("⚠️ INFO: User already exists. No new user created.");
            }
        };
    }
    // --- TEMPORARY CODE END ---
}
package com.smartleave.backend.config;

import com.smartleave.backend.entity.User;
import com.smartleave.backend.enums.UserRole;
import com.smartleave.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class HRBootstrapConfig {

    @Bean
    CommandLineRunner bootstrapFirstHR(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.bootstrap.hr.enabled:false}") boolean enabled,
            @Value("${app.bootstrap.hr.name:}") String fullName,
            @Value("${app.bootstrap.hr.email:}") String email,
            @Value("${app.bootstrap.hr.password:}") String password) {

        return args -> {

            if (!enabled) {
                return;
            }

            if (userRepository.existsByRole(UserRole.HR)) {
                System.out.println("HR bootstrap skipped: an HR account already exists.");
                return;
            }

            if (fullName.isBlank() || email.isBlank() || password.isBlank()) {
                throw new IllegalStateException(
                        "HR bootstrap is enabled, but HR name, email, or password is missing."
                );
            }

            if (password.length() < 12) {
                throw new IllegalStateException(
                        "The initial HR password must contain at least 12 characters."
                );
            }

            String normalizedEmail = email.trim().toLowerCase();

            if (userRepository.existsByEmail(normalizedEmail)) {
                throw new IllegalStateException(
                        "The configured HR email already belongs to another account."
                );
            }

            User hr = new User();
            hr.setFullName(fullName.trim());
            hr.setEmail(normalizedEmail);
            hr.setPassword(passwordEncoder.encode(password));
            hr.setRole(UserRole.HR);
            hr.setActive(true);

            userRepository.save(hr);

            System.out.println("Initial HR account created successfully.");
        };
    }
}

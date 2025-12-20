package com.elearning.elearning_platform.user.infrastructure.config;

import com.elearning.elearning_platform.shared.domain.valueobject.Email;
import com.elearning.elearning_platform.user.domain.model.Role;
import com.elearning.elearning_platform.user.domain.model.User;
import com.elearning.elearning_platform.user.domain.port.out.UserRepositoryPort;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;


/**
 * Seeds initial application data at startup.
 *
 * This class ensures that a default ADMIN user exists when the application starts.
 * It will not create the user if an admin with the same email already exists.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepositoryPort userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepositoryPort userRepository,
                      PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Executes automatically at application startup.
     *
     * @param args application startup arguments
     */
    @Override
    public void run(String... args) {
        Email adminEmail = Email.of("admin@elearning.com");

        /// Check if admin user already exists
        if (userRepository.existsByEmail(adminEmail)) {
            return;
        }

        /// Hash default admin password
        String hashedPassword = passwordEncoder.encode("Admin123!");

        /// Create admin user
        User admin = User.create(
                adminEmail,
                hashedPassword,
                "Admin",
                "User",
                Role.ADMIN
        );
        ///  Persist admin user
        userRepository.save(admin);

        /// Optional log
        System.out.println("Admin user created: admin@learning.com / Admin123!");
    }

}

package com.elearning.elearning_platform.user.infrastructure.config;

import com.elearning.elearning_platform.shared.domain.valueobject.Email;
import com.elearning.elearning_platform.user.domain.model.Role;
import com.elearning.elearning_platform.user.domain.model.User;
import com.elearning.elearning_platform.user.domain.port.out.UserRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Seeds initial application data at startup.
 *
 * <p>This class ensures that a default ADMIN user exists when the application starts.
 * It will not create the user if an admin with the same email already exists.
 *
 * <p>Executes with highest priority (@Order(1)) to ensure admin user
 * is available before other seeders run.
 */
@Component
@Order(1)  // ← Ejecuta primero
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final UserRepositoryPort userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepositoryPort userRepository,
                      PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Executes automatically at application startup.
     *
     * <p>Creates a default admin user if one does not already exist.
     * This operation is idempotent - safe to run multiple times.
     *
     * @param args application startup arguments
     */
    @Override
    public void run(String... args) {
        seedAdminUser();
    }

    /**
     * Creates the default admin user if it doesn't exist.
     *
     * <p>This method is idempotent and safe to call multiple times.
     */
    private void seedAdminUser() {
        Email adminEmail = Email.of("admin@elearning.com");

        // Check if admin user already exists
        if (userRepository.findByEmail(adminEmail).isPresent()) {
            log.info("Admin user already exists, skipping creation");
            return;
        }

        try {
            // Hash default admin password
            String hashedPassword = passwordEncoder.encode("Admin123!");

            // Create admin user
            User admin = User.create(
                    adminEmail,
                    hashedPassword,
                    "Admin",
                    "User",
                    Role.ADMIN
            );

            // Persist admin user
            userRepository.save(admin);

            log.info("Admin user created successfully: admin@elearning.com / Admin123!");
        } catch (Exception e) {
            log.error("Failed to create admin user", e);
            // No lanzar excepción para no detener la app
        }
    }
}

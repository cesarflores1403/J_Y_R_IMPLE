package com.jyr.system.config;

import com.jyr.system.entity.User;
import com.jyr.system.enums.Role;
import com.jyr.system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            User admin = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .fullName("Administrador JYR")
                    .email("admin@jyr.com")
                    .role(Role.ADMIN)
                    .build();
            admin.setActive(true);
            userRepository.save(admin);

            User cajero = User.builder()
                    .username("cajero")
                    .password(passwordEncoder.encode("cajero123"))
                    .fullName("Cajero JYR")
                    .email("cajero@jyr.com")
                    .role(Role.CAJERO)
                    .build();
            cajero.setActive(true);
            userRepository.save(cajero);

            User bodeguero = User.builder()
                    .username("bodeguero")
                    .password(passwordEncoder.encode("bodeguero123"))
                    .fullName("Bodeguero JYR")
                    .email("bodeguero@jyr.com")
                    .role(Role.BODEGUERO)
                    .build();
            bodeguero.setActive(true);
            userRepository.save(bodeguero);

            log.info("=== Usuarios iniciales creados ===");
            log.info("Admin: admin / admin123");
            log.info("Cajero: cajero / cajero123");
            log.info("Bodeguero: bodeguero / bodeguero123");
        }
    }
}

package com.example.ecommerce.config;

import com.example.ecommerce.entity.User;
import com.example.ecommerce.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(UserRepository userRepository,PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Load initial data into the database
        if (userRepository.findByUsername("admin").isPresent()) {
            return;
        }

        User user = new User();
        user.setName("admin");
        user.setUsername("admin");
        user.setPassword(passwordEncoder.encode("Abc@12345"));
        user.setRole("ADMIN");

        userRepository.save(user);
    }
}
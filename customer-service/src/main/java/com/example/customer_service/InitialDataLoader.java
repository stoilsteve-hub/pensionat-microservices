package com.example.customer_service;

import com.example.customer_service.model.Customer;
import com.example.customer_service.repository.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class InitialDataLoader implements CommandLineRunner {

    private final CustomerRepository customerRepo;
    private final PasswordEncoder passwordEncoder;

    public InitialDataLoader(CustomerRepository customerRepo, PasswordEncoder passwordEncoder) {
        this.customerRepo = customerRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (customerRepo.findByEmail("test@test.se").isEmpty()) {
            Customer customer = new Customer();
            customer.setName("Test User");
            customer.setEmail("test@test.se");
            customer.setAddress("Test Street 1");
            customer.setPhone("0701234567");
            customer.setPassword(passwordEncoder.encode("test123"));
            customerRepo.save(customer);
        }
    }
}
package com.example.customer_service.service;

import com.example.customer_service.config.RestTemplateConfig;
import com.example.customer_service.model.*;
import com.example.customer_service.repository.CustomerRepository;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final RestTemplate restTemplate;
    private final PasswordEncoder passwordEncoder;

    public CustomerService(RestTemplateConfig restTemplateConfig, CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
        this.restTemplate = restTemplateConfig.restTemplate();
    }

    public CustomerResult loginRequestIsValid(LoginRequestDTO requestDTO) {
        CustomerResult result = loginCustomer(requestDTO.getEmail(), requestDTO.getPassword());
        System.out.println("result in loginRequestIsValid: " + result.feedback());
        return result;
    }

    public CustomerResult signupRequestIsValid(CustomerDTO dto) {
        if (dto.getEmail() == null || dto.getEmail().isBlank()) {
            return new CustomerResult(null, Feedback.EMPTY_EMAIL);
        }
        if (dto.getPassword() == null || dto.getPassword().isBlank()) {
            return new CustomerResult(null, Feedback.EMPTY_PASSWORD);
        }
        return createCustomer(dto);
    }

    public CustomerDTO getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id).orElse(null);
        return customer != null ? toDTO(customer) : null;
    }

    public List<CustomerDTO> getAllCustomers() {
        return toDTOList(customerRepository.findAll());
    }

    public CustomerDTO toDTO(Customer c) {
        return new CustomerDTO(c.getId(), c.getName(), c.getEmail(), c.getAddress(), c.getPhone());
    }

    public List<CustomerDTO> toDTOList(List<Customer> customerList) {
        List<CustomerDTO> customerDTOS = new ArrayList<>();
        for (Customer c : customerList) {
            customerDTOS.add(toDTO(c));
        }
        return customerDTOS;
    }

    public CustomerResult createCustomer(CustomerDTO dto) {
        Optional<Customer> existingCustomer = customerRepository.findByEmail(dto.getEmail());
        if (existingCustomer.isPresent()) {
            return new CustomerResult(null, Feedback.USER_EXISTS);
        }

        Customer newCustomer = (customerRepository.save(new Customer(dto.getName(), dto.getEmail(), dto.getAddress(),
                dto.getPhone(), passwordEncoder.encode(dto.getPassword()))));
        return new CustomerResult(toDTO(newCustomer), Feedback.OK);
    }

    public CustomerResult updateCustomer(Long id, CustomerDTO customer) {
        Customer existing = customerRepository.findById(id).orElse(null);
        if (existing == null) {
            return new CustomerResult(null, Feedback.INVALID_USER);
        }
        Optional<Customer> emailOwner = customerRepository.findByEmail(customer.getEmail());
        if (emailOwner.isPresent() && emailOwner.get().getId() != id) {
//            throw new EmailExistsException("Email already exists");
            return new CustomerResult(null, Feedback.USER_EXISTS);
        }

        existing.setName(customer.getName());
        existing.setEmail(customer.getEmail());
        existing.setAddress(customer.getAddress());
        existing.setPhone(customer.getPhone());
        if (customer.getPassword() != null && !customer.getPassword().isBlank()) {
            existing.setPassword(passwordEncoder.encode(customer.getPassword()));
        }

        return new CustomerResult(toDTO(customerRepository.save(existing)), Feedback.OK);
    }

    public CustomerResult deleteCustomer(Long customerId) {
        ResponseEntity<Boolean> response = hasActiveBooking(customerId);
        if (response.getStatusCode() == HttpStatus.SERVICE_UNAVAILABLE) {
            return new CustomerResult(null, Feedback.BOOKING_SERVICE_UNAVAILABLE);
        }
        if (Boolean.TRUE.equals(response.getBody())) {
            return new CustomerResult(null, Feedback.HAS_ACTIVE_BOOKINGS);
        }
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if (customer != null) {
            customerRepository.deleteById(customerId);
            return new CustomerResult(null, Feedback.OK);
        }
        return new CustomerResult(null, Feedback.INVALID_USER);
    }

    public ResponseEntity<Boolean> hasActiveBooking(Long customerId) {
        try {
            return restTemplate.getForEntity("http://booking-service:8080/bookings/customer/" + customerId
                    + "/has-active-booking", Boolean.class);
        }
        catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        }
        catch (ResourceAccessException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
    }

    public CustomerResult loginCustomer(String email, String password) {
        if (email == null || email.isBlank()) {
            return new CustomerResult(null, Feedback.EMPTY_EMAIL);
        } else if (password == null || password.isBlank()) {
            return new CustomerResult(null, Feedback.EMPTY_PASSWORD);
        }
        Customer savedCustomer = customerRepository.findByEmail(email).stream().findAny().orElse(null);
        if (savedCustomer != null) {
            if (passwordEncoder.matches(password, savedCustomer.getPassword())) {
//            savedCustomer = customerRepository.findByEmail(email).stream().filter(customer ->
//                    passwordEncoder.matches(password, customer.getPassword())).findAny().orElse(null);
//            if (savedCustomer != null) {
                return new CustomerResult(toDTO(savedCustomer), Feedback.OK);
            } else {
                return new CustomerResult(null, Feedback.INVALID_PASSWORD);
            }
        }
        return new CustomerResult(null, Feedback.INVALID_EMAIL);
    }
}
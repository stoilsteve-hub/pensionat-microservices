package com.example.customer_service.controller;

import com.example.customer_service.model.*;
import com.example.customer_service.service.CustomerService;
import com.example.customer_service.service.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerRestController {
    private final CustomerService customerService;
    private final JwtService jwtService;

    public CustomerRestController(CustomerService customerService, JwtService jwtService) {
        this.customerService = customerService;
        this.jwtService = jwtService;
    }

    @GetMapping
    public ResponseEntity<List<CustomerDTO>> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerDTO> getCustomer(@PathVariable Long id) {
        CustomerDTO customer = customerService.getCustomerById(id);
        if (customer != null) {
            return ResponseEntity.ok(customer);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> customerExists(@RequestBody LoginRequestDTO requestDTO){
        CustomerResult result = customerService.loginRequestIsValid(requestDTO);
        if (result.feedback() != Feedback.OK){
            return ResponseEntity.status(getStatusFromFeedback(result.feedback(), false)).build();
        }
        String token = jwtService.generateToken(
                result.dto().getId(),
                result.dto().getEmail());

        return ResponseEntity.ok(new LoginResponseDTO(result.dto(),token));
    }

    @PostMapping("/signup")
    public ResponseEntity<CustomerDTO> registerCustomer(@RequestBody CustomerDTO customer) {
        CustomerResult result = customerService.signupRequestIsValid(customer);
        if (result.feedback() != Feedback.OK){
            return ResponseEntity.status(getStatusFromFeedback(result.feedback(), true)).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(result.dto());
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerDTO> updateCustomer(@PathVariable Long id, @RequestBody CustomerDTO customer) {
        CustomerResult updated = customerService.updateCustomer(id, customer);
        if (updated != null) {
            return ResponseEntity.ok(updated.dto());
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        CustomerResult result = customerService.deleteCustomer(id);
        if (result.feedback() != Feedback.OK){
            return ResponseEntity.status(getStatusFromFeedback(result.feedback(), false)).build();
        }
        return ResponseEntity.ok().build();
    }

    private HttpStatus getStatusFromFeedback(Feedback feedback, boolean create) {
        return switch (feedback){
            case OK -> create ? HttpStatus.CREATED : HttpStatus.OK;
            case EMPTY_EMAIL, EMPTY_PASSWORD -> HttpStatus.BAD_REQUEST;
            case USER_EXISTS, HAS_ACTIVE_BOOKINGS -> HttpStatus.CONFLICT;
            case INVALID_PASSWORD -> HttpStatus.UNAUTHORIZED;
            case INVALID_EMAIL, INVALID_USER -> HttpStatus.NOT_FOUND;
            case BOOKING_SERVICE_UNAVAILABLE -> HttpStatus.SERVICE_UNAVAILABLE;
        };
    }
}

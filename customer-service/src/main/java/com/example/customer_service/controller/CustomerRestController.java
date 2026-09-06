package com.example.customer_service.controller;

import com.example.customer_service.model.*;
import com.example.customer_service.service.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerRestController {
    private final CustomerService customerService;

    public CustomerRestController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public ResponseEntity<List<CustomerDTO>> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerDTO> getCustomer(@PathVariable Long id) {
        CustomerDTO customer = customerService.getCustomerById(id);
        return (customer != null) ? ResponseEntity.ok(customer) : ResponseEntity.notFound().build();
    }

    @PostMapping("/login")
    public ResponseEntity<CustomerDTO> customerExists(@RequestBody LoginRequestDTO requestDTO){
        CustomerResult result = customerService.loginRequestIsValid(requestDTO);
        return (result.feedback() == Feedback.OK) ? ResponseEntity.ok(result.dto()) :
                ResponseEntity.status(getStatusFromFeedback(result.feedback(), false)).build();
    }

    @PostMapping("/signup")
    public ResponseEntity<CustomerDTO> registerCustomer(@RequestBody CustomerDTO customer) {
        CustomerResult result = customerService.signupRequestIsValid(customer);
        return (result.feedback() == Feedback.OK) ? ResponseEntity.status(HttpStatus.CREATED).body(result.dto()) :
                ResponseEntity.status(getStatusFromFeedback(result.feedback(), true)).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerDTO> updateCustomer(@PathVariable Long id, @RequestBody CustomerDTO customer) {
        CustomerResult updated = customerService.updateCustomer(id, customer);
        return (updated != null) ? ResponseEntity.ok(updated.dto()) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        CustomerResult result = customerService.deleteCustomer(id);
        return (result.feedback() == Feedback.OK) ? ResponseEntity.ok().build() :
                ResponseEntity.status(getStatusFromFeedback(result.feedback(), false)).build();
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
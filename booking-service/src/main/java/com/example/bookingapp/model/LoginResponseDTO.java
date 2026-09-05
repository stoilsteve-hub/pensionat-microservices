package com.example.bookingapp.model;

public class LoginResponseDTO {
    private CustomerDTO customer;
    private String token;

    public LoginResponseDTO(CustomerDTO customer, Feedback feedback) {
    }

    public CustomerDTO getCustomer() {
        return customer;
    }

    public String getToken() {
        return token;
    }
}
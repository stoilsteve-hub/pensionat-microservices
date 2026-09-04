package com.example.customer_service.model;

public class LoginResponseDTO {
    private CustomerDTO customer;
    private String token;

    public LoginResponseDTO(CustomerDTO customer, String token) {
        this.customer = customer;
        this.token = token;
    }

    public CustomerDTO getCustomer() {
        return customer;
    }

    public String getToken() {
        return token;
    }
}

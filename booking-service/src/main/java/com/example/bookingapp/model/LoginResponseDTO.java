package com.example.bookingapp.model;

public class LoginResponseDTO {

    private CustomerDTO customer;
    private String token;

    public LoginResponseDTO() {
    }

    public LoginResponseDTO(CustomerDTO customer, String token) {
        this.customer = customer;
        this.token = token;
    }

    public CustomerDTO getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerDTO customer) {
        this.customer = customer;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
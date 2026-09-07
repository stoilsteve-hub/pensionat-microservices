package com.example.customer_service.model;

import lombok.Getter;

@Getter
public class LoginRequestDTO {
    String email;
    String password;

    public LoginRequestDTO() {
    }

    public LoginRequestDTO(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
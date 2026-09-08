package com.example.customer_service.model;

import lombok.Getter;
import lombok.Setter;

@Getter
public class CustomerDTO {
    private Long id;
    private String name;
    private String email;
    private String address;
    private String phone;
    @Setter
    private String password;

    public CustomerDTO() {
    }

    public CustomerDTO(Long id, String name, String email, String address, String phone) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.address = address;
        this.phone = phone;
    }
}
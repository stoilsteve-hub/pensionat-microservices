package com.example.customer_service.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ReviewCustomerDTO {
    private String name;

    public ReviewCustomerDTO(){

    }

    public ReviewCustomerDTO(String name) {
        this.name = name;
    }
}
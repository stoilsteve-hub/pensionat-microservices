package com.example.reviewservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Setter
@Getter
@Entity
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long roomId;
    private Long customerId;
    private LocalDate startDate;
    private LocalDate endDate;
    private int rating;
    private String comment;
    private LocalDate submitDate;

    public Review() {
    }
}

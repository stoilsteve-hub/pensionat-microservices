package com.example.reviewservice.model;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Setter
@Getter
public class ReviewRequestDTO {
    private Long roomId;
    private Long customerId;
    private LocalDate startDate;
    private LocalDate endDate;
    private int rating;
    private String comment;

    public ReviewRequestDTO() {
    }
}
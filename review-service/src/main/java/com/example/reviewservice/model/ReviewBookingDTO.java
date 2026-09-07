package com.example.reviewservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewBookingDTO {
        private Long id;
//    private Long roomid;
//    private int cost;
//    private int guestcount;
//    private boolean extrabed;
    private LocalDate startDate;
    private LocalDate endDate;
}
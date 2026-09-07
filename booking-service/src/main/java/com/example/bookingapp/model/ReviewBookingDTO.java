package com.example.bookingapp.model;

import java.time.LocalDate;

public class ReviewBookingDTO {
    private LocalDate startdate;
    private LocalDate enddate;

    public ReviewBookingDTO(LocalDate startdate, LocalDate enddate){
        this.startdate = startdate;
        this.enddate = enddate;
    }

    public LocalDate getStartdate() {
        return startdate;
    }

    public void setStartdate(LocalDate startdate) {
        this.startdate = startdate;
    }

    public LocalDate getEnddate() {
        return enddate;
    }

    public void setEnddate(LocalDate enddate) {
        this.enddate = enddate;
    }
}

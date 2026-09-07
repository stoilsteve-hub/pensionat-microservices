package com.example.reviewservice.repository;

import com.example.reviewservice.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByRoomId(Long roomId);
    boolean existsByCustomerIdAndRoomIdAndStartDateAndEndDate(Long customerId, Long roomId, LocalDate startDate, LocalDate endDate);
}
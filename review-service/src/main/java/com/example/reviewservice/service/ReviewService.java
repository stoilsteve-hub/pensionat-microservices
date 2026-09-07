package com.example.reviewservice.service;

import com.example.reviewservice.model.*;
import com.example.reviewservice.repository.ReviewRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.time.LocalDate;
import java.util.*;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final RestTemplate restTemplate;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
        this.restTemplate = new RestTemplate();
    }

    public ReviewCustomerDTO fetchReviewCustomerDTO(Long customerId){
        try {
            ResponseEntity<ReviewCustomerDTO> customerResponse = restTemplate.getForEntity(
                    "http://customer-service:8081/api/customers/review/" + customerId, ReviewCustomerDTO.class);
            if (!customerResponse.getStatusCode().is2xxSuccessful() || customerResponse.getBody() == null) {
                return null;
            }
            return customerResponse.getBody();
        } catch (Exception e) {
            return null;
        }
    }

    public ReviewBookingDTO fetchBookingDTO(Long customerId, Long roomId, LocalDate startDate, LocalDate endDate) {
        try {
            String url = "http://booking-service:8080/bookings/customer/review" + "?customerId=" + customerId +
                    "&roomId=" + roomId + "&startDate=" + startDate + "&endDate=" + endDate;
            ResponseEntity<ReviewBookingDTO> bookingResponse = restTemplate.getForEntity(url, ReviewBookingDTO.class);
            if (!bookingResponse.getStatusCode().is2xxSuccessful() || bookingResponse.getBody() == null) {
                return null;
            }
            return bookingResponse.getBody();
        } catch (Exception e) {
            return null;
        }
    }

    public boolean saveReview(ReviewRequestDTO request) {
        if (!reviewRequestIsValid(request)) {
            return false;
        }
        Review review = new Review();
        review.setRoomId(request.getRoomId());
        review.setCustomerId(request.getCustomerId());
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review.setSubmitDate(LocalDate.now());
        review.setStartDate(request.getStartDate());
        review.setEndDate(request.getEndDate());
        reviewRepository.save(review);
        return true;
    }

    public boolean reviewRequestIsValid(ReviewRequestDTO r) {
        if (r == null || r.getCustomerId() == null || r.getRoomId() == null || r.getStartDate() == null || r.getEndDate() == null) {
            return false;
        }
        if (reviewRepository.existsByCustomerIdAndRoomIdAndStartDateAndEndDate(r.getCustomerId(),
                r.getRoomId(), r.getStartDate(), r.getEndDate())) {
            return false;
        }
        if (r.getRating() < 1 || r.getRating() > 5) {
            return false;
        }
        if (r.getComment() == null || r.getComment().isBlank()) {
            return false;
        }
        ReviewCustomerDTO customer = fetchReviewCustomerDTO(r.getCustomerId());
        if (customer == null) {
            return false;
        }
        ReviewBookingDTO booking = fetchBookingDTO(r.getCustomerId(), r.getRoomId(), r.getStartDate(), r.getEndDate());
        return booking != null;
    }

    public ReviewCollectionDTO getReviewsForRoom(Long roomId) {
        List<Review> reviews = reviewRepository.findByRoomId(roomId);
        List<ReviewDTO> reviewDTOs = new ArrayList<>();
        double totalRating = 0;
        for (Review r : reviews) {
            System.out.print("r.id: " + r.getCustomerId());
            ReviewDTO dto = new ReviewDTO();
            dto.setStars(r.getRating());
            dto.setComments(r.getComment());
            dto.setSubmitdate(r.getSubmitDate());
            dto.setStartdate(r.getStartDate());
            dto.setEnddate(r.getEndDate());
            ReviewCustomerDTO response = fetchReviewCustomerDTO(r.getCustomerId());
            if (response != null) {
                dto.setCustomer(response.getName());
            } else {
                dto.setCustomer("Unknown Customer");
            }
            reviewDTOs.add(dto);
            totalRating += r.getRating();
        }
        ReviewCollectionDTO collection = new ReviewCollectionDTO();
        collection.setReviews(reviewDTOs);
        collection.setTotalReviews(reviews.size());
        if (!reviews.isEmpty()) {
            collection.setAverage(totalRating / reviews.size());
        } else {
            collection.setAverage(0);
        }
        return collection;
    }
}
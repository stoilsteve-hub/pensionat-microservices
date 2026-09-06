package com.example.reviewservice.controller;

import com.example.reviewservice.model.ReviewCollectionDTO;
import com.example.reviewservice.model.ReviewRequestDTO;
import com.example.reviewservice.service.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reviews")
@CrossOrigin(origins = "*")
public class ReviewController {
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public ResponseEntity<Void> addReview(@RequestBody ReviewRequestDTO request) {
        reviewService.saveReview(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/room/{roomId}")
    public ResponseEntity<ReviewCollectionDTO> getReviewsByRoomId(@PathVariable Long roomId) {
        ReviewCollectionDTO collection = reviewService.getReviewsForRoom(roomId);
        return ResponseEntity.ok(collection);
    }
}
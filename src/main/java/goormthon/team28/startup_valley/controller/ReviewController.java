package goormthon.team28.startup_valley.controller;

import goormthon.team28.startup_valley.annotation.UserId;
import goormthon.team28.startup_valley.dto.global.ResponseDto;
import goormthon.team28.startup_valley.dto.request.ReviewCreateDto;
import goormthon.team28.startup_valley.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/teams/{teamsId}/peer-review")
    public ResponseDto<?> listPeerReview(@UserId Long userId, @PathVariable Long teamsId) {
        return ResponseDto.ok(reviewService.listPeerReview(userId, teamsId));
    }

    @PostMapping("/teams/{teamsId}/peer-review")
    public ResponseDto<?> postPeerReview(
            @UserId Long userId,
            @PathVariable Long teamsId,
            @RequestBody ReviewCreateDto reviewCreateDto
    ) {
        return ResponseDto.created(reviewService.postPeerReview(userId, teamsId, reviewCreateDto));
    }
}

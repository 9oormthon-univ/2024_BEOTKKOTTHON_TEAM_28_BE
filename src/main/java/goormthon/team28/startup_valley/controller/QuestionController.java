package goormthon.team28.startup_valley.controller;

import goormthon.team28.startup_valley.annotation.UserId;
import goormthon.team28.startup_valley.dto.global.ResponseDto;
import goormthon.team28.startup_valley.dto.request.QuestionCreateDto;
import goormthon.team28.startup_valley.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class QuestionController {

    private final QuestionService questionService;

    @GetMapping("/teams/{teamsId}/questions/wait")
    public ResponseDto<?> listWaitingQuestion(@UserId Long userId, @PathVariable Long teamsId) {
        return ResponseDto.ok(questionService.listWaitingQuestion(userId, teamsId));
    }

    @GetMapping("/teams/{teamsId}/questions/received")
    public ResponseDto<?> listReceivedQuestion(@UserId Long userId, @PathVariable Long teamsId) {
        return ResponseDto.ok(questionService.listReceivedQuestion(userId, teamsId, Boolean.TRUE));
    }

    @GetMapping("/teams/{teamsId}/questions/sent")
    public ResponseDto<?> listSentQuestion(@UserId Long userId, @PathVariable Long teamsId) {
        return ResponseDto.ok(questionService.listReceivedQuestion(userId, teamsId, Boolean.FALSE));
    }

    @PostMapping("/team/{teamsId}/questions")
    public ResponseDto<?> postQuestion(
            @UserId Long userId,
            @PathVariable Long teamsId,
            @RequestBody QuestionCreateDto questionCreateDto
            ) {
        return ResponseDto.created(questionService.postQuestion(userId, teamsId, questionCreateDto));
    }
}

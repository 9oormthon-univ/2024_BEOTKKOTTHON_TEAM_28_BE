package goormthon.team28.startup_valley.controller;

import goormthon.team28.startup_valley.annotation.UserId;
import goormthon.team28.startup_valley.dto.global.ResponseDto;
import goormthon.team28.startup_valley.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class QuestionController {

    private final QuestionService questionService;

    @GetMapping("/teams/{teamsId}/questions/wait")
    public ResponseDto<?> listWaitingQuestion(@UserId Long userId, @PathVariable Long teamsId) {
        return ResponseDto.ok(questionService.listWaitingQuestion(userId, teamsId));
    }
}

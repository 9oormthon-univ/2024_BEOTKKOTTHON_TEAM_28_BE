package goormthon.team28.startup_valley.controller;

import goormthon.team28.startup_valley.annotation.UserId;
import goormthon.team28.startup_valley.dto.global.ResponseDto;
import goormthon.team28.startup_valley.dto.request.AnswerCreateDto;
import goormthon.team28.startup_valley.service.AnswerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AnswerController {

    private final AnswerService answerService;

    @PostMapping("/answers")
    public ResponseDto<?> postAnswer(
            @UserId Long userId,
            @RequestBody AnswerCreateDto answerCreateDto
    ) {
        return ResponseDto.created(answerService.postAnswer(userId, answerCreateDto));
    }

}

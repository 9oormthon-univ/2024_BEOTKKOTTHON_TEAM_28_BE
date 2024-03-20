package goormthon.team28.startup_valley.controller;

import goormthon.team28.startup_valley.annotation.UserId;
import goormthon.team28.startup_valley.dto.global.ResponseDto;
import goormthon.team28.startup_valley.service.ScrumService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ScrumController {

    private final ScrumService scrumService;

    @GetMapping("/members/{membersId}/scrums")
    public ResponseDto<?> listScrum(@UserId Long userId, @PathVariable Long membersId) {
        return ResponseDto.ok(scrumService.listScrum(userId, membersId));
    }
}

package goormthon.team28.startup_valley.controller;

import goormthon.team28.startup_valley.annotation.UserId;
import goormthon.team28.startup_valley.dto.global.ResponseDto;
import goormthon.team28.startup_valley.service.WorkService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class WorkController {

    private final WorkService workService;

    @GetMapping("/teams/{teamsId}/works")
    public ResponseDto<?> listMemberWork(@UserId Long userId, @PathVariable Long teamsId) {
        return ResponseDto.ok(workService.listMemberWork(userId, teamsId));
    }

    @GetMapping("/teams/{teamsId}/works/ranking")
    public ResponseDto<?> getRanking(@UserId Long userId, @PathVariable Long teamsId) {
        return ResponseDto.ok(workService.getRanking(userId, teamsId));
    }
}

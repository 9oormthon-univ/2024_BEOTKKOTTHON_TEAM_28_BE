package goormthon.team28.startup_valley.controller;

import goormthon.team28.startup_valley.annotation.UserId;
import goormthon.team28.startup_valley.dto.global.ResponseDto;
import goormthon.team28.startup_valley.service.ScrumService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ScrumController {

    private final ScrumService scrumService;

    @GetMapping("/teams/{teamsId}/scrums")
    public ResponseDto<?> listScrum(
            @UserId Long userId,
            @PathVariable Long teamsId,
            @RequestParam(required = false) Long target
    ) {
        return ResponseDto.ok(scrumService.listScrum(userId, teamsId, target));
    }
}

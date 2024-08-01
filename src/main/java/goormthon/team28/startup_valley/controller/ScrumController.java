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

    @GetMapping("/members/{membersId}/teams/{teamsId}/scrums")
    public ResponseDto<?> listScrum(
            @UserId Long userId,
            @PathVariable Long membersId,
            @PathVariable Long teamsId
    ) {
        return ResponseDto.ok(scrumService.listScrum(userId, membersId, teamsId));
    }
}

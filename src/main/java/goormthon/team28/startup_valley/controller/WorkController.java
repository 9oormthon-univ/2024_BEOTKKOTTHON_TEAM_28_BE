package goormthon.team28.startup_valley.controller;

import goormthon.team28.startup_valley.annotation.UserId;
import goormthon.team28.startup_valley.dto.global.ResponseDto;
import goormthon.team28.startup_valley.dto.request.WorkTimeDto;
import goormthon.team28.startup_valley.service.WorkService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class WorkController {

    private final WorkService workService;

    @GetMapping("/teams/{teamsId}/works")
    public ResponseDto<?> listMemberWork(
            @UserId Long userId,
            @PathVariable Long teamsId,
            @RequestParam String sort
    ) {
        return ResponseDto.ok(workService.listMemberWork(userId, teamsId, sort));
    }

    @GetMapping("/teams/{teamsId}/works/ranking")
    public ResponseDto<?> getRanking(@UserId Long userId, @PathVariable Long teamsId) {
        return ResponseDto.ok(workService.getRanking(userId, teamsId));
    }

    @GetMapping("/members/{membersId}/works")
    public ResponseDto<?> listManageWork(@UserId Long userId, @PathVariable Long membersId) {
        return ResponseDto.ok(workService.listManageWork(userId, membersId));
    }

    @PatchMapping("/members/{membersId}/works/{worksId}")
    public ResponseDto<?> patchManageWork(
            @UserId Long userId,
            @PathVariable Long membersId,
            @PathVariable Long worksId,
            @RequestBody WorkTimeDto workTimeDto
            ) {
        return ResponseDto.ok(workService.patchManageWork(userId, membersId, worksId, workTimeDto));
    }

    @GetMapping("/members/{membersId}/works/measure-one")
    public ResponseDto<?> measureTeamMemberWork(@UserId Long userId, @PathVariable Long membersId) {
        return ResponseDto.ok(workService.measureTeamMemberWork(userId, membersId));
    }

    @GetMapping("/members/{membersId}/works/measure-all")
    public ResponseDto<?> measureAllWork(@UserId Long userId, @PathVariable Long membersId) {
        return ResponseDto.ok(workService.measureAllWork(userId, membersId));
    }
}

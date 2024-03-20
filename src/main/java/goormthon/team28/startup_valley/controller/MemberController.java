package goormthon.team28.startup_valley.controller;

import goormthon.team28.startup_valley.annotation.UserId;
import goormthon.team28.startup_valley.dto.global.ResponseDto;
import goormthon.team28.startup_valley.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/teams/{teamsId}/members")
    public ResponseDto<?> listTeamMember(@UserId Long userId, @PathVariable Long teamsId) {
        return ResponseDto.ok(memberService.listTeamMember(userId, teamsId));
    }

    @PatchMapping("/members/{membersId}")
    public ResponseDto<?> toggleTeamPublic(@UserId Long userId, @PathVariable Long membersId) {
        return ResponseDto.ok(memberService.toggleTeamPublic(userId, membersId));
    }

    @GetMapping("/members/{membersId}/contribution")
    public ResponseDto<?> retrieveContributionMember(@UserId Long userId, @PathVariable Long membersId) {
        return ResponseDto.ok(memberService.retrieveContributionMember(userId, membersId));
    }
}

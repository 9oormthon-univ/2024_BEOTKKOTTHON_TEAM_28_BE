package goormthon.team28.startup_valley.controller;

import goormthon.team28.startup_valley.annotation.UserId;
import goormthon.team28.startup_valley.dto.global.ResponseDto;
import goormthon.team28.startup_valley.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class TeamController {

    private final TeamService teamService;

    @GetMapping("/teams")
    public ResponseDto<?> getTeamList(@UserId Long userId) {
        return ResponseDto.ok(teamService.getTeamList(userId));
    }

    @GetMapping("/teams/{teamsId}")
    public ResponseDto<?> retrieveTeam(@UserId Long userId, @PathVariable Long teamsId) {
        return ResponseDto.ok(teamService.retrieveTeam(userId, teamsId));
    }

    @GetMapping("/teams/{teamsId}/leader")
    public ResponseDto<?> listTeamMemberPermission(@UserId Long userId, @PathVariable Long teamsId) {
        return ResponseDto.ok(teamService.listTeamMemberPermission(userId, teamsId));
    }
}

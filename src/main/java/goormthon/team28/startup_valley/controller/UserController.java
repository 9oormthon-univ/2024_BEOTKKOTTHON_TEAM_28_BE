package goormthon.team28.startup_valley.controller;

import goormthon.team28.startup_valley.annotation.UserId;
import goormthon.team28.startup_valley.dto.global.ResponseDto;
import goormthon.team28.startup_valley.dto.request.UserPatchDto;
import goormthon.team28.startup_valley.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    @GetMapping("/users")
    public ResponseDto<?> getUserInfo(@UserId Long userId) {
        return ResponseDto.ok(userService.getUserInfo(userId));
    }

    @PatchMapping("/users")
    public ResponseDto<?> patchUser(@UserId Long userId, @RequestBody UserPatchDto userPatchDto) {
        return ResponseDto.ok(userService.patchUser(userId, userPatchDto));
    }

    @GetMapping("/members/{membersId}/user-info")
    public ResponseDto<?> getUserInfoByMembersId(@UserId Long userId, @PathVariable Long membersId) {
        return ResponseDto.ok(userService.getUserInfoByMembersId(userId, membersId));
    }
}

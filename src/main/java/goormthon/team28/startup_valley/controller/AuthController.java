package goormthon.team28.startup_valley.controller;

import goormthon.team28.startup_valley.annotation.UserId;
import goormthon.team28.startup_valley.constants.Constants;
import goormthon.team28.startup_valley.dto.global.ResponseDto;
import goormthon.team28.startup_valley.dto.request.AuthSignUpDto;
import goormthon.team28.startup_valley.dto.response.JwtTokenDto;
import goormthon.team28.startup_valley.exception.CommonException;
import goormthon.team28.startup_valley.exception.ErrorCode;
import goormthon.team28.startup_valley.service.AuthService;
import goormthon.team28.startup_valley.util.CookieUtil;
import goormthon.team28.startup_valley.util.HeaderUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    @Value("${server.domain}")
    private String domain;
    private final AuthService authService;
    @PostMapping("/sign-up")
    public ResponseDto<?> signUp(@RequestBody AuthSignUpDto authSignUpDto){
        authService.signUp(authSignUpDto);
        return ResponseDto.created(null);
    }
    @PostMapping("/reissue")
    public ResponseDto<?> reissue(
            HttpServletRequest request,
            HttpServletResponse response,
            @UserId Long userId){
        log.info("controller 진입 성공");
        String refreshToken = HeaderUtil.refineHeader(request, Constants.PREFIX_AUTH, Constants.PREFIX_BEARER)
                .orElseThrow(() -> new CommonException(ErrorCode.INVALID_HEADER_VALUE));
        log.info("헤더값 조회 성공");
        JwtTokenDto jwtTokenDto = authService.reGenerateTokens(userId, refreshToken);

        CookieUtil.addCookie(response, domain, Constants.ACCESS_COOKIE_NAME, jwtTokenDto.accessToken());
        CookieUtil.addSecureCookie(response, domain, Constants.REFRESH_COOKIE_NAME, jwtTokenDto.refreshToken(), 60 * 60 * 24 * 14);

        return ResponseDto.ok(jwtTokenDto);
    }

}

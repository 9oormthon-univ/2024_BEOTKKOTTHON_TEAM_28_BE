package goormthon.team28.startup_valley.security.handler.login;

import goormthon.team28.startup_valley.dto.response.JwtTokenDto;
import goormthon.team28.startup_valley.repository.UserRepository;
import goormthon.team28.startup_valley.security.info.AuthenticationResponse;
import goormthon.team28.startup_valley.security.info.UserPrincipal;
import goormthon.team28.startup_valley.util.JwtUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class Oauth2SuccessHandler implements AuthenticationSuccessHandler {

    @Value("${server.domain}")
    private String domain;

    @Value("${server.sign-up-redirect-url}")
    private String signUpRedirectUrl;

    @Value("${server.home-redirect-url}")
    private String homeRedirectUrl;

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        JwtTokenDto jwtTokenDto = jwtUtil.generateTokens(principal.getUserId(), principal.getRole());

        userRepository.updateRefreshToken(principal.getUserId(), jwtTokenDto.refreshToken());

        AuthenticationResponse.makeLoginSuccessResponse(response, domain, jwtTokenDto, jwtUtil.getRefreshExpiration());

        if (userRepository.existsByIdAndNicknameIsNull(principal.getUserId()))
            response.sendRedirect(signUpRedirectUrl); // 최초 로그인
        else
            response.sendRedirect(homeRedirectUrl);
    }
}

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
public class DefaultSuccessHandler implements AuthenticationSuccessHandler {
    @Value("${server.domain}")
    private String domain;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        JwtTokenDto jwtTokenDto = jwtUtil.generateTokens(userPrincipal.getUserId(), userPrincipal.getRole());

        userRepository.updateRefreshTokenAndLoginStatus(userPrincipal.getUserId(), jwtTokenDto.refreshToken());

        AuthenticationResponse.makeLoginSuccessResponse(response, domain, jwtTokenDto, jwtUtil.getRefreshExpiration());
    }
}

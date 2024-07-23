package goormthon.team28.startup_valley.security.handler.login;

import goormthon.team28.startup_valley.exception.ErrorCode;
import goormthon.team28.startup_valley.security.info.AuthenticationResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class Oauth2FailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception
    ) throws IOException, ServletException {
        log.info("소셜 로그인 실패");
        AuthenticationResponse.makeFailureResponse(response, ErrorCode.INTERNAL_SERVER_ERROR);
    }
}

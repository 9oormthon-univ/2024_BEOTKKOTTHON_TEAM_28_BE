package goormthon.team28.startup_valley.security.handler.exception;

import goormthon.team28.startup_valley.exception.ErrorCode;
import goormthon.team28.startup_valley.security.info.AuthenticationResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        AuthenticationResponse.makeFailureResponse(response, ErrorCode.FORBIDDEN_ROLE);
    }
}

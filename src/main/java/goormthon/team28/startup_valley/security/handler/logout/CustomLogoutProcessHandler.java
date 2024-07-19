package goormthon.team28.startup_valley.security.handler.logout;

import goormthon.team28.startup_valley.exception.CommonException;
import goormthon.team28.startup_valley.exception.ErrorCode;
import goormthon.team28.startup_valley.repository.UserRepository;
import goormthon.team28.startup_valley.security.info.UserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CustomLogoutProcessHandler implements LogoutHandler {
    private final UserRepository userRepository;
    @Override

  @Transactional
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        if (authentication == null){
            throw new CommonException(ErrorCode.INVALID_TOKEN_ERROR);
        }
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        userRepository.updateRefreshToken(userPrincipal.getUserId(), null);
    }
}

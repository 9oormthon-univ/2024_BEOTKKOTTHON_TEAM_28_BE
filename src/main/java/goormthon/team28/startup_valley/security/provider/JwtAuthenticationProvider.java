package goormthon.team28.startup_valley.security.provider;

import goormthon.team28.startup_valley.security.info.JwtUserInfo;
import goormthon.team28.startup_valley.security.info.UserPrincipal;
import goormthon.team28.startup_valley.security.service.CustomUserDetailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider {
    private final CustomUserDetailService customUserDetailService;
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        log.info("AuthenticationProvider 진입 성공");
        if (authentication.getPrincipal().getClass().equals(String.class)){
            // form login 요청인 경우
            log.info("로그인 로직 인증 과정");
            return authOfBeforeLogin(authentication.getPrincipal().toString());

        } else { // 로그인 이후의 경우에 String이 아닌 복합 정보가 들어오는 경우
            log.info("로그인한 사용자 검증 과정");
            return authOfAfterLogin((JwtUserInfo) authentication.getPrincipal());
        }
    }
    private Authentication authOfBeforeLogin(String serialId){
        UserPrincipal userPrincipal = customUserDetailService.loadUserByUsername(serialId);
        return new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());
    }
    private Authentication authOfAfterLogin(JwtUserInfo userInfo){
        UserPrincipal userPrincipal = customUserDetailService.loadUserById(userInfo.userId());
        return new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}

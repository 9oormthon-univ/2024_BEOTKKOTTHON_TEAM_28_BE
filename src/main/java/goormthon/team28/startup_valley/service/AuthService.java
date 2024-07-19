package goormthon.team28.startup_valley.service;

import goormthon.team28.startup_valley.domain.User;
import goormthon.team28.startup_valley.dto.request.AuthSignUpDto;
import goormthon.team28.startup_valley.dto.response.JwtTokenDto;
import goormthon.team28.startup_valley.dto.type.EProfileImage;
import goormthon.team28.startup_valley.dto.type.ERole;
import goormthon.team28.startup_valley.exception.CommonException;
import goormthon.team28.startup_valley.exception.ErrorCode;
import goormthon.team28.startup_valley.repository.UserRepository;
import goormthon.team28.startup_valley.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    @Transactional
    public void signUp(AuthSignUpDto authSignUpDto){
        userRepository.save(User.builder()
                        .profileImage(Enum.valueOf(EProfileImage.class, authSignUpDto.profileImage().toUpperCase()))
                        .serialId(authSignUpDto.serialId())
                        .password(bCryptPasswordEncoder.encode(authSignUpDto.password()))
                        .role(ERole.USER)
                .build()
        );
    }
    @Transactional
    public JwtTokenDto reGenerateTokens(Long userId, String refreshToken){
        log.info("re generate tokens 진입성공");
        User loginUser = userRepository.findByIdAndRefreshToken(userId, refreshToken)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_LOGIN_USER));
        log.info("유저 조회 성공");
        JwtTokenDto jwtTokenDto = jwtUtil.generateTokens(loginUser.getId(), loginUser.getRole());

        loginUser.updateRefreshToken(jwtTokenDto.refreshToken());
        return jwtTokenDto;
    }
}

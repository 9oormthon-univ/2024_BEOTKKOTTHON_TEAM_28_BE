package goormthon.team28.startup_valley.security.service;

import goormthon.team28.startup_valley.domain.User;
import goormthon.team28.startup_valley.dto.type.EProvider;
import goormthon.team28.startup_valley.dto.type.ERole;
import goormthon.team28.startup_valley.repository.UserRepository;
import goormthon.team28.startup_valley.security.info.UserPrincipal;
import goormthon.team28.startup_valley.security.info.factory.Oauth2UserInfo;
import goormthon.team28.startup_valley.security.info.factory.Oauth2UserInfoFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOauth2UserDetailService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public OAuth2User loadUser(
            OAuth2UserRequest userRequest
    ) throws OAuth2AuthenticationException {
        log.info("Oauth2UserRequest 진입, userRequest = {}", userRequest);
        // provider 가져오기
        EProvider provider = EProvider.valueOf(
                userRequest.getClientRegistration().getRegistrationId().toUpperCase()
        );
        log.info("oauth 제공자 정보 가져오기 성공, 제공자 = {}", provider);
        // 사용자 정보 가져오기
        Oauth2UserInfo oauth2UserInfo = Oauth2UserInfoFactory
                .getOauth2UserInfo(provider, super.loadUser(userRequest).getAttributes());
        log.info("oauth 사용자 정보 가져오기 성공");
        log.info("attributes = {}", oauth2UserInfo.getAttributes().toString());

        UserRepository.UserSecurityForm securityForm = userRepository
                .findUserSecurityFromBySerialId(oauth2UserInfo.getId())
                .orElseGet(() -> {
                    log.info("새로운 사용자 접근, 저장 로직 진입");
                    User newUser = userRepository.save(
                            User.builder()
                                    .serialId(oauth2UserInfo.getId())
                                    .password(
                                            bCryptPasswordEncoder
                                                    .encode(UUID.randomUUID().toString())
                                    )
                                    .provider(provider)
                                    .role(ERole.USER)
                                    .build()
                    );
                    return UserRepository.UserSecurityForm.invoke(newUser);
                });
        log.info("oauth2 사용자 조회 성공");
        return UserPrincipal.create(securityForm, oauth2UserInfo.getAttributes());
    }
}
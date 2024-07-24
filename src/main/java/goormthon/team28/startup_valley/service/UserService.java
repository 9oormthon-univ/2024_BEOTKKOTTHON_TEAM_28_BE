package goormthon.team28.startup_valley.service;

import goormthon.team28.startup_valley.domain.Member;
import goormthon.team28.startup_valley.domain.User;
import goormthon.team28.startup_valley.dto.request.UserPatchDto;
import goormthon.team28.startup_valley.dto.response.UserDto;
import goormthon.team28.startup_valley.dto.type.EProfileImage;
import goormthon.team28.startup_valley.exception.CommonException;
import goormthon.team28.startup_valley.exception.ErrorCode;
import goormthon.team28.startup_valley.repository.MemberRepository;
import goormthon.team28.startup_valley.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final MemberRepository memberRepository;

    public Optional<User> findByDiscordId(String discordId){
        return userRepository.findByDiscordId(discordId);
    }

    public boolean isExisted(String discordId){
        return userRepository.existsByDiscordId(discordId);
    }

    public UserDto getUserInfo(Long userId) {

        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        List<Member> memberList = memberRepository.findAllByUser(currentUser);

        return UserDto.of(
                currentUser.getId(),
                !memberList.isEmpty() ? memberList.get(0).getId() : null,
                currentUser.getNickname(),
                currentUser.getProfileImage()
        );
    }

    @Transactional
    public Boolean patchUser(Long userId, UserPatchDto userPatchDto) {

        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        currentUser.updateUserInfo(userPatchDto.nickname(), EProfileImage.fromName(userPatchDto.profileImage()));

        return Boolean.TRUE;
    }

    public UserDto getUserInfoByMembersId(Long userId, Long membersId) {

        Member targetMember = memberRepository.findById(membersId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_MEMBER));
        User targetUser = targetMember.getUser();
        return UserDto.of(
                targetUser.getId(),
                membersId,
                targetUser.getNickname(),
                targetUser.getProfileImage()
        );
    }
}

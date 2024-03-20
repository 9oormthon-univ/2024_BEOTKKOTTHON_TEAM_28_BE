package goormthon.team28.startup_valley.service;

import goormthon.team28.startup_valley.domain.User;
import goormthon.team28.startup_valley.dto.request.UserPatchDto;
import goormthon.team28.startup_valley.dto.response.UserDto;
import goormthon.team28.startup_valley.dto.type.EProfileImage;
import goormthon.team28.startup_valley.exception.CommonException;
import goormthon.team28.startup_valley.exception.ErrorCode;
import goormthon.team28.startup_valley.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    public User findBySerialId(String serialId){
        return userRepository.findBySerialId(serialId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
    }
    public boolean isExisted(String serialId){
        return userRepository.existsBySerialId(serialId);
    }

    public UserDto getUserInfo(Long userId) {

        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        return UserDto.of(
                currentUser.getId(),
                currentUser.getNickname(),
                currentUser.getProfileImage()
        );
    }

    public Boolean patchUser(Long userId, UserPatchDto userPatchDto) {

        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        currentUser.updateUserInfo(userPatchDto.nickname(), EProfileImage.fromName(userPatchDto.profileImage()));

        return Boolean.TRUE;
    }
}

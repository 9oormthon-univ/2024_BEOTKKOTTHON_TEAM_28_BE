package goormthon.team28.startup_valley.service;

import goormthon.team28.startup_valley.domain.User;
import goormthon.team28.startup_valley.exception.CommonException;
import goormthon.team28.startup_valley.exception.ErrorCode;
import goormthon.team28.startup_valley.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}

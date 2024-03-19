package goormthon.team28.startup_valley.service;

import goormthon.team28.startup_valley.domain.Member;
import goormthon.team28.startup_valley.domain.Team;
import goormthon.team28.startup_valley.domain.User;
import goormthon.team28.startup_valley.exception.CommonException;
import goormthon.team28.startup_valley.exception.ErrorCode;
import goormthon.team28.startup_valley.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    @Transactional
    public Member saveMember(Team team, User user){
        return memberRepository.findByTeamAndUser(team, user)
                .orElseGet(() -> memberRepository.save(Member.builder()
                        .team(team)
                        .user(user)
                        .build())
                );
    }
    public Member findByTeamAndUser(Team team, User user){
        return memberRepository.findByTeamAndUser(team, user)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_MEMBER));
    }
}

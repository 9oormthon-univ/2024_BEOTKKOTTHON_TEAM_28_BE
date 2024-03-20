package goormthon.team28.startup_valley.service;

import goormthon.team28.startup_valley.domain.Member;
import goormthon.team28.startup_valley.domain.Team;
import goormthon.team28.startup_valley.domain.User;
import goormthon.team28.startup_valley.dto.response.MemberDto;
import goormthon.team28.startup_valley.dto.response.MemberListDto;
import goormthon.team28.startup_valley.exception.CommonException;
import goormthon.team28.startup_valley.exception.ErrorCode;
import goormthon.team28.startup_valley.repository.MemberRepository;
import goormthon.team28.startup_valley.repository.TeamRepository;
import goormthon.team28.startup_valley.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;

    public MemberListDto listTeamMember(Long userId, Long teamsId) {

        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        if (!memberRepository.existsByUser(currentUser))
            throw new CommonException(ErrorCode.NOT_FOUND_MEMBER);

        Team team = teamRepository.findById(teamsId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_TEAM));
        List<Member> memberList = memberRepository.findAllByTeam(team);
        List<MemberDto> memberDtoList = memberList.stream()
                .map(member -> MemberDto.of(
                        member.getId(),
                        member.getUser().getNickname(),
                        member.getPart(),
                        member.getUser().getProfileImage()
                ))
                .toList();

        return MemberListDto.of(memberDtoList, memberDtoList.size());
    }

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
    @Transactional
    public void updateTotalWorkTime(Long memberId, Long totalTime){
        memberRepository.updateTotalMinute(memberId, totalTime);
    }
}

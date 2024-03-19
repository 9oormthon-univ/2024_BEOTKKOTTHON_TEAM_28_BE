package goormthon.team28.startup_valley.service;

import goormthon.team28.startup_valley.domain.Member;
import goormthon.team28.startup_valley.domain.Team;
import goormthon.team28.startup_valley.domain.User;
import goormthon.team28.startup_valley.domain.Work;
import goormthon.team28.startup_valley.dto.response.RankingDto;
import goormthon.team28.startup_valley.dto.response.RankingListDto;
import goormthon.team28.startup_valley.dto.response.WorkDto;
import goormthon.team28.startup_valley.dto.response.WorkListDto;
import goormthon.team28.startup_valley.exception.CommonException;
import goormthon.team28.startup_valley.exception.ErrorCode;
import goormthon.team28.startup_valley.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkService {

    private final UserRepository userRepository;
    private final MemberRepository memberRepository;
    private final TeamRepository teamRepository;
    private final WorkRepository workRepository;

    public WorkListDto listMemberWork(Long userId, Long teamsId) {

        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        Team team = teamRepository.findById(teamsId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_TEAM));

        // 해당 팀의 멤버가 아닐 수도 있는 경우 체크
        if (memberRepository.existsByUserAndTeam(currentUser, team))
            throw new CommonException(ErrorCode.NOT_FOUND_MEMBER);

        List<Member> memberList = memberRepository.findAllByTeam(team);
        List<WorkDto> workDtoList = new ArrayList<>();
        for (Member member : memberList) {
            List<Work> workList = workRepository.findAllByOwner(member);
            workDtoList.addAll(workList.stream()
                    .map(work -> WorkDto.of(
                            member.getId(),
                            member.getUser().getNickname(),
                            member.getPart(),
                            member.getUser().getProfileImage(),
                            work.getContent(),
                            work.getEndAt()
                    ))
                    .toList());
        }

        return WorkListDto.of(workDtoList, team.getName());
    }

    public RankingListDto getRanking(Long userId, Long teamsId) {

        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        Team team = teamRepository.findById(teamsId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_TEAM));

        // 해당 팀의 멤버가 아닐 수도 있는 경우 체크
        if (memberRepository.existsByUserAndTeam(currentUser, team))
            throw new CommonException(ErrorCode.NOT_FOUND_MEMBER);

        // 오름차순 정렬해야 되는데 쿼리에서 하는 게 좋지 않을까?
        List<Member> memberList = memberRepository.findAllByTeamOrderByTotalMinuteDesc(team);
        List<RankingDto> rankingDtoList = memberList.stream()
                .map(member -> RankingDto.of(
                        member.getId(),
                        member.getUser().getNickname(),
                        member.getUser().getProfileImage(),
                        member.getTotalMinute()
                ))
                .toList();

        return RankingListDto.of(rankingDtoList, team.getName());
    }
}

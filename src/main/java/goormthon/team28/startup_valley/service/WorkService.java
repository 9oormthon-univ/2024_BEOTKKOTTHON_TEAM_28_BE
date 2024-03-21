package goormthon.team28.startup_valley.service;

import goormthon.team28.startup_valley.domain.*;
import goormthon.team28.startup_valley.dto.request.WorkTimeDto;
import goormthon.team28.startup_valley.dto.response.*;
import goormthon.team28.startup_valley.exception.CommonException;
import goormthon.team28.startup_valley.exception.ErrorCode;
import goormthon.team28.startup_valley.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkService {

    private final UserRepository userRepository;
    private final MemberRepository memberRepository;
    private final TeamRepository teamRepository;
    private final WorkRepository workRepository;
    @Transactional
    public Work saveWork(Scrum scrum, Member member, LocalDateTime now){
        return workRepository.save(Work.builder()
                        .scrum(scrum)
                        .owner(member)
                        .startAt(now)
                .build()
        );
    }
    @Transactional
    public void updateWorkAfterOver(Long workId, String works, LocalDateTime overTime){
        workRepository.updateWorkAfterOver(workId, works, overTime);
    }
    public Work findById(Long workId){
        return workRepository.findById(workId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_WORK));
    }
    public List<Work> findAllByScrum(Scrum scrum){
        return workRepository.findAllByScrum(scrum);
    }

    public Optional<Work> findNotOverWork(Scrum scrum, Member member){
        return workRepository.findByScrumAndOwnerAndEndAtIsNull(scrum, member);
    }

    public WorkListDto listMemberWork(Long userId, Long teamsId) {

        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        Team team = teamRepository.findById(teamsId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_TEAM));

        // 해당 팀의 멤버가 아닐 수도 있는 경우 체크
        if (!memberRepository.existsByUserAndTeam(currentUser, team))
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
        if (!memberRepository.existsByUserAndTeam(currentUser, team))
            throw new CommonException(ErrorCode.NOT_FOUND_MEMBER);

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

    public WorkManageListDto listManageWork(Long userId, Long membersId) {

        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        Member targetMember = memberRepository.findById(membersId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_MEMBER));
        Team team = targetMember.getTeam();
        // 검색하려는 대상과 로그인 한 유저의 팀이 다를 경우
        Member currentMember = memberRepository.findByTeamAndUser(team, currentUser)
                .orElseThrow(() -> new CommonException(ErrorCode.MISMATCH_LOGIN_USER_AND_TEAM));
        if (!team.getLeader().equals(currentMember))
            throw new CommonException(ErrorCode.MISMATCH_MEMBER_AND_TEAM_LEADER);

        List<Work> workList = workRepository.findAllByOwner(targetMember);
        List<WorkManageDto> workManageDtoList = workList.stream()
                .map(work -> WorkManageDto.of(
                        work.getId(),
                        work.getContent(),
                        work.getStartAt(),
                        work.getEndAt()
                ))
                .toList();

        return WorkManageListDto.of(workManageDtoList);
    }

    @Transactional
    public Boolean patchManageWork(Long userId, Long membersId, Long worksId, WorkTimeDto workTimeDto) {

        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        Member targetMember = memberRepository.findById(membersId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_MEMBER));
        Team team = targetMember.getTeam();
        // 검색하려는 대상과 로그인 한 유저의 팀이 다를 경우
        Member currentMember = memberRepository.findByTeamAndUser(team, currentUser)
                .orElseThrow(() -> new CommonException(ErrorCode.MISMATCH_LOGIN_USER_AND_TEAM));
        if (!team.getLeader().equals(currentMember))
            throw new CommonException(ErrorCode.MISMATCH_MEMBER_AND_TEAM_LEADER);

        Work work = workRepository.findByIdAndOwner(worksId, targetMember)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_WORK));
        work.updateTime(workTimeDto.startAt(), workTimeDto.endAt());

        return Boolean.TRUE;
    }

    public WorkMeasureDto measureTeamMemberWork(Long userId, Long membersId) {

        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        Member targetMember = memberRepository.findById(membersId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_MEMBER));
        Team team = targetMember.getTeam();
        // 검색하려는 대상과 로그인 한 유저의 팀이 다를 경우
        if (!memberRepository.existsByUserAndTeam(currentUser, team))
            throw new CommonException(ErrorCode.MISMATCH_LOGIN_USER_AND_TEAM);

        List<Work> workList = workRepository.findAllByOwner(targetMember);
        List<WorkDateDto> workDateDtoList = workList.stream()
                .map(work -> WorkDateDto.of(
                        work.getStartAt().toLocalDate(),
                        Duration.between(work.getStartAt(), work.getEndAt()).toMinutes()
                ))
                .toList();

        Optional<Long> maxTime = workDateDtoList.stream()
                .map(WorkDateDto::time)
                .max(Long::compareTo);

        return WorkMeasureDto.of(
                targetMember.getUser().getNickname(),
                targetMember.getTotalMinute(),
                workDateDtoList.size(),
                maxTime.orElse(0L),
                workDateDtoList
        );
    }

    public WorkMeasureDto measureAllWork(Long userId, Long membersId) {

        Member targetMember = memberRepository.findById(membersId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_MEMBER));
        User targetUser = targetMember.getUser();
        List<Member> memberList = memberRepository.findAllByUser(targetUser).stream()
                .filter(Member::getIsPublic)
                .toList();

        List<WorkDateDto> workDateDtoList = new ArrayList<>();
        for (Member tempMember : memberList) {
            List<Work> workList = workRepository.findAllByOwner(tempMember);
            workDateDtoList.addAll(
                    workList.stream()
                            .map(work -> WorkDateDto.of(
                                    work.getStartAt().toLocalDate(),
                                    Duration.between(work.getStartAt(), work.getEndAt()).toMinutes()
                            ))
                            .toList()
            );
        }

        Long totalTime = workDateDtoList.stream()
                .mapToLong(WorkDateDto::time)
                .sum();

        Optional<Long> maxTime = workDateDtoList.stream()
                .map(WorkDateDto::time)
                .max(Long::compareTo);

        return WorkMeasureDto.of(
                targetUser.getNickname(),
                totalTime,
                workDateDtoList.size(),
                maxTime.orElse(0L),
                workDateDtoList
        );
    }
}

package goormthon.team28.startup_valley.service;

import goormthon.team28.startup_valley.domain.Member;
import goormthon.team28.startup_valley.domain.Scrum;
import goormthon.team28.startup_valley.domain.Team;
import goormthon.team28.startup_valley.domain.User;
import goormthon.team28.startup_valley.dto.response.ScrumDto;
import goormthon.team28.startup_valley.dto.response.ScrumListDto;
import goormthon.team28.startup_valley.dto.response.WorkForScrumDto;
import goormthon.team28.startup_valley.dto.type.EScrumStatus;
import goormthon.team28.startup_valley.exception.CommonException;
import goormthon.team28.startup_valley.exception.ErrorCode;
import goormthon.team28.startup_valley.repository.*;
import goormthon.team28.startup_valley.repository.ScrumRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScrumService {

    private final ScrumRepository scrumRepository;
    private final UserRepository userRepository;
    private final MemberRepository memberRepository;
    private final TeamRepository teamRepository;
    private final WorkRepository workRepository;

    @Transactional
    public Scrum saveOrGetScrum(Member member, LocalDate now){
        return scrumRepository.findByWorkerAndStatus(member, EScrumStatus.IN_PROGRESS)
                .orElseGet(() -> scrumRepository.save(Scrum.builder()
                                .worker(member)
                                .startAt(now)
                        .build())
                );
    }
    @Transactional
    public void updateScrum(Long scrumId, String summary, LocalDate now){
        scrumRepository.updateScrumAfterOver(scrumId, summary, EScrumStatus.FINISH, now);
    }

    public ScrumListDto listScrum(Long userId, Long membersId, Long teamsId) {

        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        Team targetTeam = teamRepository.findById(teamsId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_TEAM));
//        if (!memberRepository.existsByUserAndTeam(currentUser, targetTeam))
//            throw new CommonException(ErrorCode.MISMATCH_LOGIN_USER_AND_TEAM);

        // Target Member와 Team이 일치하는지 검증 로직
        Member targetMember = memberRepository.findById(membersId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_MEMBER));
        if (!targetMember.getTeam().equals(targetTeam))
            throw new CommonException(ErrorCode.MISMATCH_TEAM_AND_MEMBER);

        // Target Member와  로그인 한 유저가 연관이 있는지 검증 로직
        User targetUser = targetMember.getUser();
        List<Member> targetMemberList = memberRepository.findAllByUser(targetUser);
        List<Member> currentMemberList = memberRepository.findAllByUser(currentUser);
        boolean isLoginUserAndTargetAreAssociated = false;
        for (Member tempTargetMember : targetMemberList)
            for (Member tempCurrentMember : currentMemberList)
                if (tempTargetMember.getTeam().equals(tempCurrentMember.getTeam())) {
                    isLoginUserAndTargetAreAssociated = true;
                    break;
                }
        if (!isLoginUserAndTargetAreAssociated)
            throw new CommonException(ErrorCode.NOT_ASSOCIATE_LOGIN_USER_AND_TARGET_MEMBER);

        boolean isTargetMemberPublic = targetMember.getIsPublic();

        List<Scrum> scrumList = scrumRepository
                .findAllByWorkerAndStatus(targetMember, EScrumStatus.FINISH);
        List<ScrumDto> scrumDtoList = scrumList.stream()
                .map(scrum -> ScrumDto.of(
                        scrum.getId(),
                        scrum.getSummary(),
                        scrum.getStartAt(),
                        scrum.getEndAt(),
                        workRepository.findAllByScrumOrderByEndAtDesc(scrum)
                                .stream()
                                .limit(3)
                                .map(work -> WorkForScrumDto.of(
                                        work.getId(),
                                        work.getContent()
                                ))
                                .toList()
                ))
                .toList();
        scrumDtoList = scrumDtoList.stream()
                .sorted(Comparator.comparing(ScrumDto::endAt).reversed())
                .toList();

        return ScrumListDto.of(scrumDtoList);
    }
    public Optional<Scrum> findNowScrum(Member member){
        return scrumRepository.findByWorkerAndStatus(member, EScrumStatus.IN_PROGRESS);
    }
}

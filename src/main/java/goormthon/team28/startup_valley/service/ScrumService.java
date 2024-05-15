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

    public ScrumListDto listScrum(Long userId, Long teamsId, Long target) {

        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        Team targetTeam = teamRepository.findById(teamsId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_TEAM));
        if (!memberRepository.existsByUserAndTeam(currentUser, targetTeam))
            throw new CommonException(ErrorCode.MISMATCH_LOGIN_USER_AND_TEAM);

        List<Member> memberList = new ArrayList<>();
        Member targetMember;
        if (target == null)
            memberList = memberRepository.findAllByTeam(targetTeam);
        else {
            targetMember = memberRepository.findById(target)
                            .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_MEMBER));
            if (!targetMember.getTeam().equals(targetTeam))
                throw new CommonException(ErrorCode.MISMATCH_TEAM_AND_MEMBER);
            memberList.add(
                    memberRepository.findById(target)
                            .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_MEMBER))
            );
        }

        List<ScrumDto> scrumDtoList = new ArrayList<>();
        for (Member tempMember : memberList) {
            List<Scrum> scrumList = scrumRepository
                    .findAllByWorkerAndStatus(tempMember, EScrumStatus.FINISH);
            scrumDtoList.addAll(
                    scrumList.stream()
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
                            .toList()
            );
        }
        scrumDtoList = scrumDtoList.stream()
                .sorted(Comparator.comparing(ScrumDto::endAt).reversed())
                .toList();

        return ScrumListDto.of(scrumDtoList);
    }
    public Optional<Scrum> findNowScrum(Member member){
        return scrumRepository.findByWorkerAndStatus(member, EScrumStatus.IN_PROGRESS);
    }
}

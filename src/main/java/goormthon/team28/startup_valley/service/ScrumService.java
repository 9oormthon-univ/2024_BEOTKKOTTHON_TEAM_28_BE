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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScrumService {

    private final ScrumRepository scrumRepository;
    private final UserRepository userRepository;
    private final MemberRepository memberRepository;
    private final WorkRepository workRepository;

    @Transactional
    public Scrum saveScrum(Member member, LocalDate now){
        return scrumRepository.findByWorkerAndStatus(member, EScrumStatus.IN_PROGRESS)
                .orElseGet(() -> scrumRepository.save(Scrum.builder()
                                .worker(member)
                                .startAt(now)
                        .build())
                );
    }

    public ScrumListDto listScrum(Long userId, Long membersId) {

        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        Member paramMember = memberRepository.findById(membersId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_MEMBER));
        Team paramTeam = paramMember.getTeam();
        List<Member> memberList = memberRepository.findAllByTeam(paramTeam);

        // 로그인 한 사용자가 검색하고 싶은 대상의 팀이 아닌 경우 예외처리
        Optional<Member> currentMemberOptional = memberList.stream()
                .filter(member -> member.getUser().equals(currentUser))
                .findFirst();
        if (currentMemberOptional.isEmpty())
                throw new CommonException(ErrorCode.MISMATCH_LOGIN_USER_AND_TEAM);

        List<ScrumDto> scrumDtoList = new ArrayList<>();
        for (Member tempMember : memberList) {
            List<Scrum> scrumList = scrumRepository.findAllByWorkerOrderByEndAtDesc(tempMember);
            scrumDtoList.addAll(
                    scrumList.stream()
                            .map(scrum -> ScrumDto.of(
                                    scrum.getId(),
                                    scrum.getSummary(),
                                    scrum.getStartAt(),
                                    scrum.getEndAt(),
                                    workRepository.findTop3ByScrum(scrum)
                                            .stream()
                                            .map(work -> WorkForScrumDto.of(
                                                    work.getId(),
                                                    work.getContent()
                                            ))
                                            .toList()
                            ))
                            .toList()
            );
        }

        return ScrumListDto.of(scrumDtoList);
    }
}

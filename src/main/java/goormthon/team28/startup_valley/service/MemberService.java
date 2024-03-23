package goormthon.team28.startup_valley.service;

import goormthon.team28.startup_valley.domain.Member;
import goormthon.team28.startup_valley.domain.Scrum;
import goormthon.team28.startup_valley.domain.Team;
import goormthon.team28.startup_valley.domain.User;
import goormthon.team28.startup_valley.dto.request.MemberRetrospectionDto;
import goormthon.team28.startup_valley.dto.response.MemberContributionDto;
import goormthon.team28.startup_valley.dto.response.MemberDto;
import goormthon.team28.startup_valley.dto.response.MemberListDto;
import goormthon.team28.startup_valley.dto.response.ScrumContributionDto;
import goormthon.team28.startup_valley.dto.type.EPart;
import goormthon.team28.startup_valley.dto.type.EScrumStatus;
import goormthon.team28.startup_valley.exception.CommonException;
import goormthon.team28.startup_valley.exception.ErrorCode;
import goormthon.team28.startup_valley.repository.MemberRepository;
import goormthon.team28.startup_valley.repository.ScrumRepository;
import goormthon.team28.startup_valley.repository.TeamRepository;
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
public class MemberService {

    private final MemberRepository memberRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final ScrumRepository scrumRepository;

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
    public Member saveOrGetMember(Team team, User user){
        return memberRepository.findByTeamAndUser(team, user)
                .orElseGet(() -> memberRepository.save(Member.builder()
                        .team(team)
                        .user(user)
                        .build())
                );
    }
    public Optional<Member> findByTeamAndUser(Team team, User user){
        return memberRepository.findByTeamAndUser(team, user);
    }
    public List<Member> findAllByTeam(Team team){
        return memberRepository.findAllByTeam(team);
    }
    @Transactional
    public void updateTotalWorkTime(Long memberId, Long totalTime){
        memberRepository.updateTotalMinute(memberId, totalTime);
    }
    @Transactional
    public void updatePart(Long memberId, EPart part){
        memberRepository.updatePart(memberId, part);
    }

    @Transactional
    public void updateReviewSummary(Long memberId, String summary){
        memberRepository.updateReviewSummary(memberId, summary);
    }
    @Transactional
    public void updateRetrospection(Long memberId, String retrospection){
        memberRepository.updateRetrospection(memberId, retrospection);
    }
    @Transactional
    public Boolean toggleTeamPublic(Long userId, Long membersId) {

        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        Member member = memberRepository.findById(membersId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_MEMBER));

        // 수정하려는 멤버가 로그인한 유저와 다를 경우
        if (!member.getUser().equals(currentUser))
            throw new CommonException(ErrorCode.MISMATCH_LOGIN_USER_AND_TEAM);

        member.toggleIsPublic();

        return Boolean.TRUE;
    }

    public MemberContributionDto retrieveContributionMember(Long userId, Long membersId) {

        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        Member targetMember = memberRepository.findById(membersId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_MEMBER));

        // 조회하려는 멤버의 기여도가 비공개면서 조회하려는 멤버와 로그인 유저가 다른 경우 조회 불가능 처리
        if (!targetMember.getUser().equals(currentUser) && !targetMember.getIsPublic())
            throw new CommonException(ErrorCode.INVALID_CHECK_TEAM_CONTRIBUTION);
        List<Scrum> scrumList = scrumRepository
                .findAllByWorkerAndStatusOrderByEndAtDesc(targetMember, EScrumStatus.FINISH);
        List<ScrumContributionDto> scrumContributionDtoList = scrumList.stream()
                .map(scrum -> ScrumContributionDto.of(
                        scrum.getId(),
                        scrum.getSummary(),
                        scrum.getStartAt(),
                        scrum.getEndAt()
                ))
                .toList();

        return MemberContributionDto.of(
                targetMember.getTotalMinute(),
                targetMember.getPart(),
                targetMember.getPeerReviewSummary(),
                scrumContributionDtoList
        );
    }

    @Transactional
    public Boolean patchRetrospectionMember(
            Long userId,
            Long teamsId,
            MemberRetrospectionDto memberRetrospectionDto
    ) {
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        Team team = teamRepository.findById(teamsId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_TEAM));
        Member currentMember = memberRepository.findByTeamAndUser(team, currentUser)
                .orElseThrow(() -> new CommonException(ErrorCode.MISMATCH_LOGIN_USER_AND_TEAM));

        currentMember.updateRetrospection(memberRetrospectionDto.content());

        return Boolean.TRUE;
    }
}

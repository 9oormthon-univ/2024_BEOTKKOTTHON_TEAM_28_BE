package goormthon.team28.startup_valley.service;

import goormthon.team28.startup_valley.domain.Member;
import goormthon.team28.startup_valley.domain.Team;
import goormthon.team28.startup_valley.domain.User;
import goormthon.team28.startup_valley.dto.request.TeamMemberPermissionPatchDto;
import goormthon.team28.startup_valley.dto.response.*;
import goormthon.team28.startup_valley.dto.type.EProjectStatus;
import goormthon.team28.startup_valley.exception.CommonException;
import goormthon.team28.startup_valley.exception.ErrorCode;
import goormthon.team28.startup_valley.repository.MemberRepository;
import goormthon.team28.startup_valley.repository.TeamRepository;
import goormthon.team28.startup_valley.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TeamService {

    private final UserRepository userRepository;
    private final MemberRepository memberRepository;
    private final TeamRepository teamRepository;

    @Transactional
    public Team saveTeam(String guildId, String name, String image, LocalDate now){
        return teamRepository.findByGuildId(guildId)
                .orElseGet(() -> teamRepository.save(
                        Team.builder()
                                .guildId(guildId)
                                .name(name)
                                .teamImage(image)
                                .startAt(now)
                                .status(EProjectStatus.IN_PROGRESS)
                                .build())
                );
    }
    public Optional<Team> findByGuildId(String guildId) {
        return teamRepository.findByGuildId(guildId);
    }
    @Transactional
    public void updateLeader(Long teamId, Member member){
        teamRepository.updateLeader(teamId, member);
    }

    public TeamListDto getTeamList(Long userId) {

        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        List<Member> memberList = memberRepository.findAllByUser(currentUser);
        List<Team> teamList = memberList.stream()
                .map(Member::getTeam)
                .toList();

        // 스트림 두번 돌리는 거 나중에 최적화 해야 됨
        List<TeamDto> progressingTeamDtoList = teamList.stream()
                .filter(team -> team.getStatus().equals(EProjectStatus.IN_PROGRESS))
                .map(team -> TeamDto.of(
                        team.getId(),
                        team.getName(),
                        team.getTeamImage()
                ))
                .toList();
        List<TeamDto> endTeamDtoList = teamList.stream()
                .filter(team -> team.getStatus().equals(EProjectStatus.FINISH))
                .map(team -> TeamDto.of(
                        team.getId(),
                        team.getName(),
                        team.getTeamImage()
                ))
                .toList();

        return TeamListDto.of(
                progressingTeamDtoList,
                progressingTeamDtoList.size(),
                endTeamDtoList,
                endTeamDtoList.size()
        );
    }

    public TeamRetrieveListDto listProgressingTeam(Long userId) {

        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        List<Member> memberList = memberRepository.findAllByUser(currentUser);
        List<TeamRetrieveDto> teamRetrieveDtoList = memberList.stream()
                .map(member -> TeamRetrieveDto.of(
                        member.getTeam().getId(),
                        member.getTeam().getName(),
                        member.getRetrospection(),
                        member.getTeam().getTeamImage(),
                        member.getTeam().getStartAt(),
                        member.getTeam().getEndAt(),
                        member.getTeam().getStatus()
                ))
                .toList();

        return TeamRetrieveListDto.of(teamRetrieveDtoList);
    }

    public TeamSummaryDto retrieveTeam(Long userId, Long teamsId) {

        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        Team team = teamRepository.findById(teamsId).
                orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_TEAM));

        if (!memberRepository.existsByUserAndTeam(currentUser, team))
            throw new CommonException(ErrorCode.MISMATCH_LOGIN_USER_AND_TEAM);

        return TeamSummaryDto.of(
                team.getId(),
                team.getName(),
                team.getTeamImage(),
                team.getStartAt()
        );
    }

    public TeamMemberPermissionListDto listTeamMemberPermission(Long userId, Long teamsId) {

        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        Team team = teamRepository.findById(teamsId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_TEAM));

        if (!memberRepository.existsByUserAndTeam(currentUser, team))
            throw new CommonException(ErrorCode.MISMATCH_LOGIN_USER_AND_TEAM);
        List<Member> memberList = memberRepository.findAllByTeam(team);
        List<TeamMemberPermissionDto> teamMemberPermissionDtoList = memberList.stream()
                .map(member -> TeamMemberPermissionDto.of(
                        member.getId(),
                        member.getUser().getNickname(),
                        member.getUser().getProfileImage(),
                        team.getLeader().equals(member) ? Boolean.TRUE : Boolean.FALSE
                ))
                .toList();

        return TeamMemberPermissionListDto.of(teamMemberPermissionDtoList);
    }

    @Transactional
    public Boolean patchTeamLeader(
            Long userId,
            Long teamsId,
            TeamMemberPermissionPatchDto teamMemberPermissionPatchDto
    ) {
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        Team team = teamRepository.findById(teamsId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_TEAM));
        Member currentMember = memberRepository.findByTeamAndUser(team, currentUser)
                .orElseThrow(() -> new CommonException(ErrorCode.MISMATCH_LOGIN_USER_AND_TEAM));

        // 팀원은 맞지만, 팀 리더가 아닌 경우
        if (!team.getLeader().equals(currentMember))
            throw new CommonException(ErrorCode.MISMATCH_MEMBER_AND_TEAM_LEADER);

        Member targetMember = memberRepository.findById(teamMemberPermissionPatchDto.memberId())
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_MEMBER));

        team.changeTeamLeader(targetMember);

        return Boolean.TRUE;
    }
}

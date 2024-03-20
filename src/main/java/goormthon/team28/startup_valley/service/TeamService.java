package goormthon.team28.startup_valley.service;

import goormthon.team28.startup_valley.domain.Member;
import goormthon.team28.startup_valley.domain.Team;
import goormthon.team28.startup_valley.domain.User;
import goormthon.team28.startup_valley.dto.response.TeamDto;
import goormthon.team28.startup_valley.dto.response.TeamListDto;
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
                                .isPublic(true)
                                .build())
                );
    }
    public Team findByGuildId(String guildId) {
        return teamRepository.findByGuildId(guildId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_TEAM));
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
}

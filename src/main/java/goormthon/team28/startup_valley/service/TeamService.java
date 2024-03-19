package goormthon.team28.startup_valley.service;

import goormthon.team28.startup_valley.domain.Member;
import goormthon.team28.startup_valley.domain.Team;
import goormthon.team28.startup_valley.domain.User;
import goormthon.team28.startup_valley.dto.type.EProjectStatus;
import goormthon.team28.startup_valley.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class TeamService {
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
    @Transactional
    public void updateLeader(Long teamId, Member member){
        teamRepository.updateLeader(teamId, member);
    }
}

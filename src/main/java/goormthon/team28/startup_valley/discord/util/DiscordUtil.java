package goormthon.team28.startup_valley.discord.util;

import goormthon.team28.startup_valley.constants.Constants;
import goormthon.team28.startup_valley.discord.exception.DiscordExceptionHandler;
import goormthon.team28.startup_valley.domain.Scrum;
import goormthon.team28.startup_valley.domain.Team;
import goormthon.team28.startup_valley.domain.Work;
import goormthon.team28.startup_valley.service.*;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DiscordUtil {
    private final UserService userService;
    private final TeamService teamService;
    private final MemberService memberService;
    private final ScrumService scrumService;
    private final WorkService workService;

    /**
     * 각종 조회 및 예외처리
     */
    public Team myTeam(SlashCommandInteractionEvent event){
        Optional<Team> optionalTeam = teamService.findByGuildId(event.getGuild().getId());
        // 예외처리
        DiscordExceptionHandler.checkExisted(Optional.ofNullable(optionalTeam), event, Constants.DISCORD_PLZ_UPDATE_TEAM1);
        return optionalTeam.get();
    }
    public goormthon.team28.startup_valley.domain.User getUser(SlashCommandInteractionEvent event, String userId){
        Optional<goormthon.team28.startup_valley.domain.User> optionalUser = userService.findBySerialId(userId);
        // 예외처리
        DiscordExceptionHandler.checkExisted(Optional.ofNullable(optionalUser), event, Constants.DISCORD_PLZ_SIGNUP);
        return optionalUser.get();
    }
    public goormthon.team28.startup_valley.domain.Member getMember(SlashCommandInteractionEvent event, String userId){
        Optional<goormthon.team28.startup_valley.domain.Member> optionalMember = memberService.findByTeamAndUser(
                myTeam(event),
                getUser(event, userId)
        );
        // 예외처리
        DiscordExceptionHandler.checkExisted(Optional.ofNullable(optionalMember), event, Constants.DISCORD_PLZ_UPDATE_TEAM2);
        return optionalMember.get();
    }
    public Optional<Scrum> getProcessingScrum(SlashCommandInteractionEvent event, String userId){
        Optional<Scrum> nowScrum = scrumService.findNowScrum(getMember(event, userId));
        // 예외처리
        DiscordExceptionHandler.checkExisted(Optional.ofNullable(nowScrum), event, Constants.DISCORD_NO_PROCESSING_WORK);
        return nowScrum;
    }
    public Optional<Work> getMyProcessingWork(SlashCommandInteractionEvent event){
        String userId = event.getUser().getName();
        return workService.findNotOverWork(getProcessingScrum(event, userId).get(), getMember(event, userId));
    }

    /**
     * 팀원 연결하기
     */
    public List<String> findNoSignUp(List<Member> members) {
        List<String> usersWithoutInfo = new ArrayList<>();
        for (Member member : members) {
            String serialId = member.getUser().getName();
            if (!userService.isExisted(serialId)) {
                usersWithoutInfo.add(serialId);
            }
        }
        return usersWithoutInfo;
    }

    public void saveOrGetMember(Team team, Member discordMember, SlashCommandInteractionEvent event) {
        goormthon.team28.startup_valley.domain.Member teamMember = memberService.saveOrGetMember(
                team,
                getUser(event, discordMember.getUser().getName())
        );

        if (isTeamLeaderNotSet(team) && isGuildOwner(discordMember, event)) {
            teamService.updateLeader(team.getId(), teamMember);
        }
    }

    public boolean isTeamLeaderNotSet(Team team) {
        return team.getLeader() == null;
    }

    public boolean isGuildOwner(Member discordMember, SlashCommandInteractionEvent event) {
        return discordMember.getUser().getName().equals(event.getGuild().getOwner().getUser().getName());
    }
}

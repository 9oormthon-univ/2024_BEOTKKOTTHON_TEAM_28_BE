package goormthon.team28.startup_valley.discord.listener;

import goormthon.team28.startup_valley.domain.Question;
import goormthon.team28.startup_valley.domain.Team;
import goormthon.team28.startup_valley.dto.type.EProjectStatus;
import goormthon.team28.startup_valley.repository.MemberRepository;
import goormthon.team28.startup_valley.repository.TeamRepository;
import goormthon.team28.startup_valley.repository.UserRepository;
import goormthon.team28.startup_valley.service.MemberService;
import goormthon.team28.startup_valley.service.QuestionService;
import goormthon.team28.startup_valley.service.TeamService;
import goormthon.team28.startup_valley.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class DiscordListener extends ListenerAdapter {
    private final UserService userService;
    private final TeamService teamService;
    private final MemberService memberService;
    private final QuestionService questionService;
    @Override
    @Transactional
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        LocalDate nowLocalDate = LocalDate.now();
        LocalDateTime nowLocalDateTime = LocalDateTime.now();
        switch (event.getName()) {
            case "팀원업데이트":
                // 봇을 제외한 사용자 목록 생성
                List<Member> discordMembers = event.getGuild().getMembers().stream()
                        .filter(member -> !member.getUser().isBot()).toList();

                // DB에 가입 안한 사용자 이름 목록 가져오기
                List<String> noSignUp = findNoSignUp(discordMembers);
                if (noSignUp.isEmpty()){ // 모두 회원가입을 한 경우
                    // 팀을 생성하거나 조회한다
                    Team team = teamService.saveTeam(event.getGuild().getId(), event.getGuild().getName(), event.getGuild().getIconUrl(), nowLocalDate);
                    log.info("팀 생성 완료");

                    // 팀의 멤버를 생성하거나 조회한다.
                    discordMembers.forEach(
                            discordMember -> {
                                goormthon.team28.startup_valley.domain.Member teamMember = memberService.saveMember(
                                        team,
                                        userService.findBySerialId(discordMember.getUser().getName())
                                );
                                if (teamMember.getUser().getSerialId().equals(event.getGuild().getOwner().getUser().getName())){
                                    teamService.updateLeader(team.getId(), teamMember);
                                }
                            }
                    );
                    log.info("멤버 생성 완료");
                    event.reply("팀 과 팀 멤버를 연결했어요! 좋은 협업이 되길 기대합니다!").setEphemeral(true).queue();
                } else {
                    event.reply("웹에 회원가입이 필요합니다!\n\n" + "회원가입 해주세요 !! : " + noSignUp.toString()).setEphemeral(true).queue();
                }
                break;
            case "질문하기":
                String guildId = event.getGuild().getId();
                String senderId = event.getMember().getUser().getName();
                User receiver = event.getOption("receiver").getAsUser();
                String receiverId = receiver.getName();
                String questionContent = event.getOption("question_content").getAsString();
                // 질문 생성
                Question question = questionService.saveQuestion(guildId, senderId, receiverId, questionContent, nowLocalDateTime);

                // 질문 생성 완료, 질문자에게 알리는 내용
                event.reply("질문이 등록 되었습니다 ! ").setEphemeral(true).queue();
                // 질문 생성 완료, 답변자 및 팀원에게 알리는 내용
                event.getGuild().getTextChannelById(event.getChannel().getId())
                        .sendMessage(receiver.getAsMention() + "님! 답변을 기다리는 질문이 생성되었어요 ! \n\n" +
                                "디스코드를 통해 답변하는 경우에는 이 코드를 사용해주세요 ! code: "+ question.getCode())
                        .queue();

                break;
        }
    }
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

}

package goormthon.team28.startup_valley.discord.listener;

import goormthon.team28.startup_valley.domain.Question;
import goormthon.team28.startup_valley.domain.Scrum;
import goormthon.team28.startup_valley.domain.Team;
import goormthon.team28.startup_valley.domain.Work;
import goormthon.team28.startup_valley.dto.type.EProjectStatus;
import goormthon.team28.startup_valley.dto.type.EQuestionStatus;
import goormthon.team28.startup_valley.repository.MemberRepository;
import goormthon.team28.startup_valley.repository.TeamRepository;
import goormthon.team28.startup_valley.repository.UserRepository;
import goormthon.team28.startup_valley.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.hibernate.Hibernate;
import org.springframework.transaction.annotation.Transactional;


import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class DiscordListener extends ListenerAdapter {
    private final UserService userService;
    private final TeamService teamService;
    private final MemberService memberService;
    private final QuestionService questionService;
    private final AnswerService answerService;
    private final ScrumService scrumService;
    private final WorkService workService;
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
                                "디스코드를 통해 답변하는 경우에는 이 코드를 사용해주세요 ! code: "+ question.getCode() + "\n\n" +
                                "질문한 사람: " + event.getMember().getAsMention() + "\n\n" +
                                "질문 내용: " + questionContent)
                        .queue();

                break;

            case "답변하기":
                String code = event.getOption("code").getAsString();
                Member maker = event.getOption("receiver").getAsMember();
                String answerContent = event.getOption("answer_content").getAsString();

                Question findQuestion = questionService.findByCode(code);

                answerService.saveAnswer(findQuestion, getMember(event, event.getUser().getName()),answerContent, nowLocalDateTime);
                questionService.updateQuestionStatus(findQuestion, EQuestionStatus.FINISH);

                event.reply("답변이 등록 되었습니다 ! ").setEphemeral(true).queue();

                event.getGuild().getTextChannelById(event.getChannel().getId())
                        .sendMessage( maker.getAsMention() +  "님! 질문에 답변이 등록 되었어요 ! ")
                        .queue();
                break;

            case "업무시작":
                // 팀, 사용자 조회 -> 팀 멤버 조회
                Team team = teamService.saveTeam(event.getGuild().getId(), event.getGuild().getName(), event.getGuild().getIconUrl(), nowLocalDate);
                goormthon.team28.startup_valley.domain.Member member = memberService.saveMember(team, me(event));

                // 스크럼 생성 또는 조회
                Scrum scrum = scrumService.saveScrum(member, nowLocalDate);

                if (!workService.findNotOverWork(scrum, member).isEmpty()){
                    event.reply("이전의 업무가 존재합니다 ㅠㅠ, 기존 업무를 종료하고 새로운 작업을 시작해주세요 ! ")
                            .setEphemeral(true).queue();
                    return;
                }

                // 업무 생성
                Work work = workService.saveWork(scrum, member, nowLocalDateTime);

                event.reply(event.getMember().getAsMention() + "의 오늘의 업무가 등록 되었습니다 ! \n\n" +
                        "오늘의 업무 시작 시간: " + nowLocalDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                        .setEphemeral(true).queue();
                break;

            case "업무종료":
                String workList = event.getOption("work_list").getAsString();

                // 현재 내가 진행 중인 작업 조회하기
                Optional<Work> optionalWork = getMyWork(event);
                if (optionalWork.isEmpty()){ //  내가 진행 중인 작업이 없는 경우
                    event.reply("진행 중인 작업이 없습니다 ! 업무 시작을 먼저 진행해주세요 ~ ! ")
                            .setEphemeral(true).queue();
                    return;
                }

                // 현재 진행 중인 작업의 종료 데이터 업데이트 & 조회
                workService.updateWorkAfterOver(optionalWork.get().getId(), workList, nowLocalDateTime);
                Work myWork = workService.findById(optionalWork.get().getId());

                // 오늘의 업무 작업 시간 계산
                long todayWork = Duration.between(myWork.getStartAt(), myWork.getEndAt()).toMinutes();
                goormthon.team28.startup_valley.domain.Member worker = getMember(event, event.getUser().getName());
                Long totalTime = Long.valueOf(worker.getTotalMinute() + todayWork);

                // 업무 시간 DB 반영
                memberService.updateTotalWorkTime(worker.getId(), totalTime);

                List<String> works = Arrays.stream(workList.split("; ")).toList();
                event.reply("고생하셨습니다 ~ ! \n\n" +
                        "오늘의 업무는: " + works.toString() + " 입니다 !\n\n" +
                        "오늘의 업무 종료 시간은: " + nowLocalDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) + "입니다 !\n\n" +
                        "오늘의 업무는 " + todayWork/60 + " 시간 " + todayWork%60 + " 분 입니다 !!")
                        .setEphemeral(true).queue();
                break;

            case "스크럼종료":
                Optional<Scrum> nowScrum = getScrum(event, event.getUser().getName());
                if (nowScrum.isEmpty()){
                    event.reply("진행 중인 업무가 없어서 스크럼을 종료할 수 없습니다 ㅠㅠ.. 업무를 시작하여 스크럼을 생성해주세요 !!! ")
                            .setEphemeral(true).queue();
                    return;
                }
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
    private Team myTeam(SlashCommandInteractionEvent event){
        return teamService.findByGuildId(event.getGuild().getId());
    }
    private goormthon.team28.startup_valley.domain.User me(SlashCommandInteractionEvent event){
        return userService.findBySerialId(event.getUser().getName());
    }
    private goormthon.team28.startup_valley.domain.Member getMember(SlashCommandInteractionEvent event, String userId){
        return memberService.findByTeamAndUser(
                myTeam(event),
                userService.findBySerialId(userId)
        );
    }
    private Optional<Scrum> getScrum(SlashCommandInteractionEvent event, String userId){
        return scrumService.findNowScrum(getMember(event, userId));
    }
    private Optional<Work> getMyWork(SlashCommandInteractionEvent event){
        String userId = event.getUser().getName();
        return workService.findNotOverWork(getScrum(event, userId).get(), getMember(event, userId));
    }

}

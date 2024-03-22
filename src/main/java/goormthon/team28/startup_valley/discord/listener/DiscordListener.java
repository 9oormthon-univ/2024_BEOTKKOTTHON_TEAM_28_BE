package goormthon.team28.startup_valley.discord.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import goormthon.team28.startup_valley.constants.Constants;
import goormthon.team28.startup_valley.discord.util.DiscordUtil;
import goormthon.team28.startup_valley.discord.exception.DiscordExceptionHandler;
import goormthon.team28.startup_valley.domain.Question;
import goormthon.team28.startup_valley.domain.Scrum;
import goormthon.team28.startup_valley.domain.Team;
import goormthon.team28.startup_valley.domain.Work;
import goormthon.team28.startup_valley.dto.type.EPart;
import goormthon.team28.startup_valley.dto.type.EQuestionStatus;
import goormthon.team28.startup_valley.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.transaction.annotation.Transactional;


import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
public class DiscordListener extends ListenerAdapter {
    private final TeamService teamService;
    private final MemberService memberService;
    private final QuestionService questionService;
    private final AnswerService answerService;
    private final ScrumService scrumService;
    private final WorkService workService;
    private final GptService gptService;
    private final DiscordUtil discordUtil;
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
                List<String> noSignUp = discordUtil.findNoSignUp(discordMembers);
                if (noSignUp.isEmpty()){ // 모두 회원가입을 한 경우
                    // 팀을 생성하거나 조회한다
                    Team team = teamService.saveOrGetTeam(event.getGuild().getId(), event.getGuild().getName(), event.getGuild().getIconUrl(), nowLocalDate);
                    log.info("팀 생성 완료");

                    // 팀원을 생성하거나 조회한다.
                    discordMembers.forEach(discordMember -> discordUtil.saveOrGetMember(team, discordMember, event));
                    log.info("멤버 생성 완료");

                    event.reply(Constants.DISCORD_CONNECT_COMPLETE).setEphemeral(true).queue();
                } else {
                    event.reply(Constants.DISCORD_CONNECT_FAIL + noSignUp).setEphemeral(true).queue();
                }
                break;

            case "파트입력하기":
                EPart part = EPart.fromName(event.getOption("part").getAsString(), event);
                goormthon.team28.startup_valley.domain.Member me = discordUtil.getMember(event, event.getUser().getName());

                // 팀원 파트 입력하기
                memberService.updatePart(me.getId(), part);

                event.reply(event.getUser().getAsMention() + Constants.DISCORD_INSERT_PART_COMPLETE)
                        .setEphemeral(true).queue();

                break;

            case "질문하기":
                User discordReceiver = event.getOption("receiver").getAsUser();
                String questionContent = event.getOption("question_content").getAsString();

                // 발생할 예외에 대한 처리 & 객체 조회
                goormthon.team28.startup_valley.domain.Member sender = discordUtil.getMember(event, event.getMember().getUser().getName());
                goormthon.team28.startup_valley.domain.Member receiver = discordUtil.getMember(event, discordReceiver.getName());

                // 파트 선택을 안 한 경우에 예외처리
                if (!DiscordExceptionHandler.checkNull(sender.getPart(), event, event.getMember().getAsMention() + Constants.DISCORD_INSERT_PART_YET))
                    return;
                if (!DiscordExceptionHandler.checkNull(receiver.getPart(), event, event.getMember().getAsMention() + Constants.DISCORD_INSERT_PART_YET))
                    return;

                // 질문 생성
                Question question = questionService.saveQuestion(sender, receiver, questionContent, nowLocalDateTime);

                // 질문 생성 완료, 질문자에게 알리는 내용
                event.reply(Constants.DISCORD_REGISTER_QUESTION_COMPLETE).setEphemeral(true).queue();
                // 질문 생성 완료, 답변자 및 팀원에게 알리는 내용
                event.getGuild().getTextChannelById(event.getChannel().getId())
                        .sendMessage(discordReceiver.getAsMention() + "님! 답변을 기다리는 질문이 생성되었어요 ! \n\n" +
                                "디스코드를 통해 답변하는 경우에는 이 코드를 사용해주세요 ! code: "+ question.getCode() + "\n\n" +
                                "질문한 사람: " + event.getMember().getAsMention() + "\n\n" +
                                "질문 내용: " + questionContent)
                        .queue();

                break;

            case "답변하기":
                String code = Objects.requireNonNull(event.getOption("code")).getAsString();
                Member maker = Objects.requireNonNull(event.getOption("receiver")).getAsMember();
                String answerContent = Objects.requireNonNull(event.getOption("answer_content")).getAsString();

                // 답변 예외처리
                Optional<Question> optionalQuestion = questionService.findByCode(code);
                if (!DiscordExceptionHandler.checkExisted(Optional.ofNullable(optionalQuestion), event, Constants.DISCORD_INSERT_CODE_FAIL))
                    return ;
                // 실제 질문 객체 조회
                Question findQuestion = optionalQuestion.get();
                goormthon.team28.startup_valley.domain.Member speaker = discordUtil.getMember(event, event.getUser().getName());
                if (!DiscordExceptionHandler.checkNull(speaker.getPart(), event, event.getMember().getAsMention() + Constants.DISCORD_INSERT_PART_YET))
                    return;

                answerService.saveAnswer(findQuestion, speaker, answerContent, nowLocalDateTime);

                questionService.updateQuestionStatus(findQuestion, EQuestionStatus.FINISH);

                event.reply(Constants.DISCORD_REGISTER_ANSWER_COMPLETE).setEphemeral(true).queue();
                event.getGuild().getTextChannelById(event.getChannel().getId())
                        .sendMessage( maker.getAsMention() +  "님! 질문에 답변이 등록 되었어요 ! \n\n" +
                                "답변한 사람: " + event.getUser().getAsMention() +
                                "\n\n답변 내용: " + answerContent)
                        .queue();
                break;

            case "업무시작":
                // 팀, 사용자 조회 -> 팀 멤버 조회
                goormthon.team28.startup_valley.domain.Member member = discordUtil.getMember(event, event.getUser().getName());
                if (!DiscordExceptionHandler.checkNull(member.getPart(), event, event.getMember().getAsMention() + event.getMember().getAsMention() + Constants.DISCORD_INSERT_PART_YET))
                    return;

                // 스크럼 생성 또는 조회
                Scrum scrum = scrumService.saveOrGetScrum(member, nowLocalDate);
                if (!DiscordExceptionHandler.checkEmpty(Optional.ofNullable(discordUtil.getMyProcessingWork(event)), event, Constants.DISCORD_REGISTER_SCRUM_FAIL))
                    return;

                // 업무 생성
                workService.saveWork(scrum, member, nowLocalDateTime);

                event.reply(event.getMember().getAsMention() + "의 오늘의 업무가 등록 되었습니다 ! \n\n" +
                        "오늘의 업무 시작 시간: " + nowLocalDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                        .setEphemeral(true).queue();
                break;

            case "업무종료":
                String workList = Objects.requireNonNull(event.getOption("work_list")).getAsString();

                // 현재 내가 진행 중인 작업 조회하기
                Optional<Work> optionalWork = discordUtil.getMyProcessingWork(event);
                if (!DiscordExceptionHandler.checkExisted(Optional.ofNullable(optionalWork), event, Constants.DISCORD_NO_PROCESSING_WORK))
                    return ;

                Work nowWork = optionalWork.get();
                // 현재 진행 중인 작업의 종료 데이터 업데이트 & 조회
                workService.updateWorkAfterOver(nowWork.getId(), workList, nowLocalDateTime);
                Work myWork = workService.findById(nowWork.getId());

                // 오늘의 업무 작업 시간 계산 && 파트 예외 처리
                long todayWork = Duration.between(myWork.getStartAt(), myWork.getEndAt()).toMinutes();
                goormthon.team28.startup_valley.domain.Member worker = discordUtil.getMember(event, event.getUser().getName());
                if (!DiscordExceptionHandler.checkNull(worker.getPart(), event, event.getMember().getAsMention() +Constants.DISCORD_INSERT_PART_YET))
                    return ;

                // 업무 시간 DB 반영
                memberService.updateTotalWorkTime(worker.getId(), worker.getTotalMinute() + todayWork);
                event.reply("고생하셨습니다 ~ ! \n\n" +
                        "오늘의 업무는: " + Arrays.stream(workList.split("; ")).toList() + " 입니다 !\n\n" +
                        "오늘의 업무 종료 시간은: " + nowLocalDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) + "입니다 !\n\n" +
                        "오늘의 업무는 " + todayWork/60 + " 시간 " + todayWork%60 + " 분 입니다 !!")
                        .setEphemeral(true).queue();
                break;

            case "스크럼종료":
                Optional<Scrum> optionalScrum = discordUtil.getProcessingScrum(event, event.getUser().getName());
                // 종료할 스크럼이 없는 경우
                if (!DiscordExceptionHandler.checkExisted(Optional.ofNullable(optionalScrum), event, Constants.DISCORD_NO_PROCESSING_SCRUM))
                    return;

                // 업무가 아직 종료되지 않는 것이 있는 경우
                Optional<Work> optionWork = discordUtil.getMyProcessingWork(event);
                if (!DiscordExceptionHandler.checkEmpty(Optional.ofNullable(optionWork), event, Constants.DISCORD_PLZ_MAKE_WORK_DONE))
                    return;

                // 스크럼 종료 대기 중 & 업무도 모두 종료 된 상태 -> 스크럼 종료가 가능한 상태
                // 해야 하는 일 -> 스크럼 상태 종료, 스크럼 종료 날짜, 스크럼 요약(gpt)
                Scrum nowScrum = optionalScrum.get();
                List<String> worksOfUser = workService.findAllByScrum(nowScrum)
                        .stream().map(Work::getContent)
                        .toList();

                try {
                    String summary = gptService.sendMessage(worksOfUser, true);
                    log.info("summary: {}", summary);

                    scrumService.updateScrum(nowScrum.getId(), summary, nowLocalDate);
                } catch (JsonProcessingException e) {
                    event.reply(Constants.DISCORD_GPT_WRONG).setEphemeral(true).queue();
                    return ;
                }
                event.reply(Constants.DISCORD_REGISTER_SCRUM_COMPLETE).setEphemeral(true).queue();
                break;

            case "프로젝트종료":
                Team project = discordUtil.myTeam(event);
                goormthon.team28.startup_valley.domain.Member m = discordUtil.getMember(event, event.getUser().getName());
                if (!DiscordExceptionHandler.checkNull(m.getPart(), event,event.getMember().getAsMention() +Constants.DISCORD_INSERT_PART_YET))
                    return ;
                // 리더가 아닌 경우
                if (!DiscordExceptionHandler.checkSameId(project.getLeader().getId(), m.getId(), event, Constants.DISCORD_ONLY_LEADER_CAN + event.getMember().getAsMention() + Constants.DISCORD_YOU_ARENT_LEADER))
                    return;
                // 프로젝트 상태 동료 평가 단계로 변경
                teamService.updateStatus(project.getId());
                event.reply(Constants.DISCORD_CHANGE_PROJECT_STATUS_COMPLETE).setEphemeral(true).queue();
                // 팀원들에게 알림
                event.getGuild().getTextChannelById(event.getChannel().getId())
                        .sendMessage(Constants.DISCORD_ANNOUNCE_EVERYONE )
                        .queue();

                break;

            case "서버최신화":
                String newName = event.getGuild().getName();
                String newImage = event.getGuild().getIconUrl();

                Team myTeam = discordUtil.myTeam(event);
                if (!DiscordExceptionHandler.checkSameString(newName+newImage, myTeam.getName()+myTeam.getTeamImage(), event, Constants.DISCORD_INFO_SAME))
                    return;

                teamService.updateInformation(myTeam.getId(), newName, newImage);
                event.reply(Constants.DISCORD_INFO_CHANGE_COMPLETE).setEphemeral(true).queue();
                break;
        }
    }
}

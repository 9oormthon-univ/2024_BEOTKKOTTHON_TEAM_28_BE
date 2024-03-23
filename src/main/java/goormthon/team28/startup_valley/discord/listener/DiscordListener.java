package goormthon.team28.startup_valley.discord.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import goormthon.team28.startup_valley.domain.*;
import goormthon.team28.startup_valley.dto.type.EPart;
import goormthon.team28.startup_valley.dto.type.EProjectStatus;
import goormthon.team28.startup_valley.dto.type.EQuestionStatus;
import goormthon.team28.startup_valley.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;


import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

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
    private final GptService gptService;
    private final ReviewService reviewService;
    @Override
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
                    Team team = teamService.saveOrGetTeam(event.getGuild().getId(), event.getGuild().getName(), event.getGuild().getIconUrl(), nowLocalDate);
                    log.info("팀 생성 완료");

                    // 팀의 멤버를 생성하거나 조회한다.
                    discordMembers.forEach(discordMember -> saveOrGetMember(team, discordMember, event));
                    log.info("멤버 생성 완료");

                    event.reply("팀 과 팀 멤버를 연결했어요! '파트입력하기' 명령어를 통해 역할을 알려주세요 !!").setEphemeral(true).queue();
                } else {
                    event.reply("웹에 회원가입이 필요합니다!\n\n" + "회원가입 해주세요 !! : " + noSignUp.toString()).setEphemeral(true).queue();
                }
                break;

            case "파트입력하기":
                EPart part = EPart.fromName(event.getOption("part").getAsString(), event);
                goormthon.team28.startup_valley.domain.Member me = getMember(event, event.getUser().getName());

                // 팀원 파트 입력하기
                memberService.updatePart(me.getId(), part);

                event.reply(event.getUser().getAsMention() + " 파트 입력까지 완료 되었습니다 !, 앞으로의 멋진 협업을 기대합니다 ! ")
                        .setEphemeral(true).queue();

                break;

            case "전체업무정리하기":
                String rr = event.getOption("content").getAsString();

                goormthon.team28.startup_valley.domain.Member i = getMember(event, event.getUser().getName());
                boolean edit = i.getRetrospection() == null;
                memberService.updateRetrospection(i.getId(), rr);
                if (!edit){
                    event.reply(event.getUser().getAsMention() + "님의 R&R이 수정 되었습니다 !").setEphemeral(true).queue();
                    return ;
                } else {
                    event.reply(event.getUser().getAsMention() + "님의 R&R이 작성 되었습니다 !").setEphemeral(true).queue();
                }

                break;

            case "질문하기":
                User discordReceiver = event.getOption("receiver").getAsUser();
                String questionContent = event.getOption("question_content").getAsString();

                // 발생할 예외에 대한 처리 & 객체 조회
                goormthon.team28.startup_valley.domain.Member sender = getMember(event, event.getMember().getUser().getName());
                goormthon.team28.startup_valley.domain.Member receiver = getMember(event, discordReceiver.getName());

                // 파트 선택을 안 한 경우에 예외처리
                if (sender.getPart() == null){
                    event.reply(event.getMember().getAsMention() +"님이 역할을 아직 입력하지 않으셨어요 ㅠㅠ 역할 입력 해주세요 ~ !").setEphemeral(true).queue();
                    return ;
                }
                if (receiver.getPart() == null){
                    event.reply(discordReceiver.getAsMention() +"님이 역할을 아직 입력하지 않으셨어요 ㅠㅠ 역할 입력 해주세요 ~ !").setEphemeral(true).queue();
                    return ;
                }

                // 질문 생성
                Question question = questionService.saveQuestion(sender, receiver, questionContent, nowLocalDateTime);

                // 질문 생성 완료, 질문자에게 알리는 내용
                event.reply("질문이 등록 되었습니다 ! ").setEphemeral(true).queue();
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
                if (optionalQuestion.isEmpty()){
                    event.reply("잘못된 코드 입니다, 코드를 확인해주세요 ~ !").setEphemeral(true).queue();
                    return ;
                }
                // 실제 질문 객체 조회
                Question findQuestion = optionalQuestion.get();
                goormthon.team28.startup_valley.domain.Member speaker = getMember(event, event.getUser().getName());
                if (speaker.getPart() == null){
                    event.reply(event.getMember().getAsMention() +"님이 역할을 아직 입력하지 않으셨어요 ㅠㅠ 역할 입력 해주세요 ~ !").setEphemeral(true).queue();
                    return ;
                }

                answerService.saveAnswer(findQuestion, speaker, answerContent, nowLocalDateTime);
                questionService.updateQuestionStatus(findQuestion, EQuestionStatus.FINISH);

                event.reply("답변이 등록 되었습니다 ! ").setEphemeral(true).queue();
                event.getGuild().getTextChannelById(event.getChannel().getId())
                        .sendMessage( maker.getAsMention() +  "님! 질문에 답변이 등록 되었어요 ! \n\n" +
                                "답변 내용: " + answerContent)
                        .queue();
                break;

            case "업무시작":
                // 팀, 사용자 조회 -> 팀 멤버 조회
                goormthon.team28.startup_valley.domain.Member member = getMember(event, event.getUser().getName());
                if (member.getPart() == null){
                    event.reply(event.getMember().getAsMention() +"님이 역할을 아직 입력하지 않으셨어요 ㅠㅠ 역할 입력 해주세요 ~ !").setEphemeral(true).queue();
                    return ;
                }

                // 스크럼 생성 또는 조회
                Scrum scrum = scrumService.saveOrGetScrum(member, nowLocalDate);
                if (getMyProcessingWork(event).isPresent()){
                    event.reply("이전의 업무가 존재합니다 ㅠㅠ, 기존 업무를 종료하고 새로운 작업을 시작해주세요 ! ")
                            .setEphemeral(true).queue();
                    return;
                }

                // 업무 생성
                workService.saveWork(scrum, member, nowLocalDateTime);
                event.reply(event.getMember().getAsMention() + "의 오늘의 업무가 등록 되었습니다 ! \n\n" +
                                "오늘의 업무 시작 시간: " + nowLocalDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                        .setEphemeral(true).queue();
                break;

            case "업무종료":
                String workList = Objects.requireNonNull(event.getOption("work_list")).getAsString();

                // 현재 내가 진행 중인 작업 조회하기
                Optional<Work> optionalWork = getMyProcessingWork(event);
                if (optionalWork.isEmpty()){ //  내가 진행 중인 작업이 없는 경우
                    event.reply("진행 중인 작업이 없습니다 ! 업무 시작을 먼저 진행해주세요 ~ ! ")
                            .setEphemeral(true).queue();
                    return;
                }
                Work nowWork = optionalWork.get();
                // 현재 진행 중인 작업의 종료 데이터 업데이트 & 조회
                workService.updateWorkAfterOver(nowWork.getId(), workList, nowLocalDateTime);
                Work myWork = workService.findById(nowWork.getId());

                // 오늘의 업무 작업 시간 계산 && 파트 예외 처리
                long todayWork = Duration.between(myWork.getStartAt(), myWork.getEndAt()).toMinutes();
                goormthon.team28.startup_valley.domain.Member worker = getMember(event, event.getUser().getName());
                if (worker.getPart() == null){
                    event.reply(event.getMember().getAsMention() +"님이 역할을 아직 입력하지 않으셨어요 ㅠㅠ 역할 입력 해주세요 ~ !").setEphemeral(true).queue();
                    return ;
                }

                Long totalTime = worker.getTotalMinute() + todayWork;
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
                Optional<Scrum> optionalScrum = getProcessingScrum(event, event.getUser().getName());
                // 종료할 스크럼이 없는 경우
                if (optionalScrum.isEmpty()){
                    event.reply("진행 했던 업무가 없어서 스크럼을 종료할 수 없습니다 ㅠㅠ.. 업무를 시작하여 데이터를 만들어 주세요 !!! ")
                            .setEphemeral(true).queue();
                    return;
                }
                log.info("스크럼 문제 X, 통과");

                // 업무가 아직 종료되지 않는 것이 있는 경우
                Optional<Work> optionWork = getMyProcessingWork(event);
                if (optionWork.isPresent()){
                    event.reply("진행 중인 업무가 존재합니다 ㅠㅠ 업무를 종료한 뒤, 스크럼을 종료하여 주세요 ! ")
                            .setEphemeral(true).queue();
                    return ;
                }
                log.info("업무 문제 X, 통과");

                // 스크럼 종료 대기 중 & 업무도 모두 종료 된 상태 -> 스크럼 종료가 가능한 상태
                // 해야 하는 일 -> 스크럼 상태 종료, 스크럼 종료 날짜, 스크럼 요약(gpt)
                Scrum nowScrum = optionalScrum.get();
                List<String> worksOfUser = workService.findAllByScrum(nowScrum)
                        .stream().map(Work::getContent)
                        .toList();
                log.info("스크럼 하위 작업들 조회 성공");
                try {
                    String summary = gptService.sendMessage(worksOfUser, true);
                    log.info("summary: {}", summary);
                    scrumService.updateScrum(nowScrum.getId(), summary, nowLocalDate);
                } catch (JsonProcessingException e) {
                    event.reply("gpt 요약 기능에서 문제가 생겼어요.. ㅠㅠ 잠시후 시도해 주세요..!")
                            .setEphemeral(true).queue();
                    return ;
                }
                event.reply("하나의 스크럼이 마무리 됐어요 ~! 앞으로의 스크럼도 화이팅입니다 ~ !\n")
                        .setEphemeral(true).queue();
                break;

            case "프로젝트종료":
                Team project = myTeam(event);
                goormthon.team28.startup_valley.domain.Member m = getMember(event, event.getUser().getName());
                if (m.getPart() == null){
                    event.reply(event.getMember().getAsMention() +"님이 역할을 아직 입력하지 않으셨어요 ㅠㅠ 역할 입력 해주세요 ~ !").setEphemeral(true).queue();
                    return ;
                }
                // 리더가 아닌 경우
                if (!project.getLeader().getId().equals(m.getId())){
                    event.reply("프로젝트의 리더가 프로젝트의 상태를 변경할 수 있어요 ! \n\n" +
                                    event.getMember().getAsMention() + "님은 프로젝트의 리더가 아닙니다..ㅠㅠ")
                            .setEphemeral(true).queue();
                    return ;
                }
                // 프로젝트 상태 동료 평가 단계로 변경
                teamService.updateStatus(project.getId(), EProjectStatus.PEER_REVIEW);
                event.reply("프로젝트의 상태가 변경 되었습니다 !")
                        .setEphemeral(true).queue();
                // 팀원들에게 알림
                event.getGuild().getTextChannelById(event.getChannel().getId())
                        .sendMessage("@everyone ! 프로젝트는 잘 마무리 되었나요 ?! \n\n" +
                                "프로젝트의 개발 기간이 끝나고 동료평가 단계로 넘어가게 되었습니다 ! \n\n" +
                                "서로의 평가를 통해 한층 더 성장하세요 !!" )
                        .queue();

                break;

            case "서버최신화":
                String newName = event.getGuild().getName();
                String newImage = event.getGuild().getIconUrl();
                Team myTeam = myTeam(event);
                if (newName.equals(myTeam.getName()) && newImage.equals(myTeam.getTeamImage())){
                    event.reply("이름과 이미지가 모두 동일합니다 ㅠㅠ").setEphemeral(true).queue();
                    return;
                }
                teamService.updateInformation(myTeam.getId(), newName, newImage);
                event.reply("서버의 정보가 변경되었습니다 ! 웹으로 확인해주세요 ~").setEphemeral(true).queue();
                break;

            case "동료평가작성":
                Team targetTeam = myTeam(event);
                // 프로젝트 팀의 단계가 동료평가 단계인지
                if (!targetTeam.getStatus().equals(EProjectStatus.PEER_REVIEW)){
                    event.reply("동료평가는 /프로젝트종료 커멘드 이후에 작업할 수 있어요 !! 프로젝트의 상태를 변경해주세요 !!").setEphemeral(true).queue();
                    return ;
                }
                goormthon.team28.startup_valley.domain.Member sendMember = getMember(event, event.getUser().getName());
                goormthon.team28.startup_valley.domain.Member receiveMember = getMember(event, event.getOption("receiver").getAsUser().getName());

                // 이미 작성한 사람인지 -> 보낸,받는 동일
                if (reviewService.isAlreadyExistReview(sendMember, receiveMember)){
                    event.reply("이미 동료평가를 작성한 팀원입니다 ! 다른 분의 동료평가를 작성해주세요 !").setEphemeral(true).queue();
                    return ;
                }
            
                // 본인에게 작성할 수 없음
                if (sendMember.getId().equals(receiveMember.getId())){
                    event.reply("본인에게 동료평가를 작성할 수 없습니다 ! 다른 분의 동료평가를 작성해주세요 !").setEphemeral(true).queue();
                    return ;
                }

                // 동료평가 작성
                reviewService.saveReview(targetTeam, sendMember, receiveMember, event.getOption("evaluate").getAsString());
                log.info("동료평가 작성 완료");

                // 받은 사람을 기준으로 개수 파악 -> 팀 멤버 - 1 -> gpt로 요약 ㄱㄱ
                List<Review> findReviews = reviewService.findAllByReceiver(receiveMember);
                List<Member> discordPeople = event.getGuild().getMembers()
                        .stream().filter(teamMate -> !teamMate.getUser().isBot()).toList();

                // 모두가 동료평가를 작성한 경우 -> gpt 요약 필요
                if (findReviews.size() == discordPeople.size() - 1) {// 봇과 자기 자신
                    List<String> reviews = findReviews.stream()
                            .map(review -> review.getContent())
                            .toList();
                    try {
                        String summaryOfReviews = gptService.sendMessage(reviews, false);
                        log.info("동료평가 요약 성공: {}", summaryOfReviews);
                        memberService.updateReviewSummary(receiveMember.getId(), summaryOfReviews);
                        // 동료평가를 모두 받은 사람의 목록
                        List<goormthon.team28.startup_valley.domain.Member> members = memberService.findAllByTeam(targetTeam).stream()
                                .filter(one -> one.getPeerReviewSummary() != null)
                                .collect(Collectors.toList());

                        // 동료평가를 끝낸 사람의 수와 서버의 사람 수와 동일 => 프로젝트 종료 가능
                        if (members.size() == discordPeople.size()){
                            teamService.updateStatus(targetTeam.getId(), EProjectStatus.FINISH);
                            event.reply("동료평가 작성이 완료 되었습니다 !, 다른 동료평가도 진행해주세요 ~ !").setEphemeral(true).queue();
                            event.getGuild().getTextChannelById(event.getChannel().getId())
                                    .sendMessage("@everyone !! 모든 동료평가가 종료 되었습니다 !! 그동안 수고하셨습니다 ~ !")
                                    .queue();
                            return ;
                        }
                        event.reply("동료평가 작성이 완료 되었습니다 !, 다른 동료평가도 진행해주세요 ~ !").setEphemeral(true).queue();
                    } catch (JsonProcessingException e) {
                        event.reply("gpt 요약 기능에서 문제가 생겼어요.. ㅠㅠ 잠시후 시도해 주세요..!").setEphemeral(true).queue();
                        return ;
                    }
                }
                event.reply("동료평가 작성이 완료 되었습니다 !, 다른 동료평가도 진행해주세요 ~ !").setEphemeral(true).queue();
                break;

            case "동료평가조회":
                Team mine = myTeam(event);
                // 동료평가 or 끝난 상황에서 동료평가 조회 가능
                if (mine.getStatus().equals(EProjectStatus.IN_PROGRESS)){
                    event.reply("동료평가 단계가 아닙니다 ㅠㅠ. 프로젝트를 종료해주세요 !").setEphemeral(true).queue();
                    return ;
                }
                goormthon.team28.startup_valley.domain.Member findSender = getMember(event, event.getOption("writer").getAsUser().getName());
                goormthon.team28.startup_valley.domain.Member findReceiver = getMember(event, event.getOption("receiver").getAsUser().getName());
                Optional<Review> optionalReview = reviewService.findBySenderAndReceiver(findSender, findReceiver);
                if (optionalReview.isEmpty()){
                    event.reply("아직 진행되지 않은 동료평가입니다 ! 동료평가를 얼른 진행해주세요 ~ !").setEphemeral(true).queue();
                    return ;
                }
                event.reply(
                        event.getOption("writer").getAsUser().getAsMention() + "께서 " +
                                event.getOption("receiver").getAsUser().getAsMention() +"께 한 동료평가 입니다 ! \n\n" +
                                "동료평가 : " + optionalReview.get().getContent()
                ).setEphemeral(true).queue();

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
        Optional<Team> optionalTeam = teamService.findByGuildId(event.getGuild().getId());
        if (optionalTeam.isEmpty()){
            event.reply("팀원업데이트를 통해 팀을 만들어주세요 !").setEphemeral(true).queue();
        }
        return optionalTeam.get();
    }
    private goormthon.team28.startup_valley.domain.User getUser(SlashCommandInteractionEvent event, String userId){
        Optional<goormthon.team28.startup_valley.domain.User> optionalUser = userService.findBySerialId(userId);
        if (optionalUser.isEmpty()){
            event.reply("웹에 회원가입을 먼저 진행해주세요 !").setEphemeral(true).queue();
        }
        return optionalUser.get();
    }
    private goormthon.team28.startup_valley.domain.Member getMember(SlashCommandInteractionEvent event, String userId){
        Optional<goormthon.team28.startup_valley.domain.Member> optionalMember = memberService.findByTeamAndUser(
                myTeam(event),
                getUser(event, userId)
        );
        if (optionalMember.isEmpty()){
            event.reply("팀원을 조회할 수 없습니다 ㅠㅠ. 팀원 업데이트를 통해 변경 사항을 적용해주세요 ~ !").setEphemeral(true).queue();
        }
        return optionalMember.get();
    }
    private Optional<Scrum> getProcessingScrum(SlashCommandInteractionEvent event, String userId){
        Optional<Scrum> nowScrum = scrumService.findNowScrum(getMember(event, userId));
        if (nowScrum.isEmpty()){
            event.reply("시작된 작업을 찾을 수 없습니다 ㅠㅠ, 작업을 먼저 시작해주세요").setEphemeral(true).queue();
        }
        return nowScrum;
    }
    private Optional<Work> getMyProcessingWork(SlashCommandInteractionEvent event){
        String userId = event.getUser().getName();
        return workService.findNotOverWork(getProcessingScrum(event, userId).get(), getMember(event, userId));
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

package goormthon.team28.startup_valley;

import goormthon.team28.startup_valley.discord.info.DiscordBotToken;
import goormthon.team28.startup_valley.discord.listener.DiscordListener;
import goormthon.team28.startup_valley.service.*;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.TimeZone;

@Slf4j
@SpringBootApplication
public class StartupValleyApplication {
	@PostConstruct
	public void init() {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
	}
	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(StartupValleyApplication.class, args);

		log.info("Initializing JDA");
		JDA jda = JDABuilder.createDefault(context.getBean(DiscordBotToken.class).getToken())
				.enableIntents(GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS)
				.setChunkingFilter(ChunkingFilter.ALL)
				.setMemberCachePolicy(MemberCachePolicy.ALL)
				.setActivity(Activity.playing("START UP VALLEY"))
				.addEventListeners(new DiscordListener(
								context.getBean(UserService.class),
								context.getBean(TeamService.class),
								context.getBean(MemberService.class),
								context.getBean(QuestionService.class),
								context.getBean(AnswerService.class),
								context.getBean(ScrumService.class),
								context.getBean(WorkService.class),
								context.getBean(GptService.class),
								context.getBean(ReviewService.class),
								context.getBean(EmailService.class)
						)
				)
				.build();
		log.info("Finished Initializing JDA");

		jda.updateCommands().addCommands(
				Commands.slash("1-팀원업데이트", "해당 프로젝트의 팀원들을 모두 웹으로 연동해요."),
				Commands.slash("2-파트입력하기", "프로젝트에서 본인이 맡은 역할을 등록해요.")
						.addOption(OptionType.STRING, "part", "본인의 역할을 'BACKEND', 'FRONTEND', 'FULLSTACK', 'PM', 'DESIGN' 중에서 입력해주세요 !", true),
				Commands.slash("질문하기", "궁금한 점을 질문해봐요 ! ")
						.addOption(OptionType.USER, "receiver", "질문 받을 사람을 선택해주세요 !", true)
						.addOption(OptionType.STRING, "question_content", "질문 내용을 작성해주세요 !", true),
				Commands.slash("질문답변", "나에게 온 질문들을 답변해주세요 ! 답변을 위해 질문 코드를 입력해야 돼요 !")
						.addOption(OptionType.STRING, "code", "답변할 질문에 대한 코드를 입력하세요", true)
						.addOption(OptionType.USER, "receiver", "답변을 받는 사람을 선택해주세요 ! ", true)
						.addOption(OptionType.STRING, "answer_content", "답변 내용을 작성해주세요 !", true),
				Commands.slash("업무시작", "업무를 시작하기 위해 명령어를 적어주세요."),
				Commands.slash("업무종료", "업무 종료 시 해당 명령어와 함께 작업한 내용을 적어주세요.")
						.addOption(OptionType.STRING, "work_list", "오늘의 업무 내용을 '; '을 통해서 구분하여 작성해주세요 ! ", true),
				Commands.slash("백로그요약", "업무 종료 시 현재까지 진행한 업무 사항을 백로그로 요약해요."),
				Commands.slash("3-프로젝트종료", "프로젝트를 종료하고 동료 평가를 진행해요 ! 리더만이 사용할 수 있어요 !"),
				Commands.slash("e-서버최신화", "디스코드 서버에서 변경된 내용을 웹으로 업데이트 해요."),
				Commands.slash("4-동료평가작성", "팀원에 대한 리뷰를 작성해요 ! 프로젝트 마무리 시 사용 가능해요 !")
						.addOption(OptionType.USER, "receiver", "동료평가를 할 사람을 지정해주세요", true)
						.addOption(OptionType.STRING, "evaluate", "동로평가의 내용을 작성해주세요", true),
				Commands.slash("동료평가조회", "팀원들 사이의 동료평가를 조회할 수 있어요 !")
						.addOption(OptionType.USER, "writer", "동료평가의 평가자를 선택해주세요", true)
						.addOption(OptionType.USER, "receiver", "동료평가의 피평가자를 선택해주세요", true),
				Commands.slash("전체업무정리하기", "프로젝트 전체 기간 동안 본인의 업무를 간략하게 적어주세요.")
						.addOption(OptionType.STRING, "content", "프로젝트에서의 본인의 역할에 대해 작성해주세요", true),
				Commands.slash("도움말", "Startup Valley 프로덕트를 사용하시기 시작한 여러분들을 위한 안내서입니다. 📖🍀"),
				Commands.slash("문의하기", "해결이 어려운 문제나, 새로운 문의가 있다면 사용해 주세요!")
						.addOption(OptionType.STRING, "email", "회신 받으실 이메일 내용을 작성해주세요!", true)
						.addOption(OptionType.STRING, "content", "문의하실 내용을 작성해주세요!", true)
		).queue();
	}
}

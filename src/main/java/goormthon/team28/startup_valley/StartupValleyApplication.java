package goormthon.team28.startup_valley;

import goormthon.team28.startup_valley.discord.info.DiscordBotToken;
import goormthon.team28.startup_valley.discord.listener.DiscordListener;
import goormthon.team28.startup_valley.service.*;
import jakarta.annotation.PostConstruct;
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

@SpringBootApplication
public class StartupValleyApplication {
	@PostConstruct
	public void init() {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
	}
	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(StartupValleyApplication.class, args);

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
						context.getBean(ReviewService.class)
						)
				)
				.build();

		jda.updateCommands().addCommands(
				Commands.slash("팀원업데이트", "웹에 사용자들과 현재 진행하는 프로젝트를 연동해요 ! "),
				Commands.slash("파트입력하기", "프로젝트에서 맡은 본인의 역할을 입력해 주세요 !")
								.addOption(OptionType.STRING, "part", "본인의 역할을 'BACKEND', 'FRONTEND', 'FULLSTACK', 'PM', 'DESIGN' 중에서 입력해주세요 !", true),
				Commands.slash("질문하기", "궁금한 점을 질문해봐요 ! ")
						.addOption(OptionType.USER, "receiver", "질문 받을 사람을 선택해주세요 !", true)
						.addOption(OptionType.STRING, "question_content", "질문 내용을 작성해주세요 !", true),
				Commands.slash("답변하기", "답변을 통해 팀원을 도와줘요 ! ")
						.addOption(OptionType.STRING, "code", "답변할 질문에 대한 코드를 입력하세요", true)
						.addOption(OptionType.USER, "receiver", "답변을 받는 사람을 선택해주세요 ! ", true)
						.addOption(OptionType.STRING, "answer_content", "답변 내용을 작성해주세요 !", true),
				Commands.slash("업무시작", "오늘의 업무 시간을 시작해요 !"),
				Commands.slash("업무종료", "오늘의 업무를 종료해요 ! 업무 내용과 함께 기입해주세요 ~ !")
						.addOption(OptionType.STRING, "work_list", "오늘의 업무 내용을 '; '을 통해서 구분하여 작성해주세요 !! ", true),
				Commands.slash("스크럼종료", "지금까지의 업무들로 하나의 스크럼을 생성해요 ! "),
				Commands.slash("프로젝트종료", "프로젝트의 리더가 결정 할 수 있어요 !  종료하고 동료평가 단계로 넘어가요 ! "),
				Commands.slash("서버최신화", "디스코드의 서버 이름과 이미지의 변경점을 웹에 적용해요 ! "),
				Commands.slash("동료평가작성", "개발이 마무리 되셨나요? 프로젝트 종료 이후에 팀원들 사이에 동료평가를 작성할 수 있습니다 !")
						.addOption(OptionType.USER, "receiver", "동료평가를 할 사람을 지정해주세요", true)
						.addOption(OptionType.STRING, "evaluate", "동로평가의 내용을 작성해주세요", true),
				Commands.slash("동료평가조회", "팀원들 사이의 동료평가를 조회할 수 있어요 !")
						.addOption(OptionType.USER, "writer", "동료평가의 평가자를 선택해주세요", true)
						.addOption(OptionType.USER, "receiver", "동료평가의 피평가자를 선택해주세요", true)
		).queue();
	}

}

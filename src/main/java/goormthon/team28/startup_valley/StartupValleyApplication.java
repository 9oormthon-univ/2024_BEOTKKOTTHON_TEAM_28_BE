package goormthon.team28.startup_valley;

import goormthon.team28.startup_valley.discord.info.DiscordBotToken;
import goormthon.team28.startup_valley.discord.listener.DiscordListener;
import goormthon.team28.startup_valley.repository.MemberRepository;
import goormthon.team28.startup_valley.repository.TeamRepository;
import goormthon.team28.startup_valley.repository.UserRepository;
import goormthon.team28.startup_valley.service.*;
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

import java.util.Collections;

@SpringBootApplication
public class StartupValleyApplication {

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
						context.getBean(WorkService.class)
						)
				)
				.build();

		jda.updateCommands().addCommands(
				Commands.slash("팀원업데이트", "웹에 사용자들과 현재 진행하는 프로젝트를 연동해요 ! "),
				Commands.slash("질문하기", "궁금한 점을 질문해봐요 ! ")
						.addOption(OptionType.USER, "receiver", "질문 받을 사람을 선택해주세요 !", true)
						.addOption(OptionType.STRING, "question_content", "질문 내용을 작성해주세요 !", true),
				Commands.slash("답변하기", "답변을 통해 팀원을 도와줘요 ! ")
						.addOption(OptionType.STRING, "code", "답변할 질문에 대한 코드를 입력하세요", true)
						.addOption(OptionType.USER, "receiver", "답변을 받는 사람을 선택해주세요 ! ", true)
						.addOption(OptionType.STRING, "answer_content", "답변 내용을 작성해주세요 !", true),
				Commands.slash("업무시작", "오늘의 업무 시간을 시작해요 !"),
				Commands.slash("업무종료", "오늘의 업무를 종료해요 ! 업무 내용과 함께 기입해주세요 ~ !")
						.addOption(OptionType.STRING, "work_list", "오늘의 업무 내용을 '; '을 통해서 구분하여 작성해주세요 !! ", true)
		).queue();
	}

}

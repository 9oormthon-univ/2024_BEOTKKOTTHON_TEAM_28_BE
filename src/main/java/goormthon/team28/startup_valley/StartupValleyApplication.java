package goormthon.team28.startup_valley;

import goormthon.team28.startup_valley.discord.info.DiscordBotToken;
import goormthon.team28.startup_valley.discord.listener.DiscordListener;
import goormthon.team28.startup_valley.repository.MemberRepository;
import goormthon.team28.startup_valley.repository.TeamRepository;
import goormthon.team28.startup_valley.repository.UserRepository;
import goormthon.team28.startup_valley.service.MemberService;
import goormthon.team28.startup_valley.service.TeamService;
import goormthon.team28.startup_valley.service.UserService;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
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
				.addEventListeners(new DiscordListener(context.getBean(UserService.class), context.getBean(TeamService.class), context.getBean(MemberService.class)))
				.build();

		jda.updateCommands().addCommands(
				Commands.slash("웹과연동하기", "웹에 사용자들과 현재 진행하는 프로젝트를 연동해요!")
		).queue();
	}

}

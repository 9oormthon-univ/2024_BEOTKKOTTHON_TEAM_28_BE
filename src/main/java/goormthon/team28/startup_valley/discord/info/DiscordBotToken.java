package goormthon.team28.startup_valley.discord.info;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class DiscordBotToken {
    @Value("${discord.token}")
    private String token;
}

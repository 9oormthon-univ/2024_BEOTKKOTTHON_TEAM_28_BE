package goormthon.team28.startup_valley.dto.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.Arrays;
import java.util.Optional;

@Getter
@RequiredArgsConstructor
public enum EPart {
    BACKEND("BACKEND"),
    FRONTEND("FRONTEND"),
    FULLSTACK("FULLSTACK"),
    PM("PM"),
    DESIGN("DESIGN");
    private final String name;

    public static EPart fromName(String name, SlashCommandInteractionEvent event) {
        Optional<EPart> optionalPart = Arrays.stream(EPart.values())
                .filter(eProfileImage -> eProfileImage.getName().equals(name))
                .findFirst();
        if (optionalPart.isEmpty()){
            event.reply("파트가 제대로 입력되지 않았어요 ㅠㅠ \n\n" +
                    "'BACKEND', 'FRONTEND', 'FULLSTACK', 'PM', 'DESIGN' 에서 입력해주세요 !")
                    .setEphemeral(true).queue();
        }
        return optionalPart.get();
    }
}
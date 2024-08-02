package goormthon.team28.startup_valley.dto.type;

import goormthon.team28.startup_valley.constants.Constants;
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
    DESIGN("DESIGN"),
    UNSELECTED("UNSELECTED");
    private final String name;

    public static EPart fromName(String name, SlashCommandInteractionEvent event) {
        Optional<EPart> optionalPart = Arrays.stream(EPart.values())
                .filter(eProfileImage -> eProfileImage.getName().equals(name))
                .findFirst();
        if (optionalPart.isEmpty()){
            event.reply(Constants.DISCORD_INSERT_PART_FAIL).setEphemeral(true).queue();
        }
        return optionalPart.get();
    }
}
package goormthon.team28.startup_valley.discord.exception;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.Optional;

public class DiscordExceptionHandler {
    public static boolean checkNull (Object object, SlashCommandInteractionEvent event, String chatContent){
        if (object == null){
            event.reply(chatContent).setEphemeral(true).queue();
            return false;
        }
        return true;
    }
    public static boolean checkExisted(Optional<Object> optionalObject, SlashCommandInteractionEvent event, String chatContent){
        if (optionalObject.isEmpty()){
            event.reply(chatContent).setEphemeral(true).queue();
            return false;
        }
        return true;
    }
    public static boolean checkEmpty(Optional<Object> optionalObject, SlashCommandInteractionEvent event, String chatContent){
        if (optionalObject.isPresent()){
            event.reply(chatContent).setEphemeral(true).queue();
            return false;
        }
        return true;
    }
    public static boolean checkSameString(String str1, String str2, SlashCommandInteractionEvent event, String chatContent){
        if (!str1.equals(str2)){
            event.reply(chatContent).setEphemeral(true).queue();
            return false;
        }
        return true;
    }
    public static boolean checkSameId(Long id1, Long id2, SlashCommandInteractionEvent event, String chatContent){
        if (!id1.equals(id2)){
            event.reply(chatContent).setEphemeral(true).queue();
            return false;
        }
        return true;
    }
}

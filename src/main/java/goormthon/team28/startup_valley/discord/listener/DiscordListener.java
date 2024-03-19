package goormthon.team28.startup_valley.discord.listener;

import goormthon.team28.startup_valley.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;


import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class DiscordListener extends ListenerAdapter {
    private final UserRepository userRepository;
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        switch (event.getName()) {
            case "웹과연동하기":
                // 봇을 제외한 사용자 목록 생성
                List<Member> members = event.getGuild().getMembers().stream()
                        .filter(member -> !member.getUser().isBot()).toList();

                // DB에 가입 안한 사용자 이름 목록 가져오기
                List<String> noSignUp = findNoSignUp(members);

                if (noSignUp.isEmpty()){ // 모두 회원가입을 한 경우
                    // 팀 생성, 팀 멤버 생성 필요
                } else {
                    event.reply("웹에 회원가입이 필요합니다!\n\n" + "회원가입 해주세요 !! : " + noSignUp.toString()).setEphemeral(true).queue();
                }
                break;
        }
    }
    public List<String> findNoSignUp(List<Member> members) {
        List<String> usersWithoutInfo = new ArrayList<>();
        for (Member member : members) {
            String serialId = member.getUser().getName();
            if (!userRepository.existsBySerialId(serialId)) {
                usersWithoutInfo.add(serialId);
            }
        }
        return usersWithoutInfo;
    }

}

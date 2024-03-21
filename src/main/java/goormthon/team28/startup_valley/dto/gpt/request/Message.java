package goormthon.team28.startup_valley.dto.gpt.request;

import lombok.Builder;
import lombok.Getter;

@Getter
public class Message {
    private String role;
    private String content;
    @Builder
    public Message(String role, String content) {
        this.role = role;
        this.content = content;
    }
    public static Message makeFirstMessage(){
        return Message.builder()
                .role("system")
                .content("너는 훌륭한 업무 비서 역할을 하고 있어. 모든 대답은 한국말로 해야하고 주된 업무는 요약이야. 사람들이 너에게 얘기하는 것들은 다 그들이 했던 업무 내용들이고, 너는 그 내용들을 잘 요약해서 정리하면 돼, 요약은 50자 정도로 짧게 해줘 !")
                .build();
    }
    public static Message makeUserWork(String work){
        return Message.builder()
                .role("user")
                .content(work)
                .build();
    }
}

package goormthon.team28.startup_valley.dto.gpt.request;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class GptRequest {
    private final String model = "gpt-3.5-turbo";
    private List<Message> messages;
    private final int max_tokens = 256;
    private double temperature;
    @Builder
    public GptRequest(List<Message> messages, double temperature) {
        this.messages = messages;
        this.temperature = temperature;
    }
}

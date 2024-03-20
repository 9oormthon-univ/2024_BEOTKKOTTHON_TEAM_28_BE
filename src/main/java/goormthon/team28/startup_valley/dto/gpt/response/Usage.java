package goormthon.team28.startup_valley.dto.gpt.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class Usage {
    @JsonProperty(value = "prompt_tokens")
    private String promptTokens;
    @JsonProperty(value = "completion_tokens")
    private String completionTokens;
    @JsonProperty(value = "total_tokens")
    private String totalTokens;
}

package goormthon.team28.startup_valley.dto.gpt.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;

import java.util.List;

@Getter
public class GptResponse {
    private String id;
    private String object;
    private Long created;
    private String model;
    private List<Choice> choices;
    @JsonIgnore
    private Usage usage;
    @JsonIgnore
    private String fingerprint;
}

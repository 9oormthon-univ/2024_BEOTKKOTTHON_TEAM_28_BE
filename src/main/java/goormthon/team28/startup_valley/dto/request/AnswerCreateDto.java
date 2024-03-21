package goormthon.team28.startup_valley.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AnswerCreateDto(
        @JsonProperty("questionId")
        Long questionId,
        @JsonProperty("memberId")
        Long memberId,
        @JsonProperty("content")
        String content
) {
}

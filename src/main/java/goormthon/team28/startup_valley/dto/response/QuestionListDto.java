package goormthon.team28.startup_valley.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.io.Serializable;
import java.util.List;

@Builder
public record QuestionListDto(
        @JsonProperty("questionList")
        List<QuestionDto> questionDtoList,
        @JsonProperty("questionCount")
        Integer questionCount
) implements Serializable {
    public static QuestionListDto of(
            final List<QuestionDto> questionDtoList,
            final Integer questionCount
    ) {
        return QuestionListDto.builder()
                .questionDtoList(questionDtoList)
                .questionCount(questionCount)
                .build();
    }
}

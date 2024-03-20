package goormthon.team28.startup_valley.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.io.Serializable;
import java.util.List;

@Builder
public record QuestionRetrieveSetListDto(
        @JsonProperty("questionList")
        List<QuestionRetrieveSetDto> questionRetrieveSetDtoList
) implements Serializable {
    public static QuestionRetrieveSetListDto of(final List<QuestionRetrieveSetDto> questionRetrieveSetDto) {
        return QuestionRetrieveSetListDto.builder()
                .questionRetrieveSetDtoList(questionRetrieveSetDto)
                .build();
    }
}

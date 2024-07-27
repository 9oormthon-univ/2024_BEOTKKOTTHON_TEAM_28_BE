package goormthon.team28.startup_valley.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.io.Serializable;
import java.util.List;

@Builder
public record RankingDto(
        @JsonProperty("workedDate")
        List<RankingElementDto> workedDate,
        @JsonProperty("workedTime")
        List<RankingElementDto> workedTime,
        @JsonProperty("questionTimes")
        List<RankingElementDto> questionTimes,
        @JsonProperty("fastAnswered")
        List<RankingElementDto> fastAnswered,
        @JsonProperty("detailedBacklog")
        List<RankingElementDto> detailedBacklog
) implements Serializable {
    public static RankingDto of(
            final List<RankingElementDto> workedDate,
            final List<RankingElementDto> workedTime,
            final List<RankingElementDto> questionTimes,
            final List<RankingElementDto> fastAnswered,
            final List<RankingElementDto> detailedBacklog
    ) {
        return RankingDto.builder()
                .workedDate(workedDate)
                .workedTime(workedTime)
                .questionTimes(questionTimes)
                .fastAnswered(fastAnswered)
                .detailedBacklog(detailedBacklog)
                .build();
    }
}

package goormthon.team28.startup_valley.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.io.Serializable;
import java.util.List;

@Builder
public record RankingListDto(
        @JsonProperty("rankList")
        List<RankingDto> rankingDtoList,
        @JsonProperty("projectName")
        String projectName
) implements Serializable {
    public static RankingListDto of(
            final List<RankingDto> rankingDtoList,
            final String projectName
    ) {
        return RankingListDto.builder()
                .rankingDtoList(rankingDtoList)
                .projectName(projectName)
                .build();
    }
}

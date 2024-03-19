package goormthon.team28.startup_valley.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.io.Serializable;
import java.util.List;

@Builder
public record TeamListDto(
        @JsonProperty("progressingProjectList")
        List<TeamDto> progressingProjectList,
        @JsonProperty("progressingProjectCount")
        Integer progressingProjectCount,
        @JsonProperty("endProjectList")
        List<TeamDto> endProjectList,
        @JsonProperty("endProjectCount")
        Integer endProjectCount
) implements Serializable {
    public static TeamListDto of(
            final List<TeamDto> progressingProjectList,
            final Integer progressingProjectCount,
            final List<TeamDto> endProjectList,
            final Integer endProjectCount
    ) {
        return TeamListDto.builder()
                .progressingProjectList(progressingProjectList)
                .progressingProjectCount(progressingProjectCount)
                .endProjectList(endProjectList)
                .endProjectCount(endProjectCount)
                .build();
    }
}

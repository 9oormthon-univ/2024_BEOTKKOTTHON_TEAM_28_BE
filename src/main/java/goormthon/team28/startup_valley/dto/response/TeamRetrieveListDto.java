package goormthon.team28.startup_valley.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.io.Serializable;
import java.util.List;

@Builder
public record TeamRetrieveListDto(
        @JsonProperty("projectList")
        List<TeamRetrieveDto> teamRetrieveDtoList
) implements Serializable {
    public static TeamRetrieveListDto of(final List<TeamRetrieveDto> teamRetrieveDtoList) {
        return TeamRetrieveListDto.builder()
                .teamRetrieveDtoList(teamRetrieveDtoList)
                .build();
    }
}

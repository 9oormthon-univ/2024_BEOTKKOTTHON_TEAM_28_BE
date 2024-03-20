package goormthon.team28.startup_valley.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.io.Serializable;
import java.util.List;

@Builder
public record TeamMemberPermissionListDto(
        @JsonProperty("memberList")
        List<TeamMemberPermissionDto> teamMemberPermissionDtoList
) implements Serializable {
    public static TeamMemberPermissionListDto of(
            final List<TeamMemberPermissionDto> teamMemberPermissionDtoList
    ) {
        return TeamMemberPermissionListDto.builder()
                .teamMemberPermissionDtoList(teamMemberPermissionDtoList)
                .build();
    }
}

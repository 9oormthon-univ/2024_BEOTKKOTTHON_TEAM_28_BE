package goormthon.team28.startup_valley.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TeamMemberPermissionPatchDto(
        @JsonProperty("memberId")
        Long memberId
) {
}

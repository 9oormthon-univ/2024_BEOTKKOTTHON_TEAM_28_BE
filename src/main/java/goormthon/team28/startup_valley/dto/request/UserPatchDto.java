package goormthon.team28.startup_valley.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserPatchDto(
        @JsonProperty("nickname")
        String nickname,
        @JsonProperty("profileImage")
        String profileImage
) {
}

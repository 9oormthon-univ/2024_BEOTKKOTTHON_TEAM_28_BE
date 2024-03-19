package goormthon.team28.startup_valley.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AuthSignUpDto(
        @JsonProperty("profile_image")
        String profileImage,
        @JsonProperty("nickname")
        String nickname,
        @JsonProperty("discord_id")
        String serialId,
        @JsonProperty("password")
        String password
) {
}

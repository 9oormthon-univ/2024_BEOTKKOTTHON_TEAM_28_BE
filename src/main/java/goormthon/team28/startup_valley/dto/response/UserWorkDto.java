package goormthon.team28.startup_valley.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.io.Serializable;

@Builder
public record UserWorkDto(
        @JsonProperty("id")
        Long id,
        @JsonProperty("nickname")
        String nickname,
        @JsonProperty("profileImage")
        String profileImage,
        @JsonProperty("part")
        String part
) implements Serializable {
    public static UserWorkDto of(
            final Long id,
            final String nickname,
            final String profileImage,
            final String part
    ) {
        return UserWorkDto.builder()
                .id(id)
                .nickname(nickname)
                .profileImage(profileImage)
                .part(part)
                .build();
    }
}

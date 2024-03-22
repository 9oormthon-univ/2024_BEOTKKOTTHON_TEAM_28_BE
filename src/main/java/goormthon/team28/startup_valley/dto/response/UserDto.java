package goormthon.team28.startup_valley.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import goormthon.team28.startup_valley.dto.type.EProfileImage;
import jakarta.annotation.Nullable;
import lombok.Builder;

import java.io.Serializable;

@Builder
public record UserDto(
        @JsonProperty("id")
        Long id,
        @JsonProperty("memberId")
        Long memberId,
        @JsonProperty("nickname")
        String nickname,
        @JsonProperty("profileImage")
        String profileImage

) implements Serializable {
    public static UserDto of(
            final Long id,
            @Nullable
            final Long memberId,
            final String nickname,
            final EProfileImage eProfileImage
    ) {
        return UserDto.builder()
                .id(id)
                .memberId(memberId)
                .nickname(nickname)
                .profileImage(eProfileImage.getName())
                .build();
    }
}

package goormthon.team28.startup_valley.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import goormthon.team28.startup_valley.dto.type.EProfileImage;
import lombok.Builder;

import java.io.Serializable;

@Builder
public record RankingDto(
        @JsonProperty("memberId")
        Long memberId,
        @JsonProperty("nickname")
        String nickname,
        @JsonProperty("profileImage")
        String profileImage,
        @JsonProperty("totalTime")
        Long totalTime
) implements Serializable {
    public static RankingDto of(
            final Long memberId,
            final String nickname,
            final EProfileImage eProfileImage,
            final Long totalTime
    ) {
        return RankingDto.builder()
                .memberId(memberId)
                .nickname(nickname)
                .profileImage(eProfileImage.getName())
                .totalTime(totalTime)
                .build();
    }
}

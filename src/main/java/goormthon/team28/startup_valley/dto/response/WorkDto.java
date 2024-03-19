package goormthon.team28.startup_valley.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import goormthon.team28.startup_valley.dto.type.EPart;
import goormthon.team28.startup_valley.dto.type.EProfileImage;
import lombok.Builder;

import java.io.Serializable;
import java.time.LocalDateTime;

@Builder
public record WorkDto(
        @JsonProperty("memberId")
        Long memberId,
        @JsonProperty("nickname")
        String nickname,
        @JsonProperty("part")
        String part,
        @JsonProperty("profileImage")
        String profileImage,
        @JsonProperty("content")
        String content,
        @JsonProperty("createdAt")
        String createdAt

) implements Serializable {
    public static WorkDto of(
            final Long memberId,
            final String nickname,
            final EPart ePart,
            final EProfileImage eProfileImage,
            final String content,
            final LocalDateTime createdAt
    ) {
        return WorkDto.builder()
                .memberId(memberId)
                .nickname(nickname)
                .part(ePart.getName())
                .profileImage(eProfileImage.getName())
                .content(content)
                .createdAt(createdAt.toString())
                .build();
    }
}

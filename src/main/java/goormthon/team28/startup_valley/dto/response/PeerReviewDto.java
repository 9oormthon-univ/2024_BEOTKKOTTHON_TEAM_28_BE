package goormthon.team28.startup_valley.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import goormthon.team28.startup_valley.dto.type.EPart;
import goormthon.team28.startup_valley.dto.type.EProfileImage;
import jakarta.annotation.Nullable;
import lombok.Builder;

import java.io.Serializable;

@Builder
public record PeerReviewDto(
        @JsonProperty("memberId")
        Long memberId,
        @JsonProperty("nickname")
        String nickname,
        @JsonProperty("profileImage")
        String profileImage,
        @JsonProperty("part")
        String part,
        @JsonProperty("peerReview")
        String peerReview
) implements Serializable {
    public static PeerReviewDto of(
            final Long memberId,
            final String nickname,
            final EProfileImage eProfileImage,
            final EPart ePart,
            @Nullable
            final String peerReview
    ) {
        return PeerReviewDto.builder()
                .memberId(memberId)
                .nickname(nickname)
                .profileImage(eProfileImage.getName())
                .part(ePart.getName())
                .peerReview(peerReview)
                .build();
    }
}

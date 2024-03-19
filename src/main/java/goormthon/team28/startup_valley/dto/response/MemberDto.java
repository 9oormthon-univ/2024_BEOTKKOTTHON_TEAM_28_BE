package goormthon.team28.startup_valley.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import goormthon.team28.startup_valley.dto.type.EPart;
import goormthon.team28.startup_valley.dto.type.EProfileImage;
import lombok.Builder;

import java.io.Serializable;

@Builder
public record MemberDto(
        @JsonProperty("memberId")
        Long id,
        @JsonProperty("nickname")
        String nickname,
        @JsonProperty("part")
        String part,
        @JsonProperty("profileImage")
        String profileImage

) implements Serializable {
    public static MemberDto of(
            final Long id,
            final String nickname,
            final EPart ePart,
            final EProfileImage eProfileImage
    ) {
        return MemberDto.builder()
                .id(id)
                .nickname(nickname)
                .part(ePart.getName())
                .profileImage(eProfileImage.getName())
                .build();
    }
}

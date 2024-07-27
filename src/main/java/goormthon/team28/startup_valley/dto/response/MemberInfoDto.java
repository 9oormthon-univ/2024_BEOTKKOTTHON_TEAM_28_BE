package goormthon.team28.startup_valley.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import goormthon.team28.startup_valley.domain.Member;
import lombok.Builder;

import java.io.Serializable;

@Builder
public record MemberInfoDto(
        @JsonProperty("memberId")
        Long memberId,
        @JsonProperty("nickname")
        String nickname,
        @JsonProperty("profileImage")
        String profileImage
) implements Serializable {
    public static MemberInfoDto of(final Member member) {
        return MemberInfoDto.builder()
                .memberId(member.getId())
                .nickname(member.getUser().getNickname())
                .profileImage(member.getUser().getProfileImage().getName())
                .build();
    }
}

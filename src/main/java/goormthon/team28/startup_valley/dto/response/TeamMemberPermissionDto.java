package goormthon.team28.startup_valley.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import goormthon.team28.startup_valley.dto.type.EProfileImage;
import lombok.Builder;

import java.io.Serializable;

@Builder
public record TeamMemberPermissionDto(
        @JsonProperty("id")
        Long id,
        @JsonProperty("nickname")
        String nickname,
        @JsonProperty("profileImage")
        String profileImage,
        @JsonProperty("isLeader")
        Boolean isLeader

) implements Serializable {
    public static TeamMemberPermissionDto of(
            final Long id,
            final String nickname,
            final EProfileImage eProfileImage,
            final Boolean isLeader
    ) {
        return TeamMemberPermissionDto.builder()
                .id(id)
                .nickname(nickname)
                .profileImage(eProfileImage.getName())
                .isLeader(isLeader)
                .build();
    }
}

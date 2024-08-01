package goormthon.team28.startup_valley.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import goormthon.team28.startup_valley.dto.type.EProjectStatus;
import jakarta.annotation.Nullable;
import lombok.Builder;

import java.io.Serializable;
import java.time.LocalDate;

@Builder
public record TeamRetrieveDto(
        @JsonProperty("id")
        Long id,
        @JsonProperty("memberId")
        Long memberId,
        @JsonProperty("name")
        String name,
        @JsonProperty("summary")
        String retrospection,
        @JsonProperty("profileImage")
        String profileImage,
        @JsonProperty("startAt")
        String startAt,
        @JsonProperty("endAt")
        String endAt,
        @JsonProperty("status")
        String status
) implements Serializable {
    public static TeamRetrieveDto of(
            final Long id,
            final Long memberId,
            final String name,
            final String retrospection,
            final String profileImage,
            final LocalDate startAt,
            @Nullable
            final LocalDate endAt,
            final EProjectStatus eProjectStatus
    ) {
        return TeamRetrieveDto.builder()
                .id(id)
                .memberId(memberId)
                .name(name)
                .retrospection(retrospection)
                .profileImage(profileImage)
                .startAt(startAt.toString())
                .endAt(endAt != null ? endAt.toString() : null)
                .status(eProjectStatus.getStatus())
                .build();
    }
}

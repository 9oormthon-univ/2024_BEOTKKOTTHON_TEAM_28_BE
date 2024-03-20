package goormthon.team28.startup_valley.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.io.Serializable;
import java.time.LocalDate;

@Builder
public record ScrumContributionDto(
        @JsonProperty("id")
        Long id,
        @JsonProperty("content")
        String content,
        @JsonProperty("startAt")
        String startAt,
        @JsonProperty("endAt")
        String endAt
) implements Serializable {
    public static ScrumContributionDto of(
            final Long id,
            final String content,
            final LocalDate startAt,
            final LocalDate endAt
    ) {
        return ScrumContributionDto.builder()
                .id(id)
                .content(content)
                .startAt(startAt.toString())
                .endAt(endAt.toString())
                .build();
    }
}

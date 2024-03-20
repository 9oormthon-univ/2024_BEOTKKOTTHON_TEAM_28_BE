package goormthon.team28.startup_valley.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.io.Serializable;
import java.time.LocalDateTime;

@Builder
public record WorkManageDto(
        @JsonProperty("id")
        Long id,
        @JsonProperty("content")
        String content,
        @JsonProperty("startAt")
        String startAt,
        @JsonProperty("endAt")
        String endAt
) implements Serializable {
    public static WorkManageDto of(
            final Long id,
            final String content,
            final LocalDateTime startAt,
            final LocalDateTime endAt
    ) {
        return WorkManageDto.builder()
                .id(id)
                .content(content)
                .startAt(startAt.toString())
                .endAt(endAt.toString())
                .build();
    }
}

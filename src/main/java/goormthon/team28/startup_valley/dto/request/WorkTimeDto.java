package goormthon.team28.startup_valley.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public record WorkTimeDto(
        @JsonProperty("startAt")
        LocalDateTime startAt,
        @JsonProperty("endAt")
        LocalDateTime endAt
) {
}

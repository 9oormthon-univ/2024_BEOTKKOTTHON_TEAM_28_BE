package goormthon.team28.startup_valley.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record WorkCreateDto(
        @JsonProperty("content")
        String content
) {
}

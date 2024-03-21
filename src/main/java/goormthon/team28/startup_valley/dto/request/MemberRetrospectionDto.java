package goormthon.team28.startup_valley.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MemberRetrospectionDto(
        @JsonProperty("content")
        String content
) {
}

package goormthon.team28.startup_valley.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.io.Serializable;

@Builder
public record WorkForScrumDto(
        @JsonProperty("id")
        Long id,
        @JsonProperty("content")
        String content
) implements Serializable {
    public static WorkForScrumDto of(
            final Long id,
            final String content
    ) {
        return WorkForScrumDto.builder()
                .id(id)
                .content(content)
                .build();
    }
}

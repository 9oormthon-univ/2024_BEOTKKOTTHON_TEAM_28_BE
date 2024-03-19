package goormthon.team28.startup_valley.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.io.Serializable;

@Builder
public record TeamDto(
        @JsonProperty("id")
        Long id,
        @JsonProperty("name")
        String name,
        @JsonProperty("image")
        String image
) implements Serializable {
        public static TeamDto of(
                final Long id,
                final String name,
                final String image
        ) {
            return TeamDto.builder()
                    .id(id)
                    .name(name)
                    .image(image)
                    .build();
        }
}

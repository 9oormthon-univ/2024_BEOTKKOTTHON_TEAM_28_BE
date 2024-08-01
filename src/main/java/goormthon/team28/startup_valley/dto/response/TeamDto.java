package goormthon.team28.startup_valley.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.io.Serializable;

@Builder
public record TeamDto(
        @JsonProperty("id")
        Long id,
        @JsonProperty("memberId")
        Long memberId,
        @JsonProperty("name")
        String name,
        @JsonProperty("image")
        String image
) implements Serializable {
        public static TeamDto of(
                final Long id,
                final Long memberId,
                final String name,
                final String image
        ) {
            return TeamDto.builder()
                    .id(id)
                    .memberId(memberId)
                    .name(name)
                    .image(image)
                    .build();
        }
}

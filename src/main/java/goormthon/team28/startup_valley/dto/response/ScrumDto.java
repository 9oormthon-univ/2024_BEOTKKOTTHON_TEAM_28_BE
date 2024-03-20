package goormthon.team28.startup_valley.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Builder
public record ScrumDto(
        @JsonProperty("id")
        Long id,
        @JsonProperty("content")
        String content,
        @JsonProperty("startAt")
        String startAt,
        @JsonProperty("endAt")
        String endAt,
        @JsonProperty("workList")
        List<WorkForScrumDto> workForScrumDtoList
) implements Serializable {
        public static ScrumDto of(
                final Long id,
                final String content,
                final LocalDate startAt,
                final LocalDate endAt,
                final List<WorkForScrumDto> workForScrumDtoList
        ) {
                return ScrumDto.builder()
                        .id(id)
                        .content(content)
                        .startAt(startAt.toString())
                        .endAt(endAt.toString())
                        .workForScrumDtoList(workForScrumDtoList)
                        .build();
        }
}

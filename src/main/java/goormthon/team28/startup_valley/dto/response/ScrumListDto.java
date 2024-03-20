package goormthon.team28.startup_valley.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.io.Serializable;
import java.util.List;

@Builder
public record ScrumListDto(
        @JsonProperty("scrumList")
        List<ScrumDto> scrumDtoList
) implements Serializable {
    public static ScrumListDto of(final List<ScrumDto> scrumDtoList) {
        return ScrumListDto.builder()
                .scrumDtoList(scrumDtoList)
                .build();
    }
}

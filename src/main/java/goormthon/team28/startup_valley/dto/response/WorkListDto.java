package goormthon.team28.startup_valley.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.io.Serializable;
import java.util.List;

@Builder
public record WorkListDto(
        @JsonProperty("workList")
        List<WorkDto> workDtoList,
        @JsonProperty("projectName")
        String projectName

) implements Serializable {
    public static WorkListDto of(
            final List<WorkDto> workDtoList,
            final String projectName
    ) {
        return WorkListDto.builder()
                .workDtoList(workDtoList)
                .projectName(projectName)
                .build();
    }
}

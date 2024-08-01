package goormthon.team28.startup_valley.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.util.List;

@Builder
public record TeamWorkStatusDto (
        @JsonProperty("startDate")
        String startDate,
        @JsonProperty("endDate")
        String endDate,
        @JsonProperty("currentWorkerList")
        List<UserWorkDto> currentWorkerList,
        @Nullable
        @JsonProperty("latestWork")
        String latestWork,
        @JsonProperty("projectName")
        String projectName
) implements Serializable {
    public static TeamWorkStatusDto of(
            final String startDate,
            final String endDate,
            final List<UserWorkDto> currentWorkerList,
            final String latestWork,
            final String projectName
    ) {
        return TeamWorkStatusDto.builder()
                .startDate(startDate)
                .endDate(endDate)
                .currentWorkerList(currentWorkerList)
                .latestWork(latestWork)
                .projectName(projectName)
                .build();
    }

}
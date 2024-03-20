package goormthon.team28.startup_valley.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.io.Serializable;
import java.util.List;

@Builder
public record WorkManageListDto(
        @JsonProperty("workList")
        List<WorkManageDto> workManageDtoList
) implements Serializable {
    public static WorkManageListDto of(final List<WorkManageDto> workManageDtoList) {
        return WorkManageListDto.builder()
                .workManageDtoList(workManageDtoList)
                .build();
    }
}

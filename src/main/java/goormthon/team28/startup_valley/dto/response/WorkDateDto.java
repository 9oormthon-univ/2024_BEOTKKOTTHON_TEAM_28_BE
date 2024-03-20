package goormthon.team28.startup_valley.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.io.Serializable;
import java.time.LocalDate;

@Builder
public record WorkDateDto(
        @JsonProperty("date")
        String date,
        @JsonProperty("time")
        Long time

) implements Serializable {
    public static WorkDateDto of(final LocalDate date, final Long time) {
        return WorkDateDto.builder()
                .date(date.toString())
                .time(time)
                .build();
    }
}

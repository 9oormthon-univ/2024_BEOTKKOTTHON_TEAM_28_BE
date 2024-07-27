package goormthon.team28.startup_valley.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.io.Serializable;

@Builder
public record RankingElementDto(
        @JsonProperty("memberInfo")
        MemberInfoDto memberInfoDto,
        @JsonProperty("number")
        Long number
) implements Serializable {
    public static RankingElementDto of(
            final MemberInfoDto memberInfoDto,
            final Long number
    ) {
        return RankingElementDto.builder()
                .memberInfoDto(memberInfoDto)
                .number(number)
                .build();
    }
}

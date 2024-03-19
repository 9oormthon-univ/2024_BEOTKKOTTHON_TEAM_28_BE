package goormthon.team28.startup_valley.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.io.Serializable;
import java.util.List;

@Builder
public record MemberListDto(
        @JsonProperty("memberList")
        List<MemberDto> memberDtoList,
        @JsonProperty("memberCount")
        Integer memberCount
) implements Serializable {
    public static MemberListDto of(
            final List<MemberDto> memberDtoList,
            final Integer memberCount
    ) {
        return MemberListDto.builder()
                .memberDtoList(memberDtoList)
                .memberCount(memberCount)
                .build();
    }
}

package goormthon.team28.startup_valley.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import goormthon.team28.startup_valley.dto.type.EPart;
import lombok.Builder;

import java.io.Serializable;
import java.util.List;

@Builder
public record MemberContributionDto(
        @JsonProperty("totalTime")
        Long totalMinute,
        @JsonProperty("part")
        String part,
        @JsonProperty("peerReviewSummary")
        String peerReviewSummary,
        @JsonProperty("isPublic")
        boolean isPublic,
        @JsonProperty("scrumList")
        List<ScrumContributionDto> scrumContributionDtoList
) implements Serializable {
        public static MemberContributionDto of(
                final Long totalMinute,
                final EPart ePart,
                final String peerReviewSummary,
                final boolean isPublic,
                final List<ScrumContributionDto> scrumContributionDtoList
        ) {
                return MemberContributionDto.builder()
                        .totalMinute(totalMinute)
                        .part(ePart.getName())
                        .peerReviewSummary(peerReviewSummary)
                        .isPublic(isPublic)
                        .scrumContributionDtoList(scrumContributionDtoList)
                        .build();
        }
}

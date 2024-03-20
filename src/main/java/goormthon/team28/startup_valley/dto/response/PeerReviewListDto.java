package goormthon.team28.startup_valley.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.io.Serializable;
import java.util.List;

@Builder
public record PeerReviewListDto(
        @JsonProperty("projectName")
        String projectName,
        @JsonProperty("peerReviewList")
        List<PeerReviewDto> peerReviewDtoList
) implements Serializable {
        public static PeerReviewListDto of(
                final String projectName,
                final List<PeerReviewDto> peerReviewDtoList
        ) {
            return PeerReviewListDto.builder()
                    .projectName(projectName)
                    .peerReviewDtoList(peerReviewDtoList)
                    .build();
        }
}

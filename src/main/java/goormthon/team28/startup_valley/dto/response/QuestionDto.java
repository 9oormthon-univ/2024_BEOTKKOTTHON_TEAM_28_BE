package goormthon.team28.startup_valley.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import goormthon.team28.startup_valley.dto.type.EPart;
import goormthon.team28.startup_valley.dto.type.EProfileImage;
import lombok.Builder;

import java.io.Serializable;

@Builder
public record QuestionDto(
        @JsonProperty("id")
        Long id,
        @JsonProperty("title")
        String title,
        @JsonProperty("requestPart")
        String requestPart,
        @JsonProperty("profileImage")
        String profileImage
) implements Serializable {
    public static QuestionDto of(
            final Long id,
            final String title,
            final EPart ePart,
            final EProfileImage eProfileImage
    ) {
       return QuestionDto.builder()
               .id(id)
               .title(title)
               .requestPart(ePart.getName())
               .profileImage(eProfileImage.getName())
               .build();
    }
}

package goormthon.team28.startup_valley.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import goormthon.team28.startup_valley.dto.type.EPart;
import goormthon.team28.startup_valley.dto.type.EProfileImage;
import jakarta.annotation.Nullable;
import lombok.Builder;

import java.io.Serializable;
import java.time.LocalDateTime;

@Builder
@Nullable
public record QuestionRetrieveDto(
        @JsonProperty("id")
        Long id,
        @JsonProperty("name")
        String name,
        @JsonProperty("profileImage")
        String profileImage,
        @JsonProperty("part")
        String part,
        @JsonProperty("content")
        String content,
        @JsonProperty("createdAt")
        String createdAt
) implements Serializable {
    public static QuestionRetrieveDto of(
            final Long id,
            final String name,
            final EProfileImage eProfileImage,
            final EPart ePart,
            final String content,
            final LocalDateTime createdAt
            ) {
        return QuestionRetrieveDto.builder()
                .id(id)
                .name(name)
                .profileImage(eProfileImage.getName())
                .part(ePart.getName())
                .content(content)
                .createdAt(createdAt.toString())
                .build();
    }
}

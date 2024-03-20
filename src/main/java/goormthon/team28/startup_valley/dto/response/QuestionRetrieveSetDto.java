package goormthon.team28.startup_valley.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.io.Serializable;
import java.util.Optional;

@Builder
public record QuestionRetrieveSetDto(
        @JsonProperty("sender")
        QuestionRetrieveDto sender,
        @JsonProperty("receiver")
        QuestionRetrieveDto receiver
) implements Serializable {
    public static QuestionRetrieveSetDto of(
            final QuestionRetrieveDto sender,
            final QuestionRetrieveDto receiver
    ) {
        return QuestionRetrieveSetDto.builder()
                .sender(sender)
                .receiver(receiver)
                .build();
    }
}

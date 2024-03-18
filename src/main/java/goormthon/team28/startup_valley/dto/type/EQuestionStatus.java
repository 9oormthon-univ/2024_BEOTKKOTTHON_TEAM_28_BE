package goormthon.team28.startup_valley.dto.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EQuestionStatus {
    WAITING_ANSWER("WAITING_ANSWER"),
    FINISH("FINISH");
    private final String status;
}

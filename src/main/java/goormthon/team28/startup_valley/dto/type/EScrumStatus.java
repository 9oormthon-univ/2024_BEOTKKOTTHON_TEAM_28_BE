package goormthon.team28.startup_valley.dto.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EScrumStatus {
    IN_PROGRESS("IN_PROGRESS"),
    FINISH("FINISH");
    private final String status;
}

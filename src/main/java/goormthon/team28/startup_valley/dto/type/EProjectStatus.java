package goormthon.team28.startup_valley.dto.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EProjectStatus {
    IN_PROGRESS("IN_PROGRESS"),
    PEER_REVIEW("PEER_REVIEW"),
    FINISH("FINISH");
    private final String status;
}

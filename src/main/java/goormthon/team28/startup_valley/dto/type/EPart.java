package goormthon.team28.startup_valley.dto.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EPart {
    BACKEND("BACKEND"),
    FRONTEND("FRONTEND"),
    FULLSTACK("FULLSTACK"),
    PM("PM"),
    DESIGN("DESIGN");
    private final String name;
}
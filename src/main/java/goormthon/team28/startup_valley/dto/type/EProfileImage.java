package goormthon.team28.startup_valley.dto.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EProfileImage {
    BLUEBERRY("BLUEBERRY"),
    CABBAGE("CABBAGE"),
    CARROT("CARROT"),
    CLOCK("CLOCK"),
    CUCUMBER("CUCUMBER"),
    LOADER("LOADER"),
    STRAWBERRY("STRAWBERRY"),
    TOMATO("TOMATO");
    private final String name;
    // blueberry, cabbage, carrot, clock, cucumber, loader, strawberry, tomato

}

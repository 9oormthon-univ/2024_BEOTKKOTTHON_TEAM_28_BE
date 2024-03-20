package goormthon.team28.startup_valley.dto.type;

import goormthon.team28.startup_valley.exception.CommonException;
import goormthon.team28.startup_valley.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

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

    public static EProfileImage fromName(String name) {
        return Arrays.stream(EProfileImage.values())
                .filter(eProfileImage -> eProfileImage.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new CommonException(ErrorCode.INVALID_USER_PROFILE_IMAGE_ENUM));
    }


}

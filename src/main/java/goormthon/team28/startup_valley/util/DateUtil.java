package goormthon.team28.startup_valley.util;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class DateUtil {
    public static LocalDateTime getOneWeekAgoDate() {
        return LocalDateTime.now().minus(1, ChronoUnit.WEEKS);
    }
}

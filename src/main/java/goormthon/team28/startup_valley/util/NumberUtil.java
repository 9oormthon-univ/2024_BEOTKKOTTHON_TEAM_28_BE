package goormthon.team28.startup_valley.util;

import java.util.Random;

public class NumberUtil {
    public static String generateRandomCode() {
        Random random = new Random();
        int min = 0; // 4자리 숫자 코드의 최소값
        int max = 9999; // 4자리 숫자 코드의 최대값
        int randomNum = random.nextInt(max - min + 1) + min;
        return String.format("%04d", randomNum); // 4자리 숫자로 포맷팅하여 반환
    }
}

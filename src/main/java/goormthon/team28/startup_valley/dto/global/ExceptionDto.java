package goormthon.team28.startup_valley.dto.global;

import goormthon.team28.startup_valley.exception.ErrorCode;
import lombok.Getter;

@Getter
public class ExceptionDto {
    private final Integer code;
    private final String message;
    public ExceptionDto(ErrorCode errorCode){
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }
    public static ExceptionDto of(ErrorCode errorCode){
        return new ExceptionDto(errorCode);
    }
}

package goormthon.team28.startup_valley.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CommonException extends RuntimeException{
    private final ErrorCode errorCode;
    public String getMessage(){
        return this.errorCode.getMessage();
    }

}
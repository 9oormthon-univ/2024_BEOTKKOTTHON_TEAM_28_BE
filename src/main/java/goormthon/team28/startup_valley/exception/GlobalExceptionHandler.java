package goormthon.team28.startup_valley.exception;

import goormthon.team28.startup_valley.dto.global.ResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    // Convertor 에서 바인딩 실패시 발생하는 예외
    @ExceptionHandler(value = {HttpMessageNotReadableException.class})
    public ResponseDto<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.error("handleHttpMessageNotReadableException() in GlobalExceptionHandler throw HttpMessageNotReadableException : {}", e.getMessage());
        return ResponseDto.fail(new CommonException(ErrorCode.BAD_REQUEST_JSON));
    }
    // 지원되지 않는 HTTP 메소드를 사용할 때 발생하는 예외
    @ExceptionHandler(value = {NoHandlerFoundException.class, HttpRequestMethodNotSupportedException.class})
    public ResponseDto<?> handleNoPageFoundException(Exception e) {
        log.error("handleNoPageFoundException() in GlobalExceptionHandler throw NoHandlerFoundException : {}", e.getMessage());
        return ResponseDto.fail(new CommonException(ErrorCode.WRONG_ENTRY_POINT));
    }

    // 메소드의 인자 타입이 일치하지 않을 때 발생하는 예외
    @ExceptionHandler(value = {MethodArgumentTypeMismatchException.class})
    public ResponseDto<?> handleArgumentNotValidException(MethodArgumentTypeMismatchException e) {
        log.error("handleArgumentNotValidException() in GlobalExceptionHandler throw MethodArgumentTypeMismatchException : {}", e.getMessage());
        return ResponseDto.fail(e);
    }

    // 필수 파라미터가 누락되었을 때 발생하는 예외
    @ExceptionHandler(value = {MissingServletRequestParameterException.class})
    public ResponseDto<?> handleArgumentNotValidException(MissingServletRequestParameterException e) {
        log.error("handleArgumentNotValidException() in GlobalExceptionHandler throw MethodArgumentNotValidException : {}", e.getMessage());
        return ResponseDto.fail(e);
    }
    /**
     * 커스텀 exception
     */
    @ExceptionHandler(value = {CommonException.class})
    public ResponseDto<?> handleCustomException(CommonException e){
        return ResponseDto.fail(e);
    }
    /**
     * 서버 exception
     */
    @ExceptionHandler(value = {Exception.class})
    public ResponseDto<?> handleServerException(Exception e){
        log.info("occurred exception in handleServerError = {}", e.getMessage());
        e.printStackTrace();
        return ResponseDto.fail(new CommonException(ErrorCode.INTERNAL_SERVER_ERROR));
    }
}
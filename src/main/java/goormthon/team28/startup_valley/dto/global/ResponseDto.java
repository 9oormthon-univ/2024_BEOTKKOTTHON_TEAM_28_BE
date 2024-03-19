package goormthon.team28.startup_valley.dto.global;

import com.fasterxml.jackson.annotation.JsonIgnore;
import goormthon.team28.startup_valley.exception.CommonException;
import goormthon.team28.startup_valley.exception.ErrorCode;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

public record ResponseDto<T>(
        @JsonIgnore HttpStatus httpStatus,
        @NotNull boolean success,
        @Nullable T data,
        @Nullable ExceptionDto error
) {
    public static <T> ResponseDto<T> ok(T data){
        return new ResponseDto<>(
                HttpStatus.OK,
                true,
                data,
                null
        );
    }
    public static <T> ResponseDto<T> created(T data){
        return new ResponseDto<>(
                HttpStatus.CREATED,
                true,
                data,
                null
        );
    }
    public static ResponseDto<Object> fail(@NotNull CommonException e){
        return new ResponseDto<>(
                e.getErrorCode().getHttpStatus(),
                false,
                null,
                ExceptionDto.of(e.getErrorCode())
        );
    }

    public static ResponseDto<Object> fail(final MissingServletRequestParameterException e) {
        return new ResponseDto<>(HttpStatus.BAD_REQUEST, false, null, ExceptionDto.of(ErrorCode.MISSING_REQUEST_PARAMETER));
    }

    public static ResponseDto<Object> fail(final MethodArgumentTypeMismatchException e) {
        return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR, false, null, ExceptionDto.of(ErrorCode.INVALID_PARAMETER_FORMAT));
    }
}


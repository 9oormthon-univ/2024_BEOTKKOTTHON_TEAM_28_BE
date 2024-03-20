package goormthon.team28.startup_valley.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    //400
    WRONG_ENTRY_POINT(40000, HttpStatus.BAD_REQUEST, "잘못된 접근입니다"),
    MISSING_REQUEST_PARAMETER(40001, HttpStatus.BAD_REQUEST, "필수 요청 파라미터가 누락되었습니다."),
    INVALID_PARAMETER_FORMAT(40002, HttpStatus.BAD_REQUEST, "요청에 유효하지 않은 인자 형식입니다."),
    BAD_REQUEST_JSON(40003, HttpStatus.BAD_REQUEST, "잘못된 JSON 형식입니다."),
    MISMATCH_TEAM(40004, HttpStatus.BAD_REQUEST, "질문자와 답변자의 팀이 일치하지 않습니다."),
    MISMATCH_LOGIN_USER_AND_MEMBER(40005, HttpStatus.BAD_REQUEST, "로그인 사용자와 멤버가 일치하지 않습니다."),
    MISMATCH_LOGIN_USER_AND_TEAM(40006, HttpStatus.BAD_REQUEST, "로그인 사용자는 해당 팀원이 아닙니다."),

    //401
    INVALID_HEADER_VALUE(40100, HttpStatus.UNAUTHORIZED, "올바르지 않은 헤더값입니다."),

    EXPIRED_TOKEN_ERROR(40101, HttpStatus.UNAUTHORIZED, "만료된 토큰입니다."),
    INVALID_TOKEN_ERROR(40102, HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    TOKEN_MALFORMED_ERROR(40103, HttpStatus.UNAUTHORIZED, "토큰이 올바르지 않습니다."),
    TOKEN_TYPE_ERROR(40104, HttpStatus.UNAUTHORIZED, "토큰 타입이 일치하지 않거나 비어있습니다."),
    TOKEN_UNSUPPORTED_ERROR(40105, HttpStatus.UNAUTHORIZED, "지원하지않는 토큰입니다."),
    TOKEN_GENERATION_ERROR(40106, HttpStatus.UNAUTHORIZED, "토큰 생성에 실패하였습니다."),
    TOKEN_UNKNOWN_ERROR(40107, HttpStatus.UNAUTHORIZED, "알 수 없는 토큰입니다."),
    LOGIN_FAILURE(40108, HttpStatus.UNAUTHORIZED, "로그인에 실패했습니다"),

    //402

    //403
    FORBIDDEN_ROLE(40300, HttpStatus.FORBIDDEN, "권한이 존재하지 않습니다."),

    //404
    NOT_FOUND_USER(40400, HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다."),
    NOT_FOUND_LOGIN_USER(40401, HttpStatus.NOT_FOUND, "로그인한 사용자를 찾을 수 없습니다"),
    NOT_FOUND_TEAM(40402, HttpStatus.NOT_FOUND, "존재하지 않는 팀입니다."),
    NOT_FOUND_MEMBER(40403, HttpStatus.NOT_FOUND, "존재하지 않는 멤버입니다."),
    NOT_FOUND_QUESTION(40404, HttpStatus.NOT_FOUND, "존재하지 않는 질문입니다."),
    NOT_FOUND_SCRUM(40405, HttpStatus.NOT_FOUND, "존재하지 않는 스크럼입니다."),


    //500
    INTERNAL_SERVER_ERROR(50000, HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류입니다")

    ;
    private final Integer code;
    private final HttpStatus httpStatus;
    private final String message;
}

package goormthon.team28.startup_valley.security.info;

import goormthon.team28.startup_valley.constants.Constants;
import goormthon.team28.startup_valley.dto.global.ExceptionDto;
import goormthon.team28.startup_valley.dto.response.JwtTokenDto;
import goormthon.team28.startup_valley.exception.ErrorCode;
import goormthon.team28.startup_valley.util.CookieUtil;
import jakarta.servlet.http.HttpServletResponse;
import net.minidev.json.JSONValue;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AuthenticationResponse {

    public static void makeLoginSuccessResponse(HttpServletResponse response, String domain, JwtTokenDto jwtTokenDto, Integer refreshExpiration) throws IOException {
        CookieUtil.addCookie(response, domain, Constants.ACCESS_COOKIE_NAME, jwtTokenDto.accessToken());
        CookieUtil.addSecureCookie(response, domain, Constants.REFRESH_COOKIE_NAME, jwtTokenDto.refreshToken(), refreshExpiration);

        makeSuccessResponse(response);

    }
    public static void makeSuccessResponse(HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpStatus.OK.value());

        Map<String, Object> body = new HashMap<>();
        body.put("success", "true");
        body.put("data", null);
        body.put("error", null);

        response.getWriter().write(JSONValue.toJSONString(body));
    }

    public static void makeFailureResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(errorCode.getHttpStatus().value());

        Map<String, Object> body= new HashMap<>();
        body.put("success", false);
        body.put("data", null);
        body.put("error", ExceptionDto.of(errorCode));

        response.getWriter().write(JSONValue.toJSONString(body));
    }
}
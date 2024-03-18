package goormthon.team28.startup_valley.util;

import goormthon.team28.startup_valley.exception.CommonException;
import goormthon.team28.startup_valley.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;

import java.util.Optional;
public class HeaderUtil {
    public static Optional<String> refineHeader (HttpServletRequest request, String headerName, String prefix){
        String headerValue = request.getHeader(headerName);
        if (!StringUtils.hasText(headerValue) || !headerValue.startsWith(prefix))
            throw new CommonException(ErrorCode.INVALID_HEADER_VALUE);
        return Optional.of(headerValue.substring(prefix.length()));
    }
}

package goormthon.team28.startup_valley.util;

import goormthon.team28.startup_valley.constants.Constants;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;

public class CookieUtil {

    public static void addCookie(HttpServletResponse response, String domain, String key, String value){
        ResponseCookie cookie = ResponseCookie.from(key, value)
                .path("/")
                .domain(domain)
                .httpOnly(false)
                .secure(true)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }

    public static void logoutCookie(HttpServletRequest request, HttpServletResponse response, String domain) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null)
            return;

        for (Cookie cookie : cookies) {
            boolean isAccessCookie = cookie.getName().equals(Constants.ACCESS_COOKIE_NAME);
            boolean isRefreshCookie = cookie.getName().equals(Constants.REFRESH_COOKIE_NAME);

            if (isAccessCookie || isRefreshCookie) {
                ResponseCookie tempCookie = ResponseCookie.from(cookie.getName(), cookie.getValue())
                        .path("/")
                        .domain(domain)
                        .secure(true)
                        .maxAge(0)
                        .httpOnly(isRefreshCookie)
                        .build();
                response.addHeader("Set-Cookie", tempCookie.toString());
            }
        }
    }

    public static void addSecureCookie(HttpServletResponse response, String domain, String key, String value, Integer maxAge){
        Cookie cookie = new Cookie(key, value);
        cookie.setPath("/");
        cookie.setDomain(domain);
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }

    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name){
        Cookie[] cookies = request.getCookies();
        if (cookies == null){
            return ;
        }
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(name)){
                cookie.setMaxAge(0);
                cookie.setPath("/");
                response.addCookie(cookie);
            }
        }
    }
}

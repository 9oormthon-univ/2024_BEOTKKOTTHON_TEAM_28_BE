package goormthon.team28.startup_valley.util;

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

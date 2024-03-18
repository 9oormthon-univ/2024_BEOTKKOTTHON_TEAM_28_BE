package goormthon.team28.startup_valley.interceptor.pre;

import goormthon.team28.startup_valley.annotation.UserId;
import goormthon.team28.startup_valley.exception.CommonException;
import goormthon.team28.startup_valley.exception.ErrorCode;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
@Component
public class UserIdArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(Long.class)
                && parameter.hasParameterAnnotation(UserId.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        final Object userId = webRequest.getAttribute("USER_ID", WebRequest.SCOPE_REQUEST);
        if (userId == null){
            throw new CommonException(ErrorCode.INVALID_HEADER_VALUE);
        }
        return Long.valueOf(userId.toString());
    }
}

package org.youcancook.gobong.global.resolver;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.youcancook.gobong.global.util.token.TokenManager;
import org.youcancook.gobong.global.util.token.exception.EmptyAuthorizationException;
import org.youcancook.gobong.global.util.token.exception.NotBearerGrantTypeException;

@Component
@RequiredArgsConstructor
public class LoginUserIdArgumentResolver implements HandlerMethodArgumentResolver {

    private final TokenManager tokenManager;
    private final String GRANT_TOKEN_TYPE = "Bearer";

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean hasUserIdAnnotation = parameter.hasParameterAnnotation(LoginUserId.class);
        boolean isLongClass = Long.class.isAssignableFrom(parameter.getParameterType());
        return hasUserIdAnnotation && isLongClass;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        validateHasAuthorizationHeader(authorizationHeader);
        validateGrantType(authorizationHeader);
        String token = extractToken(authorizationHeader);
        return tokenManager.getUserIdFromAccessToken(token);
    }

    private void validateHasAuthorizationHeader(String authorizationHeader) {
        if (!StringUtils.hasText(authorizationHeader)) {
            throw new EmptyAuthorizationException();
        }
    }

    private void validateGrantType(String authorizationHeader) {
        if (!authorizationHeader.startsWith(GRANT_TOKEN_TYPE)) {
            throw new NotBearerGrantTypeException();
        }
    }

    private String extractToken(String authorizationHeader) {
        return authorizationHeader.substring(GRANT_TOKEN_TYPE.length()).trim();
    }
}

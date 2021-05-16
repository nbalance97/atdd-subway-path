package wooteco.subway.member.method;

import org.springframework.web.servlet.HandlerInterceptor;
import wooteco.subway.member.infra.AuthorizationExtractor;
import wooteco.subway.member.service.AuthService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginInterceptor implements HandlerInterceptor {
    private final AuthService authService;

    public LoginInterceptor(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        final String accessToken = AuthorizationExtractor.extract(request);
        authService.validateToken(accessToken);
        return true;
    }
}

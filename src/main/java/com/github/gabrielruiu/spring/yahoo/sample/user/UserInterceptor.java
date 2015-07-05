package com.github.gabrielruiu.spring.yahoo.sample.user;

import com.github.gabrielruiu.springsocial.yahoo.api.Yahoo;
import org.springframework.security.core.userdetails.User;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Before a request is handled:
 * 1. sets the current User in the {@link SecurityContext} from a cookie, if present and the user is still connected to Yahoo.
 * 2. requires that the user sign-in if he or she hasn't already.
 * @author Keith Donald
 */
public final class UserInterceptor implements HandlerInterceptor {

    private final UsersConnectionRepository connectionRepository;
    private final UserCookieGenerator userCookieGenerator = new UserCookieGenerator();

    public UserInterceptor(UsersConnectionRepository connectionRepository) {
        this.connectionRepository = connectionRepository;
    }

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        rememberUser(request, response);
        handleSignOut(request, response);
        if (SecurityContext.userSignedIn() || requestForSignIn(request)) {
            return true;
        } else {
            return requireSignIn(request, response);
        }
    }

    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        SecurityContext.remove();
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    // internal helpers

    private void rememberUser(HttpServletRequest request, HttpServletResponse response) {
        String userId = userCookieGenerator.readCookieValue(request);
        if (userId == null) {
            return;
        }
        if (!userNotFound(userId)) {
            userCookieGenerator.removeCookie(response);
            return;
        }
        SecurityContext.setCurrentUser(new User(userId, "", null));
    }

    private void handleSignOut(HttpServletRequest request, HttpServletResponse response) {
        if (SecurityContext.userSignedIn() && request.getServletPath().startsWith("/signout")) {
            connectionRepository.createConnectionRepository(SecurityContext.getCurrentUser().getUsername()).removeConnections("yahoo");
            userCookieGenerator.removeCookie(response);
            SecurityContext.remove();
        }
    }

    private boolean requestForSignIn(HttpServletRequest request) {
        return request.getServletPath().startsWith("/signin");
    }

    private boolean requireSignIn(HttpServletRequest request, HttpServletResponse response) throws Exception {
        new RedirectView("/signin/yahoo", true).render(null, request, response);
        return false;
    }

    private boolean userNotFound(String userId) {
        // doesn't bother checking a local user database: simply checks if the userId is connected to Yahoo
        return connectionRepository.createConnectionRepository(userId).findPrimaryConnection(Yahoo.class) != null;
    }
}

package com.github.gabrielruiu.spring.yahoo.sample.user;

import org.springframework.security.core.userdetails.User;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.web.SignInAdapter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.NativeWebRequest;

import javax.servlet.http.HttpServletResponse;

/**
 * @author Gabriel Mihai Ruiu (gabriel.ruiu@mail.com)
 */
@Component
public class CookieSignInAdapter implements SignInAdapter {

    @Override
    public String signIn(String userId, Connection<?> connection, NativeWebRequest request) {
        SecurityContext.setCurrentUser(new User(userId, "", null));
        new UserCookieGenerator().addCookie(userId, request.getNativeResponse(HttpServletResponse.class));
        return null;
    }
}

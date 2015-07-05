package com.github.gabrielruiu.spring.yahoo.sample.user;


import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

/**
 * Simple SecurityContext that stores the currently signed-in connection in a thread local.
 * @author Keith Donald
 */
public final class SecurityContext {

    public static User getCurrentUser() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user == null) {
            throw new IllegalStateException("No user is currently signed in");
        }
        return user;
    }

    public static void setCurrentUser(User user) {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user, null));
    }

    public static boolean userSignedIn() {
        return SecurityContextHolder.getContext().getAuthentication() != null;
    }

    public static void remove() {
        SecurityContextHolder.getContext().setAuthentication(null);
    }

}

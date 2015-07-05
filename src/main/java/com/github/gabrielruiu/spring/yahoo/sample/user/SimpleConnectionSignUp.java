package com.github.gabrielruiu.spring.yahoo.sample.user;

import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionSignUp;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Gabriel Mihai Ruiu (gabriel.ruiu@mail.com)
 */
public final class SimpleConnectionSignUp implements ConnectionSignUp {

    private final AtomicLong userIdSequence = new AtomicLong();

    public String execute(Connection<?> connection) {
        return Long.toString(userIdSequence.incrementAndGet());
    }
}

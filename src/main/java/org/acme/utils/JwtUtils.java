package org.acme.utils;

import io.smallrye.jwt.algorithm.SignatureAlgorithm;
import io.smallrye.jwt.build.Jwt;
import jakarta.ejb.Singleton;

import java.time.Duration;
import java.util.Set;

public class JwtUtils {
    public static String generateToken(String username) {
        return Jwt.issuer("quynh")
                .subject(username)
                .groups(Set.of("User"))
                .expiresIn(3600)
                .sign();
    }
}

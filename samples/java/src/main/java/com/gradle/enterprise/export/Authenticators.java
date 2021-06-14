package com.gradle.enterprise.export;

import com.google.common.base.Strings;
import com.google.common.net.HttpHeaders;
import okhttp3.Authenticator;
import okhttp3.Credentials;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Authenticators {

    public static Authenticator bearerTokenOrBasic(String accesskey, String username, String password) {
        if (!Strings.isNullOrEmpty(accesskey)) {
            return Authenticators.bearerToken(accesskey);
        }
        if (!Strings.isNullOrEmpty(username) && !Strings.isNullOrEmpty(password)) {
            return Authenticators.basic(username, password);
        }
        throw new IllegalStateException("Neither Basic nor Bearer token authorization seems to be configured, please set the required environment variables as explained in the README.md.");
    }

    public static Authenticator basic(String username, String password) {
        return (route, response) -> {
            if (response.request().header(HttpHeaders.AUTHORIZATION) != null) {
                return null; // Give up, we've already attempted to authenticate.
            }

            return response.request().newBuilder()
                    .header(HttpHeaders.AUTHORIZATION, Credentials.basic(username, password))
                    .build();
        };
    }

    public static Authenticator bearerToken(String accessKey) {
        return (route, response) -> {
            if (response.request().header(HttpHeaders.AUTHORIZATION) != null) {
                return null; // Give up, we've already attempted to authenticate.
            }

            String encoded = Base64.getEncoder().encodeToString(accessKey.getBytes(StandardCharsets.UTF_8));

            return response.request().newBuilder()
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + encoded)
                    .build();
        };
    }
}

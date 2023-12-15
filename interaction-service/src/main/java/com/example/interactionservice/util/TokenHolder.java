package com.example.interactionservice.util;

public class TokenHolder {
    private static String token;

    public static String getToken() {
        return token;
    }

    public static void setToken(String newToken) {
        token = newToken;
    }
}

package com.meraki.website.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class RequestUser {

    private RequestUser() {
    }

    public static String userIdOrNull() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) return null;
        Object p = auth.getPrincipal();
        return (p instanceof String s) ? s : null;
    }

    public static String roleOrNull() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return null;
        return auth.getAuthorities().stream()
                .findFirst()
                .map(a -> a.getAuthority())
                .orElse(null);
    }
}


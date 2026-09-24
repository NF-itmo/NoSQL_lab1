package org.controllers;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.controllers.DTO.AuthRequest;
import org.controllers.DTO.RegisterRequest;
import org.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;

    public AuthController(
            UserService userService
    ) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(
            @RequestBody @Valid final AuthRequest request,
            final HttpServletResponse response
    ) {
        final String token = userService.login(
                request.getLogin(),
                request.getPassword()
        );
        response.addCookie(this.createJwtCookie(token));

        return ResponseEntity.ok(new HashMap<>());
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(
            @RequestBody @Valid final RegisterRequest request,
            final HttpServletResponse response
    ) {
        final String token = userService.register(
                request.getLogin(),
                request.getPassword(),
                request.getInviteCode()
        );
        response.addCookie(this.createJwtCookie(token));

        return ResponseEntity.ok(new HashMap<>());
    }

    @PostMapping("/validate")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, String>> validate() {
        return ResponseEntity.ok(new HashMap<>());
    }

    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, String>> logout(
            final HttpServletResponse response
    ) {
        response.addCookie(this.removeJwtCookie());
        return ResponseEntity.ok(new HashMap<>());
    }

    private Cookie createJwtCookie(String token) {
        Cookie jwtCookie = new Cookie("JWT", token);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(3600);

        return jwtCookie;
    }

    private Cookie removeJwtCookie() {
        Cookie jwtCookie = new Cookie("JWT", null);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(0);

        return jwtCookie;
    }
}

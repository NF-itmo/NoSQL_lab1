package org.services;

import lombok.extern.slf4j.Slf4j;
import org.exceptions.UnauthorizedException;
import org.jwt.JwtProvider;
import org.models.User;
import org.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserService {
    private final String inviteCode;
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;

    public UserService(
            @Value("${auth-service.invite-code}") final String inviteCode,
            JwtProvider jwtProvider,
            UserRepository userRepository
    ){
        this.inviteCode = inviteCode;
        this.jwtProvider = jwtProvider;
        this.userRepository = userRepository;
    }

    public String register(String login, String password, String inviteCode) {
        if (!inviteCode.equals(this.inviteCode)) {
            log.warn("User {} tryied to pass invalid invite code {}", login, inviteCode);
            throw new UnauthorizedException("Invalid invite code");
        }
        final String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        userRepository.create(
                User.builder()
                        .password(hashedPassword)
                        .username(login).build()
        );

        log.info("New user added {}", login);

        return jwtProvider.generateToken(login);
    }

    public String login(String login, String password) {
        final User user = userRepository.getByUsername(login)
                .orElseThrow(() -> new UnauthorizedException("User not found"));

        if (!BCrypt.checkpw(password, user.getPassword())) {
            throw new UnauthorizedException("Invalid credentials");
        }

        return jwtProvider.generateToken(login);
    }
}

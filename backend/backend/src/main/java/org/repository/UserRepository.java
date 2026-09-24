package org.repository;

import org.models.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// TBD
@Repository
public abstract class UserRepository {
    public abstract Optional<User> getByUsername(String username);
    public abstract void create(User user);
}

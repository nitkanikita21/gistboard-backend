package com.nitkanikita.notes.service;

import com.nitkanikita.notes.model.entity.User;
import com.nitkanikita.notes.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.text.MessageFormat;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserService implements ReactiveUserDetailsService {
    private final UserRepository repository;

    public Mono<User> save(User user) {
        return repository.save(user);
    }

    public Mono<User> create(User user) {
        return repository.existsByUsername(user.getUsername())
            .flatMap(exists -> {
                if (exists) {
                    return Mono.error(new RuntimeException("User with this username already exists"));
                }
                return repository.existsByEmail(user.getEmail());
            })
            .flatMap(exists -> {
                if (exists) {
                    return Mono.error(new RuntimeException("User with this email already exists"));
                }
                return save(user);
            });
    }

    public Mono<User> getByUsername(String username) {
        return repository.findByUsername(username)
            .switchIfEmpty(Mono.error(new UsernameNotFoundException("User not found")));
    }

    public Mono<User> getById(Long id) {
        return repository.findById(id)
            .switchIfEmpty(Mono.error(new UsernameNotFoundException("User not found")));
    }

    public Mono<User> getCurrentUser() {
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        return getByUsername(username);
    }

    public Mono<User> registerOrUpdateUser(OAuth2User oAuth2User) {
        return registerOrUpdateUser(
            oAuth2User.getAttribute("username"),
            oAuth2User.getAttribute("email"),
            String.format(
                "https://cdn.discordapp.com/avatars/%s/%s.webp?size=512",
                (String)oAuth2User.getAttribute("id"),
                (String)oAuth2User.getAttribute("avatar")
            )
        );
    }

    public Mono<User> registerOrUpdateUser(String username, String email, String avatarUrl) {
        return repository.findByUsername(username)
            .flatMap(Mono::just)
            .switchIfEmpty(
                save(
                    User.builder()
                        .username(username)
                        .email(email)
                        .role(User.Role.USER)
                        .avatarUrl(avatarUrl)
                        .build()
                )
            );
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return getByUsername(username).flatMap(u -> Mono.just((UserDetails)u));
    }
}

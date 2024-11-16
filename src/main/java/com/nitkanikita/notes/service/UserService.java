package com.nitkanikita.notes.service;

import com.google.common.hash.Hashing;
import com.nitkanikita.notes.model.dto.response.UserDto;
import com.nitkanikita.notes.model.entity.User;
import com.nitkanikita.notes.repository.UserRepository;
import io.vavr.control.Option;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserService implements UserDetailsService {

    private final UserRepository repository;

    @Transactional
    public User save(User user) {
        return repository.save(user);
    }

    @Transactional
    public User create(User user) {
        if (repository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("User with this username already exists");
        }
        if (repository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("User with this email already exists");
        }
        return save(user);
    }

    public User getByUsername(String username) {
        return repository.findByUsername(username)
            .getOrElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public User getById(Long id) {
        return getByIdOption(id)
            .getOrElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public Option<User> getByIdOption(Long id) {
        return Option.ofOptional(repository.findById(id));
    }

    public User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return getByUsername(username);
    }

    @Transactional
    public User registerOrUpdateUser(OAuth2User oAuth2User) {
        return registerOrUpdateUser(
            oAuth2User.getAttribute("username"),
            oAuth2User.getAttribute("email"),
            String.format(
                "https://cdn.discordapp.com/avatars/%s/%s.webp?size=512",
                oAuth2User.getAttribute("id"),
                oAuth2User.getAttribute("avatar")
            )
        );
    }

    @Transactional
    public User registerOrUpdateUser(String username, String email, String avatarUrl) {
        return repository.findByUsername(username)
            .getOrElse(() -> save(
                User.builder()
                    .username(username)
                    .email(email)
                    .role(User.Role.ROLE_USER)
                    .avatarUrl(avatarUrl)
                    .build()
            ));
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = getByUsername(username);
        return user;
    }

    public String identifyAnonymousUser(String userAgent, String address) {

        return Hashing.murmur3_128()
            .hashString(address + ":" + userAgent, StandardCharsets.UTF_8)
            .toString();
    }

    public UserDto convertToDto(User user) {
        return UserDto.builder()
            .username(user.getUsername())
            .role(user.getRole())
            .avatarUrl(user.getAvatarUrl())
            .build();
    }

}

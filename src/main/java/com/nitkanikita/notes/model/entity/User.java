package com.nitkanikita.notes.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;


@Entity
@Table(name = "users")  // використовується name="users" замість @Table("users") з R2DBC
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails {

    public enum Role {
        ROLE_USER,
        ROLE_MODERATOR,
        ROLE_ADMIN
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // додано для автоінкременту ID
    private Long id;

    @Column(name = "avatar")  // використовується name="avatar"
    private String avatarUrl;

    @Column(name = "username")  // використовується name="username"
    private String username;

    @Column(name = "email")  // використовується name="email"
    private String email;

    @Enumerated(EnumType.STRING)  // додається для коректного зберігання значень enum
    @Column(name = "role")  // використовується name="role"
    private Role role;

    @OneToMany(mappedBy = "author") // Це зв'язок з Note
    private List<Article> articles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
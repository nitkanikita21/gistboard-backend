package com.nitkanikita.notes.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Entity
@Table(name = "users")  // використовується name="users" замість @Table("users") з R2DBC
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails {
    public enum Role {
        USER,
        MODERATOR,
        ADMIN
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

    @Singular()
    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    private Set<Role> roles;

    @OneToMany(mappedBy = "author") // Це зв'язок з Note
    private List<Article> articles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
            .map(role -> new SimpleGrantedAuthority(role.toString()))
            .collect(Collectors.toList());
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
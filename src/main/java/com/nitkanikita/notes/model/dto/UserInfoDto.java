package com.nitkanikita.notes.model.dto;

import com.nitkanikita.notes.model.entity.User;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class UserInfoDto {

    private final String username;
    private final String email;
    private final User.Role role;
    private final String avatarUrl;

    public UserInfoDto(User user) {
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.role = user.getRole();
        this.avatarUrl = user.getAvatarUrl();
    }
}

package com.nitkanikita.notes.model.dto.response;

import com.nitkanikita.notes.model.entity.User;
import lombok.*;

import java.util.Collection;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {

    private Long id;
    private String username;
    private Collection<User.Role> roles;
    private String avatarUrl;

}

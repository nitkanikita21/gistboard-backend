package com.nitkanikita.notes.model.dto.response;

import com.nitkanikita.notes.model.entity.User;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {

    private String username;
    private User.Role role;
    private String avatarUrl;

}

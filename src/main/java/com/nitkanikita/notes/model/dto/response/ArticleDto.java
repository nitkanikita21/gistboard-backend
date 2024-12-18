package com.nitkanikita.notes.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ArticleDto {
    private Long id;

    private String title;
    private String content;

    private UserDto author;
    private LocalDateTime createdAt;
    private Long views;
    private boolean isPrivate;
}

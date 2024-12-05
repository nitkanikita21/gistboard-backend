package com.nitkanikita.notes.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;

@Data
@RequiredArgsConstructor
public class UpdateArticleDto {
    @Size(min = 1, max = 36)
    @Nullable
    private final String title;

    @Size(min = 1, max = 10_000)
    @Nullable
    private final String content;

    @Nullable
    private final Boolean isPrivate;
}

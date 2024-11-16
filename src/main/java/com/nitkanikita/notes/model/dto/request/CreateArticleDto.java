package com.nitkanikita.notes.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class CreateArticleDto {
    @NotBlank
    @Size(min = 1, max = 36)
    private final String title;

    @NotBlank
    @Size(min = 1, max = 10_000)
    private final String content;
}

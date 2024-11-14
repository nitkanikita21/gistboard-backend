package com.nitkanikita.notes.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("notes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Note {
    @Id
    private Long id;
    private String name;
    private String content;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("views")
    private Long views;

    public Note(String name, String content) {
        this.name = name;
        this.content = content;
        this.createdAt = LocalDateTime.now();
        this.views = 0L;
    }
}
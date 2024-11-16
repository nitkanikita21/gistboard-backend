package com.nitkanikita.notes.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "notes")  // Назва таблиці в базі даних
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Автоматичне генерування значення для id
    private Long id;

    private String title;
    @Lob
    private String content;

    @Column(name = "created_at")  // Для співвідношення з полем у таблиці
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    private Long views = 0L;

    @ManyToOne
    @JoinColumn(name = "user_id") // Це вказує на зовнішній ключ
    private User author; // Посилання на користувача (автора)
}
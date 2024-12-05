package com.nitkanikita.notes.repository;

import com.nitkanikita.notes.model.entity.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    Page<Article> findAllBy(Pageable pageable);

    @Query("SELECT a FROM Article a WHERE a.author.id = :userId")
    Page<Article> findAllByUser(long userId, Pageable pageable);

    @Query("SELECT a FROM Article a WHERE a.isPrivate = :isPrivate")
    Page<Article> findAllByPrivate(boolean isPrivate, Pageable pageable);

    @Modifying
    @Query("UPDATE Article a SET a.views = a.views + 1 WHERE a.id = :id")
    void incrementViewCount(@Param("id") Long id);
}
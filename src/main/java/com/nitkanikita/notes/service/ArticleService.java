package com.nitkanikita.notes.service;

import com.nitkanikita.notes.model.dto.request.CreateArticleDto;
import com.nitkanikita.notes.model.dto.request.UpdateArticleDto;
import com.nitkanikita.notes.model.dto.response.ArticleDto;
import com.nitkanikita.notes.model.entity.Article;
import com.nitkanikita.notes.model.entity.User;
import com.nitkanikita.notes.repository.ArticleRepository;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ArticleService {

    private final UserService userService;
    private final ArticleRepository articleRepository;


    @Transactional
    public ArticleDto create(CreateArticleDto createDto) {
        return convertToDto(articleRepository.save(convertFromDto(createDto)));
    }

    @Transactional
    public void incrementView(Long id) {
        articleRepository.incrementViewCount(id);
    }

    @Transactional
    public Page<ArticleDto> findAllByUser(Pageable pageable, String query, User user) {
        Page<Article> articles = articleRepository.findAllByUser(user.getId(), pageable);
        return getArticleDtosByQuery(pageable, query, articles);
    }


    @Transactional
    public Page<ArticleDto> findAll(Pageable pageable, String query) {
        Page<Article> articles = articleRepository.findAllByPrivate(false, pageable);
        return getArticleDtosByQuery(pageable, query, articles);
    }

    @Transactional
    public Option<ArticleDto> findById(Long id) {
        return Option.ofOptional(articleRepository.findById(id).map(this::convertToDto));
    }

    @Transactional
    public void update(Long id, UpdateArticleDto updateDto) {
        Article article = articleRepository.findById(id).orElseThrow(() -> new RuntimeException("Article not found"));

        if (!canEditArticle(article, userService.getCurrentUser())) {
            throw new SecurityException("You do not have permission to edit this article");
        }

        if (updateDto.getIsPrivate() != null) article.setPrivate(updateDto.getIsPrivate());
        if (updateDto.getTitle() != null) article.setTitle(updateDto.getTitle());
        if (updateDto.getContent() != null) article.setContent(updateDto.getContent());

        articleRepository.save(article);
    }

    @Transactional
    public void delete(Long id) {
        Article article = articleRepository.findById(id).orElseThrow(() -> new RuntimeException("Article not found"));

        if (!canEditArticle(article, userService.getCurrentUser())) {
            throw new SecurityException("You do not have permission to edit this article");
        }

        articleRepository.delete(article);
    }

    private boolean canEditArticle(Article article, User user) {
        // Якщо користувач є автором або має роль ADMIN
        return article.getAuthor().getId().equals(user.getId()) || user.getRoles().contains(User.Role.ADMIN);
    }

    @NotNull
    private Page<ArticleDto> getArticleDtosByQuery(Pageable pageable, String query, Page<Article> articles) {
        String[] splitQuery = query.split("\\W+");

        List<Article> filteredArticles = articles
            .getContent()
            .stream()
            .filter(article ->
                Arrays.stream(splitQuery)
                    .anyMatch((s) ->
                        article.getContent().contains(s) || article.getTitle().contains(s)
                    )
            ).toList();

        Page<Article> filteredPage = new PageImpl<>(filteredArticles, pageable, articles.getTotalElements());

        return filteredPage.map(this::convertToDto);
    }

    private ArticleDto convertToDto(Article article) {
        return ArticleDto.builder()
            .title(article.getTitle())
            .content(article.getContent())
            .author(userService.convertToDto(article.getAuthor()))
            .createdAt(article.getCreatedAt())
            .id(article.getId())
            .views(article.getViews())
            .isPrivate(article.isPrivate())
            .build();
    }

    private Article convertFromDto(CreateArticleDto article) {
        return Article.builder()
            .title(article.getTitle())
            .content(article.getContent())
            .author(userService.getCurrentUser())
            .createdAt(LocalDateTime.now())
            .isPrivate(article.isPrivate())
            .build();
    }
}

package com.nitkanikita.notes.service;

import com.nitkanikita.notes.model.dto.request.CreateArticleDto;
import com.nitkanikita.notes.model.dto.response.ArticleDto;
import com.nitkanikita.notes.model.entity.Article;
import com.nitkanikita.notes.repository.ArticleRepository;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

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
    public Page<ArticleDto> findAll(Pageable pageable) {
        return articleRepository.findAllBy(pageable)
            .map(this::convertToDto);
    }

    @Transactional
    public Option<ArticleDto> findById(Long id) {
        return Option.ofOptional(
            articleRepository.findById(id)
                .map(this::convertToDto)
        );
    }

    private ArticleDto convertToDto(Article article) {
        return ArticleDto.builder()
            .title(article.getTitle())
            .content(article.getContent())
            .author(userService.convertToDto(article.getAuthor()))
            .createdAt(article.getCreatedAt())
            .id(article.getId())
            .views(article.getViews())
            .build();
    }

    private Article convertFromDto(CreateArticleDto article) {
        return Article.builder()
            .title(article.getTitle())
            .content(article.getContent())
            .author(userService.getCurrentUser())
            .createdAt(LocalDateTime.now())
            .build();

    }
}

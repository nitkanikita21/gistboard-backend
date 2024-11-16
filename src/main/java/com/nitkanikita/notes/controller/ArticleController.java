package com.nitkanikita.notes.controller;

import com.nitkanikita.notes.model.dto.request.CreateArticleDto;
import com.nitkanikita.notes.model.dto.response.ArticleDto;
import com.nitkanikita.notes.repository.ArticleRepository;
import com.nitkanikita.notes.service.ArticleService;
import com.nitkanikita.notes.service.ArticleViewService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/article")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class ArticleController {

    private final ArticleRepository articleRepository;
    private final ArticleService articleService;
    private final ArticleViewService articleViewService;

    @GetMapping()
    public Page<ArticleDto> getAll(Pageable pageable) {
        return articleService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public ArticleDto getById(@PathVariable Long id) {
        return articleService.findById(id).getOrElseThrow(() -> new RuntimeException("Note not found"));
    }

    @PostMapping("/new")
    @PreAuthorize("isAuthenticated()")
    public ArticleDto create(@RequestBody CreateArticleDto createArticleDto) {
        return articleService.create(createArticleDto);
    }

    @PostMapping("/{id}/view")
    public ResponseEntity<?> view(@PathVariable Long id, HttpServletRequest request) {
        String userAgent = request.getHeader(HttpHeaders.USER_AGENT);
        String ipAddress = request.getRemoteAddr();

        if(articleViewService.processPageView(id, userAgent, ipAddress)) {
            articleService.incrementView(id);
        }

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteById(@PathVariable Long id) {
        articleRepository.deleteById(id);
    }
}
package com.nitkanikita.notes.service;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ArticleViewService {

    private static final Duration VIEW_EXPIRATION = Duration.ofSeconds(30);
    private final Map<Long, Map<String, LocalDateTime>> pageViews = new ConcurrentHashMap<>();
    private final UserService userService;

    public ArticleViewService(UserService userService) {
        this.userService = userService;
    }

    /**
     * Записує перегляд сторінки та повертає true, якщо перегляд було зараховано,
     * і false, якщо перегляд не можна записати (через короткий інтервал часу).
     *
     * @param pageId    Ідентифікатор сторінки
     * @param userAgent Агент користувача
     * @param ip        IP-адреса користувача
     * @return true, якщо перегляд було зараховано, false — якщо перегляд уже зараховано нещодавно.
     */
    public boolean processPageView(Long pageId, String userAgent, String ip) {
        String userHash = userService.identifyAnonymousUser(userAgent, ip);

        Map<String, LocalDateTime> userViews = pageViews.computeIfAbsent(pageId, k -> new ConcurrentHashMap<>());

        LocalDateTime lastViewTime = userViews.get(userHash);
        if (lastViewTime != null && Duration.between(lastViewTime, LocalDateTime.now()).compareTo(VIEW_EXPIRATION) <= 0) {
            return false;
        }

        userViews.put(userHash, LocalDateTime.now());
        return true;
    }

}
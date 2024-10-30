package com.ldubgd.botforuni.services;

import com.ldubgd.botforuni.domain.StatementCache;
import com.ldubgd.botforuni.domain.TelegramUserCache;
import com.ldubgd.botforuni.repositories.StatementCacheRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class StatementCacheService {

    private final StatementCacheRepository statementCacheRepository;

    @Autowired
    public StatementCacheService(StatementCacheRepository statementCacheRepository) {
        this.statementCacheRepository = statementCacheRepository;
    }

    /**
     * Видаляє всі записи StatementCache для користувача за його ID.
     *
     * @param id Телеграм ID користувача.
     */
    public void removeAllByUserId(Long id) {
        log.info("Починається видалення всіх записів StatementCache для користувача з Telegram ID: {}", id);
        statementCacheRepository.removeAllById(id);
        log.info("Успішно видалено всі записи StatementCache для користувача з Telegram ID: {}", id);
    }

    /**
     * Генерує новий StatementCache для користувача.
     *
     * @param telegramUserCache Кеш користувача з Telegram.
     * @param typeOfStatement Тип заяви (довідки), що генерується.
     * @return Згенерований StatementCache.
     */
    public StatementCache generateStatement(TelegramUserCache telegramUserCache, String typeOfStatement) {
        log.info("Генерується новий StatementCache для користувача з Telegram ID: {} та типом заяви: {}", telegramUserCache.getTelegramId(), typeOfStatement);
        StatementCache statementCache = new StatementCache();
        statementCache.setId(telegramUserCache.getTelegramId());
        statementCache.setTypeOfStatement(typeOfStatement);
        statementCache.setTelegramUserCache(telegramUserCache);
        log.info("Успішно згенеровано StatementCache для користувача з Telegram ID: {}", telegramUserCache.getTelegramId());
        return statementCache;
    }
}

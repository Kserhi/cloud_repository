package com.ldubgd.botforuni.services;

import com.ldubgd.botforuni.domain.enums.Position;
import com.ldubgd.botforuni.domain.TelegramUserCache;
import com.ldubgd.botforuni.repositories.TelegramUserCacheRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class TelegramUserService {

    private final TelegramUserCacheRepository telegramUserCacheRepository;

    @Autowired
    public TelegramUserService(TelegramUserCacheRepository telegramUserCacheRepository) {
        this.telegramUserCacheRepository = telegramUserCacheRepository;
    }

    /**
     * Зберігає TelegramUserCache у базі даних.
     *
     * @param telegramUserCache Об'єкт TelegramUserCache для збереження.
     */
    public void save(TelegramUserCache telegramUserCache) {
        telegramUserCacheRepository.save(telegramUserCache);
        log.info("TelegramUserCache з ID: {} успішно збережено", telegramUserCache.getTelegramId());
    }

    /**
     * Отримує TelegramUserCache або створює новий, якщо не знайдено.
     *
     * @param telegramId ID Telegram користувача.
     * @return Об'єкт TelegramUserCache.
     */
    public TelegramUserCache getOrGenerate(Long telegramId) {
        log.info("Отримання або генерація TelegramUserCache для ID: {}", telegramId);

        return findById(telegramId)
                .orElseGet(() -> {
                    TelegramUserCache telegramUserCache = generateNewTelegramUser(telegramId);
                    save(telegramUserCache);
                    log.info("Новий TelegramUserCache з ID: {} створено і збережено", telegramId);
                    return telegramUserCache;
                });
    }

    /**
     * Знаходить TelegramUserCache за ID.
     *
     * @param telegramId ID Telegram користувача.
     * @return Optional з TelegramUserCache.
     */
    public Optional<TelegramUserCache> findById(Long telegramId) {
        log.info("Пошук TelegramUserCache за ID: {}", telegramId);
        return telegramUserCacheRepository.findById(telegramId);
    }

    /**
     * Генерує новий TelegramUserCache.
     *
     * @param telegramId ID Telegram користувача.
     * @return Новий TelegramUserCache.
     */
    private TelegramUserCache generateNewTelegramUser(Long telegramId) {
        log.info("Генерація нового TelegramUserCache для ID: {}", telegramId);
        return new TelegramUserCache(telegramId, null, Position.NONE, null);
    }

    /**
     * Зберігає ID повідомлення для Telegram користувача.
     *
     * @param chatId    ID чату.
     * @param massageId ID повідомлення.
     */
    public void saveMassageId(Long chatId, Integer massageId) {
        log.info("Оновлення ID повідомлення для чату з ID: {}", chatId);
        Optional<TelegramUserCache> telegramUserCacheOptional = findById(chatId);

        if (telegramUserCacheOptional.isPresent()) {
            TelegramUserCache telegramUserCache = telegramUserCacheOptional.get();
            telegramUserCache.setMassageId(massageId);
            save(telegramUserCache);
            log.info("ID повідомлення для чату з ID: {} успішно оновлено на {}", chatId, massageId);
        } else {
            log.warn("TelegramUserCache для чату з ID: {} не знайдено", chatId);
        }
    }
}

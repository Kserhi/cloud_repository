package com.ldubgd.botforuni.services;

import com.ldubgd.botforuni.domain.enums.Position;
import com.ldubgd.botforuni.domain.Statement;
import com.ldubgd.botforuni.domain.StatementInfo;
import com.ldubgd.botforuni.domain.TelegramUserCache;
import com.ldubgd.botforuni.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class UpdateUserStatusService {

    @Autowired
    private TelegramUserService telegramUserService;

    @Autowired
    private SendMessageService sendMessageService;

    @Autowired
    private StatementInfoService statementInfoService;

    @Autowired
    private ScheduledExecutorService scheduler;

    private Runnable task;

    @PostConstruct
    public void init() {
        task = () -> {
            try {
                sendNotificationAboutStatementStatus();
            } catch (Exception e) {
                log.error("Проблеми із сповіщенням про статус заявки: ", e);
            }
        };
        // Планування завдання кожні 12 годин
        scheduler.scheduleAtFixedRate(
                task,
                0,
                Constants.TIMETOSTATEMENTUPDATE,
                TimeUnit.SECONDS);
    }

    @PreDestroy
    public void destroy() {
        scheduler.shutdown();
        log.info("Планувальник завдань зупинено.");
    }

    private void sendNotificationAboutStatementStatus() {
        List<StatementInfo> infoList = statementInfoService.getReadyStatement();
        List<StatementInfo> readyInfoList = new ArrayList<>();

        if (!infoList.isEmpty()) {
            log.info("Знайдено {} нових заявок для оновлення статусу.", infoList.size());

            infoList.forEach(statementInfo -> {
                try {
                    Statement statement = statementInfo.getStatement();
                    log.debug("Обробка заяви з id: {}", statement.getId());

                    TelegramUserCache telegramUser = telegramUserService
                            .findById(statement.getTelegramId())
                            .orElseThrow(() -> new IllegalArgumentException("Користувача не знайдено: " + statement.getTelegramId()));

                    log.info("Знайдено користувача з id: {}", telegramUser.getTelegramId());

                    if (telegramUser.getPosition() == Position.NONE) {
                        log.info("Відправка повідомлення для користувача з id: {}", telegramUser.getTelegramId());

                        if (statementInfoService.checkFileExistence(statement.getId())) {
                            sendMessageService.sendInfoAboutReadyStatementWithFile(statement);
                            log.info("Повідомлення з файлом надіслано для заяви з id: {}", statement.getId());
                        } else {
                            sendMessageService.sendInfoAboutReadyStatement(statement);
                            log.info("Повідомлення без файлу надіслано для заяви з id: {}", statement.getId());
                        }
                        readyInfoList.add(statementInfo);
                    } else {
                        log.info("Користувач з id: {} вже має позицію.", telegramUser.getTelegramId());
                    }
                } catch (Exception e) {
                    log.error("Помилка під час відправки повідомлення про готовність заяви для користувача", e);
                }
            });

            // Оновлюємо статус для готових заявок
            if (!readyInfoList.isEmpty()) {
                readyInfoList.forEach(statementInfo -> statementInfo.setReady(true));
                statementInfoService.saveAll(readyInfoList);
                log.info("Статус готових заявок оновлено для {} заяв.", readyInfoList.size());
            } else {
                log.info("Немає готових заявок для оновлення статусу.");
            }
        } else {
            log.info("Немає нових заявок для оновлення статусу.");
        }
    }

}

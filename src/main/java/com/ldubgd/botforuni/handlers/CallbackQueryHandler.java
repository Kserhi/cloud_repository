package com.ldubgd.botforuni.handlers;

import com.ldubgd.botforuni.domain.enums.Position;
import com.ldubgd.botforuni.keybords.Keyboards;
import com.ldubgd.botforuni.utils.Constants;
import com.ldubgd.botforuni.domain.*;
import com.ldubgd.botforuni.services.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

@Slf4j
@Component
public class CallbackQueryHandler implements Handler<CallbackQuery> {

    private final TelegramUserService telegramUserService;
    private final StatementService statementService;
    private final StatementCacheService statementCacheService;
    private final SendMessageService sendMessageService;
    private final StatementInfoService statementInfoService;

    @Autowired
    public CallbackQueryHandler(TelegramUserService telegramUserService,
                                StatementService statementService,
                                StatementCacheService statementCacheService,
                                SendMessageService sendMessageService,
                                StatementInfoService statementInfoService) {
        this.telegramUserService = telegramUserService;
        this.statementService = statementService;
        this.statementCacheService = statementCacheService;
        this.sendMessageService = sendMessageService;
        this.statementInfoService = statementInfoService;
    }

    @Override
    public void choose(CallbackQuery callbackQuery) {
        Message message = callbackQuery.getMessage();
        Long telegramId = message.getChatId();
        String callbackData = callbackQuery.getData();

        log.info("Обробка callback запиту від користувача з ID: {}, callbackData: {}", telegramId, callbackData);

        TelegramUserCache telegramUserCache = telegramUserService.getOrGenerate(telegramId);

        switch (callbackData) {
            case "/menu" -> handleMenuSelection(telegramId);
            case "choose_statement" -> handleChooseStatement(telegramId);
            case "statements" -> handleUserStatements(telegramUserCache);
            case "statementForMilitaryOfficer" -> handleStatementForMilitaryOfficer(telegramUserCache);
            case "statementForStudy" -> handleStatementForStudy(telegramUserCache);
            case "statementForm9" ->handleStatementFrom9(telegramUserCache);
            case "confirm" -> handleConfirmation(telegramUserCache);
            case "cancel" -> handleCancellation(telegramUserCache);
            default ->
                    log.warn("Отримано невідомий callbackData: {} від користувача з ID: {}", callbackData, telegramId);
        }
    }

    private void handleMenuSelection(Long telegramId) {
        log.info("Користувач з ID: {} вибрав меню", telegramId);
        sendMessageService.sendMessage(telegramId, Constants.MENU, Keyboards.menuKeyboard());
    }

    private void handleChooseStatement(Long telegramId) {
        log.info("Користувач з ID: {} вибрав пункт 'Вибрати заяву'", telegramId);
        sendMessageService.sendMessage(telegramId, Constants.CHOOSESTATEMENT, Keyboards.chooseStatementKeyboard());
    }

    private void handleUserStatements(TelegramUserCache telegramUserCache) {
        Long telegramId = telegramUserCache.getTelegramId();
        log.info("Користувач з ID: {} вибрав перегляд своїх заяв", telegramId);

        List<Statement> statements = statementService.getAllUserStatements(telegramId);

        if (statements.isEmpty()) {
            log.info("У користувача з ID: {} немає жодної зареєстрованої заявки", telegramId);
            sendMessageService.sendMessage(telegramId, "У вас немає жодної зареєстрованої заявки", Keyboards.linkToMenuKeyboard());
        } else {
            log.info("У користувача з ID: {} знайдено {} заяв(и)", telegramId, statements.size());
            Statement lastStatement = statements.remove(0);
            statements.forEach(statement -> sendMessageService.sendMessage(telegramId, statement.toString()));
            sendMessageService.sendMessage(telegramId, lastStatement.toString(), Keyboards.linkToMenuKeyboard());
        }
    }

    private void handleStatementForMilitaryOfficer(TelegramUserCache telegramUserCache) {
        Long telegramId = telegramUserCache.getTelegramId();
        log.info("Користувач з ID: {} вибрав пункт 'Заява для військомату'", telegramId);

        StatementCache statementCache = statementCacheService.generateStatement(telegramUserCache, Constants.STATEMENTFORMILITARI);

        telegramUserCache.setPosition(Position.INPUT_USER_NAME);
        telegramUserCache.setStatementCache(statementCache);
        telegramUserService.save(telegramUserCache);

        sendMessageService.sendMessage(telegramId, "Введіть свій ПІБ:");
        log.debug("Збережено новий кеш заяви для військової кафедри для користувача з ID: {}", telegramId);
    }

    private void handleStatementForStudy(TelegramUserCache telegramUserCache) {
        Long telegramId = telegramUserCache.getTelegramId();
        log.info("Користувач з ID: {} вибрав пункт 'Заява для навчання'", telegramId);

        StatementCache statementCache = statementCacheService.generateStatement(telegramUserCache, Constants.STATEMENTFORSTUDY);

        telegramUserCache.setPosition(Position.INPUT_USER_NAME);
        telegramUserCache.setStatementCache(statementCache);
        telegramUserService.save(telegramUserCache);

        sendMessageService.sendMessage(telegramId, "Введіть свій ПІБ:");
        log.debug("Збережено новий кеш заяви для навчання для користувача з ID: {}", telegramId);
    }

    private void handleStatementFrom9(TelegramUserCache telegramUserCache) {
        Long telegramId = telegramUserCache.getTelegramId();
        log.info("Користувач з ID: {} вибрав пункт 'Заява форма 9'", telegramId);

        StatementCache statementCache = statementCacheService.generateStatement(telegramUserCache, Constants.STATEMENTFORM9);

        telegramUserCache.setPosition(Position.INPUT_USER_NAME);
        telegramUserCache.setStatementCache(statementCache);
        telegramUserService.save(telegramUserCache);

        sendMessageService.sendMessage(telegramId, "Введіть свій ПІБ:");
        log.debug("Збережено новий кеш заяви форма9 з ID: {}", telegramId);
    }

    private void handleConfirmation(TelegramUserCache telegramUserCache) {
        Long telegramId = telegramUserCache.getTelegramId();
        log.info("Користувач з ID: {} підтвердив реєстрацію заяви", telegramId);

        StatementCache statementCache = telegramUserCache.getStatementCache();
        if (statementCache == null) {
            log.error("Кеш заяви відсутній для користувача з ID: {}. Підтвердження неможливе.", telegramId);
            sendMessageService.sendMessage(telegramId, "Виникла помилка. Будь ласка, спробуйте ще раз.");
            return;
        }

        Statement statement = statementService.mapStatement(statementCache);
        StatementInfo statementInfo = statementInfoService.generate(statement);

        statement.setStatementInfo(statementInfo);
        statementService.save(statement);

        statementCacheService.removeAllByUserId(telegramId);

        telegramUserCache.setStatementCache(null);
        telegramUserCache.setPosition(Position.NONE);
        telegramUserService.save(telegramUserCache);

        sendMessageService.sendMessage(telegramId, "Реєстрація пройшла успішно❗", Keyboards.linkToMenuKeyboard());
        log.info("Заяву користувача з ID: {} успішно зареєстровано", telegramId);
    }

    private void handleCancellation(TelegramUserCache telegramUserCache) {
        Long telegramId = telegramUserCache.getTelegramId();
        log.info("Користувач з ID: {} скасував реєстрацію заяви", telegramId);

        statementCacheService.removeAllByUserId(telegramId);

        telegramUserCache.setStatementCache(null);
        telegramUserCache.setPosition(Position.NONE);
        telegramUserService.save(telegramUserCache);

        sendMessageService.sendMessage(telegramId, "Реєстрацію довідки скасовано", Keyboards.linkToMenuKeyboard());
        log.info("Заява користувача з ID: {} успішно скасована", telegramId);

    }
}

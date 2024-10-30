package com.ldubgd.botforuni.handlers;

import com.ldubgd.botforuni.domain.enums.Position;
import com.ldubgd.botforuni.domain.StatementCache;
import com.ldubgd.botforuni.domain.TelegramUserCache;
import com.ldubgd.botforuni.keybords.Keyboards;
import com.ldubgd.botforuni.services.SendMessageService;
import com.ldubgd.botforuni.services.TelegramUserService;
import com.ldubgd.botforuni.utils.Constants;
import com.ldubgd.botforuni.utils.validator.ValidationResult;
import com.ldubgd.botforuni.utils.validator.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

@Slf4j
@Component
public class MessageHandler implements Handler<Message> {

    private final TelegramUserService telegramUserService;
    private final SendMessageService sendMessageService;

    @Autowired
    public MessageHandler(TelegramUserService telegramUserService, SendMessageService sendMessageService) {
        this.telegramUserService = telegramUserService;
        this.sendMessageService = sendMessageService;
    }

    @Override
    public void choose(Message message) {
        Long chatId = message.getChatId();
        log.info("Отримано повідомлення від користувача з ID: {}. Текст повідомлення: '{}'", chatId, message.getText());

        TelegramUserCache userCache = telegramUserService.getOrGenerate(chatId);
        Position currentPosition = userCache.getPosition();
        log.debug("Поточна позиція користувача з ID {}: {}", chatId, currentPosition);

        if (currentPosition != Position.NONE) {
            log.debug("Користувач з ID {} перебуває в позиції: {}. Розпочато обробку введення даних.", chatId, currentPosition);
            handleUserInputByPosition(currentPosition, message, userCache);
        } else if (message.hasText()) {
            log.debug("Користувач з ID {} не має активної позиції. Перевірка на команди.", chatId);
            handleCommands(message.getText(), chatId);
        }
    }

    private void handleUserInputByPosition(Position position, Message message, TelegramUserCache userCache) {
        Long telegramId = userCache.getTelegramId();
        StatementCache statementCache = userCache.getStatementCache();
        String messageText = message.getText();

        log.debug("Обробка даних користувача з ID {} для позиції: {}", telegramId, position);

        switch (position) {
            case INPUT_USER_NAME -> handleNameInput(messageText, telegramId, userCache, statementCache);
            case INPUT_USER_GROUP -> handleGroupInput(messageText, telegramId, userCache, statementCache);
            case INPUT_USER_YEAR -> handleYearBirthday(messageText, telegramId, userCache, statementCache);
            case INPUT_USER_FACULTY -> handleFacultyInput(messageText, telegramId, userCache, statementCache);
            case INPUT_USER_PHONE -> handlePhoneInput(message, telegramId, userCache, statementCache);
            default -> log.warn("Некоректна позиція користувача: {} для користувача з ID {}", position, telegramId);
        }
    }

    private void handleNameInput(String name, Long telegramId, TelegramUserCache userCache, StatementCache statementCache) {
        log.info("Користувач з ID: {} вводить своє ім'я: {}", telegramId, name);
        ValidationResult result = Validator.validateName(name);
        if (result.isValid()) {
            statementCache.setFullName(name);
            userCache.setPosition(Position.INPUT_USER_GROUP);
            userCache.setStatementCache(statementCache);
            telegramUserService.save(userCache);
            log.debug("Ім'я користувача з ID {} успішно збережено. Оновлено позицію на INPUT_USER_GROUP.", telegramId);
            sendMessageService.sendMessage(telegramId, "Введіть вашу групу (Наприклад: КН23c)⤵");
        } else {
            log.warn("Невдала валідація імені для користувача з ID {}. Невірний формат імені: {}", telegramId, name);
            sendValidationError(telegramId, result.message());
        }
    }

    private void handleGroupInput(String group, Long telegramId, TelegramUserCache userCache, StatementCache statementCache) {
        log.info("Користувач з ID: {} вводить групу: {}", telegramId, group);
        ValidationResult result = Validator.validateGroup(group);
        if (result.isValid()) {
            statementCache.setGroupe(group);
            userCache.setPosition(Position.INPUT_USER_YEAR);
            userCache.setStatementCache(statementCache);
            telegramUserService.save(userCache);
            log.debug("Групу користувача з ID {} успішно збережено. Оновлено позицію на INPUT_USER_YEAR.", telegramId);
            sendMessageService.sendMessage(telegramId, "Введіть ваш рік народження." +
                    " \nДата повинна бути у форматі день/місяць/рік \n" +
                    "наприклад :12/09/2024.");

        } else {
            log.warn("Невдала валідація групи для користувача з ID {}. Невірний формат групи: {}", telegramId, group);
            sendValidationError(telegramId, result.message());
        }
    }

    private void handleYearBirthday(String year, Long telegramId, TelegramUserCache userCache, StatementCache statementCache) {
        log.info("Користувач з ID: {} вводить рік народження: {}", telegramId, year);
        ValidationResult result = Validator.validateYear(year);
        if (result.isValid()) {
            statementCache.setYearBirthday(year);
            userCache.setPosition(Position.INPUT_USER_FACULTY);
            userCache.setStatementCache(statementCache);
            telegramUserService.save(userCache);
            log.debug("Рік народження користувача з ID {} успішно збережено. Оновлено позицію на INPUT_USER_FACULTY.", telegramId);
            sendMessageService.sendMessage(telegramId, "Виберіть ваш факультет", Keyboards.chooseFaculty());
        } else {
            log.warn("Невдала валідація року для користувача з ID {}. Невірний формат року: {}", telegramId, year);
            sendValidationError(telegramId, result.message());
        }
    }

    private void handleFacultyInput(String faculty, Long telegramId, TelegramUserCache userCache, StatementCache statementCache) {

        log.info("Користувач з ID: {} вибирає факультет: {}", telegramId, faculty);
        ValidationResult result=Validator.validateFaculty(faculty);
        if (result.isValid()) {
            statementCache.setFaculty(faculty);
            userCache.setPosition(Position.INPUT_USER_PHONE);
            userCache.setStatementCache(statementCache);
            telegramUserService.save(userCache);
            log.debug("Факультет користувача з ID {} успішно збережено. Оновлено позицію на INPUT_USER_PHONE.", telegramId);
            sendMessageService.sendMessage(telegramId, "Введіть ваш номер телефону⤵", Keyboards.keyboardRemove());
            sendMessageService.sendMessage(telegramId, "Нажміть, щоб поділитися контактом", Keyboards.phoneKeyboard());
        }else {
            log.warn("Невдала валідація факультету для користувача з ID {}. Невірний формат року: {}", telegramId, faculty);
            sendMessageService.sendMessage(telegramId, "Нажміть кнопку, щоб вибрати факультет", Keyboards.chooseFaculty());

        }

    }

    private void handlePhoneInput(Message message, Long telegramId, TelegramUserCache userCache, StatementCache statementCache) {
        if (message.hasContact()) {
            log.info("Користувач з ID: {} поділився номером телефону", telegramId);
            statementCache.setPhoneNumber(message.getContact().getPhoneNumber());
            userCache.setPosition(Position.CONFIRMATION);
            userCache.setStatementCache(statementCache);
            telegramUserService.save(userCache);
            log.debug("Номер телефону користувача з ID {} успішно збережено. Оновлено позицію на CONFIRMATION.", telegramId);
            sendMessageService.sendMessage(telegramId, statementCache.toString(), Keyboards.keyboardRemove());
            sendMessageService.sendMessage(telegramId, "Нажміть, щоб підтвердити дані", Keyboards.confirmationKeyboard());
        } else {
            log.warn("Користувач з ID: {} не надав контакт", telegramId);
            sendMessageService.sendMessage(telegramId, "Нажміть кнопку, щоб поділитися контактом", Keyboards.phoneKeyboard());
        }
    }

    private void handleCommands(String text, Long telegramId) {
        log.debug("Обробка команди '{}' для користувача з ID: {}", text, telegramId);
        switch (text) {
            case "/start" -> {
                log.info("Команда /start отримана від користувача з ID: {}", telegramId);
                sendMessageService.sendMessage(telegramId, Constants.STARTTEXT, Keyboards.starKeyboard());
            }
            case "/help" -> {
                log.info("Команда /help отримана від користувача з ID: {}", telegramId);
                sendMessageService.sendMessage(telegramId, Constants.HELP, Keyboards.helpMenu());
            }
            default -> log.warn("Невідома команда '{}' від користувача з ID: {}", text, telegramId);
        }
    }

    private void sendValidationError(Long telegramId, String errorMessage) {
        log.warn("Помилка валідації для користувача з ID {}. Повідомлення про помилку: '{}'", telegramId, errorMessage);
        sendMessageService.sendMessage(telegramId, errorMessage);
    }
}

package com.ldubgd.botforuni.services;

import com.ldubgd.botforuni.domain.Statement;
import com.ldubgd.botforuni.domain.TelegramUserCache;
import com.ldubgd.botforuni.domain.enums.LinkType;
import com.ldubgd.botforuni.keybords.Keyboards;
import com.ldubgd.botforuni.messageSender.MessageSender;
import com.ldubgd.utils.CryptoTool;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;

import java.util.Optional;

@Slf4j
@Service
public class SendMessageService {
    private final MessageSender messageSender;
    private final TelegramUserService telegramUserService;
    private final CryptoTool cryptoTool;

    public SendMessageService(MessageSender messageSender, TelegramUserService telegramUserService, CryptoTool cryptoTool) {
        this.messageSender = messageSender;
        this.telegramUserService = telegramUserService;
        this.cryptoTool = cryptoTool;
    }

    @Value("${link.address}")
    private String linkAddress;

    public void sendMessage(Long chatId, String text) {
        log.info("Відправка простого повідомлення до чату з ID: {}", chatId);
        SendMessage message = SendMessage.builder()
                .text(text)
                .chatId(String.valueOf(chatId))
                .build();
        messageSender.sendMessage(message);
    }

    public void sendMessage(Long chatId, String text, InlineKeyboardMarkup inlineKeyboard) {
        log.info("Відправка повідомлення з інлайн клавіатурою до чату з ID: {}", chatId);
        removePreviousKeyboard(chatId);
        Integer messageId = sendTextMessage(chatId, text, inlineKeyboard);
        telegramUserService.saveMassageId(chatId, messageId);
    }

    private Integer sendTextMessage(Long chatId, String text, InlineKeyboardMarkup inlineKeyboard) {
        log.info("Створення повідомлення з текстом для чату з ID: {}", chatId);
        SendMessage message = SendMessage.builder()
                .text(text)
                .chatId(String.valueOf(chatId))
                .replyMarkup(inlineKeyboard)
                .build();
        return messageSender.sendMessage(message);
    }

    private void removePreviousKeyboard(Long chatId) {
        Optional<TelegramUserCache> telegramUserCacheOptional = telegramUserService.findById(chatId);
        telegramUserCacheOptional.ifPresent(telegramUserCache -> {
            if (telegramUserCache.getMassageId() != null) {
                log.info("Видалення попередньої клавіатури для повідомлення з ID: {} у чаті з ID: {}", telegramUserCache.getMassageId(), chatId);
                deleteInlineKeyboard(chatId, telegramUserCache.getMassageId());
            } else {
                log.warn("Не знайдено ID повідомлення для користувача з ID: {}", chatId);
            }
        });
    }

    private void deleteInlineKeyboard(Long chatId, Integer messageId) {
        log.info("Видалення інлайн клавіатури для повідомлення з ID: {} у чаті з ID: {}", messageId, chatId);
        EditMessageReplyMarkup editMessageReplyMarkup = EditMessageReplyMarkup.builder()
                .chatId(String.valueOf(chatId))
                .messageId(messageId)
                .replyMarkup(null)
                .build();
        messageSender.sendMessage(editMessageReplyMarkup);
    }

    public void sendMessage(Long tgId, String text, ReplyKeyboardMarkup replyKeyboard) {
        log.info("Відправка повідомлення з клавіатурою відповіді до чату з ID: {}", tgId);
        SendMessage message = SendMessage.builder()
                .text(text)
                .chatId(String.valueOf(tgId))
                .replyMarkup(replyKeyboard)
                .build();
        messageSender.sendMessage(message);
    }

    public void sendInfoAboutReadyStatement(Statement statement) {
        log.info("Відправка інформації про готову довідку для користувача з ID: {}", statement.getTelegramId());
        sendMessage(
                statement.getTelegramId(),
                formatStatement(statement),
                Keyboards.linkToMenuKeyboard());
    }

    public void sendInfoAboutReadyStatementWithFile(Statement statement) {
        log.info("Відправка інформації про готову довідку для користувача з ID: {}", statement.getTelegramId());
        sendMessage(
                statement.getTelegramId(),
                formatStatement(statement)
        );

        log.info("Відправка посилання на файл з ID: {} для користувача з ID: {} ", statement.getId(), statement.getTelegramId());

        String fileUrl = generateLink(statement.getId(), LinkType.GET_DOC);
        sendFileUrl(statement.getTelegramId(), fileUrl);
    }

    private String generateLink(Long docId, LinkType linkType) {
        log.info("Генерація посилання для документа з ID: {} тип: {}", docId, linkType);
        var hash = cryptoTool.hashOf(docId);
        return "http://" + linkAddress + "/" + linkType + "?id=" + hash;
    }

    public void sendFileUrl(Long telegramId, String fileUrl) {
        log.info("Відправка URL файлу для користувача з ID: {}", telegramId);
        String textOfMassage = "Щоб завантажити файл із довідкою натисніть на [це посилання](" + fileUrl + ")";

        SendMessage message = SendMessage.builder()
                .text(textOfMassage)
                .chatId(String.valueOf(telegramId))
                .replyMarkup(Keyboards.linkToMenuKeyboard())
                .build();
        message.enableMarkdown(true);

        removePreviousKeyboard(telegramId);
        Integer messageId = messageSender.sendMessage(message);
        telegramUserService.saveMassageId(telegramId, messageId);
    }

    private String formatStatement(Statement statement) {
        return "📄 Ваша довідка готова:\n\n" +
                statement.toString();
    }

    public void sendMessage(Long chatId, String text, ReplyKeyboardRemove replyKeyboardRemove) {
        log.info("Відправка повідомлення з видаленням клавіатури replyKeyboard до чату з ID: {}", chatId);
        SendMessage message = SendMessage.builder()
                .text(text)
                .chatId(String.valueOf(chatId))
                .replyMarkup(replyKeyboardRemove)
                .build();
        messageSender.sendMessage(message);
    }
}


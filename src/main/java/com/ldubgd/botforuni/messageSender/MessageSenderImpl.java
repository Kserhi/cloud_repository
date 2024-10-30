package com.ldubgd.botforuni.messageSender;

import com.ldubgd.botforuni.TelegramBot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Service
public class MessageSenderImpl implements MessageSender {
    private TelegramBot telegramBot;

    @Override
    public Integer sendMessage(SendMessage sendMessage) {
        try {
            log.info("Відправка повідомлення користувачу з ID: {}", sendMessage.getChatId());
            return telegramBot.execute(sendMessage).getMessageId();
        } catch (TelegramApiException e) {
            log.error("Не вдалося надіслати повідомлення користувачу з ID: {}. Помилка: {}", sendMessage.getChatId(), e.getMessage());
            throw new RuntimeException("Помилка відправки повідомлення", e);
        }
    }

    public void sendMessage(EditMessageReplyMarkup editMessageReplyMarkup) {
        try {
            log.info("Оновлення повідомлення з ID: {} для користувача з ID: {}",
                    editMessageReplyMarkup.getMessageId(),
                    editMessageReplyMarkup.getChatId());
            telegramBot.execute(editMessageReplyMarkup);
        } catch (TelegramApiException e) {
            log.error("Не вдалося оновити повідомлення з ID: {} для користувача з ID: {}. Помилка: {}",
                    editMessageReplyMarkup.getMessageId(),
                    editMessageReplyMarkup.getChatId(),
                    e.getMessage());
            throw new RuntimeException("Помилка оновлення повідомлення", e);
        }
    }

    @Autowired
    public void setTelegramBot(@Lazy TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }
}

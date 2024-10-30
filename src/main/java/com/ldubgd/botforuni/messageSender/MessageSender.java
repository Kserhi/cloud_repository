package com.ldubgd.botforuni.messageSender;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;

public interface MessageSender {
    Integer sendMessage(SendMessage sendMessage);
    void sendMessage(EditMessageReplyMarkup editMessageReplyMarkup);
}

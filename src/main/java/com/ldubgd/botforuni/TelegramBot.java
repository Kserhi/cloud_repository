package com.ldubgd.botforuni;

import com.ldubgd.botforuni.processors.Processor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;


@Data
@EqualsAndHashCode(callSuper = false) // Або callSuper = false, залежно від ваших вимог
@Component
public class TelegramBot extends TelegramLongPollingBot {
    @Value("${telegram.bot.username}")
    private String botUsername;

    @Value("${telegram.bot.token}")
    private String botToken;

    @Autowired
    private Processor processor;


    @Override
    public void onUpdateReceived(Update update) {
        processor.process(update);
    }

}
package com.ldubgd.botforuni.keybords;
import com.ldubgd.botforuni.utils.Constants;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Keyboards {


    public static InlineKeyboardMarkup chooseStatementKeyboard() {
        //менюшка вибору запиту

        return InlineKeyboardMarkup.builder()
                .keyboardRow(
                        Collections.singletonList(
                                InlineKeyboardButton.builder()
                                        .text(Constants.STATEMENTFORSTUDY)
                                        .callbackData("statementForStudy")

                                        .build()
                        ))
                .keyboardRow(
                        Collections.singletonList(
                                InlineKeyboardButton.builder()
                                        .text(Constants.STATEMENTFORMILITARI)
                                        .callbackData("statementForMilitaryOfficer")
                                        .build()
                        )
                )
                .keyboardRow(
                        Collections.singletonList(
                                InlineKeyboardButton.builder()
                                        .text(Constants.STATEMENTFORM9)
                                        .callbackData("statementForm9")
                                        .build()
                        )
                )
                .keyboardRow(
                        Collections.singletonList(
                                InlineKeyboardButton.builder()
                                        .text("Головне меню")
                                        .callbackData("/menu").build()


                        )
                )
                .build();

    }

    public static InlineKeyboardMarkup menuKeyboard() {
        //менюшка вибору запиту


        return InlineKeyboardMarkup.builder()
                .keyboardRow(
                        Collections.singletonList(
                                InlineKeyboardButton.builder()
                                        .text("Замовити довідку")
                                        .callbackData("choose_statement")
                                        .build()
                        ))
                .keyboardRow(
                        Collections.singletonList(
                                InlineKeyboardButton.builder()
                                        .text("Переглянути мої довідки")
                                        .callbackData("statements")
                                        .build()
                        )
                )
                .build();

    }

    public static InlineKeyboardMarkup starKeyboard() {

        return InlineKeyboardMarkup.builder()
                .keyboardRow(
                        Collections.singletonList(
                                InlineKeyboardButton.builder()
                                        .text("❗Потрібна послуга деканату")
                                        .callbackData("/menu").build()


                        )

                )
                .build();


    }

    public static InlineKeyboardMarkup linkToMenuKeyboard() {


        return InlineKeyboardMarkup.builder()
                .keyboardRow(
                        Collections.singletonList(
                                InlineKeyboardButton.builder()
                                        .text("На головну❗")
                                        .callbackData("/menu").build()


                        )

                )
                .build();

    }

    public static InlineKeyboardMarkup helpMenu() {




        return InlineKeyboardMarkup.builder()
                .keyboardRow(
                        Collections.singletonList(
                                InlineKeyboardButton.builder()
                                        .text("Посилання")
                                        .url(Constants.URLFORHELLP)
                                        .build()
                        ))
                .keyboardRow(
                        Collections.singletonList(
                                InlineKeyboardButton.builder()
                                        .text("На головну❗")
                                        .callbackData("/menu").build()


                        )

                )
                .build();

    }

    public static InlineKeyboardMarkup confirmationKeyboard() {

        return InlineKeyboardMarkup.builder()
                .keyboardRow(
                        Collections.singletonList(
                                InlineKeyboardButton.builder()
                                        .text("Підтвердити✔")
                                        .callbackData("confirm")
                                        .build()
                        ))
                .keyboardRow(
                        Collections.singletonList(
                                InlineKeyboardButton.builder()
                                        .text("Скасувати❌")
                                        .callbackData("cancel")
                                        .build()
                        )
                )
                .build();

    }

    public static ReplyKeyboardMarkup phoneKeyboard() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();

        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);

        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow keyboardRow = new KeyboardRow();
        keyboardRow.add(KeyboardButton.builder().text("Номер телефону")
                .requestContact(true)
                .build());
        keyboardRows.add(keyboardRow);
        replyKeyboardMarkup.setKeyboard(keyboardRows);
        return replyKeyboardMarkup;
    }

    public static ReplyKeyboardMarkup  chooseFaculty(){
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();

        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);

        List<KeyboardRow> keyboardRows = new ArrayList<>();

        List<String> list=new ArrayList<>();
        list.add(Constants.FACULTYCIVILPROTECTION);
        list.add(Constants.FACULTYFIRETECHNOLOGYSAFETY);
        list.add(Constants.FACULTYPSYCHOLOGYSOCIALPROTECTION);
        list.add(Constants.INSTITUTEPOSTGRADUATEEDUCATION);
        list.add(Constants.ADJUNCTURE);
        list.add(Constants.TRAININGMETHODICALCENTER);

        list.forEach(string -> {
            KeyboardRow keyboardRow = new KeyboardRow();
            keyboardRow.add(new KeyboardButton(string));
            keyboardRows.add(keyboardRow);
        });

        replyKeyboardMarkup.setKeyboard(keyboardRows);
        return replyKeyboardMarkup;
    }

    public static ReplyKeyboardRemove keyboardRemove(){
        ReplyKeyboardRemove keyboardRemove = new ReplyKeyboardRemove();
        keyboardRemove.setRemoveKeyboard(true);
        return keyboardRemove;
    }

}

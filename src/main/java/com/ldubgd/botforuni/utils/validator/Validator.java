package com.ldubgd.botforuni.utils.validator;

import com.ldubgd.botforuni.utils.Constants;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Validator {


    private static final String NAME_PATTERN = "^[а-яА-ЯїЇіІєЄґҐ\\s]+$"; // Українські літери та пробіли
    private static final String GROUP_PATTERN = "^[А-Яа-я]{2}\\d{2}[А-Яа-я]$"; // Дві українські літери, дві цифри, одна українська літера
    private static final String DATE_PATTERN =  "^\\d{2}/\\d{2}/\\d{4}$";
    private static final int NAME_MIN_LENGTH = 2;
    private static final int NAME_MAX_LENGTH = 100;
    private static final int YEAR_MIN_VALUE = 1900;
    private static final int YEAR_MAX_VALUE = 2024;


    private static final List<String> VALID_FACULTIES = Arrays.asList(
            Constants.FACULTYCIVILPROTECTION,
            Constants.FACULTYFIRETECHNOLOGYSAFETY,
            Constants.FACULTYPSYCHOLOGYSOCIALPROTECTION,
            Constants.INSTITUTEPOSTGRADUATEEDUCATION,
            Constants.ADJUNCTURE,
            Constants.TRAININGMETHODICALCENTER
    );
    private static final Set<String> VALID_FACULTY_SET = new HashSet<>(VALID_FACULTIES);


    public static ValidationResult validateName(String name) {
        if (name.length() < NAME_MIN_LENGTH || name.length() > NAME_MAX_LENGTH) {
            return new ValidationResult(false, "Ім'я повинно містити від " + NAME_MIN_LENGTH + " до " + NAME_MAX_LENGTH + " символів.");
        }
        if (!name.matches(NAME_PATTERN)) {
            return new ValidationResult(false, "Ім'я може містити тільки українські літери та пробіли.");
        }
        return new ValidationResult(true, "Ім'я валідне.");
    }

    public static ValidationResult validateGroup(String group) {
        if (!group.matches(GROUP_PATTERN)) {
            return new ValidationResult(false, "Група повинна відповідати формату: дві літери, дві цифри та одна літера (наприклад, AB12c).");
        }
        return new ValidationResult(true, "Група валідна.");
    }

    public static ValidationResult validateYear(String date) {
        // Формат дати: день/місяць/рік
        if (!date.matches(DATE_PATTERN)) {
            return new ValidationResult(false, "Введіть ваш рік народження." +
                    " \nДата повинна бути у форматі день/місяць/рік \n" +
                    "наприклад :12/09/2024.");
        }

        String[] parts = date.split("/");
        int day = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        int year = Integer.parseInt(parts[2]);

        if (year < YEAR_MIN_VALUE || year > YEAR_MAX_VALUE) {
            return new ValidationResult(false, "Рік повинен бути в діапазоні від " + YEAR_MIN_VALUE + " до " + YEAR_MAX_VALUE + ".");
        }

        // Перевірка на коректність місяців і днів
        if (month < 1 || month > 12) {
            return new ValidationResult(false, "Місяць повинен бути в діапазоні від 1 до 12.");
        }
        if (day < 1 || day > 31) {
            return new ValidationResult(false, "День повинен бути в діапазоні від 1 до 31.");
        }
        if ((month == 4 || month == 6 || month == 9 || month == 11) && day > 30) {
            return new ValidationResult(false, "У цьому місяці не може бути більше 30 днів.");
        }
        if (month == 2) {
            boolean isLeapYear = (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
            if (day > 29 || (day == 29 && !isLeapYear)) {
                return new ValidationResult(false, "Лютий має максимум 28 днів, або 29 у високосний рік.");
            }
        }

        return new ValidationResult(true, "Дата валідна.");
    }



    public static ValidationResult validateFaculty(String faculty) {
        if (faculty == null || faculty.trim().isEmpty()) {
            return new ValidationResult(false, "Факультет не може бути порожнім.");
        }
        if (!VALID_FACULTY_SET.contains(faculty)) {
            return new ValidationResult(false, "Факультет не є дійсним. Введіть один з наступних факультетів: " + String.join(", ", VALID_FACULTIES) + ".");
        }
        return new ValidationResult(true, "Факультет валідний.");
    }


}

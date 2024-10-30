package com.ldubgd.botforuni.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Data
public class StatementCache {

    @Id
    @Column(name = "id")
    private Long id;


    private String fullName;
    private String yearBirthday;
    private String groupe;
    private String phoneNumber;
    private String faculty;
    private String typeOfStatement;


    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private TelegramUserCache telegramUserCache;


    @Override
    public String toString() {

        return "ПІБ: " + fullName + "\n" +
                "Група: " + groupe + "\n" +
                "Рік народження: " + yearBirthday + "\n" +
                "Факультет: " + faculty + "\n" +
                "Номер телефону: " + phoneNumber + "\n" +
                "Тип заявки: " + typeOfStatement + "\n";
    }

}

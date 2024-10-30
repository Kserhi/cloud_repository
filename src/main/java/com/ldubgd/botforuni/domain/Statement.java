package com.ldubgd.botforuni.domain;

import com.ldubgd.botforuni.domain.enums.StatementStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Data
@Table(name = "statement")
public class Statement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "full_name")
    private String fullName;

    @Column(name = "year_birthday")
    private String yearBirthday;

    @Column(name = "group_name")
    private String groupe;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "faculty")
    private String faculty;

    @Column(name = "type_of_statement")
    private String typeOfStatement;

    @Column(name = "telegram_id")
    private Long telegramId;

    @OneToOne(mappedBy = "statement", cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private StatementInfo statementInfo;


    @Override
    public String toString() {
        String status = (statementInfo != null && statementInfo.getStatementStatus() == StatementStatus.READY) ? "Готова" : "В обробці";
        return "ПІБ: " + fullName + "\n" +
                "Група: " + groupe + "\n" +
                "Рік народження: " + yearBirthday + "\n" +
                "Факультет: " + faculty + "\n" +
                "Номер телефону: " + phoneNumber + "\n" +
                "Тип заявки: " + typeOfStatement + "\n" +
                "Статус заявки: " + status;
    }


}

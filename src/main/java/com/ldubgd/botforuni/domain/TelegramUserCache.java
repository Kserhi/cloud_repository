package com.ldubgd.botforuni.domain;

import com.ldubgd.botforuni.domain.enums.Position;
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
@Table(name = "telegram_cache")
public class TelegramUserCache {

    @Id
    @Column(name = "id")
    private Long telegramId;

    @Column(name = "massage_id")
    private Integer massageId;


    @Enumerated(EnumType.STRING)
    @Column(name = "user_position")
    private Position position;

    @OneToOne(mappedBy = "telegramUserCache", cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private StatementCache statementCache;



}

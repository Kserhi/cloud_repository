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
@Table(name = "statement_info")
public class StatementInfo {

    @Id
    @Column(name = "id", nullable = false, unique = true)
    private Long statementId;

    @Column(name = "is_ready")
    private boolean isReady;

    @Column(name = "statement_status")
    @Enumerated(EnumType.STRING)
    private StatementStatus statementStatus;


    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private Statement statement;


}

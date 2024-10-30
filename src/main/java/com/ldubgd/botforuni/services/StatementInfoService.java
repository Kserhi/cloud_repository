package com.ldubgd.botforuni.services;

import com.ldubgd.botforuni.domain.Statement;
import com.ldubgd.botforuni.domain.StatementInfo;
import com.ldubgd.botforuni.domain.enums.StatementStatus;
import com.ldubgd.botforuni.repositories.StatementInfoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class StatementInfoService {

    private final StatementInfoRepository statementInfoRepository;

    @Autowired
    public StatementInfoService(StatementInfoRepository statementInfoRepository) {
        this.statementInfoRepository = statementInfoRepository;
    }

    /**
     * Генерує StatementInfo на основі Statement.
     *
     * @param statement Об'єкт Statement, на основі якого створюється StatementInfo.
     * @return Згенерований StatementInfo.
     */
    public StatementInfo generate(Statement statement) {
        log.info("Генерація StatementInfo для Statement з ID: {}", statement.getId());
        StatementInfo statementInfo = new StatementInfo(statement.getId(), false, StatementStatus.PENDING, statement);
        log.info("Успішно згенеровано StatementInfo з ID: {}", statementInfo.getStatementId());
        return statementInfo;
    }

    /**
     * Отримує список готових заявок (заявок, які мають статус 'готово').
     *
     * @return Список StatementInfo, де statusIsReady=true.
     */
    public List<StatementInfo> getReadyStatement() {
        log.info("Запит на отримання готових заявок");
        List<StatementInfo> readyStatements = statementInfoRepository.findWhereIsReadyFalse(StatementStatus.READY);
        log.info("Знайдено {} готових заявок", readyStatements.size());
        return readyStatements;
    }

    /**
     * Зберігає всі StatementInfo у базі даних.
     *
     * @param infoList Список StatementInfo для збереження.
     */
    public void saveAll(List<StatementInfo> infoList) {
        log.info("Запуск збереження {} StatementInfo", infoList.size());
        statementInfoRepository.saveAll(infoList);
        log.info("Успішно збережено {} StatementInfo", infoList.size());
    }


    public boolean checkFileExistence(Long id) {
        return statementInfoRepository.existsFileInfoById(id);
    }
}

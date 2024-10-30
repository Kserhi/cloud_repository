package com.ldubgd.botforuni.services;

import com.ldubgd.botforuni.domain.Statement;
import com.ldubgd.botforuni.domain.StatementCache;
import com.ldubgd.botforuni.repositories.StatementRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class StatementService {

    private final StatementRepository statementRepository;

    @Autowired
    public StatementService(StatementRepository statementRepository) {
        this.statementRepository = statementRepository;
    }

    /**
     * Зберігає Statement у базі даних.
     *
     * @param statement Об'єкт Statement для збереження.
     */
    public void save(Statement statement) {
        statementRepository.save(statement);
        log.info("Statement з ID: {} успішно збережено", statement.getId());
    }

    /**
     * Отримує всі Statement для конкретного Telegram користувача.
     *
     * @param telegramId ID Telegram користувача.
     * @return Список Statement для даного Telegram ID.
     */
    public List<Statement> getAllUserStatements(Long telegramId) {
        log.info("Отримання всіх Statement для Telegram ID: {}", telegramId);
        List<Statement> statements = statementRepository.findAllByTelegramId(telegramId);
        log.info("Знайдено {} Statement для Telegram ID: {}", statements.size(), telegramId);
        return statements;
    }

    public Statement mapStatement(StatementCache statementCache) {

        Statement statement = new Statement();
        statement.setFullName(statementCache.getFullName());
        statement.setYearBirthday(statementCache.getYearBirthday());
        statement.setGroupe(statementCache.getGroupe());
        statement.setPhoneNumber(statementCache.getPhoneNumber());
        statement.setFaculty(statementCache.getFaculty());
        statement.setTypeOfStatement(statementCache.getTypeOfStatement());
        statement.setTelegramId(statementCache.getId());

        return statement;
    }
}

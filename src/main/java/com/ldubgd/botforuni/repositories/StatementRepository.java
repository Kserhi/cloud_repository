package com.ldubgd.botforuni.repositories;

import com.ldubgd.botforuni.domain.Statement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StatementRepository extends JpaRepository<Statement,Long> {

    List<Statement> findAllByTelegramId(Long telegramId);




}
package com.ldubgd.botforuni.repositories;

import com.ldubgd.botforuni.domain.StatementCache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;

public interface StatementCacheRepository extends JpaRepository<StatementCache,Long> {
    @Transactional
    @Modifying
    @Query("DELETE FROM StatementCache sc WHERE sc.id = :id ")
    void removeAllById(@Param("id")Long id);
}

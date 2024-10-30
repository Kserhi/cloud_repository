package com.ldubgd.botforuni.repositories;

import com.ldubgd.botforuni.domain.StatementInfo;
import com.ldubgd.botforuni.domain.enums.StatementStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StatementInfoRepository extends JpaRepository<StatementInfo,Long> {



    @Query("SELECT s_i FROM StatementInfo s_i WHERE s_i.statementStatus = :status AND s_i.isReady = false")
    List<StatementInfo> findWhereIsReadyFalse(@Param("status") StatementStatus status);


    @Query(value = "SELECT COUNT(*) > 0 FROM file_info WHERE statement_id = :id", nativeQuery = true)
    boolean existsFileInfoById(@Param("id") Long id);

}
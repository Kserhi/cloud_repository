package com.ldubgd.botforuni.repositories;

import com.ldubgd.botforuni.domain.TelegramUserCache;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TelegramUserCacheRepository extends JpaRepository<TelegramUserCache,Long> {


}

package com.ruijie.rcos.rcdc.terminal.module.impl.quartz;

import com.ruijie.rcos.rcdc.terminal.module.impl.cache.GatherLogCache;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.GatherLogCacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;

/**
 * Description: 定时清除日志收集保存的缓存
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/11/6
 *
 * @author Jarman
 */
@Service
public class GatherLogQuartz {

    @Autowired
    private GatherLogCacheManager cacheManager;

    /**
     * 没10秒执行一次检查并清除过期的缓存
     */
    @Scheduled(cron = "0/10 * *  * * ?")
    public void checkAndCleanExpireCache() {
        Map<String, GatherLogCache> caches = cacheManager.getGatherLogCaches();
        caches.forEach((k, v) -> {
            long expire = v.getExpireTime();
            long now = Instant.now().toEpochMilli();
            if (now > expire) {
                caches.remove(k);
            }
        });
    }
}


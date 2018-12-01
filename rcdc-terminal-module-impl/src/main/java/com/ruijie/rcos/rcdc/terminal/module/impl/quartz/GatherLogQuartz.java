package com.ruijie.rcos.rcdc.terminal.module.impl.quartz;

import com.ruijie.rcos.rcdc.terminal.module.impl.cache.GatherLogCache;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.GatherLogCacheManager;
import com.ruijie.rcos.sk.base.quartz.QuartzTask;
import com.ruijie.rcos.sk.modulekit.api.isolation.GlobalUniqueBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Iterator;
import java.util.Map;

/**
 * Description: 定时清除日志收集保存的缓存
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/11/6
 *
 * @author Jarman
 */
@GlobalUniqueBean("gatherLogQuartz")
public class GatherLogQuartz implements QuartzTask {

    @Autowired
    private GatherLogCacheManager cacheManager;

    @Override
    public void execute() throws Exception {
        checkAndCleanExpireCache();
    }

    /**
     * 每10秒执行一次检查并清除过期的缓存
     */
    public void checkAndCleanExpireCache() {
        Map<String, GatherLogCache> caches = cacheManager.getGatherLogCaches();
        Iterator<Map.Entry<String, GatherLogCache>> it = caches.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, GatherLogCache> item = it.next();
            GatherLogCache logCache = item.getValue();
            long expire = logCache.getExpireTime();
            long now = Instant.now().toEpochMilli();
            if (now > expire) {
                it.remove();
            }
        }
    }
}


package com.ruijie.rcos.rcdc.terminal.module.impl.quartz;

import com.ruijie.rcos.rcdc.terminal.module.impl.cache.CollectLogCache;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.CollectLogCacheManager;
import com.ruijie.rcos.sk.base.quartz.QuartzTask;
import com.ruijie.rcos.sk.modulekit.api.isolation.GlobalUniqueBean;
import org.springframework.beans.factory.annotation.Autowired;

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
@GlobalUniqueBean("collectLogQuartz")
public class CollectLogQuartz implements QuartzTask {

    @Autowired
    private CollectLogCacheManager cacheManager;

    @Override
    public void execute() throws Exception {
        checkAndCleanExpireCache();
    }

    /**
     * 每10秒执行一次检查并清除过期的缓存
     */
    public void checkAndCleanExpireCache() {
        Map<String, CollectLogCache> caches = cacheManager.getCollectLogCaches();
        Iterator<Map.Entry<String, CollectLogCache>> it = caches.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, CollectLogCache> item = it.next();
            CollectLogCache logCache = item.getValue();
            long expire = logCache.getExpireTime();
            long now = Instant.now().toEpochMilli();
            if (now > expire) {
                it.remove();
            }
        }
    }
}


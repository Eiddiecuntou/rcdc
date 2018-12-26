package com.ruijie.rcos.rcdc.terminal.module.impl.quartz;

import com.ruijie.rcos.rcdc.terminal.module.impl.cache.CollectLogCache;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.CollectLogCacheManager;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.*;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/11/8
 *
 * @author Jarman
 */
@RunWith(JMockit.class)
public class CollectLogQuartzTest {

    @Tested
    private CollectLogQuartz collectLogQuartz;

    @Injectable
    private CollectLogCacheManager cacheManager;

    @Test
    public void checkAndCleanExpireCache() {
        Map<String, CollectLogCache> cacheMap = generateCaches();
        new Expectations() {{
            cacheManager.getCollectLogCaches();
            result = cacheMap;
        }};

        try {
            collectLogQuartz.checkAndCleanExpireCache();
            Assert.assertEquals(cacheMap.size(), 3);

        } catch (Exception e) {
            fail();
        }
    }

    private Map<String, CollectLogCache> generateCaches() {
        Map<String, CollectLogCache> caches = new ConcurrentHashMap<>();
        for (int i = 0; i < 5; i++) {
            CollectLogCache cache = new CollectLogCache();
            cache.setLogFileName("test" + 1);
            long millisecond = Instant.now().toEpochMilli();
            if (i % 2 == 0) {
                long expireTime = millisecond + 2000;
                cache.setExpireTime(expireTime);
            } else {
                long expireTime = millisecond - 2000;
                cache.setExpireTime(expireTime);
            }
            caches.put("key" + i, cache);

        }

        return caches;
    }


}
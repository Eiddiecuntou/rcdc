package com.ruijie.rcos.rcdc.terminal.module.impl.quartz;

import com.ruijie.rcos.rcdc.terminal.module.impl.cache.GatherLogCache;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.GatherLogCacheManager;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.Verifications;
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
public class GatherLogQuartzTest {

    @Tested
    private GatherLogQuartz gatherLogQuartz;

    @Injectable
    private GatherLogCacheManager cacheManager;

    @Test
    public void checkAndCleanExpireCache() {
        Map<String, GatherLogCache> cacheMap = generateCaches();
        new Expectations() {{
            cacheManager.getGatherLogCaches();
            result = cacheMap;
        }};

        try {
            gatherLogQuartz.checkAndCleanExpireCache();
            Assert.assertEquals(cacheMap.size(), 3);

        } catch (Exception e) {
            fail();
        }
    }

    private Map<String, GatherLogCache> generateCaches() {
        Map<String, GatherLogCache> caches = new ConcurrentHashMap<>();
        for (int i = 0; i < 5; i++) {
            GatherLogCache cache = new GatherLogCache();
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
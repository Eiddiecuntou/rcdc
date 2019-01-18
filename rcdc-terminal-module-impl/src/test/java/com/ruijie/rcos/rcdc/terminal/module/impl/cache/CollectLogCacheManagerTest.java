package com.ruijie.rcos.rcdc.terminal.module.impl.cache;

import static org.junit.Assert.fail;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.google.common.cache.Cache;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CollectLogStateEnums;
import mockit.Deencapsulation;
import mockit.Mock;
import mockit.MockUp;
import mockit.Tested;
import mockit.integration.junit4.JMockit;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/11/13
 *
 * @author Jarman
 */
@RunWith(JMockit.class)
public class CollectLogCacheManagerTest {

    @Tested
    private CollectLogCacheManager cacheManager;

    @Test
    public void testAddCache() {
        Cache<String, CollectLogCache> caches = Deencapsulation.getField(cacheManager, "COLLECT_LOG_CACHE");
        String terminalId = "123";
        try {
            cacheManager.addCache(terminalId);
        } catch (Exception e) {
            fail();
        }

        Assert.assertEquals(caches.size(), 1);
        Assert.assertEquals(caches.getIfPresent(terminalId).getState(), CollectLogStateEnums.DOING);
        caches.invalidateAll();
    }

    @Test
    public void testRemoveCache() {
        Cache<String, CollectLogCache> caches = Deencapsulation.getField(cacheManager, "COLLECT_LOG_CACHE");
        String terminalId = "123";
        CollectLogCache cache = new CollectLogCache();
        caches.put(terminalId, cache);
        try {
            cacheManager.removeCache(terminalId);
        } catch (Exception e) {
            fail();
        }
        Assert.assertEquals(caches.size(), 0);
    }

    @Test
    public void testUpdateState() {

        new MockUp<CollectLogCacheManager>() {
            @Mock
            public void updateState(String terminalId, CollectLogStateEnums state, String logFileName) {

            }
        };

        String terminalId = "123";
        try {
            cacheManager.updateState(terminalId, CollectLogStateEnums.FAILURE);
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testUpdateState2() {
        String terminalId = "123";
        try {
            Cache<String, CollectLogCache> caches = Deencapsulation.getField(cacheManager, "COLLECT_LOG_CACHE");
            CollectLogCache cache = new CollectLogCache();
            caches.put(terminalId, cache);
            cacheManager.updateState(terminalId, CollectLogStateEnums.FAILURE, "shine.zip");
            Assert.assertEquals(caches.size(), 1);
            Assert.assertEquals(caches.getIfPresent(terminalId).getState(), CollectLogStateEnums.FAILURE);

        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testGetCache() {
        try {
            String terminalId = "123";
            Cache<String, CollectLogCache> caches = Deencapsulation.getField(cacheManager, "COLLECT_LOG_CACHE");
            CollectLogCache cache = new CollectLogCache();
            caches.put(terminalId, cache);
            cacheManager.getCache(terminalId);
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testGetCollectLogCaches() {
        String terminalId = "123";
        Cache<String, CollectLogCache> caches = Deencapsulation.getField(cacheManager, "COLLECT_LOG_CACHE");
        CollectLogCache cache = new CollectLogCache();
        caches.put(terminalId, cache);
        try {
            Cache<String, CollectLogCache> result = cacheManager.getCollectLogCaches();
            Assert.assertEquals(result.size(), 1);
        } catch (Exception e) {
            fail();
        }
    }
}
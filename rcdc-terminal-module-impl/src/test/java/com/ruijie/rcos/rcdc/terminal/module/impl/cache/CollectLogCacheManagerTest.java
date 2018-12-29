package com.ruijie.rcos.rcdc.terminal.module.impl.cache;

import com.ruijie.rcos.rcdc.terminal.module.impl.enums.CollectLogStateEnums;
import mockit.*;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Map;

import static org.junit.Assert.fail;

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
        Map<String, CollectLogCache> caches = Deencapsulation.getField(cacheManager, "COLLECT_LOG_CACHE_MAP");
        String terminalId = "123";
        try {
            cacheManager.addCache(terminalId);
        } catch (Exception e) {
            fail();
        }

        Assert.assertEquals(caches.size(), 1);
        Assert.assertEquals(caches.get(terminalId).getState(), CollectLogStateEnums.DOING);
        caches.clear();
    }

    @Test
    public void testRemoveCache() {
        Map<String, CollectLogCache> caches = Deencapsulation.getField(cacheManager, "COLLECT_LOG_CACHE_MAP");
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
            Map<String, CollectLogCache> caches = Deencapsulation.getField(cacheManager, "COLLECT_LOG_CACHE_MAP");
            CollectLogCache cache = new CollectLogCache();
            caches.put(terminalId, cache);
            cacheManager.updateState(terminalId, CollectLogStateEnums.FAILURE, "shine.zip");
            Assert.assertEquals(caches.size(), 1);
            Assert.assertEquals(caches.get(terminalId).getState(), CollectLogStateEnums.FAILURE);

        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testGetCache() {
        try {
            String terminalId = "123";
            Map<String, CollectLogCache> caches = Deencapsulation.getField(cacheManager, "COLLECT_LOG_CACHE_MAP");
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
        Map<String, CollectLogCache> caches = Deencapsulation.getField(cacheManager, "COLLECT_LOG_CACHE_MAP");
        CollectLogCache cache = new CollectLogCache();
        caches.put(terminalId, cache);
        try {
            Map<String, CollectLogCache> result = cacheManager.getCollectLogCaches();
            Assert.assertEquals(result.size(), 1);
        } catch (Exception e) {
            fail();
        }
    }
}
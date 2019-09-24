package com.ruijie.rcos.rcdc.terminal.module.impl.cache;

import static org.junit.Assert.fail;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.google.common.cache.Cache;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbCollectLogStateEnums;
import mockit.Deencapsulation;
import mockit.Injectable;
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

    /**
     * 测试添加收集日志缓存
     */
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
        Assert.assertEquals(caches.getIfPresent(terminalId).getState(), CbbCollectLogStateEnums.DOING);
        caches.invalidateAll();
    }

    /**
     * 测试移除收集日志缓存
     */
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

    /**
     * 测试更新收集日志状态
     */
    @Test
    public void testUpdateState() {

        new MockUp<CollectLogCacheManager>() {
            @Mock
            public void updateState(String terminalId, CbbCollectLogStateEnums state, String logFileName) {

            }
        };

        String terminalId = "123";
        try {
            cacheManager.updateState(terminalId, CbbCollectLogStateEnums.FAILURE);
        } catch (Exception e) {
            fail();
        }
    }

    /**
     * 测试更新收集日志状态
     */
    @Test
    public void testUpdateState2() {
        String terminalId = "123";
        try {
            Cache<String, CollectLogCache> caches = Deencapsulation.getField(cacheManager, "COLLECT_LOG_CACHE");
            CollectLogCache cache = new CollectLogCache();
            caches.put(terminalId, cache);
            cacheManager.updateState(terminalId, CbbCollectLogStateEnums.FAILURE, "shine.zip");
            Assert.assertEquals(caches.size(), 1);
            Assert.assertEquals(caches.getIfPresent(terminalId).getState(), CbbCollectLogStateEnums.FAILURE);

        } catch (Exception e) {
            fail();
        }
    }

    /**
     * 测试更新收集日志状态,cache为空
     * 
     * @param caches mock Caches
     */
    @Test
    public void testUpdateState2CacheIsNull(@Injectable Cache<String, CollectLogCache> caches) {
        String terminalId = "2222";
        try {
            cacheManager.updateState(terminalId, CbbCollectLogStateEnums.FAILURE, "shine.zip");
            Cache<String, CollectLogCache> caches1 = Deencapsulation.getField(cacheManager, "COLLECT_LOG_CACHE");
            Assert.assertEquals(1, caches1.size());
            Assert.assertEquals(CbbCollectLogStateEnums.FAILURE, caches1.getIfPresent(terminalId).getState());
            cacheManager.removeCache(terminalId);

        } catch (Exception e) {
            fail();
        }
    }

    /**
     * 测试获取收集日志
     */
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

    /**
     * 测试获取收集日志集合
     */
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

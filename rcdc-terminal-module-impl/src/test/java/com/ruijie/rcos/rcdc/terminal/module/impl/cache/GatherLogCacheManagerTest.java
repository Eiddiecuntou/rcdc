package com.ruijie.rcos.rcdc.terminal.module.impl.cache;

import com.ruijie.rcos.rcdc.terminal.module.impl.enums.GatherLogStateEnums;
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
public class GatherLogCacheManagerTest {

    @Tested
    private GatherLogCacheManager cacheManager;

    /**
     * 测试添加收集日志缓存
     */
    @Test
    public void testAddCache() {
        Map<String, GatherLogCache> caches = Deencapsulation.getField(cacheManager, "GATHER_LOG_CACHE_MAP");
        String terminalId = "123";
        try {
            cacheManager.addCache(terminalId);
        } catch (Exception e) {
            fail();
        }

        Assert.assertEquals(caches.size(), 1);
        Assert.assertEquals(caches.get(terminalId).getState(), GatherLogStateEnums.DOING);
        caches.clear();
    }

    /**
     *  测试移除收集日志缓存
     */
    @Test
    public void testRemoveCache() {
        Map<String, GatherLogCache> caches = Deencapsulation.getField(cacheManager, "GATHER_LOG_CACHE_MAP");
        String terminalId = "123";
        GatherLogCache cache = new GatherLogCache();
        caches.put(terminalId, cache);
        try {
            cacheManager.removeCache(terminalId);
        } catch (Exception e) {
            fail();
        }
        Assert.assertEquals(caches.size(), 0);
    }

    /**
     * 测试更新收集日志缓存
     */
    @Test
    public void testUpdateState() {

        new MockUp<GatherLogCacheManager>() {
            @Mock
            public void updateState(String terminalId, GatherLogStateEnums state, String logFileName) {

            }
        };

        String terminalId = "123";
        try {
            cacheManager.updateState(terminalId, GatherLogStateEnums.FAILURE);
        } catch (Exception e) {
            fail();
        }
    }

    /**
     * 测试更新日志收集缓存状态到收集失败
     */
    @Test
    public void testUpdateState2() {
        String terminalId = "123";
        try {
            Map<String, GatherLogCache> caches = Deencapsulation.getField(cacheManager, "GATHER_LOG_CACHE_MAP");
            GatherLogCache cache = new GatherLogCache();
            caches.put(terminalId, cache);
            cacheManager.updateState(terminalId, GatherLogStateEnums.FAILURE, "shine.zip");
            Assert.assertEquals(caches.size(), 1);
            Assert.assertEquals(caches.get(terminalId).getState(), GatherLogStateEnums.FAILURE);

        } catch (Exception e) {
            fail();
        }
    }

    /**
     * 测试获取日志收集缓存
     */
    @Test
    public void testGetCache() {
        try {
            String terminalId = "123";
            Map<String, GatherLogCache> caches = Deencapsulation.getField(cacheManager, "GATHER_LOG_CACHE_MAP");
            GatherLogCache cache = new GatherLogCache();
            caches.put(terminalId, cache);
            cacheManager.getCache(terminalId);
        } catch (Exception e) {
            fail();
        }
    }

    /**
     * 测试获取日志收集缓存集合
     */
    @Test
    public void testGetGatherLogCaches() {
        String terminalId = "123";
        Map<String, GatherLogCache> caches = Deencapsulation.getField(cacheManager, "GATHER_LOG_CACHE_MAP");
        GatherLogCache cache = new GatherLogCache();
        caches.put(terminalId, cache);
        try {
            Map<String, GatherLogCache> result = cacheManager.getGatherLogCaches();
            Assert.assertEquals(result.size(), 1);
        } catch (Exception e) {
            fail();
        }
    }
}
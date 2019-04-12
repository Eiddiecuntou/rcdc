package com.ruijie.rcos.rcdc.terminal.module.impl.cache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalComponentUpdateListDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.TerminalPlatformEnums;
import com.ruijie.rcos.sk.base.test.ThrowExceptionTester;
import mockit.Deencapsulation;
import mockit.Tested;
import mockit.integration.junit4.JMockit;

/**
 * 
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年1月23日
 * 
 * @author ls
 */
@RunWith(JMockit.class)
public class ComponentUpdateListCacheManagerTest {

    @Tested
    private ComponentUpdateListCacheManager manager;

    /**
     * 测试addCache,参数为空
     * 
     * @throws Exception 异常
     */
    @Test
    public void testAddCacheArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> manager.addCache(null, new CbbTerminalComponentUpdateListDTO()),
                "platform can not be null");
        ThrowExceptionTester.throwIllegalArgumentException(() -> manager.addCache(TerminalPlatformEnums.VDI, null), "updatelist can not be null");
        assertTrue(true);
    }

    /**
     * 测试addCache,
     */
    @Test
    public void testAddCache() {
        CbbTerminalComponentUpdateListDTO updatelist = new CbbTerminalComponentUpdateListDTO();
        manager.addCache(TerminalPlatformEnums.VDI, updatelist);
        Map<TerminalPlatformEnums, CbbTerminalComponentUpdateListDTO> caches = Deencapsulation.getField(manager, "UPDATE_LIST_CACHE_MAP");
        assertEquals(1, caches.size());
        assertEquals(updatelist, caches.get(TerminalPlatformEnums.VDI));
        caches.remove(TerminalPlatformEnums.VDI);
    }

    /**
     * 测试removeCache,参数为空
     * 
     * @throws Exception 异常
     */
    @Test
    public void testRemoveCacheArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> manager.removeCache(null), "platform can not be null");
        assertTrue(true);
    }

    /**
     * 测试removeCache,
     */
    @Test
    public void testRemoveCache() {
        CbbTerminalComponentUpdateListDTO updatelist = new CbbTerminalComponentUpdateListDTO();
        Map<TerminalPlatformEnums, CbbTerminalComponentUpdateListDTO> caches = Deencapsulation.getField(manager, "UPDATE_LIST_CACHE_MAP");
        caches.put(TerminalPlatformEnums.VDI, updatelist);
        manager.removeCache(TerminalPlatformEnums.VDI);
        assertEquals(0, caches.size());
    }

    /**
     * 测试getCache,参数为空
     * 
     * @throws Exception 异常
     */
    @Test
    public void testGetCacheArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> manager.getCache(null), "platform can not be null");
        assertTrue(true);
    }

    /**
     * 测试getCache,
     */
    @Test
    public void testGetCache() {
        CbbTerminalComponentUpdateListDTO updatelist = new CbbTerminalComponentUpdateListDTO();
        Map<TerminalPlatformEnums, CbbTerminalComponentUpdateListDTO> caches = Deencapsulation.getField(manager, "UPDATE_LIST_CACHE_MAP");
        caches.put(TerminalPlatformEnums.VDI, updatelist);
        assertEquals(updatelist, manager.getCache(TerminalPlatformEnums.VDI));
        caches.remove(TerminalPlatformEnums.VDI);
    }

    /**
     * 测试getUpdateListCaches
     */
    @Test
    public void testGetUpdateListCaches() {
        Map<TerminalPlatformEnums, CbbTerminalComponentUpdateListDTO> caches = Deencapsulation.getField(manager, "UPDATE_LIST_CACHE_MAP");
        assertEquals(caches, manager.getUpdateListCaches());
    }
}

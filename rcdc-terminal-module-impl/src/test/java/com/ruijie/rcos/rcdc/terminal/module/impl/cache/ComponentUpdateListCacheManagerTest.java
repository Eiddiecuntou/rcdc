package com.ruijie.rcos.rcdc.terminal.module.impl.cache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.util.Map;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalTypeEnums;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.updatelist.CbbLinuxVDIUpdateListDTO;
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
    private VDITerminalUpdateListCacheManager manager;

    /**
     * 测试addCache,参数为空
     * 
     * @throws Exception 异常
     */
    @Test
    public void testAddArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> manager.add(null, new CbbLinuxVDIUpdateListDTO()),
                "platform can not be null");
        ThrowExceptionTester.throwIllegalArgumentException(() -> manager.add(CbbTerminalTypeEnums.LINUX, null), "updatelist can not be null");
        assertTrue(true);
    }

    /**
     * 测试addCache,
     */
    @Test
    public void testAdd() {
        CbbLinuxVDIUpdateListDTO updatelist = new CbbLinuxVDIUpdateListDTO();
        manager.add(CbbTerminalTypeEnums.LINUX, updatelist);
        Map<TerminalPlatformEnums, CbbLinuxVDIUpdateListDTO> caches = Deencapsulation.getField(manager, "UPDATE_LIST_CACHE_MAP");
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
    public void testRemoveArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> manager.remove(null), "platform can not be null");
        assertTrue(true);
    }

    /**
     * 测试removeCache,
     */
    @Test
    public void testRemove() {
        CbbLinuxVDIUpdateListDTO updatelist = new CbbLinuxVDIUpdateListDTO();
        Map<CbbTerminalTypeEnums, CbbLinuxVDIUpdateListDTO> caches = Deencapsulation.getField(manager, "UPDATE_LIST_CACHE_MAP");
        caches.put(CbbTerminalTypeEnums.LINUX, updatelist);
        manager.remove(CbbTerminalTypeEnums.LINUX);
        assertEquals(0, caches.size());
    }

    /**
     * 测试getCache,参数为空
     * 
     * @throws Exception 异常
     */
    @Test
    public void testGetArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> manager.get(null), "platform can not be null");
        assertTrue(true);
    }

    /**
     * 测试getCache,
     */
    @Test
    public void testGet() {
        CbbLinuxVDIUpdateListDTO updatelist = new CbbLinuxVDIUpdateListDTO();
        Map<TerminalPlatformEnums, CbbLinuxVDIUpdateListDTO> caches = Deencapsulation.getField(manager, "UPDATE_LIST_CACHE_MAP");
        caches.put(TerminalPlatformEnums.VDI, updatelist);
        assertEquals(updatelist, manager.get(CbbTerminalTypeEnums.LINUX));
        caches.remove(TerminalPlatformEnums.VDI);
    }

    /**
     * 测试getUpdateListCaches
     */
    @Test
    public void testGetUpdateListCaches() {
        Map<TerminalPlatformEnums, CbbLinuxVDIUpdateListDTO> caches = Deencapsulation.getField(manager, "UPDATE_LIST_CACHE_MAP");
        assertEquals(caches, manager.getUpdateListCache());
    }
}

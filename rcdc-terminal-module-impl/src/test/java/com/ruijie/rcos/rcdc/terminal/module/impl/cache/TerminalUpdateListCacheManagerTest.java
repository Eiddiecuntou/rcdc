package com.ruijie.rcos.rcdc.terminal.module.impl.cache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.updatelist.CbbCommonUpdatelistDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.updatelist.CbbLinuxVDIUpdateListDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.updatelist.CbbWinAppUpdateListDTO;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.TerminalTypeEnums;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import com.ruijie.rcos.sk.base.test.ThrowExceptionTester;

import mockit.Deencapsulation;
import mockit.Tested;

/**
 * 
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年1月23日
 * 
 * @author ls
 */
@RunWith(SkyEngineRunner.class)
public class TerminalUpdateListCacheManagerTest {

    @Tested
    private TerminalUpdateListCacheManager manager;

    /**
     * 测试addCache,参数为空
     * 
     * @throws Exception 异常
     */
    @Test
    public void testAddArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> manager.add(null, new CbbWinAppUpdateListDTO()),
                "terminalType can not be null");
        ThrowExceptionTester.throwIllegalArgumentException(() -> manager.add(TerminalTypeEnums.APP_WINDOWS, null),
                "updatelist can not be null");
        assertTrue(true);
    }

    /**
     * 测试addCache,
     */
    @Test
    public void testAdd() {
        CbbWinAppUpdateListDTO updatelist = new CbbWinAppUpdateListDTO();
        manager.add(TerminalTypeEnums.VDI_LINUX, updatelist);
        Map<TerminalTypeEnums, CbbLinuxVDIUpdateListDTO> caches =
                Deencapsulation.getField(manager, "UPDATE_LIST_CACHE_MAP");
        assertEquals(updatelist, caches.get(TerminalTypeEnums.VDI_LINUX));
        caches.remove(TerminalTypeEnums.VDI_LINUX);
    }

    /**
     * 测试getCache,参数为空
     * 
     * @throws Exception 异常
     */
    @Test
    public void testGetArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> manager.get(null, null),
                "terminalType can not be null");
        ThrowExceptionTester.throwIllegalArgumentException(() -> manager.get(TerminalTypeEnums.VDI_LINUX, null),
                "clz can not be null");
        assertTrue(true);
    }

    /**
     * 测试getUpdateListCaches
     */
    @Test
    public void testGet() {
        Map<TerminalTypeEnums, CbbCommonUpdatelistDTO> caches =
                Deencapsulation.getField(manager, "UPDATE_LIST_CACHE_MAP");
        CbbWinAppUpdateListDTO dto = new CbbWinAppUpdateListDTO();
        caches.put(TerminalTypeEnums.APP_WINDOWS, dto);
        CbbWinAppUpdateListDTO getDTO = manager.get(TerminalTypeEnums.APP_WINDOWS, CbbWinAppUpdateListDTO.class);
        assertEquals(dto, getDTO);
    }

    /**
     * 测试getUpdateListCaches
     */
    @Test
    public void testGetUpdateListCaches() {
        Map<TerminalTypeEnums, CbbWinAppUpdateListDTO> caches =
                Deencapsulation.getField(manager, "UPDATE_LIST_CACHE_MAP");
        assertEquals(caches, manager.getUpdateListCache());
    }

    /**
     * 测试setUpdatelistCacheReady
     */
    @Test
    public void testSetUpdatelistCacheReady() {

        manager.setUpdatelistCacheReady(TerminalTypeEnums.APP_WINDOWS);
        Map<TerminalTypeEnums, Boolean> stateMap = Deencapsulation.getField(manager, "UPDATE_LIST_CACHE_READY_STATE_MAP");
        assertEquals(true, stateMap.get(TerminalTypeEnums.APP_WINDOWS));
    }

    /**
     * 测试setUpdatelistCacheNotReady
     */
    @Test
    public void testSetUpdatelistCacheNotReady() {

        manager.setUpdatelistCacheNotReady(TerminalTypeEnums.APP_WINDOWS);
        Map<TerminalTypeEnums, Boolean> stateMap =
                Deencapsulation.getField(manager, "UPDATE_LIST_CACHE_READY_STATE_MAP");
        assertEquals(false, stateMap.get(TerminalTypeEnums.APP_WINDOWS));

    }

    /**
     * 测试isCacheNotReady
     */
    @Test
    public void testIsCacheNotReady() {
        boolean isNotReady = manager.isCacheNotReady(TerminalTypeEnums.APP_WINDOWS);
        assertEquals(true, isNotReady);
    }

}

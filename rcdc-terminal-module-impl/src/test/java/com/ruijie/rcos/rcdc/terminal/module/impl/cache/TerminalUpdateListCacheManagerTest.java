package com.ruijie.rcos.rcdc.terminal.module.impl.cache;

import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.dto.AppUpdateListDTO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dto.BaseUpdateListDTO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dto.CommonUpdateListDTO;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import com.ruijie.rcos.sk.base.test.ThrowExceptionTester;
import mockit.Deencapsulation;
import mockit.Tested;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
        ThrowExceptionTester.throwIllegalArgumentException(() -> manager.add(null, new AppUpdateListDTO()),
                "terminalType can not be null");
        ThrowExceptionTester.throwIllegalArgumentException(() -> manager.add(CbbTerminalTypeEnums.APP_WINDOWS, null),
                "updatelist can not be null");
        assertTrue(true);
    }

    /**
     * 测试addCache,
     */
    @Test
    public void testAdd() {
        AppUpdateListDTO updatelist = new AppUpdateListDTO();
        manager.add(CbbTerminalTypeEnums.VDI_LINUX, updatelist);
        Map<CbbTerminalTypeEnums, CommonUpdateListDTO> caches =
                Deencapsulation.getField(manager, "UPDATE_LIST_CACHE_MAP");
        assertEquals(updatelist, caches.get(CbbTerminalTypeEnums.VDI_LINUX));
        caches.remove(CbbTerminalTypeEnums.VDI_LINUX);
    }

    /**
     * 测试getCache,参数为空
     * 
     * @throws Exception 异常
     */
    @Test
    public void testGetArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> manager.get(null),
                "terminalType can not be null");
        assertTrue(true);
    }

    /**
     * 测试getUpdateListCaches
     */
    @Test
    public void testGet() {
        Map<CbbTerminalTypeEnums, BaseUpdateListDTO> caches =
                Deencapsulation.getField(manager, "UPDATE_LIST_CACHE_MAP");
        AppUpdateListDTO dto = new AppUpdateListDTO();
        caches.put(CbbTerminalTypeEnums.APP_WINDOWS, dto);
        AppUpdateListDTO getDTO = manager.get(CbbTerminalTypeEnums.APP_WINDOWS);
        assertEquals(dto, getDTO);
    }

    /**
     * 测试getUpdateListCaches
     */
    @Test
    public void testGetUpdateListCaches() {
        Map<CbbTerminalTypeEnums, AppUpdateListDTO> caches =
                Deencapsulation.getField(manager, "UPDATE_LIST_CACHE_MAP");
        assertEquals(caches, manager.getUpdateListCache());
    }

    /**
     * 测试setUpdatelistCacheReady
     */
    @Test
    public void testSetUpdatelistCacheReady() {

        manager.setUpdatelistCacheReady(CbbTerminalTypeEnums.APP_WINDOWS);
        Map<CbbTerminalTypeEnums, Boolean> stateMap = Deencapsulation.getField(manager, "UPDATE_LIST_CACHE_READY_STATE_MAP");
        assertEquals(true, stateMap.get(CbbTerminalTypeEnums.APP_WINDOWS));
    }

    /**
     * 测试setUpdatelistCacheNotReady
     */
    @Test
    public void testSetUpdatelistCacheNotReady() {

        manager.setUpdatelistCacheNotReady(CbbTerminalTypeEnums.APP_WINDOWS);
        Map<CbbTerminalTypeEnums, Boolean> stateMap =
                Deencapsulation.getField(manager, "UPDATE_LIST_CACHE_READY_STATE_MAP");
        assertEquals(false, stateMap.get(CbbTerminalTypeEnums.APP_WINDOWS));

    }

    /**
     * 测试isCacheNotReady
     */
    @Test
    public void testIsCacheNotReady() {
        boolean isReady = manager.isCacheReady(CbbTerminalTypeEnums.APP_WINDOWS);
        assertEquals(false, isReady);
    }

}

package com.ruijie.rcos.rcdc.terminal.module.impl.cache;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalOsTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.dto.AppUpdateListDTO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dto.BaseUpdateListDTO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dto.CommonUpdateListDTO;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.TerminalOsArchType;
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
                "osType can not be null");
        ThrowExceptionTester.throwIllegalArgumentException(() -> manager.add(TerminalOsArchType.WINDOWS_X86, null),
                "updatelist can not be null");
        assertTrue(true);
    }

    /**
     * 测试addCache,
     */
    @Test
    public void testAdd() {
        AppUpdateListDTO updatelist = new AppUpdateListDTO();
        manager.add(TerminalOsArchType.LINUX_X86, updatelist);
        Map<CbbTerminalTypeEnums, CommonUpdateListDTO> caches =
                Deencapsulation.getField(manager, "UPDATE_LIST_CACHE_MAP");
        assertEquals(updatelist, caches.get(TerminalOsArchType.LINUX_X86));
        caches.remove(TerminalOsArchType.LINUX_X86);
    }

    /**
     * 测试getCache,参数为空
     * 
     * @throws Exception 异常
     */
    @Test
    public void testGetArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> manager.get(null),
                "osType can not be null");
        assertTrue(true);
    }

    /**
     * 测试getUpdateListCaches
     */
    @Test
    public void testGet() {
        Map<TerminalOsArchType, BaseUpdateListDTO> caches =
                Deencapsulation.getField(manager, "UPDATE_LIST_CACHE_MAP");
        AppUpdateListDTO dto = new AppUpdateListDTO();
        caches.put(TerminalOsArchType.WINDOWS_X86, dto);
        AppUpdateListDTO getDTO = manager.get(TerminalOsArchType.WINDOWS_X86);
        assertEquals(dto, getDTO);
    }

    /**
     * 测试getUpdateListCaches
     */
    @Test
    public void testGetUpdateListCaches() {
        Map<CbbTerminalOsTypeEnums, AppUpdateListDTO> caches =
                Deencapsulation.getField(manager, "UPDATE_LIST_CACHE_MAP");
        assertEquals(caches, manager.getUpdateListCache());
    }

    /**
     * 测试setUpdatelistCacheReady
     */
    @Test
    public void testSetUpdatelistCacheReady() {

        manager.setUpdatelistCacheReady(TerminalOsArchType.WINDOWS_X86);
        Map<TerminalOsArchType, Boolean> stateMap = Deencapsulation.getField(manager, "UPDATE_LIST_CACHE_READY_STATE_MAP");
        assertEquals(true, stateMap.get(TerminalOsArchType.WINDOWS_X86));
    }

    /**
     * 测试setUpdatelistCacheNotReady
     */
    @Test
    public void testSetUpdatelistCacheNotReady() {

        manager.setUpdatelistCacheNotReady(TerminalOsArchType.WINDOWS_X86);
        Map<TerminalOsArchType, Boolean> stateMap =
                Deencapsulation.getField(manager, "UPDATE_LIST_CACHE_READY_STATE_MAP");
        assertEquals(false, stateMap.get(TerminalOsArchType.WINDOWS_X86));

    }

    /**
     * 测试isCacheNotReady
     */
    @Test
    public void testIsCacheNotReady() {
        boolean isReady = manager.isCacheReady(TerminalOsArchType.WINDOWS_X86);
        assertEquals(false, isReady);
    }

    /**
     * 测试testIsCacheNotReadyIsNull
     */
    @Test
    public void testIsCacheNotReadyIsNull() {

        Map<TerminalOsArchType, Boolean> stateMap = Deencapsulation.getField(manager, "UPDATE_LIST_CACHE_READY_STATE_MAP");
        stateMap.put(TerminalOsArchType.WINDOWS_X86, null);
        boolean isReady = manager.isCacheReady(TerminalOsArchType.WINDOWS_X86);
        assertEquals(false, isReady);
        stateMap.put(TerminalOsArchType.WINDOWS_X86, false);
    }

}

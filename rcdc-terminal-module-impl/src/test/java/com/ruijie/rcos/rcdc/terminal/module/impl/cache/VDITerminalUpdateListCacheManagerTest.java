package com.ruijie.rcos.rcdc.terminal.module.impl.cache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.updatelist.CbbLinuxVDIUpdateListDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.updatelist.CbbWinAppUpdateListDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalTypeEnums;
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
public class VDITerminalUpdateListCacheManagerTest {

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
                "terminalType can not be null");
        ThrowExceptionTester.throwIllegalArgumentException(() -> manager.add(CbbTerminalTypeEnums.WINDOWS, null),
                "updatelist can not be null");
        assertTrue(true);
    }

    /**
     * 测试addCache,
     */
    @Test
    public void testAdd() {
        CbbLinuxVDIUpdateListDTO updatelist = new CbbLinuxVDIUpdateListDTO();
        manager.add(CbbTerminalTypeEnums.LINUX, updatelist);
        Map<CbbTerminalTypeEnums, CbbLinuxVDIUpdateListDTO> caches =
                Deencapsulation.getField(manager, "UPDATE_LIST_CACHE_MAP");
        assertEquals(1, caches.size());
        assertEquals(updatelist, caches.get(CbbTerminalTypeEnums.LINUX));
        caches.remove(CbbTerminalTypeEnums.LINUX);
    }

    /**
     * 测试getCache,参数为空
     *
     * @throws Exception 异常
     */
    @Test
    public void testGetArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> manager.get(null), "terminalType can not be null");
        assertTrue(true);
    }

    /**
     * 测试getUpdateListCaches
     */
    @Test
    public void testGet() {
        Map<CbbTerminalTypeEnums, CbbLinuxVDIUpdateListDTO> caches =
                Deencapsulation.getField(manager, "UPDATE_LIST_CACHE_MAP");
        CbbLinuxVDIUpdateListDTO dto = new CbbLinuxVDIUpdateListDTO();
        caches.put(CbbTerminalTypeEnums.LINUX, dto);
        CbbLinuxVDIUpdateListDTO getDTO = manager.get(CbbTerminalTypeEnums.LINUX);
        assertEquals(dto, getDTO);
    }

    /**
     * 测试getUpdateListCaches
     */
    @Test
    public void testGetUpdateListCaches() {
        Map<CbbTerminalTypeEnums, CbbWinAppUpdateListDTO> caches =
                Deencapsulation.getField(manager, "UPDATE_LIST_CACHE_MAP");
        assertEquals(caches, manager.getUpdateListCache());
    }

    /**
     * 测试setUpdatelistCacheReady
     */
    @Test
    public void testSetUpdatelistCacheReady() {

        manager.setUpdatelistCacheReady();
        boolean isUpdate = Deencapsulation.getField(manager, "isUpdate");
        assertEquals(false, isUpdate);
    }

    /**
     * 测试setUpdatelistCacheNotReady
     */
    @Test
    public void testSetUpdatelistCacheNotReady() {

        manager.setUpdatelistCacheNotReady();
        boolean isUpdate = Deencapsulation.getField(manager, "isUpdate");
        assertEquals(true, isUpdate);

        AppTerminalUpdateListCacheManager.isUpdate = true;
    }

    /**
     * 测试isCacheNotReady
     */
    @Test
    public void testIsCacheNotReady() {
        boolean isNotReady = manager.isCacheNotReady();
        assertEquals(true, isNotReady);
    }
}

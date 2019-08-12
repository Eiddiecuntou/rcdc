package com.ruijie.rcos.rcdc.terminal.module.impl.init.updatelist;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.collect.Lists;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.updatelist.CbbWinAppComponentVersionInfoDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.updatelist.CbbWinAppUpdateListDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.AppTerminalUpdateListCacheManager;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;

import mockit.Tested;
import mockit.Verifications;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/8/11
 *
 * @author nt
 */
@RunWith(SkyEngineRunner.class)
public class WinAppTerminalUpdatelistCacheInitTest {

    @Tested
    private WinAppTerminalUpdatelistCacheInit cacheInit;

    @Test
    public void testGetUpdateListPath() {
        String updateListPath = cacheInit.getUpdateListPath();
        Assert.assertEquals("/opt/ftp/terminal/terminal_component/windows_app/update.list", updateListPath);
    }

    @Test
    public void testGetUpdateListCacheManager() {
        Map<CbbTerminalTypeEnums, CbbWinAppUpdateListDTO> updateListCacheManager =
                cacheInit.getUpdateListCacheManager();
        Assert.assertEquals(AppTerminalUpdateListCacheManager.getUpdateListCache(), updateListCacheManager);
    }

    @Test
    public void testGetTerminalType() {
        CbbTerminalTypeEnums terminalType = cacheInit.getTerminalType();
        Assert.assertEquals(CbbTerminalTypeEnums.WINDOWS, terminalType);
    }

    @Test
    public void testCacheInitPre() {

        cacheInit.cacheInitPre();

        new Verifications() {
            {
                AppTerminalUpdateListCacheManager.setUpdatelistCacheNotReady();
                times = 1;
            }
        };
    }

    @Test
    public void testCacheInitFinished() {

        cacheInit.cacheInitFinished();

        new Verifications() {
            {
                AppTerminalUpdateListCacheManager.setUpdatelistCacheReady();
                times = 1;
            }
        };
    }

    /**
     * 测试填充updatelist计算MD5时出现IOException
     */
    @Test
    public void testFillUpdateListHasIOException() {

        CbbWinAppUpdateListDTO dto = new CbbWinAppUpdateListDTO();
        List<CbbWinAppComponentVersionInfoDTO> versionList = Lists.newArrayList();
        CbbWinAppComponentVersionInfoDTO versionInfoDTO = new CbbWinAppComponentVersionInfoDTO();
        versionInfoDTO.setCompletePackageName("aaa");
        versionList.add(versionInfoDTO);
        dto.setComponentList(versionList);

        cacheInit.fillUpdateList(dto);

        Assert.assertEquals("/terminal_component/windows_app/component/aaa", versionInfoDTO.getUrl());
    }
}

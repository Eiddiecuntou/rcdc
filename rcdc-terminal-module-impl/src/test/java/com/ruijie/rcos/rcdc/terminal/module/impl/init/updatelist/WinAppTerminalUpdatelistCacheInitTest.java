package com.ruijie.rcos.rcdc.terminal.module.impl.init.updatelist;

import java.util.List;

import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.collect.Lists;
import com.ruijie.rcos.rcdc.terminal.module.impl.dto.WinAppComponentVersionInfoDTO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dto.WinAppUpdateListDTO;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;

import mockit.Tested;

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

    /**
     * testGetUpdateListPath
     */
    @Test
    public void testGetUpdateListPath() {
        String updateListPath = cacheInit.getUpdateListPath();
        Assert.assertEquals("/opt/ftp/terminal/terminal_component/windows_app/update.list", updateListPath);
    }

    /**
     * testGetTerminalType
     */
    @Test
    public void testGetTerminalType() {
        CbbTerminalTypeEnums terminalType = cacheInit.getTerminalType();
        Assert.assertEquals(CbbTerminalTypeEnums.APP_WINDOWS, terminalType);
    }

    /**
     * 测试填充updatelist计算MD5时出现IOException
     */
    @Test
    public void testFillUpdateListHasIOException() {

        WinAppUpdateListDTO dto = new WinAppUpdateListDTO();
        List<WinAppComponentVersionInfoDTO> versionList = Lists.newArrayList();
        WinAppComponentVersionInfoDTO versionInfoDTO = new WinAppComponentVersionInfoDTO();
        versionInfoDTO.setCompletePackageName("aaa");
        versionList.add(versionInfoDTO);
        dto.setComponentList(versionList);

        cacheInit.fillUpdateList(dto);

        Assert.assertEquals("/terminal_component/windows_app/component/aaa", versionInfoDTO.getCompletePackageUrl());
    }
}

package com.ruijie.rcos.rcdc.terminal.module.impl.init.updatelist;

import static org.junit.Assert.assertTrue;

import com.ruijie.rcos.rcdc.terminal.module.impl.dto.CommonUpdateListDTO;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
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
public class LinuxUpdatelistCacheInitTest {

    @Tested
    private LinuxUpdatelistCacheInit cacheInit;

    /**
     * testGetUpdateListPath
     */
    @Test
    public void testGetUpdateListPath() {
        String updateListPath = cacheInit.getUpdateListPath();
        Assert.assertEquals("/opt/upgrade/app/terminal_component/terminal_vdi_linux/origin/update.list", updateListPath);
    }

    /**
     * testGetTerminalType
     */
    @Test
    public void testGetTerminalType() {
        CbbTerminalTypeEnums terminalType = cacheInit.getTerminalOsType();
        Assert.assertEquals(CbbTerminalTypeEnums.VDI_LINUX, terminalType);
    }

    /**
     * testFillUpdateList
     */
    @Test
    public void testFillUpdateList() {

        // 这个方法啥也没干
        cacheInit.fillUpdateList(new CommonUpdateListDTO());
        assertTrue(true);
    }
}

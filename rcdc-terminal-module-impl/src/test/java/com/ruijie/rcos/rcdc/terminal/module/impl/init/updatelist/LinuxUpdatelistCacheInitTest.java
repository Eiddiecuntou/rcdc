package com.ruijie.rcos.rcdc.terminal.module.impl.init.updatelist;

import static org.junit.Assert.assertTrue;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalOsTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.dto.CommonUpdateListDTO;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.TerminalOsArchType;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

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
        Assert.assertEquals("/opt/upgrade/app/terminal_component/terminal_linux/origin/update.list", updateListPath);
    }

    /**
     * testGetTerminalType
     */
    @Test
    public void testGetTerminalType() {
        TerminalOsArchType osArch = cacheInit.getTerminalOsArch();
        Assert.assertEquals(TerminalOsArchType.LINUX_X86, osArch);
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

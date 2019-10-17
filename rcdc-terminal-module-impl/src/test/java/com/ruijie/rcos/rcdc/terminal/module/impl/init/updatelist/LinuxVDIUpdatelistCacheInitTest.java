package com.ruijie.rcos.rcdc.terminal.module.impl.init.updatelist;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.updatelist.CbbLinuxVDIUpdateListDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.TerminalTypeEnums;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import mockit.Tested;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertTrue;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/8/11
 *
 * @author nt
 */
@RunWith(SkyEngineRunner.class)
public class LinuxVDIUpdatelistCacheInitTest {

    @Tested
    private LinuxVDIUpdatelistCacheInit cacheInit;

    /**
     * 测试getUpdateListPath() {
     */
    @Test
    public void testGetUpdateListPath() {
        String updateListPath = cacheInit.getUpdateListPath();
        Assert.assertEquals("/opt/upgrade/app/terminal_component/terminal_vdi_linux/origin/update.list", updateListPath);
    }

    /**
     * 测试getTerminalType
     */
    @Test
    public void testGetTerminalType() {
        TerminalTypeEnums terminalType = cacheInit.getTerminalType();
        Assert.assertEquals(TerminalTypeEnums.VDI_LINUX, terminalType);
    }

    /**
     * 测试fillUpdateList
     */
    @Test
    public void testFillUpdateList() {

        // 这个方法啥也没干
        cacheInit.fillUpdateList(new CbbLinuxVDIUpdateListDTO());
        assertTrue(true);
    }
}

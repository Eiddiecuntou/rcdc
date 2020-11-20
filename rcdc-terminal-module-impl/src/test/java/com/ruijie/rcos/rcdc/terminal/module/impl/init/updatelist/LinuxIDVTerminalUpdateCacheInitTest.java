package com.ruijie.rcos.rcdc.terminal.module.impl.init.updatelist;

import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.dto.CommonUpdateListDTO;
import mockit.Tested;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/10/14
 *
 * @author XiaoJiaXin
 */
public class LinuxIDVTerminalUpdateCacheInitTest {

    @Tested
    private LinuxIDVUpdatelistCacheInit cacheInit;

    @Test
    public void testGetUpdateListPath() {
        String updateListPath = cacheInit.getUpdateListPath();
        Assert.assertEquals("/opt/upgrade/app/terminal_component/terminal_idv_linux/origin/update.list", updateListPath);
    }

    @Test
    public void testGetTerminalType() {
        CbbTerminalTypeEnums terminalType = cacheInit.getTerminalOsType();
        Assert.assertEquals(CbbTerminalTypeEnums.IDV_LINUX, terminalType);
    }

    @Test
    public void testFillUpdateList() {
        cacheInit.fillUpdateList(new CommonUpdateListDTO());
        assertTrue(true);
    }
}

package com.ruijie.rcos.rcdc.terminal.module.impl.init.updatelist;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.updatelist.CbbAndroidVDIUpdateListDTO;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.TerminalTypeEnums;
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
public class AndroidVDITerminalUpdateCacheInitTest {

    @Tested
    private AndroidVDIUpdatelistCacheInit cacheInit;

    @Test
    public void testGetUpdateListPath() {
        String updateListPath = cacheInit.getUpdateListPath();
        Assert.assertEquals("/opt/upgrade/app/terminal_component/terminal_vdi_android/origin/update.list", updateListPath);
    }

    @Test
    public void testGetTerminalType() {
        TerminalTypeEnums terminalType = cacheInit.getTerminalType();
        Assert.assertEquals(TerminalTypeEnums.VDI_ANDROID, terminalType);
    }

    @Test
    public void testFillUpdateList() {
        cacheInit.fillUpdateList(new CbbAndroidVDIUpdateListDTO());
        assertTrue(true);
    }
}

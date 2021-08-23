package com.ruijie.rcos.rcdc.terminal.module.impl.init.updatelist;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalOsTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.dto.CommonUpdateListDTO;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.TerminalOsArchType;
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
public class AndroidTerminalUpdateCacheInitTest {

    @Tested
    private AndroidUpdatelistCacheInit cacheInit;

    @Test
    public void testGetUpdateListPath() {
        String updateListPath = cacheInit.getUpdateListPath();
        Assert.assertEquals("/opt/upgrade/app/terminal_component/terminal_android/origin/update.list", updateListPath);
    }

    @Test
    public void testGetTerminalOsArch() {
        TerminalOsArchType osArch = cacheInit.getTerminalOsArch();
        Assert.assertEquals(TerminalOsArchType.ANDROID_ARM, osArch);
    }

    @Test
    public void testFillUpdateList() {
        cacheInit.fillUpdateList(new CommonUpdateListDTO());
        assertTrue(true);
    }
}

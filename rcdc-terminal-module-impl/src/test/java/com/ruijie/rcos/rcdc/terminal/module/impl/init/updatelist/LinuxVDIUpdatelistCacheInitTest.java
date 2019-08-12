package com.ruijie.rcos.rcdc.terminal.module.impl.init.updatelist;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.updatelist.CbbLinuxVDIUpdateListDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.VDITerminalUpdateListCacheManager;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import mockit.Expectations;
import mockit.Tested;
import mockit.Verifications;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Map;

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

    @Test
    public void testGetUpdateListPath() {
        String updateListPath = cacheInit.getUpdateListPath();
        Assert.assertEquals("/opt/upgrade/app/terminal_component/terminal_vdi_linux/origin/update.list", updateListPath);
    }

    @Test
    public void testGetUpdateListCacheManager() {
        Map<CbbTerminalTypeEnums, CbbLinuxVDIUpdateListDTO> cacheManager = cacheInit.getUpdateListCacheManager();
        Assert.assertEquals(VDITerminalUpdateListCacheManager.getUpdateListCache(), cacheManager);
    }

    @Test
    public void testGetTerminalType() {
        CbbTerminalTypeEnums terminalType = cacheInit.getTerminalType();
        Assert.assertEquals(CbbTerminalTypeEnums.LINUX, terminalType);
    }

    @Test
    public void testCacheInitPre() {

        cacheInit.cacheInitPre();

        new Verifications(){
            {
                VDITerminalUpdateListCacheManager.setUpdatelistCacheNotReady();
                times = 1;
            }
        };
    }

    @Test
    public void testCacheInitFinished() {

        cacheInit.cacheInitFinished();

        new Verifications(){
            {
                VDITerminalUpdateListCacheManager.setUpdatelistCacheReady();
                times = 1;
            }
        };
    }

    @Test
    public void testFillUpdateList() {

        // 这个方法啥也没干
        cacheInit.fillUpdateList(new CbbLinuxVDIUpdateListDTO());
        assertTrue(true);
    }
}

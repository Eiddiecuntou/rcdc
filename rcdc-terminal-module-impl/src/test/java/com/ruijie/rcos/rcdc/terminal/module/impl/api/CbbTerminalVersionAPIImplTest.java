package com.ruijie.rcos.rcdc.terminal.module.impl.api;

import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbCpuArchType;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.TerminalUpdateListCacheManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.dto.AppComponentVersionInfoDTO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dto.AppUpdateListDTO;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.TerminalOsArchType;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import mockit.Expectations;
import mockit.Tested;
import mockit.Verifications;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/10/20
 *
 * @author linke
 */
@RunWith(SkyEngineRunner.class)
public class CbbTerminalVersionAPIImplTest {

    @Tested
    CbbTerminalVersionAPIImpl cbbTerminalVersionAPI;

    /**
     * 测试listTerminalModel参数为空
     *
     * @throws Exception 异常
     */
    @Test
    public void testInitAndroidOTA() throws Exception {

        AppUpdateListDTO updatelist = new AppUpdateListDTO();
        updatelist.setVersion("1.0.0");
        List<AppComponentVersionInfoDTO> componentVersionInfoDTOList = new ArrayList<>();
        AppComponentVersionInfoDTO appComponentVersionInfoDTO = new AppComponentVersionInfoDTO();
        componentVersionInfoDTOList.add(appComponentVersionInfoDTO);
        updatelist.setComponentList(componentVersionInfoDTOList);

        new Expectations(TerminalUpdateListCacheManager.class) {
            {
                TerminalUpdateListCacheManager.get((TerminalOsArchType) any);
                result = updatelist;
            }
        };

        String version = cbbTerminalVersionAPI.getTerminalVersion(CbbCpuArchType.X86_64, "Windows");
        Assert.assertEquals("1.0.0", version);

        new Verifications() {
            {
                TerminalUpdateListCacheManager.get((TerminalOsArchType) any);
                times = 1;
            }
        };
    }
}

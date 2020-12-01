package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.componentupgrade;

import com.google.common.collect.Lists;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalOsTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.TerminalUpdateListCacheManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.dto.AppComponentVersionInfoDTO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dto.AppUpdateListDTO;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalVersionResultDTO;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import mockit.Expectations;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

/**
 * Description:
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/7/27 16:41
 *
 * @author conghaifeng
 */
@RunWith(SkyEngineRunner.class)
public class AbstractAppComponentUpgradeHandlerTest {

    /**
     *测试
     */
    @Test
    public void testGetVersionCacheReady() {
        TestedComponentUpgradeHandler handler = new TestedComponentUpgradeHandler();
        GetVersionDTO request = new GetVersionDTO();

        AppUpdateListDTO updatelist = new AppUpdateListDTO();
        List<AppComponentVersionInfoDTO> componentList = Lists.newArrayList();
        updatelist.setComponentList(componentList);

        new Expectations(TerminalUpdateListCacheManager.class) {
            {
                TerminalUpdateListCacheManager.isCacheReady((CbbTerminalOsTypeEnums) any);
                result = true;
                TerminalUpdateListCacheManager.get((CbbTerminalOsTypeEnums) any);
                result = updatelist;
            }
        };

        TerminalVersionResultDTO result = handler.getVersion(request);
        Assert.assertEquals(-1, result.getResult().intValue());
    }

    /**
     *测试
     */
    @Test
    public void testGetVersionCacheReadySecond() {
        TestedComponentUpgradeHandler handler = new TestedComponentUpgradeHandler();
        GetVersionDTO request = new GetVersionDTO();
        request.setRainUpgradeVersion("1.1.0.1");

        AppUpdateListDTO updatelist = new AppUpdateListDTO();
        updatelist.setVersion("1.1.0.1");
        List<AppComponentVersionInfoDTO> componentList = Lists.newArrayList();
        componentList.add(new AppComponentVersionInfoDTO());
        updatelist.setComponentList(componentList);

        new Expectations(TerminalUpdateListCacheManager.class) {
            {
                TerminalUpdateListCacheManager.isCacheReady((CbbTerminalOsTypeEnums) any);
                result = true;
                TerminalUpdateListCacheManager.get((CbbTerminalOsTypeEnums) any);
                result = updatelist;
            }
        };

        TerminalVersionResultDTO result = handler.getVersion(request);
        Assert.assertEquals(0, result.getResult().intValue());
    }

    /**
     *测试
     */
    @Test
    public void testGetVersionCompareOsLimitFail() {
        TestedComponentUpgradeHandler handler = new TestedComponentUpgradeHandler();
        GetVersionDTO request = new GetVersionDTO();
        request.setRainUpgradeVersion("1.1.0.1");

        AppUpdateListDTO updatelist = new AppUpdateListDTO();
        updatelist.setVersion("1.1.0.2");
        List<AppComponentVersionInfoDTO> componentList = Lists.newArrayList();
        componentList.add(new AppComponentVersionInfoDTO());
        updatelist.setComponentList(componentList);

        new Expectations(TerminalUpdateListCacheManager.class) {
            {
                TerminalUpdateListCacheManager.isCacheReady((CbbTerminalOsTypeEnums) any);
                result = true;
                TerminalUpdateListCacheManager.get((CbbTerminalOsTypeEnums) any);
                result = updatelist;
            }
        };

        TerminalVersionResultDTO result = handler.getVersion(request);
        Assert.assertEquals(0, result.getResult().intValue());
    }

    /**
     *测试
     */
    @Test
    public void testGetVersionIncrementUpgrade() {
        TestedComponentUpgradeHandler handler = new TestedComponentUpgradeHandler();
        GetVersionDTO request = new GetVersionDTO();
        request.setRainUpgradeVersion("1.1.0.1");

        AppUpdateListDTO updatelist = new AppUpdateListDTO();
        updatelist.setVersion("1.1.0.2");
        updatelist.setLimitVersion("1.1.0.1");
        List<AppComponentVersionInfoDTO> componentList = Lists.newArrayList();
        componentList.add(new AppComponentVersionInfoDTO());
        updatelist.setComponentList(componentList);

        new Expectations(TerminalUpdateListCacheManager.class) {
            {
                TerminalUpdateListCacheManager.isCacheReady((CbbTerminalOsTypeEnums) any);
                result = true;
                TerminalUpdateListCacheManager.get((CbbTerminalOsTypeEnums) any);
                result = updatelist;
            }
        };

        TerminalVersionResultDTO result = handler.getVersion(request);
        Assert.assertEquals(2, result.getResult().intValue());
    }

    /**
     *测试
     */
    @Test
    public void testGetVersionCompleteUpgrade() {
        TestedComponentUpgradeHandler handler = new TestedComponentUpgradeHandler();
        GetVersionDTO request = new GetVersionDTO();
        request.setRainUpgradeVersion("1.1.0.1");

        AppUpdateListDTO updatelist = new AppUpdateListDTO();
        updatelist.setVersion("1.1.0.2");
        updatelist.setLimitVersion("1.1.0.2");
        List<AppComponentVersionInfoDTO> componentList = Lists.newArrayList();
        componentList.add(new AppComponentVersionInfoDTO());
        updatelist.setComponentList(componentList);

        new Expectations(TerminalUpdateListCacheManager.class) {
            {
                TerminalUpdateListCacheManager.isCacheReady((CbbTerminalOsTypeEnums) any);
                result = true;
                TerminalUpdateListCacheManager.get((CbbTerminalOsTypeEnums) any);
                result = updatelist;
            }
        };

        TerminalVersionResultDTO result = handler.getVersion(request);
        Assert.assertEquals(2, result.getResult().intValue());
    }


    private class TestedComponentUpgradeHandler extends AbstractAppComponentUpgradeHandler {

        @Override
        protected CbbTerminalOsTypeEnums getTerminalOsType() {
            return CbbTerminalOsTypeEnums.WINDOWS;
        }
    }

}

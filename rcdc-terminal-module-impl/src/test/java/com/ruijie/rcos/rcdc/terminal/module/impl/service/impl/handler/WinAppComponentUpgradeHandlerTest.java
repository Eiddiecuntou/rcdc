package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.updatelist.CbbLinuxVDIComponentVersionInfoDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.updatelist.CbbLinuxVDIUpdateListDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.updatelist.CbbWinAppComponentVersionInfoDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.updatelist.CbbWinAppUpdateListDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalComponentUpgradeResultEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.TerminalPlatformEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.AppTerminalUpdateListCacheManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.VDITerminalUpdateListCacheManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalVersionResultDTO;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.test.ThrowExceptionTester;
import mockit.Expectations;
import mockit.Mock;
import mockit.MockUp;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;

import mockit.Tested;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年8月10日
 *
 * @author nt
 */
@RunWith(SkyEngineRunner.class)
public class WinAppComponentUpgradeHandlerTest {

    @Tested
    private WinAppComponentUpgradeHandler handler;


    /**
     * 测试getVersion,参数为空
     *
     * @throws Exception 异常
     */
    @Test
    public void testGetVersionArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> handler.getVersion(null),
                "get version request can not be null");
        Assert.assertTrue(true);
    }

    /**
     * 测试getVersion,updatelist为空
     */
    @Test
    public void testGetVersionUpdatelistIsNull() {
        CbbWinAppUpdateListDTO updatelist = new CbbWinAppUpdateListDTO();
        updatelist.setComponentList(Collections.emptyList());

        new MockUp(AppTerminalUpdateListCacheManager.class) {

            private boolean isFirst = true;

            @Mock
            public CbbWinAppUpdateListDTO get(CbbTerminalTypeEnums terminalType) {
                if (isFirst) {
                    isFirst = false;
                    // 模拟返回空
                    return null;
                }

                return updatelist;
            }
        };

        AppTerminalUpdateListCacheManager.setUpdatelistCacheReady();
        GetVersionRequest request = new GetVersionRequest();
        request.setRainUpgradeVersion("123");
        request.setValidateMd5("xxx");
        TerminalVersionResultDTO terminalVersionResultDTO = handler.getVersion(request);
        assertEquals(CbbTerminalComponentUpgradeResultEnums.ABNORMAL.getResult(),
                terminalVersionResultDTO.getResult().intValue());
        TerminalVersionResultDTO terminalVersionResultDTO1 = handler.getVersion(request);
        assertEquals(CbbTerminalComponentUpgradeResultEnums.ABNORMAL.getResult(),
                terminalVersionResultDTO1.getResult().intValue());
        AppTerminalUpdateListCacheManager.setUpdatelistCacheNotReady();
    }

    /**
     * 测试getVersion,不升级
     */
    @Test
    public void testGetVersionNoUpgrade() {
        CbbWinAppUpdateListDTO updatelist = new CbbWinAppUpdateListDTO();
        List<CbbWinAppComponentVersionInfoDTO> componentList = new ArrayList<>();
        componentList.add(new CbbWinAppComponentVersionInfoDTO());
        updatelist.setComponentList(componentList);
        updatelist.setVersion("1.1.0.1");
        updatelist.setValidateMd5("123");
        updatelist.setComponentSize(1);

        new MockUp(AppTerminalUpdateListCacheManager.class) {
            @Mock
            public CbbWinAppUpdateListDTO get(CbbTerminalTypeEnums terminalType) {
                return updatelist;
            }
        };

        GetVersionRequest request = new GetVersionRequest();
        request.setRainUpgradeVersion("1.1.0.1");
        request.setValidateMd5("123");
        AppTerminalUpdateListCacheManager.setUpdatelistCacheReady();
        TerminalVersionResultDTO terminalVersionResultDTO = handler.getVersion(request);
        assertEquals(CbbTerminalComponentUpgradeResultEnums.NOT.getResult(),
                terminalVersionResultDTO.getResult().intValue());
        AppTerminalUpdateListCacheManager.setUpdatelistCacheNotReady();
    }

    /**
     * 测试getVersion,非法的版本号
     */
    @Test
    public void testGetVersionRainUpgradeVersionIsIllegale() {
        CbbWinAppUpdateListDTO updatelist = new CbbWinAppUpdateListDTO();
        List<CbbWinAppComponentVersionInfoDTO> componentList = new ArrayList<>();
        CbbWinAppComponentVersionInfoDTO complete = new CbbWinAppComponentVersionInfoDTO();
        complete.setComplete(true);
        CbbWinAppComponentVersionInfoDTO component = new CbbWinAppComponentVersionInfoDTO();
        component.setComplete(false);
        componentList.add(complete);
        componentList.add(component);
        updatelist.setComponentList(componentList);
        updatelist.setVersion("1.1.0.1");
        updatelist.setComponentSize(1);
        updatelist.setLimitVersion("1.0.1.1");
        updatelist.setValidateMd5("123");
        new MockUp(AppTerminalUpdateListCacheManager.class) {
            @Mock
            public CbbWinAppUpdateListDTO get(CbbTerminalTypeEnums terminalType) {
                return updatelist;
            }
        };

        GetVersionRequest request = new GetVersionRequest();
        request.setRainUpgradeVersion("111");
        request.setValidateMd5("123");
        AppTerminalUpdateListCacheManager.setUpdatelistCacheReady();
        TerminalVersionResultDTO terminalVersionResultDTO = handler.getVersion(request);
        assertEquals(CbbTerminalComponentUpgradeResultEnums.START.getResult(),
                terminalVersionResultDTO.getResult().intValue());
        AppTerminalUpdateListCacheManager.setUpdatelistCacheNotReady();
    }

    /**
     * 测试getVersion,低于最低支持版本
     */
    @Test
    public void testGetVersionLimitVersion() {
        CbbWinAppUpdateListDTO updatelist = new CbbWinAppUpdateListDTO();
        List<CbbWinAppComponentVersionInfoDTO> componentList = new ArrayList<>();
        CbbWinAppComponentVersionInfoDTO complete = new CbbWinAppComponentVersionInfoDTO();
        complete.setComplete(true);
        CbbWinAppComponentVersionInfoDTO component = new CbbWinAppComponentVersionInfoDTO();
        component.setComplete(false);
        componentList.add(complete);
        componentList.add(component);
        updatelist.setComponentList(componentList);
        updatelist.setVersion("1.1.0.1");
        updatelist.setComponentSize(1);
        updatelist.setLimitVersion("1.0.1.1");
        updatelist.setValidateMd5("123");


        new MockUp(AppTerminalUpdateListCacheManager.class) {
            @Mock
            public CbbWinAppUpdateListDTO get(CbbTerminalTypeEnums terminalType) {
                return updatelist;
            }
        };

        GetVersionRequest request = new GetVersionRequest();
        request.setRainUpgradeVersion("1.0.0.1");
        request.setValidateMd5("123");
        AppTerminalUpdateListCacheManager.setUpdatelistCacheReady();
        TerminalVersionResultDTO terminalVersionResultDTO = handler.getVersion(request);
        assertEquals(CbbTerminalComponentUpgradeResultEnums.START.getResult(),
                terminalVersionResultDTO.getResult().intValue());
        AppTerminalUpdateListCacheManager.setUpdatelistCacheNotReady();
    }

    /**
     * 测试getVersion,正处于更新中
     */
    @Test
    public void testGetVersionIsUpdating() {
        CbbWinAppUpdateListDTO updatelist = new CbbWinAppUpdateListDTO();
        List<CbbWinAppComponentVersionInfoDTO> componentList = new ArrayList<>();
        componentList.add(new CbbWinAppComponentVersionInfoDTO());
        updatelist.setComponentList(componentList);
        updatelist.setVersion("1.1.0.1");
        updatelist.setComponentSize(1);
        updatelist.setLimitVersion("1.0.1.1");

        GetVersionRequest request = new GetVersionRequest();
        request.setRainUpgradeVersion("1.0.0.1");
        request.setValidateMd5("123");
        TerminalVersionResultDTO terminalVersionResultDTO = handler.getVersion(request);
        assertEquals(CbbTerminalComponentUpgradeResultEnums.PREPARING.getResult(),
                terminalVersionResultDTO.getResult().intValue());

    }

}

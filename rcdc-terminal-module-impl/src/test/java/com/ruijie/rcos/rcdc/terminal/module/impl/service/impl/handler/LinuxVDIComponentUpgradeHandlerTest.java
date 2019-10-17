package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.updatelist.CbbLinuxVDIComponentVersionInfoDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.updatelist.CbbLinuxVDIUpdateListDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalComponentUpgradeResultEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.TerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.TerminalUpdateListCacheManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalVersionResultDTO;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.test.ThrowExceptionTester;
import mockit.Mock;
import mockit.MockUp;
import mockit.Tested;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

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
public class LinuxVDIComponentUpgradeHandlerTest {

    @Tested
    private LinuxVDIComponentUpgradeHandler handler;


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
        CbbLinuxVDIUpdateListDTO updatelist = new CbbLinuxVDIUpdateListDTO();
        updatelist.setComponentList(Collections.emptyList());

        new MockUp(TerminalUpdateListCacheManager.class) {

            private boolean isFirst = true;

            @Mock
            public CbbLinuxVDIUpdateListDTO get(TerminalTypeEnums terminalType) {
                if (isFirst) {
                    isFirst = false;
                    // 模拟返回空
                    return null;
                }

                return updatelist;
            }
        };

        TerminalUpdateListCacheManager.setUpdatelistCacheReady(TerminalTypeEnums.VDI_LINUX);
        GetVersionRequest request = new GetVersionRequest();
        request.setRainUpgradeVersion("123");
        request.setValidateMd5("xxx");
        TerminalVersionResultDTO terminalVersionResultDTO = handler.getVersion(request);
        assertEquals(CbbTerminalComponentUpgradeResultEnums.ABNORMAL.getResult(),
                terminalVersionResultDTO.getResult().intValue());
        TerminalVersionResultDTO terminalVersionResultDTO1 = handler.getVersion(request);
        assertEquals(CbbTerminalComponentUpgradeResultEnums.ABNORMAL.getResult(),
                terminalVersionResultDTO1.getResult().intValue());
        TerminalUpdateListCacheManager.setUpdatelistCacheNotReady(TerminalTypeEnums.VDI_LINUX);
    }

    /**
     * 测试getVersion,不升级
     */
    @Test
    public void testGetVersionNoUpgrade() {
        CbbLinuxVDIUpdateListDTO updatelist = new CbbLinuxVDIUpdateListDTO();
        List<CbbLinuxVDIComponentVersionInfoDTO> componentList = new ArrayList<>();
        componentList.add(new CbbLinuxVDIComponentVersionInfoDTO());
        updatelist.setComponentList(componentList);
        updatelist.setVersion("1.1.0.1");
        updatelist.setValidateMd5("123");
        updatelist.setComponentSize(1);
        updatelist.setBaseVersion("1.0.1.1");

        new MockUp(TerminalUpdateListCacheManager.class) {
            @Mock
            public CbbLinuxVDIUpdateListDTO get(TerminalTypeEnums terminalType) {
                return updatelist;
            }
        };

        GetVersionRequest request = new GetVersionRequest();
        request.setRainUpgradeVersion("1.1.0.1");
        request.setValidateMd5("123");
        TerminalUpdateListCacheManager.setUpdatelistCacheReady(TerminalTypeEnums.VDI_LINUX);
        TerminalVersionResultDTO terminalVersionResultDTO = handler.getVersion(request);
        assertEquals(CbbTerminalComponentUpgradeResultEnums.NOT.getResult(),
                terminalVersionResultDTO.getResult().intValue());
        TerminalUpdateListCacheManager.setUpdatelistCacheNotReady(TerminalTypeEnums.VDI_LINUX);
    }

    /**
     * 测试getVersion,非法的版本号
     */
    @Test
    public void testGetVersionRainUpgradeVersionIsIllegale() {
        CbbLinuxVDIUpdateListDTO updatelist = new CbbLinuxVDIUpdateListDTO();
        List<CbbLinuxVDIComponentVersionInfoDTO> componentList = new ArrayList<>();
        componentList.add(new CbbLinuxVDIComponentVersionInfoDTO());
        updatelist.setComponentList(componentList);
        updatelist.setVersion("1.1.0.1");
        updatelist.setComponentSize(1);
        updatelist.setBaseVersion("1.0.1.1");
        updatelist.setLimitVersion("1.0.0.1");
        updatelist.setValidateMd5("123");
        new MockUp(TerminalUpdateListCacheManager.class) {
            @Mock
            public CbbLinuxVDIUpdateListDTO get(TerminalTypeEnums terminalType) {
                return updatelist;
            }
        };

        GetVersionRequest request = new GetVersionRequest();
        request.setRainUpgradeVersion("111");
        request.setValidateMd5("123");
        TerminalUpdateListCacheManager.setUpdatelistCacheReady(TerminalTypeEnums.VDI_LINUX);
        TerminalVersionResultDTO terminalVersionResultDTO = handler.getVersion(request);
        assertEquals(CbbTerminalComponentUpgradeResultEnums.START.getResult(),
                terminalVersionResultDTO.getResult().intValue());
        TerminalUpdateListCacheManager.setUpdatelistCacheNotReady(TerminalTypeEnums.VDI_LINUX);
    }

    /**
     * 测试getVersion,低于最低支持版本
     */
    @Test
    public void testGetVersionLimitVersion() {
        CbbLinuxVDIUpdateListDTO updatelist = new CbbLinuxVDIUpdateListDTO();
        List<CbbLinuxVDIComponentVersionInfoDTO> componentList = new ArrayList<>();
        componentList.add(new CbbLinuxVDIComponentVersionInfoDTO());
        updatelist.setComponentList(componentList);
        updatelist.setVersion("1.1.0.1");
        updatelist.setComponentSize(1);
        updatelist.setBaseVersion("1.0.2.1");
        updatelist.setLimitVersion("1.0.1.1");

        new MockUp(Logger.class) {
            @Mock
            public boolean isDebugEnabled() {
                return true;
            }
        };

        new MockUp(TerminalUpdateListCacheManager.class) {
            @Mock
            public CbbLinuxVDIUpdateListDTO get(TerminalTypeEnums terminalType) {
                return updatelist;
            }
        };

        GetVersionRequest request = new GetVersionRequest();
        request.setRainUpgradeVersion("1.0.0.1");
        request.setValidateMd5("123");
        TerminalUpdateListCacheManager.setUpdatelistCacheReady(TerminalTypeEnums.VDI_LINUX);
        TerminalVersionResultDTO terminalVersionResultDTO = handler.getVersion(request);
        assertEquals(CbbTerminalComponentUpgradeResultEnums.NOT_SUPPORT.getResult(),
                terminalVersionResultDTO.getResult().intValue());
        TerminalUpdateListCacheManager.setUpdatelistCacheNotReady(TerminalTypeEnums.VDI_LINUX);
    }

    /**
     * 测试getVersion,正处于更新中
     */
    @Test
    public void testGetVersionIsUpdating() {
        CbbLinuxVDIUpdateListDTO updatelist = new CbbLinuxVDIUpdateListDTO();
        List<CbbLinuxVDIComponentVersionInfoDTO> componentList = new ArrayList<>();
        componentList.add(new CbbLinuxVDIComponentVersionInfoDTO());
        updatelist.setComponentList(componentList);
        updatelist.setVersion("1.1.0.1");
        updatelist.setComponentSize(1);
        updatelist.setBaseVersion("1.0.2.1");
        updatelist.setLimitVersion("1.0.1.1");

        GetVersionRequest request = new GetVersionRequest();
        request.setRainUpgradeVersion("1.0.0.1");
        request.setValidateMd5("123");
        TerminalVersionResultDTO terminalVersionResultDTO = handler.getVersion(request);
        assertEquals(CbbTerminalComponentUpgradeResultEnums.PREPARING.getResult(),
                terminalVersionResultDTO.getResult().intValue());

    }

}

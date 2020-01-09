package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.componentupgrade;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.updatelist.CbbWinAppComponentVersionInfoDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.updatelist.CbbWinAppUpdateListDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalComponentUpgradeResultEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.TerminalUpdateListCacheManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalVersionResultDTO;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import com.ruijie.rcos.sk.base.test.ThrowExceptionTester;

import mockit.Mock;
import mockit.MockUp;
import mockit.Tested;

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
        ThrowExceptionTester.throwIllegalArgumentException(() -> handler.getVersion(null), "get version request can not be null");
        Assert.assertTrue(true);
    }

    /**
     * 测试getVersion,updatelist为空
     */
    @Test
    public void testGetVersionUpdatelistIsNull() {
        CbbWinAppUpdateListDTO updatelist = new CbbWinAppUpdateListDTO();
        updatelist.setComponentList(Collections.emptyList());

        new MockUp(TerminalUpdateListCacheManager.class) {

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

        TerminalUpdateListCacheManager.setUpdatelistCacheReady(CbbTerminalTypeEnums.APP_WINDOWS);
        GetVersionDTO request = new GetVersionDTO();
        request.setRainUpgradeVersion("123");
        request.setValidateMd5("xxx");
        TerminalVersionResultDTO terminalVersionResultDTO = handler.getVersion(request);
        assertEquals(CbbTerminalComponentUpgradeResultEnums.ABNORMAL.getResult(), terminalVersionResultDTO.getResult().intValue());
        TerminalVersionResultDTO terminalVersionResultDTO1 = handler.getVersion(request);
        assertEquals(CbbTerminalComponentUpgradeResultEnums.ABNORMAL.getResult(), terminalVersionResultDTO1.getResult().intValue());
        TerminalUpdateListCacheManager.setUpdatelistCacheNotReady(CbbTerminalTypeEnums.APP_WINDOWS);
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

        new MockUp(TerminalUpdateListCacheManager.class) {
            @Mock
            public CbbWinAppUpdateListDTO get(CbbTerminalTypeEnums terminalType) {
                return updatelist;
            }
        };

        GetVersionDTO request = new GetVersionDTO();
        request.setRainUpgradeVersion("1.1.0.1");
        request.setValidateMd5("123");
        TerminalUpdateListCacheManager.setUpdatelistCacheReady(CbbTerminalTypeEnums.APP_WINDOWS);
        TerminalVersionResultDTO terminalVersionResultDTO = handler.getVersion(request);
        assertEquals(CbbTerminalComponentUpgradeResultEnums.NOT.getResult(), terminalVersionResultDTO.getResult().intValue());
        TerminalUpdateListCacheManager.setUpdatelistCacheNotReady(CbbTerminalTypeEnums.APP_WINDOWS);
    }

    /**
     * 测试getVersion,非法的版本号
     */
    @Test
    public void testGetVersionRainUpgradeVersionIsIllegale() {
        CbbWinAppUpdateListDTO updatelist = getCbbWinAppUpdateListDTO();
        new MockUp(TerminalUpdateListCacheManager.class) {
            @Mock
            public CbbWinAppUpdateListDTO get(CbbTerminalTypeEnums terminalType) {
                return updatelist;
            }
        };

        GetVersionDTO request = new GetVersionDTO();
        request.setRainUpgradeVersion("111");
        request.setValidateMd5("123");
        TerminalUpdateListCacheManager.setUpdatelistCacheReady(CbbTerminalTypeEnums.APP_WINDOWS);
        TerminalVersionResultDTO<CbbWinAppUpdateListDTO> version = handler.getVersion(request);
        assertEquals(0, version.getResult().intValue());
        TerminalUpdateListCacheManager.setUpdatelistCacheNotReady(CbbTerminalTypeEnums.APP_WINDOWS);
    }

    /**
     * 测试getVersion,低于最低支持版本
     */
    @Test
    public void testGetVersionLessThanLimitVersion() {
        CbbWinAppUpdateListDTO updateList = getCbbWinAppUpdateListDTO();


        new MockUp(TerminalUpdateListCacheManager.class) {
            @Mock
            public CbbWinAppUpdateListDTO get(CbbTerminalTypeEnums terminalType) {
                return updateList;
            }
        };

        GetVersionDTO request = new GetVersionDTO();
        request.setRainUpgradeVersion("1.0.0.1");
        request.setValidateMd5("123");
        TerminalUpdateListCacheManager.setUpdatelistCacheReady(CbbTerminalTypeEnums.APP_WINDOWS);

        CbbWinAppUpdateListDTO expectUpdatelist = getCbbWinAppUpdateListDTO();
        expectUpdatelist.setComponentList(Collections.emptyList());
        expectUpdatelist.setComponentSize(0);

        TerminalVersionResultDTO terminalVersionResultDTO = handler.getVersion(request);
        assertEquals(CbbTerminalComponentUpgradeResultEnums.START.getResult(), terminalVersionResultDTO.getResult().intValue());
        CbbWinAppUpdateListDTO returnUpdateList = (CbbWinAppUpdateListDTO) terminalVersionResultDTO.getUpdatelist();
        assertEquals(expectUpdatelist, returnUpdateList);

        TerminalUpdateListCacheManager.setUpdatelistCacheNotReady(CbbTerminalTypeEnums.APP_WINDOWS);
    }

    /**
     * 测试getVersion, 不低于最低支持版本
     */
    @Test
    public void testGetVersion() {
        CbbWinAppUpdateListDTO updatelist = getCbbWinAppUpdateListDTO();


        new MockUp(TerminalUpdateListCacheManager.class) {
            @Mock
            public CbbWinAppUpdateListDTO get(CbbTerminalTypeEnums terminalType) {
                return updatelist;
            }
        };

        CbbWinAppUpdateListDTO expectUpdatelist = getCbbWinAppUpdateListDTO();
        expectUpdatelist.setName(StringUtils.EMPTY);
        expectUpdatelist.setCompletePackageName(StringUtils.EMPTY);
        expectUpdatelist.setCompletePackageUrl(StringUtils.EMPTY);
        expectUpdatelist.setMd5(StringUtils.EMPTY);

        GetVersionDTO request = new GetVersionDTO();
        request.setRainUpgradeVersion("1.0.1.1");
        request.setValidateMd5("123");
        TerminalUpdateListCacheManager.setUpdatelistCacheReady(CbbTerminalTypeEnums.APP_WINDOWS);
        TerminalVersionResultDTO terminalVersionResultDTO = handler.getVersion(request);
        CbbWinAppUpdateListDTO returnUpdateList = (CbbWinAppUpdateListDTO) terminalVersionResultDTO.getUpdatelist();

        assertEquals(CbbTerminalComponentUpgradeResultEnums.START.getResult(), terminalVersionResultDTO.getResult().intValue());
        assertEquals(expectUpdatelist, returnUpdateList);

        TerminalUpdateListCacheManager.setUpdatelistCacheNotReady(CbbTerminalTypeEnums.APP_WINDOWS);
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

        GetVersionDTO request = new GetVersionDTO();
        request.setRainUpgradeVersion("1.0.0.1");
        request.setValidateMd5("123");
        TerminalVersionResultDTO terminalVersionResultDTO = handler.getVersion(request);
        assertEquals(CbbTerminalComponentUpgradeResultEnums.PREPARING.getResult(), terminalVersionResultDTO.getResult().intValue());

    }

    private CbbWinAppUpdateListDTO getCbbWinAppUpdateListDTO() {
        CbbWinAppUpdateListDTO updatelist = new CbbWinAppUpdateListDTO();
        List<CbbWinAppComponentVersionInfoDTO> componentList = new ArrayList<>();
        CbbWinAppComponentVersionInfoDTO complete = new CbbWinAppComponentVersionInfoDTO();
        CbbWinAppComponentVersionInfoDTO component = new CbbWinAppComponentVersionInfoDTO();
        componentList.add(complete);
        componentList.add(component);
        updatelist.setComponentList(componentList);
        updatelist.setVersion("1.1.0.1");
        updatelist.setComponentSize(1);
        updatelist.setLimitVersion("1.0.1.1");
        updatelist.setValidateMd5("123");
        return updatelist;
    }

}

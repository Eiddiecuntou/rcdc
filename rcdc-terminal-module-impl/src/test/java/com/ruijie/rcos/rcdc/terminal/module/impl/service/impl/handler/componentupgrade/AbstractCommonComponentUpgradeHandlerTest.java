package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.componentupgrade;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.ruijie.rcos.rcdc.terminal.module.impl.dto.CommonComponentVersionInfoDTO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dto.CommonUpdateListDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalComponentUpgradeResultEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.TerminalUpdateListCacheManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalVersionResultDTO;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.test.ThrowExceptionTester;

import mockit.Mock;
import mockit.MockUp;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/11/7
 *
 * @author nt
 */
@RunWith(SkyEngineRunner.class)
public class AbstractCommonComponentUpgradeHandlerTest {


    /**
     * 测试getVersion,参数为空
     *
     * @throws Exception 异常
     */
    @Test
    public void testGetVersionArgumentIsNull() throws Exception {
        TestedComponentUpgradeHandler handler = new TestedComponentUpgradeHandler();
        ThrowExceptionTester.throwIllegalArgumentException(() -> handler.getVersion(null), "get version request can not be null");
        Assert.assertTrue(true);
    }

    /**
     * 测试getVersion,updatelist为空
     */
    @Test
    public void testGetVersionUpdatelistIsNull() {
        TestedComponentUpgradeHandler handler = new TestedComponentUpgradeHandler();

        CommonUpdateListDTO updatelist = new CommonUpdateListDTO();
        updatelist.setComponentList(Collections.emptyList());

        new MockUp(TerminalUpdateListCacheManager.class) {

            private boolean isFirst = true;

            @Mock
            public CommonUpdateListDTO get(CbbTerminalTypeEnums terminalType) {
                if (isFirst) {
                    isFirst = false;
                    // 模拟返回空
                    return null;
                }

                return updatelist;
            }
        };

        TerminalUpdateListCacheManager.setUpdatelistCacheReady(CbbTerminalTypeEnums.VDI_ANDROID);
        GetVersionDTO request = new GetVersionDTO();
        request.setRainUpgradeVersion("123");
        request.setValidateMd5("xxx");
        TerminalVersionResultDTO terminalVersionResultDTO = handler.getVersion(request);
        assertEquals(CbbTerminalComponentUpgradeResultEnums.ABNORMAL.getResult(), terminalVersionResultDTO.getResult().intValue());
        TerminalVersionResultDTO terminalVersionResultDTO1 = handler.getVersion(request);
        assertEquals(CbbTerminalComponentUpgradeResultEnums.ABNORMAL.getResult(), terminalVersionResultDTO1.getResult().intValue());
        TerminalUpdateListCacheManager.setUpdatelistCacheNotReady(CbbTerminalTypeEnums.VDI_ANDROID);
    }

    /**
     * 测试getVersion,不升级
     */
    @Test
    public void testGetVersionNoUpgrade() {
        TestedComponentUpgradeHandler handler = new TestedComponentUpgradeHandler();

        CommonUpdateListDTO updatelist = new CommonUpdateListDTO();
        List<CommonComponentVersionInfoDTO> componentList = new ArrayList<>();
        componentList.add(new CommonComponentVersionInfoDTO());
        updatelist.setComponentList(componentList);
        updatelist.setVersion("1.1.0.1");
        updatelist.setValidateMd5("123");
        updatelist.setComponentSize(1);
        updatelist.setBaseVersion("1.0.1.1");
        updatelist.setOsLimit("1.0.2.1");
        new MockUp(TerminalUpdateListCacheManager.class) {
            @Mock
            public CommonUpdateListDTO get(CbbTerminalTypeEnums terminalType) {
                return updatelist;
            }
        };

        GetVersionDTO request = new GetVersionDTO();
        request.setRainUpgradeVersion("1.1.0.1");
        request.setValidateMd5("123");
        request.setOsInnerVersion("1.1.0.1");
        TerminalUpdateListCacheManager.setUpdatelistCacheReady(CbbTerminalTypeEnums.VDI_ANDROID);
        TerminalVersionResultDTO terminalVersionResultDTO = handler.getVersion(request);
        assertEquals(CbbTerminalComponentUpgradeResultEnums.NOT.getResult(), terminalVersionResultDTO.getResult().intValue());
        TerminalUpdateListCacheManager.setUpdatelistCacheNotReady(CbbTerminalTypeEnums.VDI_ANDROID);
    }

    /**
     * 测试getVersion,版本号长度超过限制版本
     */
    @Test
    public void testGetVersionRainUpgradeVersionIsIllegale() {
        TestedComponentUpgradeHandler handler = new TestedComponentUpgradeHandler();

        CommonUpdateListDTO updatelist = new CommonUpdateListDTO();
        List<CommonComponentVersionInfoDTO> componentList = new ArrayList<>();
        componentList.add(new CommonComponentVersionInfoDTO());
        updatelist.setComponentList(componentList);
        updatelist.setVersion("1.1.0.1");
        updatelist.setComponentSize(1);
        updatelist.setBaseVersion("1.0.1.1");
        updatelist.setLimitVersion("1.0.0.1");
        updatelist.setValidateMd5("123");
        updatelist.setOsLimit("1.0.2.1");
        new MockUp(TerminalUpdateListCacheManager.class) {
            @Mock
            public CommonUpdateListDTO get(CbbTerminalTypeEnums terminalType) {
                return updatelist;
            }
        };

        GetVersionDTO request = new GetVersionDTO();
        request.setRainUpgradeVersion("111");
        request.setValidateMd5("123");
        request.setOsInnerVersion("1.1.1.1.1");
        TerminalUpdateListCacheManager.setUpdatelistCacheReady(CbbTerminalTypeEnums.VDI_ANDROID);
        TerminalVersionResultDTO<CommonUpdateListDTO> version = handler.getVersion(request);
        assertEquals(0, version.getResult().intValue());
        TerminalUpdateListCacheManager.setUpdatelistCacheNotReady(CbbTerminalTypeEnums.VDI_ANDROID);
    }

    /**
     * 测试getVersion,低于最低支持版本
     */
    @Test
    public void testGetVersionLimitVersion() {
        TestedComponentUpgradeHandler handler = new TestedComponentUpgradeHandler();

        CommonUpdateListDTO updatelist = new CommonUpdateListDTO();
        List<CommonComponentVersionInfoDTO> componentList = new ArrayList<>();
        componentList.add(new CommonComponentVersionInfoDTO());
        updatelist.setComponentList(componentList);
        updatelist.setVersion("1.1.0.1");
        updatelist.setComponentSize(1);
        updatelist.setBaseVersion("1.0.2.1");
        updatelist.setLimitVersion("1.0.1.1");
        updatelist.setOsLimit("1.0.2");

        new MockUp(Logger.class) {
            @Mock
            public boolean isDebugEnabled() {
                return true;
            }
        };

        new MockUp(TerminalUpdateListCacheManager.class) {
            @Mock
            public CommonUpdateListDTO get(CbbTerminalTypeEnums terminalType) {
                return updatelist;
            }
        };

        GetVersionDTO request = new GetVersionDTO();
        request.setRainUpgradeVersion("1.0.0.1");
        request.setValidateMd5("123");
        request.setOsInnerVersion("1.0.1");
        TerminalUpdateListCacheManager.setUpdatelistCacheReady(CbbTerminalTypeEnums.VDI_ANDROID);
        TerminalVersionResultDTO terminalVersionResultDTO = handler.getVersion(request);
        assertEquals(CbbTerminalComponentUpgradeResultEnums.NOT_SUPPORT_FOR_LOWER_OS_VERSION.getResult(),
                terminalVersionResultDTO.getResult().intValue());
        TerminalUpdateListCacheManager.setUpdatelistCacheNotReady(CbbTerminalTypeEnums.VDI_ANDROID);
    }

    /**
     * 测试getVersion,正处于更新中
     */
    @Test
    public void testGetVersionIsUpdating() {
        TestedComponentUpgradeHandler handler = new TestedComponentUpgradeHandler();

        new MockUp<TerminalUpdateListCacheManager>() {
            @Mock
            public boolean isCacheReady(CbbTerminalTypeEnums terminalType) {
                return false;
            }
        };

        CommonUpdateListDTO updatelist = new CommonUpdateListDTO();
        List<CommonComponentVersionInfoDTO> componentList = new ArrayList<>();
        componentList.add(new CommonComponentVersionInfoDTO());
        updatelist.setComponentList(componentList);
        updatelist.setVersion("1.1.0.1");
        updatelist.setComponentSize(1);
        updatelist.setBaseVersion("1.0.2.1");
        updatelist.setLimitVersion("1.0.1.1");

        GetVersionDTO request = new GetVersionDTO();
        request.setRainUpgradeVersion("1.0.0.1");
        request.setValidateMd5("123");
        TerminalVersionResultDTO terminalVersionResultDTO = handler.getVersion(request);
        assertEquals(CbbTerminalComponentUpgradeResultEnums.PREPARING.getResult(), terminalVersionResultDTO.getResult().intValue());

    }

    /**
     * 测试getVersion - 进行差异升级
     */
    @Test
    public void testGetVersionStartDiffUpgrade() {
        TestedComponentUpgradeHandler handler = new TestedComponentUpgradeHandler();

        CommonUpdateListDTO updatelist = new CommonUpdateListDTO();
        List<CommonComponentVersionInfoDTO> componentList = getCbbCommonComponentVersionInfoDTOS();
        updatelist.setComponentList(componentList);
        updatelist.setVersion("1.1.0.1");
        updatelist.setComponentSize(1);
        updatelist.setBaseVersion("1.0.2.1");
        updatelist.setLimitVersion("1.0.1.1");
        updatelist.setOsLimit("1.0.2");

        new MockUp(Logger.class) {
            @Mock
            public boolean isDebugEnabled() {
                return true;
            }
        };

        new MockUp(TerminalUpdateListCacheManager.class) {
            @Mock
            public CommonUpdateListDTO get(CbbTerminalTypeEnums terminalType) {
                return updatelist;
            }
        };

        GetVersionDTO request = new GetVersionDTO();
        request.setRainUpgradeVersion("1.0.2.1");
        request.setValidateMd5("123");
        request.setOsInnerVersion("1.0.2");

        TerminalUpdateListCacheManager.setUpdatelistCacheReady(CbbTerminalTypeEnums.VDI_ANDROID);
        TerminalVersionResultDTO<CommonUpdateListDTO> terminalVersionResultDTO = handler.getVersion(request);

        CommonComponentVersionInfoDTO expectComponent = new CommonComponentVersionInfoDTO();
        expectComponent.setCompletePackageName("abc");
        expectComponent.setIncrementalPackageMd5("123");
        expectComponent.setIncrementalPackageName("234");
        expectComponent.setIncrementalTorrentMd5("345");
        expectComponent.setIncrementalTorrentUrl("456");
        expectComponent.setBasePackageName("567");
        expectComponent.setBasePackageMd5("678");

        assertEquals(CbbTerminalComponentUpgradeResultEnums.START.getResult(),
                terminalVersionResultDTO.getResult().intValue());
        assertEquals(1, terminalVersionResultDTO.getUpdatelist().getComponentList().size());
        assertEquals(expectComponent, terminalVersionResultDTO.getUpdatelist().getComponentList().get(0));
        TerminalUpdateListCacheManager.setUpdatelistCacheNotReady(CbbTerminalTypeEnums.VDI_ANDROID);
    }

    /**
     * 测试getVersion - 进行完整升级
     */
    @Test
    public void testGetVersionStartCompleteUpgrade() {
        TestedComponentUpgradeHandler handler = new TestedComponentUpgradeHandler();

        CommonUpdateListDTO updatelist = new CommonUpdateListDTO();
        List<CommonComponentVersionInfoDTO> componentList = getCbbCommonComponentVersionInfoDTOS();
        updatelist.setComponentList(componentList);
        updatelist.setVersion("1.1.0.1");
        updatelist.setComponentSize(1);
        updatelist.setBaseVersion("1.0.3.1");
        updatelist.setLimitVersion("1.0.1.1");
        updatelist.setOsLimit("1.0.2");

        new MockUp(Logger.class) {
            @Mock
            public boolean isDebugEnabled() {
                return true;
            }
        };

        new MockUp(TerminalUpdateListCacheManager.class) {
            @Mock
            public CommonUpdateListDTO get(CbbTerminalTypeEnums terminalType) {
                return updatelist;
            }
        };

        GetVersionDTO request = new GetVersionDTO();
        request.setRainUpgradeVersion("1.0.2.1");
        request.setValidateMd5("123");
        request.setOsInnerVersion("1.0.2");

        TerminalUpdateListCacheManager.setUpdatelistCacheReady(CbbTerminalTypeEnums.VDI_ANDROID);
        TerminalVersionResultDTO<CommonUpdateListDTO> terminalVersionResultDTO = handler.getVersion(request);

        CommonComponentVersionInfoDTO expectComponent = new CommonComponentVersionInfoDTO();
        expectComponent.setCompletePackageName("abc");

        assertEquals(CbbTerminalComponentUpgradeResultEnums.START.getResult(),
                terminalVersionResultDTO.getResult().intValue());
        assertEquals(1, terminalVersionResultDTO.getUpdatelist().getComponentList().size());
        assertEquals(expectComponent, terminalVersionResultDTO.getUpdatelist().getComponentList().get(0));
        TerminalUpdateListCacheManager.setUpdatelistCacheNotReady(CbbTerminalTypeEnums.VDI_ANDROID);
    }

    private List<CommonComponentVersionInfoDTO> getCbbCommonComponentVersionInfoDTOS() {
        List<CommonComponentVersionInfoDTO> componentList = new ArrayList<>();
        CommonComponentVersionInfoDTO component = new CommonComponentVersionInfoDTO();
        component.setCompletePackageName("abc");
        component.setIncrementalPackageMd5("123");
        component.setIncrementalPackageName("234");
        component.setIncrementalTorrentMd5("345");
        component.setIncrementalTorrentUrl("456");
        component.setBasePackageName("567");
        component.setBasePackageMd5("678");
        componentList.add(component);
        return componentList;
    }

    /**
     * Description: 测试类
     * Copyright: Copyright (c) 2018
     * Company: Ruijie Co., Ltd.
     * Create Time: 2019/11/7
     *
     * @author nt
     */
    private class TestedComponentUpgradeHandler extends AbstractCommonComponentUpgradeHandler {

        @Override
        protected CbbTerminalTypeEnums getTerminalType() {
            return CbbTerminalTypeEnums.VDI_ANDROID;
        }
    }
}

package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalTypeEnums;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.updatelist.CbbLinuxVDIUpdateListDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.updatelist.CbbLinuxVDIComponentVersionInfoDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalComponentUpgradeResultEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.TerminalPlatformEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.VDITerminalUpdateListCacheManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalVersionResultDTO;
import com.ruijie.rcos.sk.base.test.ThrowExceptionTester;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;

/**
 * 
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年1月23日
 * 
 * @author ls
 */
@RunWith(JMockit.class)
public class TerminalComponentUpgradeServiceImplTest {

    @Tested
    private TerminalComponentUpgradeServiceImpl serviceImpl;


    @Injectable
    private VDITerminalUpdateListCacheManager cacheManager;

    /**
     * 测试getVersion,参数为空
     * 
     * @throws Exception 异常
     */
    @Test
    public void testGetVersionArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> serviceImpl.getVersion("", null, TerminalPlatformEnums.VDI),
                "rainOsVersion can not be blank");
        ThrowExceptionTester.throwIllegalArgumentException(() -> serviceImpl.getVersion("123",null, null), "platform can not be null");
        assertTrue(true);
    }

    /**
     * 测试getVersion,updatelist为空
     */
    @Test
    public void testGetVersionUpdatelistIsNull() {
        CbbLinuxVDIUpdateListDTO updatelist = new CbbLinuxVDIUpdateListDTO();
        updatelist.setComponentList(Collections.emptyList());
        new Expectations() {
            {
                cacheManager.get(CbbTerminalTypeEnums.LINUX);
                returns(null, updatelist);
            }
        };
        cacheManager.setUpdatelistCacheReady();
        TerminalVersionResultDTO terminalVersionResultDTO = serviceImpl.getVersion("123", null, TerminalPlatformEnums.VDI);
        assertEquals(CbbTerminalComponentUpgradeResultEnums.ABNORMAL.getResult(), terminalVersionResultDTO.getResult().intValue());
        TerminalVersionResultDTO terminalVersionResultDTO1 = serviceImpl.getVersion("123", null, TerminalPlatformEnums.VDI);
        assertEquals(CbbTerminalComponentUpgradeResultEnums.ABNORMAL.getResult(), terminalVersionResultDTO1.getResult().intValue());
        cacheManager.setUpdatelistCacheNotReady();
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
        updatelist.setComponentSize(1);
        updatelist.setBaseVersion("1.0.1.1");
        new Expectations() {
            {
                cacheManager.get(CbbTerminalTypeEnums.LINUX);
                result = updatelist;
            }
        };
        cacheManager.setUpdatelistCacheReady();
        TerminalVersionResultDTO terminalVersionResultDTO = serviceImpl.getVersion("1.1.0.1", null, TerminalPlatformEnums.VDI);
        assertEquals(CbbTerminalComponentUpgradeResultEnums.NOT.getResult(), terminalVersionResultDTO.getResult().intValue());
        cacheManager.setUpdatelistCacheNotReady();
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
        new Expectations() {
            {
                cacheManager.get(CbbTerminalTypeEnums.LINUX);
                result = updatelist;
            }
        };
        cacheManager.setUpdatelistCacheReady();
        TerminalVersionResultDTO terminalVersionResultDTO = serviceImpl.getVersion("111", null, TerminalPlatformEnums.VDI);
        assertEquals(CbbTerminalComponentUpgradeResultEnums.START.getResult(), terminalVersionResultDTO.getResult().intValue());
        cacheManager.setUpdatelistCacheNotReady();
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
        new Expectations() {
            {
                cacheManager.get(CbbTerminalTypeEnums.LINUX);
                result = updatelist;
            }
        };
        cacheManager.setUpdatelistCacheReady();
        TerminalVersionResultDTO terminalVersionResultDTO = serviceImpl.getVersion("1.0.0.1", null, TerminalPlatformEnums.VDI);
        assertEquals(CbbTerminalComponentUpgradeResultEnums.NOT_SUPPORT.getResult(), terminalVersionResultDTO.getResult().intValue());
        cacheManager.setUpdatelistCacheNotReady();
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
        TerminalVersionResultDTO terminalVersionResultDTO = serviceImpl.getVersion("1.0.1.1", null, TerminalPlatformEnums.VDI);
        assertEquals(CbbTerminalComponentUpgradeResultEnums.PREPARING.getResult(), terminalVersionResultDTO.getResult().intValue());

    }

}

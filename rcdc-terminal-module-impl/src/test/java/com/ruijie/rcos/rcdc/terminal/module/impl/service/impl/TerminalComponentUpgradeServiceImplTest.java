package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.util.Collections;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalComponentUpdateListDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalComponentUpgradeResultEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.TerminalPlatformEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.ComponentUpdateListCacheManager;
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
    private ComponentUpdateListCacheManager cacheManager;
    
    /**
     * 测试getVersion,参数为空
     * @throws Exception 异常
     */
    @Test
    public void testGetVersionArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> serviceImpl.getVersion("", TerminalPlatformEnums.VDI),
                "rainOsVersion can not be blank");
        ThrowExceptionTester.throwIllegalArgumentException(() -> serviceImpl.getVersion("123", null),
                "platform can not be null");
        assertTrue(true);
    }
    
    /**
     * 测试getVersion,updatelist为空
     */
    @Test
    public void testGetVersionUpdatelistIsNull() {
        CbbTerminalComponentUpdateListDTO updatelist = new CbbTerminalComponentUpdateListDTO();
        updatelist.setComponentList(Collections.emptyList());
        new Expectations() {
            {
                cacheManager.getCache(TerminalPlatformEnums.VDI);
                returns(null, updatelist);
            }
        };
        TerminalVersionResultDTO terminalVersionResultDTO = serviceImpl.getVersion("123", TerminalPlatformEnums.VDI);
        assertEquals(CbbTerminalComponentUpgradeResultEnums.NOT_SUPPORT.getResult(), terminalVersionResultDTO.getResult().intValue());
        TerminalVersionResultDTO terminalVersionResultDTO1 = serviceImpl.getVersion("123", TerminalPlatformEnums.VDI);
        assertEquals(CbbTerminalComponentUpgradeResultEnums.NOT_SUPPORT.getResult(), terminalVersionResultDTO1.getResult().intValue());
    }
    
    /**
     * 测试getVersion,updatelist为空
     */
    @Test
    public void testGetVersion() {
        CbbTerminalComponentUpdateListDTO updatelist = new CbbTerminalComponentUpdateListDTO();
        updatelist.setComponentList(Collections.emptyList());
        updatelist.setVersion("1.1.0");
        updatelist.setComponentSize(1);
        updatelist.setBaseVersion("1.0.1");
        new Expectations() {
            {
                cacheManager.getCache(TerminalPlatformEnums.VDI);
                result = updatelist;
            }
        };
        TerminalVersionResultDTO terminalVersionResultDTO = serviceImpl.getVersion("123", TerminalPlatformEnums.VDI);
        assertEquals(CbbTerminalComponentUpgradeResultEnums.NOT_SUPPORT.getResult(), terminalVersionResultDTO.getResult().intValue());
    }

}

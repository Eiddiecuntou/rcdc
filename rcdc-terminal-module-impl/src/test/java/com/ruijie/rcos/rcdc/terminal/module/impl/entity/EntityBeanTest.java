package com.ruijie.rcos.rcdc.terminal.module.impl.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalDetectDTO;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.DetectStateEnums;
import com.ruijie.rcos.sk.base.i18n.LocaleI18nResolver;
import com.ruijie.rcos.sk.base.test.GetSetTester;
import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;

/**
 * 
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年1月24日
 * 
 * @author ls
 */
@RunWith(JMockit.class)
public class EntityBeanTest {

    /**
     * 测试TerminalDetectionEntity
     * @param resolver mock LocaleI18nResolver
     */
    @Test
    public void testTerminalDetectionEntity(@Mocked LocaleI18nResolver resolver) {
        GetSetTester tester = new GetSetTester(TerminalDetectionEntity.class);
        tester.runTest();
        assertTrue(true);
        
        TerminalDetectionEntity entity = new TerminalDetectionEntity();
        entity.setDetectState(DetectStateEnums.SUCCESS);
        
        new Expectations() {
            {
                LocaleI18nResolver.resolve(DetectStateEnums.SUCCESS.getName());
                result = "msg";
            }
        };
        CbbTerminalDetectDTO detectDTO = new CbbTerminalDetectDTO();
        entity.convertTo(detectDTO);
        assertEquals(entity.getTerminalId(), detectDTO.getTerminalId());
        assertEquals(entity.getAccessInternet(), detectDTO.getAccessInternet());
        assertEquals(entity.getBandwidth(), detectDTO.getBandwidth());
        assertEquals(entity.getNetworkDelay(), detectDTO.getDelay());
        assertEquals(entity.getIpConflict(), detectDTO.getIpConflict());
        assertEquals(entity.getPacketLossRate(), detectDTO.getPacketLossRate());
        assertEquals(entity.getDetectTime(), detectDTO.getDetectTime());
        assertEquals(entity.getDetectState().name(), detectDTO.getCheckState().getState());
        assertEquals("msg", detectDTO.getCheckState().getMessage());
    }
    
    /**
     * 测试TerminalEntity
     */
    @Test
    public void testTerminalEntity() {
        GetSetTester tester = new GetSetTester(TerminalEntity.class);
        tester.runTest();
        assertTrue(true);
    }
    
    /**
     * 测试TerminalSystemUpgradePackageEntity
     */
    @Test
    public void testTerminalSystemUpgradePackageEntity() {
        GetSetTester tester = new GetSetTester(TerminalSystemUpgradePackageEntity.class);
        tester.runTest();
        assertTrue(true);
    }
}

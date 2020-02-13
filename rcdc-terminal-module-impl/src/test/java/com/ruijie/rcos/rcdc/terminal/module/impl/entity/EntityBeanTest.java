package com.ruijie.rcos.rcdc.terminal.module.impl.entity;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalDetectDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.DetectStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalUpgradeVersionFileInfo;
import com.ruijie.rcos.sk.base.i18n.LocaleI18nResolver;
import com.ruijie.rcos.sk.base.test.GetSetTester;
import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
     * 
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

    /**
     * 测试TerminalUpgradeVersionFileInfo
     */
    @Test
    public void testTerminalUpgradeVersionFileInfo() {
        GetSetTester tester = new GetSetTester(TerminalUpgradeVersionFileInfo.class);
        tester.runTest();
        TerminalUpgradeVersionFileInfo info = new TerminalUpgradeVersionFileInfo();
        info.setFilePath("filePath");
        info.setImgName("imgName");
        info.setPackageName("packageName");
        info.setPackageType(CbbTerminalTypeEnums.VDI_LINUX);
        info.setVersion("version");
        info.setFileMD5("fileMD5");
        info.setSeedLink("seedLink");
        info.setSeedMD5("seedMD5");
        info.setOtaScriptPath("otaScriptPath");
        info.setOtaScriptMD5("otaScriptMD5");
        info.setFileSaveDir("fileSaveDir");
        info.setRealFileName("realFileName");
        String result = "TerminalUpgradeVersionFileInfo{" +
                "packageName='" + "packageName" + '\'' +
                ", imgName='" + "imgName" + '\'' +
                ", version='" + "version" + '\'' +
                ", filePath='" + "filePath" + '\'' +
                ", packageType=" + "VDI_LINUX" +
                ", fileMD5='" + "fileMD5" + '\'' +
                ", seedLink='" + "seedLink" + '\'' +
                ", seedMD5='" + "seedMD5" + '\'' +
                ", fileSaveDir='" + "fileSaveDir" + '\'' +
                ", realFileName='" + "realFileName" + '\'' +
                ", otaScriptPath='" + "otaScriptPath" + '\'' +
                ", otaScriptMD5='" + "otaScriptMD5" + '\'' +
                '}';
        assertEquals(result, info.toString());
    }

    /**
     * 测试TerminalGroupEntity
     */
    @Test
    public void testTerminalGroupEntity() {
        GetSetTester tester = new GetSetTester(TerminalGroupEntity.class);
        tester.runTest();
        assertTrue(true);
    }

    /**
     * 测试TerminalSystemUpgradeEntity
     */
    @Test
    public void testTerminalSystemUpgradeEntity() {
        GetSetTester tester = new GetSetTester(TerminalSystemUpgradeEntity.class);
        tester.runTest();
        assertTrue(true);
    }

    /**
     * 测试TerminalSystemUpgradeTerminalEntity
     */
    @Test
    public void testTerminalSystemUpgradeTerminalEntity() {
        GetSetTester tester = new GetSetTester(TerminalSystemUpgradeTerminalEntity.class);
        tester.runTest();
        assertTrue(true);
    }

    /**
     * 测试ViewUpgradeableTerminalEntity
     */
    @Test
    public void testViewUpgradeableTerminalEntity() {
        GetSetTester tester = new GetSetTester(ViewUpgradeableTerminalEntity.class);
        tester.runTest();
        assertTrue(true);
    }

    /**
     * 测试TerminalSystemUpgradeTerminalGroupEntity
     */
    @Test
    public void testTerminalSystemUpgradeTerminalGroupEntity() {
        GetSetTester tester = new GetSetTester(TerminalSystemUpgradeTerminalGroupEntity.class);
        tester.runTest();
        assertTrue(true);
    }

    /**
     * 测试TerminalModelDriverEntity
     */
    @Test
    public void testTerminalModelDriverEntity() {
        GetSetTester tester = new GetSetTester(TerminalModelDriverEntity.class);
        tester.runTest();
        assertTrue(true);
    }
}

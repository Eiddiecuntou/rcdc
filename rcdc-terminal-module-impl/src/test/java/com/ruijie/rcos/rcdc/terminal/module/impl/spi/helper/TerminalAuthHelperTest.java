package com.ruijie.rcos.rcdc.terminal.module.impl.spi.helper;

import com.alibaba.fastjson.JSON;
import com.ruijie.rcos.rcdc.codec.adapter.def.dto.CbbDispatcherRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbShineTerminalBasicInfo;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalBizConfigDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeTaskStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalWorkModeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalPlatformEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.CbbTerminalConnectHandlerSPI;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.TerminalAuthResultEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.SystemUpgradeResultInfo;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalAuthResult;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalBasicInfoService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalLicenseService;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.Verifications;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.UUID;

/**
 * Description:
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/5/24
 *
 * @author nt
 */
@RunWith(SkyEngineRunner.class)
public class TerminalAuthHelperTest {

    @Tested
    private TerminalAuthHelper helper;

    @Injectable
    private TerminalLicenseService terminalLicenseService;

    @Injectable
    private TerminalBasicInfoService basicInfoService;

    @Injectable
    private CbbTerminalConnectHandlerSPI connectHandlerSPI;

    /**
     * testProcessTerminalAuth
     */
    @Test
    public void testProcessTerminalAuthNotNewTerminal() {

        String terminalId = "123";
        commonExpectations();

        new Expectations() {
            {
                basicInfoService.isAuthed(terminalId);
                result = true;
            }
        };
        CbbShineTerminalBasicInfo basicInfo = new CbbShineTerminalBasicInfo();
        basicInfo.setTerminalId(terminalId);
        TerminalAuthResult authResult = helper.processTerminalAuth(true, true, basicInfo);
        Assert.assertEquals(authResult.getAuthResult(), TerminalAuthResultEnums.SKIP);
        Assert.assertTrue(authResult.isNeedSaveTerminalInfo());

        new Verifications() {
            {
                connectHandlerSPI.notifyTerminalSupport(basicInfo);
                times = 1;

                basicInfoService.isAuthed(terminalId);
                times = 1;
            }
        };
    }

    /**
     * testProcessTerminalAuthIsInUpgradeProcess
     */
    @Test
    public void testProcessTerminalAuthIsInUpgradeProcess() {

        String terminalId = "123";
        commonExpectations();

        new Expectations() {
            {
                basicInfoService.isAuthed(terminalId);
                result = false;
            }
        };
        CbbShineTerminalBasicInfo basicInfo = new CbbShineTerminalBasicInfo();
        basicInfo.setTerminalId(terminalId);
        TerminalAuthResult authResult = helper.processTerminalAuth(true, true, basicInfo);
        Assert.assertEquals(authResult.getAuthResult(), TerminalAuthResultEnums.SKIP);
        Assert.assertTrue(!authResult.isNeedSaveTerminalInfo());

        new Verifications() {
            {
                connectHandlerSPI.notifyTerminalSupport(basicInfo);
                times = 1;

                basicInfoService.isAuthed(terminalId);
                times = 1;
            }
        };
    }

    /**
     * testProcessTerminalAuthWorkModeIsEmpty
     */
    @Test
    public void testProcessTerminalAuthWorkModeIsEmpty() {

        String terminalId = "123";
        CbbTerminalBizConfigDTO bizConfigDTO = new CbbTerminalBizConfigDTO();
        bizConfigDTO.setTerminalPlatform(CbbTerminalPlatformEnums.IDV);
        bizConfigDTO.setTerminalWorkModeArr(new CbbTerminalWorkModeEnums[]{});
        new Expectations() {
            {
                connectHandlerSPI.notifyTerminalSupport((CbbShineTerminalBasicInfo) any);
                result = bizConfigDTO;

                basicInfoService.isAuthed(terminalId);
                result = false;
            }
        };

        CbbShineTerminalBasicInfo basicInfo = new CbbShineTerminalBasicInfo();
        basicInfo.setTerminalId(terminalId);
        TerminalAuthResult authResult = helper.processTerminalAuth(true, false, basicInfo);
        Assert.assertEquals(authResult.getAuthResult(), TerminalAuthResultEnums.SKIP);
        Assert.assertTrue(!authResult.isNeedSaveTerminalInfo());

        new Verifications() {
            {
                connectHandlerSPI.notifyTerminalSupport(basicInfo);
                times = 1;

                basicInfoService.isAuthed(terminalId);
                times = 1;
            }
        };
    }

    /**
     * testProcessTerminalAuthWorkModeContainsIDVAndVOI
     */
    @Test
    public void testProcessTerminalAuthWorkModeContainsIDVAndVOI() {
        CbbShineTerminalBasicInfo basicInfo = new CbbShineTerminalBasicInfo();
        String terminalId = "123";
        CbbTerminalBizConfigDTO bizConfigDTO = new CbbTerminalBizConfigDTO();
        bizConfigDTO.setTerminalPlatform(CbbTerminalPlatformEnums.IDV);
        bizConfigDTO.setTerminalWorkModeArr(new CbbTerminalWorkModeEnums[]{CbbTerminalWorkModeEnums.IDV, CbbTerminalWorkModeEnums.VOI});
        new Expectations() {
            {
                connectHandlerSPI.notifyTerminalSupport((CbbShineTerminalBasicInfo) any);
                result = bizConfigDTO;

                terminalLicenseService.getIDVTerminalLicenseNum();
                result = 100;

                terminalLicenseService.authIDV(terminalId, true, basicInfo);
                result = true;

                basicInfoService.isAuthed(terminalId);
                result = false;
            }
        };

        basicInfo.setTerminalId(terminalId);
        TerminalAuthResult authResult = helper.processTerminalAuth(true, false, basicInfo);
        Assert.assertEquals(authResult.getAuthResult(), TerminalAuthResultEnums.SUCCESS);
        Assert.assertTrue(!authResult.isNeedSaveTerminalInfo());

        new Verifications() {
            {
                connectHandlerSPI.notifyTerminalSupport(basicInfo);
                times = 1;

                basicInfoService.isAuthed(terminalId);
                times = 1;

                terminalLicenseService.getIDVTerminalLicenseNum();
                times = 1;

                terminalLicenseService.authIDV(terminalId, true, basicInfo);
                times = 1;
            }
        };
    }

    /**
     * testProcessTerminalAuthWorkModeContainsIDVAndVOI
     */
    @Test
    public void testProcessTerminalIDVAuthLicenseNumIsNoLimit() {
        CbbShineTerminalBasicInfo basicInfo = new CbbShineTerminalBasicInfo();
        String terminalId = "123";
        CbbTerminalBizConfigDTO bizConfigDTO = new CbbTerminalBizConfigDTO();
        bizConfigDTO.setTerminalPlatform(CbbTerminalPlatformEnums.IDV);
        bizConfigDTO.setTerminalWorkModeArr(new CbbTerminalWorkModeEnums[]{CbbTerminalWorkModeEnums.IDV});
        new Expectations() {
            {
                connectHandlerSPI.notifyTerminalSupport((CbbShineTerminalBasicInfo) any);
                result = bizConfigDTO;

                terminalLicenseService.getIDVTerminalLicenseNum();
                result = -1;

                basicInfoService.isAuthed(terminalId);
                result = false;
            }
        };

        basicInfo.setTerminalId(terminalId);
        TerminalAuthResult authResult = helper.processTerminalAuth(true, false, basicInfo);
        Assert.assertEquals(authResult.getAuthResult(), TerminalAuthResultEnums.SUCCESS);
        Assert.assertTrue(authResult.isNeedSaveTerminalInfo());

        new Verifications() {
            {
                connectHandlerSPI.notifyTerminalSupport(basicInfo);
                times = 1;

                basicInfoService.isAuthed(terminalId);
                times = 1;

                terminalLicenseService.getIDVTerminalLicenseNum();
                times = 1;
            }
        };
    }

    /**
     * testProcessTerminalAuthWorkModeContainsIDVAndVOI
     */
    @Test
    public void testProcessTerminalIDVAuthFail() {
        CbbShineTerminalBasicInfo basicInfo = new CbbShineTerminalBasicInfo();
        String terminalId = "123";
        CbbTerminalBizConfigDTO bizConfigDTO = new CbbTerminalBizConfigDTO();
        bizConfigDTO.setTerminalPlatform(CbbTerminalPlatformEnums.IDV);
        bizConfigDTO.setTerminalWorkModeArr(new CbbTerminalWorkModeEnums[]{CbbTerminalWorkModeEnums.IDV});
        new Expectations() {
            {
                connectHandlerSPI.notifyTerminalSupport((CbbShineTerminalBasicInfo) any);
                result = bizConfigDTO;

                basicInfoService.isAuthed(terminalId);
                result = false;

                terminalLicenseService.getIDVTerminalLicenseNum();
                result = 100;

                terminalLicenseService.authIDV(terminalId, true, basicInfo);
                result = false;
            }
        };

        basicInfo.setTerminalId(terminalId);
        TerminalAuthResult authResult = helper.processTerminalAuth(true, false, basicInfo);
        Assert.assertEquals(authResult.getAuthResult(), TerminalAuthResultEnums.FAIL);
        Assert.assertTrue(!authResult.isNeedSaveTerminalInfo());

        new Verifications() {
            {
                connectHandlerSPI.notifyTerminalSupport(basicInfo);
                times = 1;

                basicInfoService.isAuthed(terminalId);
                times = 1;

                terminalLicenseService.getIDVTerminalLicenseNum();
                times = 1;

                terminalLicenseService.authIDV(terminalId, true, basicInfo);
                times = 1;
            }
        };
    }

    private void commonExpectations() {

        CbbTerminalBizConfigDTO bizConfigDTO = new CbbTerminalBizConfigDTO();
        bizConfigDTO.setTerminalPlatform(CbbTerminalPlatformEnums.IDV);
        bizConfigDTO.setTerminalWorkModeArr(new CbbTerminalWorkModeEnums[]{CbbTerminalWorkModeEnums.VDI});
        new Expectations() {
            {
                connectHandlerSPI.notifyTerminalSupport((CbbShineTerminalBasicInfo) any);
                result = bizConfigDTO;
            }
        };
    }

    private TerminalSystemUpgradeEntity buildUpgradeEntity() {
        TerminalSystemUpgradeEntity upgradeEntity = new TerminalSystemUpgradeEntity();
        upgradeEntity.setId(UUID.randomUUID());
        upgradeEntity.setState(CbbSystemUpgradeTaskStateEnums.UPGRADING);
        return upgradeEntity;
    }

    private TerminalEntity buildTerminalEntity() {
        TerminalEntity terminalEntity = new TerminalEntity();
        terminalEntity.setPlatform(CbbTerminalPlatformEnums.VDI);
        terminalEntity.setTerminalOsType("Linux");
        terminalEntity.setTerminalId("123");

        return terminalEntity;
    }

    private CbbDispatcherRequest buildRequest(UUID taskId, CbbSystemUpgradeStateEnums state) {
        CbbDispatcherRequest request = new CbbDispatcherRequest();
        SystemUpgradeResultInfo resultInfo = new SystemUpgradeResultInfo();
        resultInfo.setTaskId(taskId);
        resultInfo.setUpgradeState(state);
        request.setData(JSON.toJSONString(resultInfo));
        request.setTerminalId("123");
        return request;
    }
}

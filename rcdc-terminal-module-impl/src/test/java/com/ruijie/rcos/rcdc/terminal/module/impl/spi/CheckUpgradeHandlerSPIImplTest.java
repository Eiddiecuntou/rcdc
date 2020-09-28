package com.ruijie.rcos.rcdc.terminal.module.impl.spi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.alibaba.fastjson.JSON;
import com.ruijie.rcos.rcdc.codec.adapter.def.api.CbbTranspondMessageHandlerAPI;
import com.ruijie.rcos.rcdc.codec.adapter.def.dto.CbbDispatcherRequest;
import com.ruijie.rcos.rcdc.codec.adapter.def.dto.CbbResponseShineMessage;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbShineTerminalBasicInfo;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalPlatformEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.MessageUtils;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalVersionResultDTO;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalBasicInfoService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalComponentUpgradeService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalLicenseService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.systemupgrade.TerminalSystemUpgradeHandlerFactory;
import com.ruijie.rcos.rcdc.terminal.module.impl.spi.response.TerminalUpgradeResult;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mock;
import mockit.MockUp;
import mockit.Tested;
import mockit.Verifications;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/11/2
 *
 * @author Jarman
 */
@RunWith(SkyEngineRunner.class)
public class CheckUpgradeHandlerSPIImplTest {

    @Tested
    private CheckUpgradeHandlerSPIImpl checkUpgradeHandler;

    @Injectable
    private CbbTranspondMessageHandlerAPI messageHandlerAPI;

    @Injectable
    private TerminalComponentUpgradeService componentUpgradeService;

    @Injectable
    private TerminalBasicInfoService basicInfoService;

    @Injectable
    private TerminalSystemUpgradeHandlerFactory handlerFactory;

    @Injectable
    private TerminalLicenseService terminalLicenseService;


    /**
     * 测试检查组件升级- 更新终端信息
     */
    @Test
    public void testDispatchUpdateTerminalBasicInfo() {
        String terminalId = "123";
        TerminalEntity entity = new TerminalEntity();
        entity.setTerminalId("123456");
        entity.setTerminalName("t-box3");
        entity.setCpuType("intel");
        entity.setTerminalOsType("Linux");
        entity.setPlatform(CbbTerminalPlatformEnums.VDI);
        new Expectations() {
            {
                basicInfoService.saveBasicInfo(anyString, anyBoolean, (CbbShineTerminalBasicInfo) any);
                basicInfoService.convertBasicInfo2TerminalEntity(anyString,anyBoolean,(CbbShineTerminalBasicInfo)any);
                result = entity;
                try {
                    messageHandlerAPI.response((CbbResponseShineMessage) any);
                } catch (Exception e) {
                    fail();
                }
            }
        };

        new MockUp(CbbTerminalTypeEnums.class) {
            @Mock
            public CbbTerminalTypeEnums convert(String platform, String osType) {
                return CbbTerminalTypeEnums.VDI_LINUX;
            }
        };

        try {
            CbbDispatcherRequest request = new CbbDispatcherRequest();
            request.setTerminalId(terminalId);
            request.setRequestId("456");
            request.setData(generateJson());
            request.setNewConnection(true);
            checkUpgradeHandler.dispatch(request);

            saveVerifications();
        } catch (Exception e) {
            fail();
        }
    }

    /**
     * 测试检查组件升级-添加终端信息
     */
    @Test
    public void testDispatchAddTerminalBasicInfo() {
        String terminalId = "123";

        TerminalEntity terminalEntity = new TerminalEntity();
        terminalEntity.setPlatform(CbbTerminalPlatformEnums.VDI);
        terminalEntity.setTerminalOsType("Linux");
        new Expectations() {
            {
                basicInfoService.convertBasicInfo2TerminalEntity(anyString,anyBoolean,(CbbShineTerminalBasicInfo)any);
                result = terminalEntity;
                basicInfoService.saveBasicInfo(anyString, anyBoolean, (CbbShineTerminalBasicInfo) any);
                try {
                    messageHandlerAPI.response((CbbResponseShineMessage) any);
                } catch (Exception e) {
                    fail();
                }
            }
        };

        new MockUp(CbbTerminalTypeEnums.class) {
            @Mock
            public CbbTerminalTypeEnums convert(String platform, String osType) {
                return CbbTerminalTypeEnums.VDI_LINUX;
            }
        };

        try {
            CbbDispatcherRequest request = new CbbDispatcherRequest();
            request.setTerminalId(terminalId);
            request.setRequestId("4567");
            request.setData(generateJson());
            request.setNewConnection(true);
            checkUpgradeHandler.dispatch(request);

            saveVerifications();
        } catch (Exception e) {
            fail();
        }
    }

    /**
     * 测试检查组件升级- 获取系统升级处理对象异常
     */
    @Test
    public void testDispatchGetHandlerhasException() throws BusinessException {
        String terminalId = "123";

        TerminalEntity terminalEntity = new TerminalEntity();
        terminalEntity.setPlatform(CbbTerminalPlatformEnums.VDI);
        terminalEntity.setTerminalOsType("Linux");
        new Expectations() {
            {
                basicInfoService.convertBasicInfo2TerminalEntity(anyString,anyBoolean,(CbbShineTerminalBasicInfo)any);
                result = terminalEntity;

                handlerFactory.getHandler(CbbTerminalTypeEnums.VDI_LINUX);
                result = new BusinessException("123");

                try {
                    messageHandlerAPI.response((CbbResponseShineMessage) any);
                } catch (Exception e) {
                    fail();
                }
            }
        };

        try {
            CbbDispatcherRequest request = new CbbDispatcherRequest();
            request.setTerminalId(terminalId);
            request.setRequestId("4567");
            request.setData(generateJson());
            request.setNewConnection(false);
            checkUpgradeHandler.dispatch(request);
        } catch (Exception e) {
            fail();
        }

        new Verifications() {
            {
                handlerFactory.getHandler(CbbTerminalTypeEnums.VDI_LINUX);
                times = 1;

                CbbResponseShineMessage cbbShineMessageRequest;
                messageHandlerAPI.response(cbbShineMessageRequest = withCapture());
                times = 1;
                TerminalUpgradeResult terminalUpgradeResult = (TerminalUpgradeResult) cbbShineMessageRequest.getContent();
                assertEquals(2, terminalUpgradeResult.getSystemUpgradeCode().intValue());
                assertEquals(null, terminalUpgradeResult.getSystemUpgradeInfo());
            }
        };
    }

    /**
     * 测试idv场景、新终端接入无须升级、无授权场景
     */
    @Test
    public void testDispatcherNewIDVNotUpgradeNoAuth() {
        String terminalId = "123";

        TerminalEntity terminalEntity = new TerminalEntity();
        terminalEntity.setPlatform(CbbTerminalPlatformEnums.IDV);
        terminalEntity.setTerminalOsType("Linux");
        TerminalVersionResultDTO versionResultDTO = new TerminalVersionResultDTO();
        versionResultDTO.setResult(0);
        new Expectations(MessageUtils.class) {
            {
                basicInfoService.convertBasicInfo2TerminalEntity(anyString,anyBoolean,(CbbShineTerminalBasicInfo)any);
                result = terminalEntity;
                basicInfoService.isNewTerminal(withEqual("123"));
                result = true;
                terminalLicenseService.authIDV(withEqual("123"));
                result = false;
                componentUpgradeService.getVersion(terminalEntity, anyString);
                result = versionResultDTO;
            }
        };
        CbbDispatcherRequest request = new CbbDispatcherRequest();
        try {
            request.setTerminalId(terminalId);
            request.setRequestId("4567");
            request.setData(generateJson());
            request.setNewConnection(true);
            checkUpgradeHandler.dispatch(request);
        } catch (Exception e) {
            fail();
        }

        new Verifications() {
            {
                TerminalUpgradeResult terminalUpgradeResult;
                basicInfoService.saveBasicInfo(anyString, anyBoolean, (CbbShineTerminalBasicInfo) any);
                times = 0;
                MessageUtils.buildResponseMessage(request, terminalUpgradeResult = withCapture());
                times = 1;
                Assert.assertEquals(Integer.valueOf(5), terminalUpgradeResult.getResult());
            }
        };
    }

    /**
     * 测试idv场景、新终端接入需要升级、无授权
     */
    @Test
    public void testDispatcherNewIDVNeedUpgradeNoAuth() {
        String terminalId = "123";

        TerminalEntity terminalEntity = new TerminalEntity();
        terminalEntity.setPlatform(CbbTerminalPlatformEnums.IDV);
        terminalEntity.setTerminalOsType("Linux");
        TerminalVersionResultDTO versionResultDTO = new TerminalVersionResultDTO();
        versionResultDTO.setResult(2);
        new Expectations(MessageUtils.class) {
            {
                basicInfoService.convertBasicInfo2TerminalEntity(anyString,anyBoolean,(CbbShineTerminalBasicInfo)any);
                result = terminalEntity;
                basicInfoService.isNewTerminal(withEqual(terminalId));
                result = true;
                componentUpgradeService.getVersion(terminalEntity, anyString);
                result = versionResultDTO;
            }
        };
        CbbDispatcherRequest request = new CbbDispatcherRequest();
        try {
            request.setTerminalId(terminalId);
            request.setRequestId("4567");
            request.setData(generateJson());
            request.setNewConnection(true);
            checkUpgradeHandler.dispatch(request);
        } catch (Exception e) {
            fail();
        }

        new Verifications() {
            {
                TerminalUpgradeResult terminalUpgradeResult;
                basicInfoService.saveBasicInfo(anyString, anyBoolean, (CbbShineTerminalBasicInfo) any);
                times = 0;
                MessageUtils.buildResponseMessage(request, terminalUpgradeResult = withCapture());
                times = 1;
                Assert.assertEquals(Integer.valueOf(2), terminalUpgradeResult.getResult());
            }
        };
    }

    /**
     * 测试idv场景、新终端接入无须升级、有授权
     */
    @Test
    public void testDispatcherNewIDVNotUpgradeHasAuth() {
        String terminalId = "123";

        TerminalEntity terminalEntity = new TerminalEntity();
        terminalEntity.setPlatform(CbbTerminalPlatformEnums.IDV);
        terminalEntity.setTerminalOsType("Linux");
        TerminalVersionResultDTO versionResultDTO = new TerminalVersionResultDTO();
        versionResultDTO.setResult(0);
        new Expectations(MessageUtils.class) {
            {
                basicInfoService.convertBasicInfo2TerminalEntity(anyString,anyBoolean,(CbbShineTerminalBasicInfo)any);
                result = terminalEntity;
                basicInfoService.isNewTerminal(withEqual("123"));
                result = true;
                terminalLicenseService.authIDV(withEqual("123"));
                result = true;
                componentUpgradeService.getVersion(terminalEntity, anyString);
                result = versionResultDTO;
            }
        };
        CbbDispatcherRequest request = new CbbDispatcherRequest();
        try {
            request.setTerminalId(terminalId);
            request.setRequestId("4567");
            request.setData(generateJson());
            request.setNewConnection(true);
            checkUpgradeHandler.dispatch(request);
        } catch (Exception e) {
            fail();
        }

        new Verifications() {
            {
                TerminalUpgradeResult terminalUpgradeResult;
                basicInfoService.saveBasicInfo(anyString, anyBoolean, (CbbShineTerminalBasicInfo) any);
                times = 1;
                MessageUtils.buildResponseMessage(request, terminalUpgradeResult = withCapture());
                times = 1;
                Assert.assertEquals(Integer.valueOf(0), terminalUpgradeResult.getResult());
            }
        };
    }

    /**
     * 测试idv场景、新终端接入需要升级、无授权
     */
    @Test
    public void testDispatcherNewIDVNeedUpgradeHasAuth() {
        String terminalId = "123";

        TerminalEntity terminalEntity = new TerminalEntity();
        terminalEntity.setPlatform(CbbTerminalPlatformEnums.IDV);
        terminalEntity.setTerminalOsType("Linux");
        TerminalVersionResultDTO versionResultDTO = new TerminalVersionResultDTO();
        versionResultDTO.setResult(2);
        new Expectations(MessageUtils.class) {
            {
                basicInfoService.convertBasicInfo2TerminalEntity(anyString,anyBoolean,(CbbShineTerminalBasicInfo)any);
                result = terminalEntity;
                basicInfoService.isNewTerminal(withEqual(terminalId));
                result = true;
                componentUpgradeService.getVersion(terminalEntity, anyString);
                result = versionResultDTO;
            }
        };
        CbbDispatcherRequest request = new CbbDispatcherRequest();
        try {
            request.setTerminalId(terminalId);
            request.setRequestId("4567");
            request.setData(generateJson());
            request.setNewConnection(true);
            checkUpgradeHandler.dispatch(request);
        } catch (Exception e) {
            fail();
        }

        new Verifications() {
            {
                TerminalUpgradeResult terminalUpgradeResult;
                basicInfoService.saveBasicInfo(anyString, anyBoolean, (CbbShineTerminalBasicInfo) any);
                times = 0;
                MessageUtils.buildResponseMessage(request, terminalUpgradeResult = withCapture());
                times = 1;
                Assert.assertEquals(Integer.valueOf(2), terminalUpgradeResult.getResult());
            }
        };
    }

    private void saveVerifications() {
        new Verifications() {
            {
                basicInfoService.saveBasicInfo(anyString, anyBoolean, (CbbShineTerminalBasicInfo) any);
                times = 1;
                basicInfoService.convertBasicInfo2TerminalEntity(anyString,anyBoolean,(CbbShineTerminalBasicInfo)any);
                times = 1;
            }
        };
    }


    private String generateJson() {
        CbbShineTerminalBasicInfo info = new CbbShineTerminalBasicInfo();
        info.setTerminalId("123");
        info.setTerminalName("t-box2");
        info.setCpuType("intel5");
        return JSON.toJSONString(info);
    }

}

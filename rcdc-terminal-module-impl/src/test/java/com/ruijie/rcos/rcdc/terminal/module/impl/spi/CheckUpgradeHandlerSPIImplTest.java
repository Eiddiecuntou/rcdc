package com.ruijie.rcos.rcdc.terminal.module.impl.spi;

import com.alibaba.fastjson.JSON;
import com.ruijie.rcos.rcdc.codec.adapter.def.api.CbbTranspondMessageHandlerAPI;
import com.ruijie.rcos.rcdc.codec.adapter.def.dto.CbbDispatcherRequest;
import com.ruijie.rcos.rcdc.codec.adapter.def.dto.CbbResponseShineMessage;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbShineTerminalBasicInfo;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalBizConfigDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalWorkModeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalPlatformEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.CbbTerminalConnectHandlerSPI;
import com.ruijie.rcos.rcdc.terminal.module.impl.connect.SessionManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.TerminalAuthResultEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.MessageUtils;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalAuthResult;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalVersionResultDTO;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalBasicInfoService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalComponentUpgradeService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalLicenseService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.TerminalAuthHelper;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.systemupgrade.TerminalSystemUpgradeHandlerFactory;
import com.ruijie.rcos.rcdc.terminal.module.impl.spi.response.TerminalUpgradeResult;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import com.ruijie.rcos.sk.connectkit.api.tcp.session.Session;
import mockit.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

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

    @Injectable
    private CbbTerminalConnectHandlerSPI connectHandlerSPI;

    @Injectable
    private SessionManager sessionManager;

    @Injectable
    private TerminalAuthHelper terminalAuthHelper;

    @Test
    public void testDispatchUpdateTerminalWherePlatformTypeIsPc() {
        CbbShineTerminalBasicInfo info = new CbbShineTerminalBasicInfo();
        info.setTerminalId("123");
        info.setPlatform(CbbTerminalPlatformEnums.PC);
        info.setTerminalOsType("Windows");

        CbbTerminalBizConfigDTO config = new CbbTerminalBizConfigDTO();
        config.setTerminalPlatform(CbbTerminalPlatformEnums.PC);
        config.setTerminalWorkModeArr(new CbbTerminalWorkModeEnums[] {CbbTerminalWorkModeEnums.VDI});

        new Expectations() {
            {
                connectHandlerSPI.isAllowConnect((CbbShineTerminalBasicInfo) any);
                result = true;
                connectHandlerSPI.notifyTerminalSupport((CbbShineTerminalBasicInfo) any);
                result = config;
            }
        };

        CbbDispatcherRequest request = new CbbDispatcherRequest();
        request.setData(JSON.toJSONString(info));
        request.setNewConnection(true);
        checkUpgradeHandler.dispatch(request);

        new Verifications() {
            {
                connectHandlerSPI.isAllowConnect((CbbShineTerminalBasicInfo) any);
                times = 1;

                connectHandlerSPI.notifyTerminalSupport((CbbShineTerminalBasicInfo) any);
                times = 1;

                basicInfoService.convertBasicInfo2TerminalEntity(anyString, true, (CbbShineTerminalBasicInfo) any);
                times = 0;
            }
        };
    }

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

        CbbTerminalBizConfigDTO config = new CbbTerminalBizConfigDTO();
        config.setTerminalPlatform(CbbTerminalPlatformEnums.VDI);
        config.setTerminalWorkModeArr(new CbbTerminalWorkModeEnums[] {CbbTerminalWorkModeEnums.VDI});

        new Expectations() {
            {
                connectHandlerSPI.isAllowConnect((CbbShineTerminalBasicInfo) any);
                result = true;
                connectHandlerSPI.notifyTerminalSupport((CbbShineTerminalBasicInfo) any);
                result = config;
                basicInfoService.saveBasicInfo(anyString, anyBoolean, (CbbShineTerminalBasicInfo) any, Boolean.TRUE);
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
            request.setData(generateLinuxIDVJson());
            request.setNewConnection(true);
            checkUpgradeHandler.dispatch(request);

        } catch (Exception e) {
            fail();
        }

        saveVerifications();

        new Verifications() {
            {
                connectHandlerSPI.isAllowConnect((CbbShineTerminalBasicInfo) any);
                times = 1;

                connectHandlerSPI.notifyTerminalSupport((CbbShineTerminalBasicInfo) any);
                times = 1;
            }
        };
    }

    /**
     * 测试检查组件升级-添加终端信息
     */
    @Test
    public void testDispatchAddTerminalBasicInfo() {
        String terminalId = "123";

        TerminalEntity terminalEntity = new TerminalEntity();
        terminalEntity.setPlatform(CbbTerminalPlatformEnums.APP);
        terminalEntity.setTerminalOsType("Linux");

        CbbTerminalBizConfigDTO config = new CbbTerminalBizConfigDTO();
        config.setTerminalPlatform(CbbTerminalPlatformEnums.APP);
        config.setTerminalWorkModeArr(new CbbTerminalWorkModeEnums[] {CbbTerminalWorkModeEnums.APP});

        new Expectations() {
            {
                connectHandlerSPI.isAllowConnect((CbbShineTerminalBasicInfo) any);
                result = true;
                connectHandlerSPI.notifyTerminalSupport((CbbShineTerminalBasicInfo) any);
                result = config;

                basicInfoService.convertBasicInfo2TerminalEntity(anyString, anyBoolean,(CbbShineTerminalBasicInfo) any);
                result = terminalEntity;
                basicInfoService.saveBasicInfo(anyString, anyBoolean, (CbbShineTerminalBasicInfo) any, Boolean.TRUE);
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
            request.setData(generateLinuxIDVJson());
            request.setNewConnection(true);
            checkUpgradeHandler.dispatch(request);

        } catch (Exception e) {
            fail();
        }

        saveVerifications();

        new Verifications() {
            {
                connectHandlerSPI.isAllowConnect((CbbShineTerminalBasicInfo) any);
                times = 1;

                connectHandlerSPI.notifyTerminalSupport((CbbShineTerminalBasicInfo) any);
                times = 1;
            }
        };
    }

    /**
     * 测试检查组件升级-添加终端信息
     */
    @Test
    public void testDispatchAddTerminalBasicInfoWithNoAuth() {
        String terminalId = "123";

        TerminalEntity terminalEntity = new TerminalEntity();
        terminalEntity.setPlatform(CbbTerminalPlatformEnums.VDI);
        terminalEntity.setTerminalOsType("Linux");

        CbbTerminalBizConfigDTO config = new CbbTerminalBizConfigDTO();
        config.setTerminalPlatform(CbbTerminalPlatformEnums.IDV);
        config.setTerminalWorkModeArr(new CbbTerminalWorkModeEnums[] {CbbTerminalWorkModeEnums.IDV});

        new Expectations() {
            {
                connectHandlerSPI.isAllowConnect((CbbShineTerminalBasicInfo) any);
                result = true;
                connectHandlerSPI.notifyTerminalSupport((CbbShineTerminalBasicInfo) any);
                result = config;
                basicInfoService.convertBasicInfo2TerminalEntity(anyString, anyBoolean,(CbbShineTerminalBasicInfo)any);
                result = terminalEntity;
                basicInfoService.saveBasicInfo(anyString, anyBoolean, (CbbShineTerminalBasicInfo) any, Boolean.TRUE);
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
            request.setData(generateLinuxIDVJson());
            request.setNewConnection(true);
            checkUpgradeHandler.dispatch(request);

        } catch (Exception e) {
            fail();
        }

        saveVerifications();

        new Verifications() {
            {
                connectHandlerSPI.isAllowConnect((CbbShineTerminalBasicInfo) any);
                times = 1;

                connectHandlerSPI.notifyTerminalSupport((CbbShineTerminalBasicInfo) any);
                times = 1;
            }
        };
    }

    /**
     * 测试检查组件升级- 获取系统升级处理对象异常
     */
    @Test
    public void testDispatchGetHandlerHasException() throws BusinessException {
        String terminalId = "123";

        TerminalEntity terminalEntity = new TerminalEntity();
        terminalEntity.setPlatform(CbbTerminalPlatformEnums.VDI);
        terminalEntity.setTerminalOsType("Linux");

        CbbTerminalBizConfigDTO config = new CbbTerminalBizConfigDTO();
        config.setTerminalPlatform(CbbTerminalPlatformEnums.VDI);
        config.setTerminalWorkModeArr(new CbbTerminalWorkModeEnums[] {CbbTerminalWorkModeEnums.VDI});

        new Expectations() {
            {
                connectHandlerSPI.isAllowConnect((CbbShineTerminalBasicInfo) any);
                result = true;

                connectHandlerSPI.notifyTerminalSupport((CbbShineTerminalBasicInfo) any);
                result = config;

                basicInfoService.convertBasicInfo2TerminalEntity(anyString,anyBoolean,(CbbShineTerminalBasicInfo) any);
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
            request.setData(generateLinuxIDVJson());
            request.setNewConnection(false);
            checkUpgradeHandler.dispatch(request);
        } catch (Exception e) {
            fail();
        }

        new Verifications() {
            {
                connectHandlerSPI.isAllowConnect((CbbShineTerminalBasicInfo) any);
                times = 1;

                connectHandlerSPI.notifyTerminalSupport((CbbShineTerminalBasicInfo) any);
                times = 1;

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

        CbbTerminalBizConfigDTO config = new CbbTerminalBizConfigDTO();
        config.setTerminalPlatform(CbbTerminalPlatformEnums.IDV);
        config.setTerminalWorkModeArr(new CbbTerminalWorkModeEnums[] {CbbTerminalWorkModeEnums.IDV});

        new Expectations(MessageUtils.class) {
            {
                connectHandlerSPI.isAllowConnect((CbbShineTerminalBasicInfo) any);
                result = true;
                connectHandlerSPI.notifyTerminalSupport((CbbShineTerminalBasicInfo) any);
                result = config;
                basicInfoService.convertBasicInfo2TerminalEntity(anyString,anyBoolean,(CbbShineTerminalBasicInfo)any);
                result = terminalEntity;
                componentUpgradeService.getVersion(terminalEntity, anyString);
                result = versionResultDTO;
            }
        };

        authHelperExpectations(false, TerminalAuthResultEnums.FAIL);

        CbbDispatcherRequest request = new CbbDispatcherRequest();
        try {
            request.setTerminalId(terminalId);
            request.setRequestId("4567");
            request.setData(generateLinuxIDVJson());
            request.setNewConnection(true);
            checkUpgradeHandler.dispatch(request);
        } catch (Exception e) {
            fail();
        }

        new Verifications() {
            {
                connectHandlerSPI.isAllowConnect((CbbShineTerminalBasicInfo) any);
                times = 1;
                TerminalUpgradeResult terminalUpgradeResult;
                basicInfoService.saveBasicInfo(anyString, anyBoolean, (CbbShineTerminalBasicInfo) any, Boolean.TRUE);
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

        CbbTerminalBizConfigDTO config = new CbbTerminalBizConfigDTO();
        config.setTerminalPlatform(CbbTerminalPlatformEnums.IDV);
        config.setTerminalWorkModeArr(new CbbTerminalWorkModeEnums[] {CbbTerminalWorkModeEnums.IDV});

        new Expectations() {
            {
                connectHandlerSPI.isAllowConnect((CbbShineTerminalBasicInfo) any);
                result = true;
                connectHandlerSPI.notifyTerminalSupport((CbbShineTerminalBasicInfo) any);
                result = config;
                basicInfoService.convertBasicInfo2TerminalEntity(anyString,anyBoolean,(CbbShineTerminalBasicInfo)any);
                result = terminalEntity;
                componentUpgradeService.getVersion(terminalEntity, anyString);
                result = versionResultDTO;
                messageHandlerAPI.response((CbbResponseShineMessage) any);

                terminalAuthHelper.processTerminalAuth(anyBoolean, anyBoolean, (CbbShineTerminalBasicInfo) any);
                result = new TerminalAuthResult(true, TerminalAuthResultEnums.SKIP);
            }
        };

        CbbDispatcherRequest request = new CbbDispatcherRequest();
        try {
            request.setTerminalId(terminalId);
            request.setRequestId("4567");
            request.setData(generateLinuxIDVJson());
            request.setNewConnection(true);
            checkUpgradeHandler.dispatch(request);
        } catch (Exception e) {
            fail();
        }

        new Verifications() {
            {
                connectHandlerSPI.isAllowConnect((CbbShineTerminalBasicInfo) any);
                times = 1;

                basicInfoService.saveBasicInfo(anyString, anyBoolean, (CbbShineTerminalBasicInfo) any, Boolean.TRUE);
                times = 1;

                messageHandlerAPI.response((CbbResponseShineMessage) any);
                times = 1;
            }
        };
    }

    /**
     * 测试idv场景、新终端接入需要升级、无授权
     */
    @Test
    public void testDispatcherWorkModeIllegal() {
        String terminalId = "123";

        TerminalEntity terminalEntity = new TerminalEntity();
        terminalEntity.setPlatform(CbbTerminalPlatformEnums.IDV);
        terminalEntity.setTerminalOsType("Linux");
        TerminalVersionResultDTO versionResultDTO = new TerminalVersionResultDTO();
        versionResultDTO.setResult(2);

        CbbTerminalBizConfigDTO config = new CbbTerminalBizConfigDTO();
        config.setTerminalPlatform(CbbTerminalPlatformEnums.IDV);
        config.setTerminalWorkModeArr(new CbbTerminalWorkModeEnums[] {CbbTerminalWorkModeEnums.UNKNOWN});

        new Expectations() {
            {
                connectHandlerSPI.isAllowConnect((CbbShineTerminalBasicInfo) any);
                result = true;
                connectHandlerSPI.notifyTerminalSupport((CbbShineTerminalBasicInfo) any);
                result = config;
                basicInfoService.convertBasicInfo2TerminalEntity(anyString,anyBoolean,(CbbShineTerminalBasicInfo)any);
                result = terminalEntity;
                componentUpgradeService.getVersion(terminalEntity, anyString);
                result = versionResultDTO;
            }
        };

        CbbDispatcherRequest request = new CbbDispatcherRequest();
        try {
            request.setTerminalId(terminalId);
            request.setRequestId("4567");
            request.setData(generateLinuxIDVJson());
            request.setNewConnection(true);
            checkUpgradeHandler.dispatch(request);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("配置的终端工作类型[UNKNOWN]不支持系统升级", e.getMessage());

        }

        new Verifications() {
            {
                connectHandlerSPI.isAllowConnect((CbbShineTerminalBasicInfo) any);
                times = 1;

                basicInfoService.saveBasicInfo(anyString, anyBoolean, (CbbShineTerminalBasicInfo) any, Boolean.TRUE);
                times = 0;

                messageHandlerAPI.response((CbbResponseShineMessage) any);
                times = 0;
            }
        };
    }

    /**
     * 测试idv场景、新终端接入无须升级、有授权
     */
    @Test
    public void testDispatcherNewIDVNotUpgradeHasAuth() throws BusinessException {
        String terminalId = "123";

        TerminalEntity terminalEntity = new TerminalEntity();
        terminalEntity.setPlatform(CbbTerminalPlatformEnums.IDV);
        terminalEntity.setTerminalOsType("Linux");
        TerminalVersionResultDTO versionResultDTO = new TerminalVersionResultDTO();
        versionResultDTO.setResult(0);

        CbbTerminalBizConfigDTO config = new CbbTerminalBizConfigDTO();
        config.setTerminalPlatform(CbbTerminalPlatformEnums.IDV);
        config.setTerminalWorkModeArr(new CbbTerminalWorkModeEnums[] {CbbTerminalWorkModeEnums.IDV, CbbTerminalWorkModeEnums.VDI});

        new Expectations(MessageUtils.class) {
            {
                connectHandlerSPI.isAllowConnect((CbbShineTerminalBasicInfo) any);
                result = true;
                connectHandlerSPI.notifyTerminalSupport((CbbShineTerminalBasicInfo) any);
                result = config;
                basicInfoService.convertBasicInfo2TerminalEntity(anyString,anyBoolean,(CbbShineTerminalBasicInfo)any);
                result = terminalEntity;
                componentUpgradeService.getVersion(terminalEntity, anyString);
                result = versionResultDTO;

            }
        };

        authHelperExpectations(false, TerminalAuthResultEnums.SUCCESS);

        CbbDispatcherRequest request = new CbbDispatcherRequest();
        try {
            request.setTerminalId(terminalId);
            request.setRequestId("4567");
            request.setData(generateLinuxIDVJson());
            request.setNewConnection(true);
            checkUpgradeHandler.dispatch(request);
        } catch (Exception e) {
            fail();
        }

        new Verifications() {
            {
                connectHandlerSPI.isAllowConnect((CbbShineTerminalBasicInfo) any);
                times = 1;
                TerminalUpgradeResult terminalUpgradeResult;
                basicInfoService.saveBasicInfo(anyString, anyBoolean, (CbbShineTerminalBasicInfo) any, Boolean.TRUE);
                times = 0;
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

        CbbTerminalBizConfigDTO config = new CbbTerminalBizConfigDTO();
        config.setTerminalPlatform(CbbTerminalPlatformEnums.IDV);
        config.setTerminalWorkModeArr(new CbbTerminalWorkModeEnums[] {CbbTerminalWorkModeEnums.IDV});

        new Expectations(MessageUtils.class) {
            {
                connectHandlerSPI.isAllowConnect((CbbShineTerminalBasicInfo) any);
                result = true;
                connectHandlerSPI.notifyTerminalSupport((CbbShineTerminalBasicInfo) any);
                result = config;
                basicInfoService.convertBasicInfo2TerminalEntity(anyString,anyBoolean,(CbbShineTerminalBasicInfo)any);
                result = terminalEntity;
                componentUpgradeService.getVersion(terminalEntity, anyString);
                result = versionResultDTO;
            }
        };

        authHelperExpectations(false, TerminalAuthResultEnums.SUCCESS);

        CbbDispatcherRequest request = new CbbDispatcherRequest();
        try {
            request.setTerminalId(terminalId);
            request.setRequestId("4567");
            request.setData(generateLinuxIDVJson());
            request.setNewConnection(true);
            checkUpgradeHandler.dispatch(request);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        new Verifications() {
            {
                connectHandlerSPI.isAllowConnect((CbbShineTerminalBasicInfo) any);
                times = 1;
                TerminalUpgradeResult terminalUpgradeResult;
                basicInfoService.saveBasicInfo(anyString, anyBoolean, (CbbShineTerminalBasicInfo) any, Boolean.TRUE);
                times = 0;
                MessageUtils.buildResponseMessage(request, terminalUpgradeResult = withCapture());
                times = 1;
                Assert.assertEquals(Integer.valueOf(2), terminalUpgradeResult.getResult());
            }
        };
    }

    /**
     *  不允许接入
     */
    @Test
    public void testDispatchNotAllowConnect() {

        new Expectations() {
            {
                connectHandlerSPI.isAllowConnect((CbbShineTerminalBasicInfo) any);
                result = false;
            }
        };
        CbbDispatcherRequest request = new CbbDispatcherRequest();
        request.setData(generateLinuxIDVJson());
        request.setTerminalId("123");
        checkUpgradeHandler.dispatch(request);

        new Verifications() {
            {
                connectHandlerSPI.isAllowConnect((CbbShineTerminalBasicInfo) any);
                times = 1;

                sessionManager.getSessionByAlias("123");
                times = 1;

                sessionManager.removeSession("123", (Session) any);
                times = 1;
            }
        };

    }

    private void authHelperExpectations(boolean needSave, TerminalAuthResultEnums authResult) {
        TerminalAuthResult resultInfo = new TerminalAuthResult(needSave, authResult);

        new Expectations() {
            {
                terminalAuthHelper.processTerminalAuth(anyBoolean, anyBoolean, (CbbShineTerminalBasicInfo) any);
                result = resultInfo;
            }
        };
    }


    private void saveVerifications() {
        new Verifications() {
            {
                basicInfoService.saveBasicInfo(anyString, anyBoolean, (CbbShineTerminalBasicInfo) any, Boolean.TRUE);
                times = 1;
                basicInfoService.convertBasicInfo2TerminalEntity(anyString,anyBoolean,(CbbShineTerminalBasicInfo)any);
                times = 1;
            }
        };
    }


    private String generateLinuxIDVJson() {
        CbbShineTerminalBasicInfo info = new CbbShineTerminalBasicInfo();
        info.setTerminalId("123");
        info.setTerminalName("t-box2");
        info.setCpuType("intel5");
        info.setPlatform(CbbTerminalPlatformEnums.IDV);
        info.setTerminalOsType("Linux");
        return JSON.toJSONString(info);
    }

    private String generateLinuxVDIJson() {
        CbbShineTerminalBasicInfo info = new CbbShineTerminalBasicInfo();
        info.setTerminalId("123");
        info.setTerminalName("t-box2");
        info.setCpuType("intel5");
        info.setPlatform(CbbTerminalPlatformEnums.VDI);
        info.setTerminalOsType("Linux");
        return JSON.toJSONString(info);
    }
}

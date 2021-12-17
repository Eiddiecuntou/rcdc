package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.ruijie.rcos.rcdc.codec.adapter.base.sender.DefaultRequestMessageSender;
import com.ruijie.rcos.rcdc.terminal.module.def.PublicBusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTerminalLicenseMgmtAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbShineTerminalBasicInfo;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbNetworkModeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbCpuArchType;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalPlatformEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.connect.SessionManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalModelDriverDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalModelDriverEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.ShineNetworkConfig;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalAuthorizationWhitelistService;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.i18n.LocaleI18nResolver;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import com.ruijie.rcos.sk.base.test.ThrowExceptionTester;
import com.ruijie.rcos.sk.commkit.base.message.Message;
import com.ruijie.rcos.sk.connectkit.api.tcp.session.Session;

import java.io.IOException;
import java.util.Date;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;
import org.junit.Before;
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
public class TerminalBasicInfoServiceImplTest {

    @Tested
    private TerminalBasicInfoServiceImpl basicInfoService;

    @Injectable
    private TerminalBasicInfoDAO basicInfoDAO;

    @Injectable
    private SessionManager sessionManager;

    @Injectable
    private DefaultRequestMessageSender sender;

    @Injectable
    private TerminalModelDriverDAO terminalModelDriverDAO;

    @Injectable
    private TerminalLockHelper terminalLockHelper;

    @Injectable
    private TerminalAuthorizationWhitelistService terminalAuthorizationWhitelistService;

    @Injectable
    private CbbTerminalLicenseMgmtAPI cbbTerminalLicenseMgmtAPI;

    private static final CbbTerminalPlatformEnums PLATFORM_ENUMS = CbbTerminalPlatformEnums.IDV;

    @Before
    public void before() {

        new MockUp<LocaleI18nResolver>() {

            /**
             *
             * @param key key
             * @param args args
             * @return key
             */
            @Mock
            public String resolve(String key, String... args) {
                return key;
            }

        };
    }

    /**
     * 测试修改终端名称成功
     *
     * @throws IOException          exception
     * @throws InterruptedException exception
     */
    @Test
    public void testModifyTerminalNameSuccess() throws IOException, InterruptedException {
        new Expectations() {
            {
                try {
                    sessionManager.getRequestMessageSender(anyString);
                    result = sender;
                } catch (BusinessException e) {
                    result = sender;
                }
            }
        };
        String terminalId = "123";
        String terminalName = "t-box";
        try {
            basicInfoService.modifyTerminalName(terminalId, terminalName);
        } catch (BusinessException e) {
            fail();
        }

        new Verifications() {
            {
                sender.syncRequest((Message) any);
                times = 1;
            }
        };
    }

    /**
     * 测试修改终端失败
     */
    @Test
    public void testModifyTerminalNameFail() {
        new Expectations() {
            {
                try {
                    sessionManager.getRequestMessageSender(anyString);
                    result = null;
                } catch (BusinessException e) {
                    result = null;
                }

            }
        };
        String terminalId = "123";
        String terminalName = "t-box";
        try {
            basicInfoService.modifyTerminalName(terminalId, terminalName);
            fail();
        } catch (BusinessException e) {
            assertEquals(e.getKey(), PublicBusinessKey.RCDC_TERMINAL_OFFLINE);
        }
    }

    @Test
    public void testModifyTerminalNameByException() throws Exception {
        new Expectations() {
            {
                try {
                    sessionManager.getRequestMessageSender(anyString);
                    result = sender;

                    sender.syncRequest((Message) any);
                    result = new BusinessException("error");

                } catch (BusinessException e) {
                    result = sender;
                }
            }
        };
        String terminalId = "123";
        String terminalName = "t-box";
        try {
            basicInfoService.modifyTerminalName(terminalId, terminalName);
            fail();
        } catch (BusinessException e) {
            assertEquals(e.getKey(), BusinessKey.RCDC_TERMINAL_OPERATE_MSG_SEND_FAIL);

        }

        new Verifications() {
            {
                sender.syncRequest((Message) any);
                times = 1;
            }
        };
    }

    /**
     * 测试修改终端网络成功
     */
    @Test
    public void testModifyTerminalNetworkConfigSuccess() {
        new Expectations() {
            {
                try {
                    sessionManager.getRequestMessageSender(anyString);
                    result = sender;
                } catch (BusinessException e) {
                    result = sender;
                }
            }
        };
        String terminalId = "123";
        ShineNetworkConfig config = new ShineNetworkConfig();
        config.setTerminalId(terminalId);
        config.setGetIpMode(1);
        config.setGetDnsMode(0);
        config.setMainDns("main_dns");
        try {
            basicInfoService.modifyTerminalNetworkConfig(terminalId, config);
        } catch (BusinessException e) {
            fail();
        }

        new Verifications() {
            {
                Message message;
                sender.request(message = withCapture());
                ShineNetworkConfig shineNetworkConfig = (ShineNetworkConfig) message.getData();
                assertNotNull(shineNetworkConfig);
                assertEquals(shineNetworkConfig.getTerminalId(), terminalId);
                assertEquals(shineNetworkConfig.getGetDnsMode(), (Integer) 0);
                assertEquals(shineNetworkConfig.getGetIpMode(), (Integer) 1);
                assertEquals(shineNetworkConfig.getMainDns(), "main_dns");
            }
        };
    }

    /**
     * 测试修改终端网络失败
     */
    @Test
    public void testModifyTerminalNetworkConfigFail() {
        new Expectations() {
            {
                try {
                    sessionManager.getRequestMessageSender(anyString);
                    result = null;
                } catch (BusinessException e) {
                    result = null;
                }
            }
        };
        String terminalId = "123";
        ShineNetworkConfig config = new ShineNetworkConfig();
        config.setTerminalId(terminalId);
        config.setGetIpMode(1);
        config.setGetDnsMode(0);
        config.setMainDns("main_dns");
        try {
            basicInfoService.modifyTerminalNetworkConfig(terminalId, config);
            fail();
        } catch (BusinessException e) {
            assertEquals(e.getKey(), PublicBusinessKey.RCDC_TERMINAL_OFFLINE);
        }
    }

    /**
     * 测试modifyTerminalState，参数为空
     *
     * @throws Exception 异常
     */
    @Test
    public void testModifyTerminalStateArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> basicInfoService.modifyTerminalState("", CbbTerminalStateEnums.ONLINE),
                "terminalId 不能为空");
        ThrowExceptionTester.throwIllegalArgumentException(() -> basicInfoService.modifyTerminalState("s", null), "state 不能为空");
        assertTrue(true);
    }

    /**
     * 测试modifyTerminalState，修改失败
     */
    @Test
    public void testModifyTerminalStateIsFail() {
        String terminalId = "123";
        TerminalEntity basicInfoEntity = null;
        new Expectations() {
            {
                basicInfoDAO.findTerminalEntityByTerminalId(terminalId);
                result = basicInfoEntity;
            }
        };
        basicInfoService.modifyTerminalState(terminalId, CbbTerminalStateEnums.ONLINE);

        new Verifications() {
            {
                basicInfoDAO.findTerminalEntityByTerminalId(terminalId);
                times = 4;
                basicInfoDAO.modifyTerminalStateOffline(CbbTerminalStateEnums.ONLINE, (Date) any, terminalId, anyInt);
                times = 0;
            }
        };
    }

    /**
     * 测试modifyTerminalState，一次修改成功
     */
    @Test
    public void testModifyTerminalStateIsSuccess() {
        String terminalId = "123";
        TerminalEntity basicInfoEntity = new TerminalEntity();
        new Expectations() {
            {
                basicInfoDAO.findTerminalEntityByTerminalId(terminalId);
                result = basicInfoEntity;
                basicInfoDAO.modifyTerminalStateOffline(CbbTerminalStateEnums.ONLINE, (Date) any, anyString, anyInt);
                result = 1;
            }
        };
        basicInfoService.modifyTerminalState(terminalId, CbbTerminalStateEnums.ONLINE);

        new Verifications() {
            {
                basicInfoDAO.findTerminalEntityByTerminalId(terminalId);
                times = 1;
                basicInfoDAO.modifyTerminalStateOffline(CbbTerminalStateEnums.ONLINE, (Date) any, terminalId, anyInt);
                times = 1;
            }
        };
    }

    /**
     * 测试modifyTerminalStateToOffline，参数为空
     *
     * @throws Exception 异常
     */
    @Test
    public void testModifyTerminalStateToOfflineArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> basicInfoService.modifyTerminalStateToOffline(""), "terminalId 不能为空");
        assertTrue(true);
    }


    /**
     * 测试modifyTerminalStateToOffline，升级中状态
     */
    @Test
    public void testModifyTerminalStateToOfflineIsUploading() {
        String terminalId = "123";
        TerminalEntity basicInfoEntity = new TerminalEntity();
        basicInfoEntity.setState(CbbTerminalStateEnums.UPGRADING);
        new Expectations() {
            {
                sessionManager.getSessionByAlias(anyString);
                result = null;
                basicInfoDAO.findTerminalEntityByTerminalId(terminalId);
                result = basicInfoEntity;
            }
        };
        basicInfoService.modifyTerminalStateToOffline(terminalId);

        new Verifications() {
            {
                basicInfoDAO.findTerminalEntityByTerminalId(terminalId);
                times = 1;
                basicInfoDAO.modifyTerminalStateOffline(CbbTerminalStateEnums.OFFLINE, (Date) any, terminalId, anyInt);
                times = 0;
            }
        };
    }

    /**
     * 测试modifyTerminalStateToOffline，一次修改成功
     */
    @Test
    public void testModifyTerminalStateToOfflineIsSuccess() {
        String terminalId = "123";
        TerminalEntity basicInfoEntity = new TerminalEntity();
        new Expectations() {
            {
                basicInfoDAO.findTerminalEntityByTerminalId(terminalId);
                result = basicInfoEntity;
                basicInfoDAO.modifyTerminalStateOffline(CbbTerminalStateEnums.OFFLINE, (Date) any, anyString, anyInt);
                result = 1;

                sessionManager.getSessionByAlias(terminalId);
                result = null;
            }
        };
        basicInfoService.modifyTerminalStateToOffline(terminalId);

        new Verifications() {
            {
                basicInfoDAO.findTerminalEntityByTerminalId(terminalId);
                times = 2;
                basicInfoDAO.modifyTerminalStateOffline(CbbTerminalStateEnums.OFFLINE, (Date) any, terminalId, anyInt);
                times = 1;
            }
        };
    }

    /**
     * 测试modifyTerminalStateToOffline，修改失败
     */
    @Test
    public void testModifyTerminalStateToOfflineIsFail() {
        String terminalId = "123";
        TerminalEntity basicInfoEntity = new TerminalEntity();
        basicInfoEntity.setState(CbbTerminalStateEnums.ONLINE);
        new Expectations() {
            {
                basicInfoDAO.findTerminalEntityByTerminalId(terminalId);
                result = basicInfoEntity;
                basicInfoDAO.modifyTerminalStateOffline(CbbTerminalStateEnums.OFFLINE, (Date) any, terminalId, anyInt);
                result = 0;

                sessionManager.getSessionByAlias(terminalId);
                result = null;
            }
        };
        basicInfoService.modifyTerminalStateToOffline(terminalId);

        new Verifications() {
            {
                basicInfoDAO.findTerminalEntityByTerminalId(terminalId);
                times = 5;
                basicInfoDAO.modifyTerminalStateOffline(CbbTerminalStateEnums.OFFLINE, (Date) any, terminalId, basicInfoEntity.getVersion());
                times = 4;
            }
        };
    }

    /**
     * 测试isTerminalOnline，参数为空
     *
     * @throws Exception 异常
     */
    @Test
    public void testIsTerminalOnlineArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> basicInfoService.isTerminalOnline(""), "terminalId can not empty");
        assertTrue(true);
    }

    /**
     * 测试isTerminalOnline，离线
     */
    @Test
    public void testIsTerminalOnlineIsOffLine() {
        Session session = null;

        new Expectations() {
            {
                sessionManager.getSessionByAlias("123");
                result = session;
            }
        };
        assertFalse(basicInfoService.isTerminalOnline("123"));
    }

    /**
     * 测试isTerminalOnline，在线
     *
     * @param session mock对象
     */
    @Test
    public void testIsTerminalOnlineIsOnLine(@Mocked Session session) {
        new Expectations() {
            {
                sessionManager.getSessionByAlias("123");
                result = session;
            }
        };
        assertTrue(basicInfoService.isTerminalOnline("123"));
    }

    /**
     * 测试保存终端基本信息
     */
    @Test
    public void testSaveBasicInfo() {
        TerminalEntity terminalEntity = buildTerminalEntity();

        CbbShineTerminalBasicInfo basicInfo = buildShineTerminalBasicInfo();

        basicInfoService.saveBasicInfo(terminalEntity, basicInfo, Boolean.TRUE);

        new Verifications() {
            {
                terminalModelDriverDAO.findByProductIdAndPlatform(anyString, PLATFORM_ENUMS);
                times = 0;

                terminalModelDriverDAO.save((TerminalModelDriverEntity) any);
                times = 0;
            }
        };
    }

    /**
     * 测试保存终端基本信息 - 新接入终端
     */
    @Test
    public void testSaveBasicInfoIsNewTerminal() {
        TerminalEntity terminalEntity = buildTerminalEntity();
        CbbShineTerminalBasicInfo basicInfo = buildShineTerminalBasicInfo();

        basicInfoService.saveBasicInfo(terminalEntity, basicInfo, Boolean.TRUE);

        new Verifications() {
            {
                terminalModelDriverDAO.findByProductIdAndPlatform(anyString, PLATFORM_ENUMS);
                times = 0;

                terminalModelDriverDAO.save((TerminalModelDriverEntity) any);
                times = 0;
            }
        };
    }

    private CbbShineTerminalBasicInfo buildShineTerminalBasicInfo() {
        CbbShineTerminalBasicInfo basicInfo = new CbbShineTerminalBasicInfo();
        basicInfo.setGateway("gateway");
        basicInfo.setMemorySize(213L);
        basicInfo.setNetworkAccessMode(CbbNetworkModeEnums.WIRED);
        basicInfo.setTerminalId("123");
        basicInfo.setCpuArch(CbbCpuArchType.X86_64);
        basicInfo.setCpuType("AMD1");
        return basicInfo;
    }

    private TerminalEntity buildTerminalEntity() {
        TerminalEntity terminalEntity = new TerminalEntity();
        terminalEntity.setTerminalId("123");
        terminalEntity.setCpuType("AMD1");

        return terminalEntity;
    }
}

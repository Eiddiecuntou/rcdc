package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.connect.SessionManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.ShineNetworkConfig;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.test.ThrowExceptionTester;
import com.ruijie.rcos.sk.commkit.base.message.Message;
import com.ruijie.rcos.sk.commkit.base.sender.DefaultRequestMessageSender;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;

import java.util.Date;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/11/2
 *
 * @author Jarman
 */
@RunWith(JMockit.class)
public class TerminalBasicInfoServiceImplTest {

    @Tested
    private TerminalBasicInfoServiceImpl basicInfoService;

    @Injectable
    private TerminalBasicInfoDAO basicInfoDAO;

    @Injectable
    private SessionManager sessionManager;
    
    @Injectable
    private DefaultRequestMessageSender sender;

    /**
     * 测试修改终端名称成功
     */
    @Test
    public void testModifyTerminalNameSuccess() {
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
                sender.request((Message) any);
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
            assertEquals(e.getKey(), BusinessKey.RCDC_TERMINAL_OFFLINE);
        }
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
            assertEquals(e.getKey(), BusinessKey.RCDC_TERMINAL_OFFLINE);
        }
    }

    /**
     * 测试modifyTerminalState，参数为空
     * @throws Exception 异常
     */
    @Test
    public void testModifyTerminalStateArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> basicInfoService.modifyTerminalStateToOffline(""),
                "terminalId 不能为空");
        assertTrue(true);
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
                basicInfoDAO.modifyTerminalStateOffline(CbbTerminalStateEnums.OFFLINE, (Date) any,anyString, anyInt);
                result = 1;
            }
        };
        basicInfoService.modifyTerminalStateToOffline(terminalId);
        
        new Verifications() {
            {
                basicInfoDAO.findTerminalEntityByTerminalId(terminalId);
                times = 1;
                basicInfoDAO.modifyTerminalStateOffline(CbbTerminalStateEnums.OFFLINE, (Date) any,terminalId, anyInt);
                times = 1;
            }
        };
    }
    
    /**
     * 测试modifyTerminalState，修改失败
     */
    @Test
    public void testModifyTerminalStateIsFail() {
        String terminalId = "123";
        TerminalEntity basicInfoEntity = new TerminalEntity();
        new Expectations() {
            {
                basicInfoDAO.findTerminalEntityByTerminalId(terminalId);
                returns(null, basicInfoEntity);
                basicInfoDAO.modifyTerminalStateOffline(CbbTerminalStateEnums.OFFLINE, (Date) any,terminalId, anyInt);
                result = 0;
            }
        };
        basicInfoService.modifyTerminalStateToOffline(terminalId);
        
        new Verifications() {
            {
                basicInfoDAO.findTerminalEntityByTerminalId(terminalId);
                times = 4;
                basicInfoDAO.modifyTerminalStateOffline(CbbTerminalStateEnums.OFFLINE, (Date) any,terminalId, basicInfoEntity.getVersion());
                times = 3;
            }
        };
    }

}


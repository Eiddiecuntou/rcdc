package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.connect.SessionManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.ShineNetworkConfig;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.commkit.base.message.Message;
import com.ruijie.rcos.sk.commkit.base.sender.DefaultRequestMessageSender;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

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

    @Test
    public void testModifyTerminalNameSuccess() {
        new Expectations() {{
            try {
                sessionManager.getRequestMessageSender(anyString);
            } catch (BusinessException e) {
                e.printStackTrace();
            }
            result = sender;
        }};
        String terminalId = "123";
        String terminalName = "t-box";
        try {
            basicInfoService.modifyTerminalName(terminalId, terminalName);
        } catch (BusinessException e) {
            fail();
        }

        new Verifications() {{
            sender.request((Message) any);
            times = 1;
        }};
    }

    @Test
    public void testModifyTerminalNameFail() {
        new Expectations() {{
            try {
                sessionManager.getRequestMessageSender(anyString);
            } catch (BusinessException e) {
                e.printStackTrace();
            }
            result = null;
        }};
        String terminalId = "123";
        String terminalName = "t-box";
        try {
            basicInfoService.modifyTerminalName(terminalId, terminalName);
            fail();
        } catch (BusinessException e) {
            assertEquals(e.getKey(), BusinessKey.RCDC_TERMINAL_OFFLINE);
        }
    }

    @Test
    public void testModifyTerminalNetworkConfigSuccess() {
        new Expectations() {{
            try {
                sessionManager.getRequestMessageSender(anyString);
                result = sender;
            } catch (BusinessException e) {
                e.printStackTrace();
            }
        }};
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

        new Verifications() {{
            Message message;
            sender.request(message = withCapture());
            ShineNetworkConfig shineNetworkConfig = (ShineNetworkConfig) message.getData();
            assertNotNull(shineNetworkConfig);
            assertEquals(shineNetworkConfig.getTerminalId(), terminalId);
            assertEquals(shineNetworkConfig.getGetDnsMode(), (Integer) 0);
            assertEquals(shineNetworkConfig.getGetIpMode(), (Integer) 1);
            assertEquals(shineNetworkConfig.getMainDns(), "main_dns");
        }};
    }

    @Test
    public void testModifyTerminalNetworkConfigFail() {
        new Expectations() {{
            try {
                sessionManager.getRequestMessageSender(anyString);
                result = null;
            } catch (BusinessException e) {
                e.printStackTrace();
            }
        }};
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

    @Test
    public void testModifyTerminalState() {
        new Expectations() {{
            basicInfoDAO.findTerminalBasicInfoEntitiesByTerminalId(anyString);
            result = null;
        }};

        String terminalId = "123";
        try {
            basicInfoService.modifyTerminalState(terminalId, CbbTerminalStateEnums.OFFLINE);
        } catch (BusinessException e) {
            Assert.assertEquals(e.getKey(), BusinessKey.RCDC_TERMINAL_NOT_FOUND_TERMINAL);
        }
    }
    @Test
    public void testModifyTerminalState2() {
        new Expectations() {{
            basicInfoDAO.modifyTerminalState(anyString, anyInt, anyInt);
            result = 0;
        }};

        String terminalId = "123";
        try {
            basicInfoService.modifyTerminalState(terminalId, CbbTerminalStateEnums.OFFLINE);
        } catch (BusinessException e) {
            Assert.assertEquals(e.getKey(), BusinessKey.RCDC_TERMINAL_NOT_FOUND_TERMINAL);
        }
    }

}
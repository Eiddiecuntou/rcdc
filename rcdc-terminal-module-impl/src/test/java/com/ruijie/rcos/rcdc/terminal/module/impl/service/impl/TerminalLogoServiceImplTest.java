package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.connect.SessionManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.SendTerminalEventEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalLogoService;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.commkit.base.message.Message;
import com.ruijie.rcos.sk.commkit.base.sender.DefaultRequestMessageSender;
import com.ruijie.rcos.sk.modulekit.api.tool.GlobalParameterAPI;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Description:
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/8/4
 *
 * @author hs
 */
@RunWith(JMockit.class)
public class TerminalLogoServiceImplTest {

    @Tested
    TerminalLogoServiceImpl terminalLogoService;

    @Injectable
    private SessionManager sessionManager;

    @Injectable
    private GlobalParameterAPI globalParameterAPI;

    @Injectable
    private DefaultRequestMessageSender sender;

    /**
     * syncTerminalLogo，onlineTerminalIdList为空
     *
     * @throws BusinessException 异常
     * @throws InterruptedException ex
     */
    @Test
    public void testSyncTerminalLogoOnlineTerminalIdListIsEmpty() throws BusinessException, InterruptedException {
        String logoName = "logo.png";
        SendTerminalEventEnums name = SendTerminalEventEnums.MODIFY_TERMINAL_NAME;
        new Expectations() {
            {
                sessionManager.getOnlineTerminalId();
                result = Collections.emptyList();
            }
        };
        terminalLogoService.syncTerminalLogo(logoName, name);
        Thread.sleep(1000);

        new Verifications() {
            {
                sessionManager.getOnlineTerminalId();
                times = 1;
                sessionManager.getRequestMessageSender(anyString);
                times = 0;
            }
        };
    }

    /**
     * syncTerminalLogo，DefaultRequestMessageSender为空
     *
     * @throws BusinessException 异常
     * @throws InterruptedException ex
     */
    @Test
    public void testSyncTerminalLogoDefaultRequestMessageSenderIsNull() throws BusinessException, InterruptedException {
        String logoName = "logo.png";
        SendTerminalEventEnums name = SendTerminalEventEnums.MODIFY_TERMINAL_NAME;
        List<String> onlineTerminalIdList = new ArrayList<>();
        onlineTerminalIdList.add("1");
        new Expectations() {
            {
                sessionManager.getOnlineTerminalId();
                result = onlineTerminalIdList;
                sessionManager.getRequestMessageSender(anyString);
                result = null;
            }
        };
        terminalLogoService.syncTerminalLogo(logoName, name);
        Thread.sleep(1000);
        new Verifications() {
            {
                sessionManager.getOnlineTerminalId();
                times = 1;
                sessionManager.getRequestMessageSender(anyString);
                times = 1;

            }
        };
    }

    /**
     * syncTerminalLogo
     *
     * @throws BusinessException 异常
     * @throws IOException 异常
     * @throws InterruptedException ex
     */
    @Test
    public void testSyncTerminalLogoException() throws BusinessException, IOException, InterruptedException {
        String logoName = "logo.png";
        SendTerminalEventEnums name = SendTerminalEventEnums.MODIFY_TERMINAL_NAME;
        List<String> onlineTerminalIdList = new ArrayList<>();
        onlineTerminalIdList.add("1");
        new Expectations() {
            {
                sessionManager.getOnlineTerminalId();
                result = onlineTerminalIdList;
                sessionManager.getRequestMessageSender(anyString);
                result = sender;
                sender.syncRequest((Message) any);
                result = new Exception();
            }
        };
        try {
            terminalLogoService.syncTerminalLogo(logoName, name);
            Thread.sleep(1000);
        } catch (BusinessException e) {
            Assert.assertEquals(BusinessKey.RCDC_TERMINAL_OPERATE_MSG_SEND_FAIL, e.getKey());
        }

        new Verifications() {
            {
                sessionManager.getOnlineTerminalId();
                times = 1;
                sessionManager.getRequestMessageSender(anyString);
                times = 1;
                sender.syncRequest((Message) any);
                times = 1;


            }
        };
    }

    /**
     * syncTerminalLogo， 成功
     *
     * @throws BusinessException 异常
     * @throws IOException 异常
     * @throws InterruptedException ex
     */
    @Test
    public void testSyncTerminalLogoSuccess() throws BusinessException, IOException, InterruptedException {
        String logoName = "logo.png";
        SendTerminalEventEnums name = SendTerminalEventEnums.MODIFY_TERMINAL_NAME;
        List<String> onlineTerminalIdList = new ArrayList<>();
        onlineTerminalIdList.add("1");
        new Expectations() {
            {
                sessionManager.getOnlineTerminalId();
                result = onlineTerminalIdList;
                sessionManager.getRequestMessageSender(anyString);
                result = sender;
                sender.syncRequest((Message) any);
            }
        };
        terminalLogoService.syncTerminalLogo(logoName, name);
        Thread.sleep(1000);
        new Verifications() {
            {
                sessionManager.getOnlineTerminalId();
                times = 1;
                sessionManager.getRequestMessageSender(anyString);
                times = 1;
                sender.syncRequest((Message) any);
                times = 1;
            }
        };
    }

    /**
     * 测试getTerminalLogoName, logoPath is Null
     *
     */
    @Test
    public void testGetTerminalLogoNameLogoPathIsNull() {
        new Expectations() {
            {
                globalParameterAPI.findParameter(TerminalLogoService.TERMINAL_LOGO);
                result = null;
            }
        };
        terminalLogoService.getTerminalLogoName();
        assertEquals(StringUtils.EMPTY, terminalLogoService.getTerminalLogoName());
    }

    /**
     * 测试getTerminalLogoName, logoPath Not Null
     *
     */
    @Test
    public void testGetTerminalLogoNameLogoPathNotNull() {
        new Expectations() {
            {
                globalParameterAPI.findParameter(TerminalLogoService.TERMINAL_LOGO);
                result = anyString;
                anyString.substring(anyString.lastIndexOf("/") + 1);
                result = "logo.png";

            }
        };
        terminalLogoService.getTerminalLogoName();
        assertEquals("logo.png", terminalLogoService.getTerminalLogoName());
    }


}

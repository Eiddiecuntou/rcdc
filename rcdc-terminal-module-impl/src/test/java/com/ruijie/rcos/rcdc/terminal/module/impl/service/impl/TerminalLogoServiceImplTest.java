package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import com.alibaba.fastjson.JSON;
import com.ruijie.rcos.rcdc.codec.adapter.base.sender.DefaultRequestMessageSender;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.connect.SessionManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.SendTerminalEventEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalLogoInfo;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalLogoService;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.commkit.base.message.Message;
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
     * syncTerminalBackground，onlineTerminalIdList为空
     *
     * @throws BusinessException 异常
     * @throws InterruptedException ex
     */
    @Test
    public void testSyncTerminalLogoOnlineTerminalIdListIsEmpty() throws BusinessException, InterruptedException {
        TerminalLogoInfo terminalLogoInfo = new TerminalLogoInfo();
        terminalLogoInfo.setLogoPath("/logo/logo.png");
        terminalLogoInfo.setMd5("123456");
        SendTerminalEventEnums name = SendTerminalEventEnums.MODIFY_TERMINAL_NAME;
        new Expectations() {
            {
                sessionManager.getOnlineTerminalId();
                result = Collections.emptyList();
            }
        };
        terminalLogoService.syncTerminalLogo(terminalLogoInfo, name);
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
     * syncTerminalBackground，DefaultRequestMessageSender为空
     *
     * @throws BusinessException 异常
     * @throws InterruptedException ex
     */
    @Test
    public void testSyncTerminalLogoDefaultRequestMessageSenderIsNull() throws BusinessException, InterruptedException {
        TerminalLogoInfo terminalLogoInfo = new TerminalLogoInfo();
        terminalLogoInfo.setLogoPath("/logo/logo.png");
        terminalLogoInfo.setMd5("123456");
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
        terminalLogoService.syncTerminalLogo(terminalLogoInfo, name);
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
     * syncTerminalBackground
     *
     * @throws BusinessException 异常
     * @throws IOException 异常
     * @throws InterruptedException ex
     */
    @Test
    public void testSyncTerminalLogoException() throws BusinessException, IOException, InterruptedException {
        TerminalLogoInfo terminalLogoInfo = new TerminalLogoInfo();
        terminalLogoInfo.setLogoPath("/logo/logo.png");
        terminalLogoInfo.setMd5("123456");
        SendTerminalEventEnums name = SendTerminalEventEnums.MODIFY_TERMINAL_NAME;
        List<String> onlineTerminalIdList = new ArrayList<>();
        onlineTerminalIdList.add("1");

        new Expectations() {
            {
                sessionManager.getOnlineTerminalId();
                result = onlineTerminalIdList;
                sessionManager.getRequestMessageSender(anyString);
                result = sender;
                sender.request((Message) any);
                result = new Exception();
            }
        };
        try {
            terminalLogoService.syncTerminalLogo(terminalLogoInfo, name);
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
                sender.request((Message) any);
                times = 1;


            }
        };
    }

    /**
     * syncTerminalBackground， 成功
     *
     * @throws BusinessException 异常
     * @throws IOException 异常
     * @throws InterruptedException ex
     */
    @Test
    public void testSyncTerminalLogoSuccess() throws BusinessException, IOException, InterruptedException {
        TerminalLogoInfo terminalLogoInfo = new TerminalLogoInfo();
        terminalLogoInfo.setLogoPath("/logo/logo.png");
        terminalLogoInfo.setMd5("123456");
        SendTerminalEventEnums name = SendTerminalEventEnums.MODIFY_TERMINAL_NAME;
        List<String> onlineTerminalIdList = new ArrayList<>();
        onlineTerminalIdList.add("1");
        new Expectations() {
            {
                sessionManager.getOnlineTerminalId();
                result = onlineTerminalIdList;
                sessionManager.getRequestMessageSender(anyString);
                result = sender;
                sender.request((Message) any);
            }
        };
        terminalLogoService.syncTerminalLogo(terminalLogoInfo, name);
        Thread.sleep(1000);
        new Verifications() {
            {
                sessionManager.getOnlineTerminalId();
                times = 1;
                sessionManager.getRequestMessageSender(anyString);
                times = 1;
                sender.request((Message) any);
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
        assertEquals(StringUtils.EMPTY, terminalLogoService.getTerminalLogoInfo().getLogoPath());
        assertEquals(StringUtils.EMPTY, terminalLogoService.getTerminalLogoInfo().getMd5());
    }

    /**
     * 测试getTerminalLogoName, logoPath Not Null
     *
     */
    @Test
    public void testGetTerminalLogoNameLogoPathNotNull() {
        TerminalLogoInfo terminalLogoInfo = new TerminalLogoInfo();
        terminalLogoInfo.setLogoPath("/logo/logo.png");
        terminalLogoInfo.setMd5("123456");
        String logoInfo = JSON.toJSONString(terminalLogoInfo);
        new Expectations() {
            {
                globalParameterAPI.findParameter(TerminalLogoService.TERMINAL_LOGO);
                result = logoInfo;

            }
        };
        assertEquals("/logo/logo.png", terminalLogoService.getTerminalLogoInfo().getLogoPath());
        assertEquals("123456", terminalLogoService.getTerminalLogoInfo().getMd5());
    }


}

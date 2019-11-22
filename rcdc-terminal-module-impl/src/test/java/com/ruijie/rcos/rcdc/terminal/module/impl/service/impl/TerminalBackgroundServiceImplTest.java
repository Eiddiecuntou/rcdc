package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.hamcrest.CustomMatcher;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.collect.Lists;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.connect.SessionManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.SendTerminalEventEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalBackgroundInfo;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.test.ThrowExceptionTester;
import com.ruijie.rcos.sk.commkit.base.message.Message;
import com.ruijie.rcos.sk.commkit.base.sender.DefaultRequestMessageSender;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.Verifications;

/**
 * Description: 终端背景业务测试类
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/11/8
 *
 * @author songxiang
 */

@RunWith(SkyEngineRunner.class)
public class TerminalBackgroundServiceImplTest {

    @Tested
    TerminalBackgroundServiceImpl terminalBackgroundService;

    @Injectable
    private SessionManager sessionManager;

    @Injectable
    private Logger logger;

    @Injectable
    private DefaultRequestMessageSender sender;


    /**
     * 测试参数为空的情况
     */
    @Test
    public void testParamNullError() {
        try {
            ThrowExceptionTester.throwIllegalArgumentException(() -> terminalBackgroundService.syncTerminalBackground(null),
                    "terminalSyncBackgroundInfo must not be null");
        } catch (Exception e) {
            Assert.fail();
        }
    }

    /**
     * 测试同步终端背景图，没有在线终端的情况
     * 
     * @throws BusinessException 业务一场
     * @throws InterruptedException 终端异常
     */
    @Test
    public void testSyncTerminalBackgroundWhenListIsEmpty() throws BusinessException, InterruptedException {
        new Expectations() {
            {
                sessionManager.getOnlineTerminalId();
                result = Collections.EMPTY_LIST;
            }
        };
        TerminalBackgroundInfo TerminalBackgroundInfo = new TerminalBackgroundInfo();
        TerminalBackgroundInfo.setImageName("background.png");
        TerminalBackgroundInfo.setImagePath("C:/");
        TerminalBackgroundInfo.setMd5("md5");
        terminalBackgroundService.syncTerminalBackground(TerminalBackgroundInfo);
        Thread.sleep(1000);
        new Verifications() {
            {
                sessionManager.getOnlineTerminalId();
                times = 1;
            }
        };
    }

    /**
     * 测试同步终端背景图时，sender为空的情况
     * 
     * @throws BusinessException 业务异常
     * @throws InterruptedException 中断异常
     */
    @Test
    public void testSyncTerminalBackgroundWhenSenderIsEmpty() throws BusinessException, InterruptedException {
        String terminalId = UUID.randomUUID().toString();
        List<String> onlineTerminalIdList = Lists.newArrayList(terminalId);
        new Expectations() {
            {
                sessionManager.getOnlineTerminalId();
                result = onlineTerminalIdList;
                sessionManager.getRequestMessageSender(anyString);
                result = null;
            }
        };
        TerminalBackgroundInfo TerminalBackgroundInfo = new TerminalBackgroundInfo();
        TerminalBackgroundInfo.setImageName("background.png");
        TerminalBackgroundInfo.setImagePath("C:/");
        TerminalBackgroundInfo.setMd5("md5");
        terminalBackgroundService.syncTerminalBackground(TerminalBackgroundInfo);
        Thread.sleep(1000);
        new Verifications() {
            {
                sessionManager.getOnlineTerminalId();
                times = 1;
            }
        };
    }

    /**
     * 测试同步终端背景图片的时候，同步失败
     * 
     * @throws BusinessException 业务异常
     * @throws InterruptedException 中断异常
     * @throws IOException IO异常
     */
    @Test
    public void testSyncTerminalBackgroundWhenSyncError() throws BusinessException, InterruptedException, IOException {
        String terminalId = UUID.randomUUID().toString();
        List<String> onlineTerminalIdList = Lists.newArrayList(terminalId);
        new Expectations() {
            {
                sessionManager.getOnlineTerminalId();
                result = onlineTerminalIdList;
                sessionManager.getRequestMessageSender(anyString);
                result = sender;
                sender.syncRequest(withArgThat(new CustomMatcher<Message>("") {
                    @Override
                    public boolean matches(Object o) {
                        Assert.assertNotNull(o);
                        Message message = (Message) o;
                        EqualsBuilder equalsBuilder = new EqualsBuilder();
                        equalsBuilder.append(message.getSystemType(), Constants.SYSTEM_TYPE);
                        equalsBuilder.append(message.getAction(), SendTerminalEventEnums.CHANGE_TERMINAL_BACKGROUND.getName());
                        return equalsBuilder.isEquals();
                    }
                }));
                result = new RuntimeException("");
            }
        };
        TerminalBackgroundInfo TerminalBackgroundInfo = new TerminalBackgroundInfo();
        TerminalBackgroundInfo.setImageName("background.png");
        TerminalBackgroundInfo.setImagePath("C:/");
        TerminalBackgroundInfo.setMd5("md5");
        terminalBackgroundService.syncTerminalBackground(TerminalBackgroundInfo);
        Thread.sleep(1000);
        new Verifications() {
            {
                sessionManager.getOnlineTerminalId();
                times = 1;
                sender.syncRequest((Message) any);
                times = 1;
            }
        };
    }

    /**
     * 测试同步终端背景图，正常的情况
     * 
     * @throws BusinessException 业务异常
     * @throws InterruptedException 中断异常
     * @throws IOException IO异常
     */
    @Test
    public void testSyncTerminalBackground() throws BusinessException, InterruptedException, IOException {
        String terminalId = UUID.randomUUID().toString();
        List<String> onlineTerminalIdList = Lists.newArrayList(terminalId);
        new Expectations() {
            {
                sessionManager.getOnlineTerminalId();
                result = onlineTerminalIdList;
                sessionManager.getRequestMessageSender(anyString);
                result = sender;
                sender.syncRequest(withArgThat(new CustomMatcher<Message>("") {
                    @Override
                    public boolean matches(Object o) {
                        Assert.assertNotNull(o);
                        Message message = (Message) o;
                        EqualsBuilder equalsBuilder = new EqualsBuilder();
                        equalsBuilder.append(message.getSystemType(), Constants.SYSTEM_TYPE);
                        equalsBuilder.append(message.getAction(), SendTerminalEventEnums.CHANGE_TERMINAL_BACKGROUND.getName());
                        return equalsBuilder.isEquals();
                    }
                }));

            }
        };
        TerminalBackgroundInfo TerminalBackgroundInfo = new TerminalBackgroundInfo();
        TerminalBackgroundInfo.setImageName("background.png");
        TerminalBackgroundInfo.setImagePath("C:/");
        TerminalBackgroundInfo.setMd5("md5");
        terminalBackgroundService.syncTerminalBackground(TerminalBackgroundInfo);
        Thread.sleep(1000);
        new Verifications() {
            {
                sessionManager.getOnlineTerminalId();
                times = 1;
                sender.syncRequest((Message) any);
                times = 1;
            }
        };
    }
}

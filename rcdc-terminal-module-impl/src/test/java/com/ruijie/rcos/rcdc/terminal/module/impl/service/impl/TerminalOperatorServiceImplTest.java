package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.*;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.alibaba.fastjson.JSON;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbCollectLogStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.CollectLogCache;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.CollectLogCacheManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.connect.SessionManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalDetectionDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalDetectionEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.DetectStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.SendTerminalEventEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalDetectService;
import com.ruijie.rcos.sk.base.crypto.AesUtil;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.test.ThrowExceptionTester;
import com.ruijie.rcos.sk.commkit.base.message.Message;
import com.ruijie.rcos.sk.commkit.base.message.base.BaseMessage;
import com.ruijie.rcos.sk.commkit.base.sender.DefaultRequestMessageSender;
import com.ruijie.rcos.sk.modulekit.api.tool.GlobalParameterAPI;

import mockit.*;
import mockit.integration.junit4.JMockit;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/11/2
 *
 * @author Jarman
 */
@RunWith(JMockit.class)
public class TerminalOperatorServiceImplTest {

    @Injectable
    private SessionManager sessionManager;

    @Injectable
    private CollectLogCacheManager collectLogCacheManager;

    @Injectable
    DefaultRequestMessageSender sender;

    @Injectable
    private TerminalBasicInfoDAO basicInfoDAO;

    @Tested
    private TerminalOperatorServiceImpl operatorService;

    @Injectable
    private TerminalDetectService terminalDetectService;

    @Injectable
    private GlobalParameterAPI globalParameterAPI;

    @Injectable
    private TerminalDetectionDAO terminalDetectionDAO;

    /**
     * 测试关机成功
     * 
     * @throws IOException exception
     * @throws InterruptedException exception
     */
    @Test
    public void testShutdownSuccess() throws IOException, InterruptedException {
        String action = "login";
        Map<String, Object> data = new HashMap<>();
        data.put("code", 100);
        data.put("content", "hello");
        BaseMessage baseMessage = new BaseMessage(action, JSON.toJSONString(data));
        new Expectations() {
            {
                try {
                    sessionManager.getRequestMessageSender(anyString);
                    result = sender;
                } catch (BusinessException e) {
                    result = sender;
                }
                sender.syncRequest((Message) any);
                result = baseMessage;
            }
        };
        String terminalId = "123";

        try {
            operatorService.shutdown(terminalId);
        } catch (BusinessException e) {
            fail();
        }

        new Verifications() {
            {
                Message message;
                sender.syncRequest(message = withCapture());
                assertEquals(message.getAction(), SendTerminalEventEnums.SHUTDOWN_TERMINAL.getName());
            }
        };
    }

    /**
     * 测试关机,离线终端关机失败
     */
    @Test
    public void testShutdownOfflineFail() {
        String terminalId = "123";

        new Expectations() {
            {
                sessionManager.getSession(terminalId);
                result = null;
            }
        };

        try {
            operatorService.shutdown(terminalId);
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_TERMINAL_OFFLINE_CANNOT_SHUTDOWN, e.getKey());
        }
        new Verifications() {
            {
                basicInfoDAO.getTerminalNameByTerminalId(terminalId);
                times = 1;
            }
        };
    }


    /**
     * 测试重启
     * 
     * @throws IOException exception
     * @throws InterruptedException exception
     */
    @Test
    public void testRestart() throws IOException, InterruptedException {
        String action = "login";
        Map<String, Object> data = new HashMap<>();
        data.put("code", 100);
        data.put("content", "hello");
        BaseMessage baseMessage = new BaseMessage(action, JSON.toJSONString(data));
        new Expectations() {
            {
                try {
                    sessionManager.getRequestMessageSender(anyString);
                    result = sender;
                } catch (BusinessException e) {
                    result = sender;
                }
                sender.syncRequest((Message) any);
                result = baseMessage;
            }
        };
        String terminalId = "123";

        try {
            operatorService.restart(terminalId);
        } catch (BusinessException e) {
            fail();
        }

        new Verifications() {
            {
                Message message;
                sender.syncRequest(message = withCapture());
                assertEquals(message.getAction(), SendTerminalEventEnums.RESTART_TERMINAL.getName());
            }
        };
    }

    /**
     * 测试收集日志-状态为正在进行中
     */
    @Test
    public void testCollectLogIsDoing() {
        String terminalId = "12334";
        CollectLogCache collectLogCache = new CollectLogCache();
        collectLogCache.setState(CbbCollectLogStateEnums.DOING);
        new Expectations() {
            {
                collectLogCacheManager.getCache(terminalId);
                result = collectLogCache;
            }
        };

        try {
            operatorService.collectLog(terminalId);
        } catch (BusinessException e) {
            assertEquals(e.getKey(), BusinessKey.RCDC_TERMINAL_COLLECT_LOG_DOING);
        }
    }

    /**
     * 测试收集日志不存在并且状态为正在进行
     *
     * @throws IOException exception
     * @throws InterruptedException exception
     */
    @Test
    public void testCollectLogNoExistsAndIsDoing() throws IOException, InterruptedException {
        String terminalId = "123";
        CollectLogCache logCache = new CollectLogCache();
        logCache.setState(CbbCollectLogStateEnums.DOING);
        String action = "login";
        Map<String, Object> data = new HashMap<>();
        data.put("code", 100);
        data.put("content", "hello");
        BaseMessage baseMessage = new BaseMessage(action, JSON.toJSONString(data));
        new Expectations() {
            {
                collectLogCacheManager.getCache(terminalId);
                result = null;
                collectLogCacheManager.addCache(terminalId);
                result = logCache;
                sender.syncRequest((Message) any);
                result = baseMessage;
            }
        };

        try {
            operatorService.collectLog(terminalId);
        } catch (BusinessException e) {
            assertEquals(e.getKey(), BusinessKey.RCDC_TERMINAL_COLLECT_LOG_DOING);
        }
    }

    /**
     * 测试发送收集日志
     * 
     * @throws BusinessException exception
     * @throws IOException exception
     * @throws InterruptedException exception
     */
    @Test
    public void testCollectLogSend() throws BusinessException, IOException, InterruptedException {
        CollectLogCache logCache = new CollectLogCache();
        logCache.setState(CbbCollectLogStateEnums.DONE);
        String action = "login";
        Map<String, Object> data = new HashMap<>();
        data.put("code", 100);
        data.put("content", "hello");
        BaseMessage baseMessage = new BaseMessage(action, JSON.toJSONString(data));
        new Expectations() {
            {
                collectLogCacheManager.getCache(anyString);
                result = logCache;
                sessionManager.getRequestMessageSender(anyString);
                result = sender;
                sender.syncRequest((Message) any);
                result = baseMessage;
            }
        };

        try {
            String terminalId = "123";
            operatorService.collectLog(terminalId);
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
     * 测试发送收集日志,DefaultRequestMessageSender为空
     * 
     * @throws BusinessException 业务异常
     */
    @Test
    public void testCollectLogSendDefaultRequestMessageSenderIsNull() throws BusinessException {
        CollectLogCache logCache = new CollectLogCache();
        logCache.setState(CbbCollectLogStateEnums.DONE);
        new Expectations() {
            {
                collectLogCacheManager.getCache(anyString);
                result = logCache;
                sessionManager.getRequestMessageSender(anyString);
                result = sender;
                sessionManager.getRequestMessageSender(anyString);
                result = null;
            }
        };

        try {
            String terminalId = "123";
            operatorService.collectLog(terminalId);
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_TERMINAL_OFFLINE, e.getKey());
        }

        new Verifications() {
            {
                collectLogCacheManager.removeCache("123");
                times = 1;
            }
        };
    }

    /**
     * 测试检测
     * 
     * @throws BusinessException 业务异常
     */
    @Test
    public void testDetect() throws BusinessException {
        String terminalId = "123";

        new Expectations() {
            {
                terminalDetectService.findInCurrentDate(anyString);
                result = new TerminalDetectionEntity();
            }
        };
        // 未完成
        operatorService.detect(terminalId);

        new Verifications() {
            {
                terminalDetectService.findInCurrentDate(anyString);
                times = 1;
                terminalDetectionDAO.save((TerminalDetectionEntity) any);
                times = 0;
            }
        };
    }

    /**
     * 测试检测,参数为空
     * 
     * @throws Exception 异常
     */
    @Test
    public void testDetectArgumentIsNull() throws Exception {
        String terminalId = null;
        ThrowExceptionTester.throwIllegalArgumentException(() -> operatorService.detect(terminalId), "terminalId不能为空");
        assertTrue(true);
    }

    /**
     * 测试检测,TerminalDetectionEntity is null
     * 
     * @throws BusinessException 业务异常
     */
    @Test
    public void testDetectTerminalDetectionEntityIsNull() throws BusinessException {
        String terminalId = "123";

        new Expectations() {
            {
                terminalDetectService.findInCurrentDate(anyString);
                result = null;
            }
        };
        operatorService.detect(terminalId);

        new Verifications() {
            {
                terminalDetectService.findInCurrentDate(anyString);
                times = 1;
                terminalDetectService.save(terminalId);
                times = 1;
            }
        };
    }

    /**
     * 测试检测,正在检测,状态为wait
     * 
     * @throws BusinessException 业务异常
     */
    @Test
    public void testDetectTerminalDetectStateIsWait() throws BusinessException {
        String terminalId = "123";
        TerminalDetectionEntity terminalDetectionEntity = new TerminalDetectionEntity();
        terminalDetectionEntity.setDetectState(DetectStateEnums.WAIT);
        new Expectations() {
            {
                terminalDetectService.findInCurrentDate(anyString);
                result = terminalDetectionEntity;
            }
        };
        try {
            operatorService.detect(terminalId);
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_TERMINAL_DETECT_IS_DOING, e.getKey());
        }

        new Verifications() {
            {
                terminalDetectService.findInCurrentDate(anyString);
                times = 1;
                terminalDetectService.delete((UUID) any);
                times = 0;
            }
        };
    }

    /**
     * 测试检测,正在检测，状态为checking
     *
     * @throws BusinessException 业务异常
     */
    @Test
    public void testDetectTerminalDetectStateIsChecking() throws BusinessException {
        String terminalId = "123";
        TerminalDetectionEntity terminalDetectionEntity = new TerminalDetectionEntity();
        terminalDetectionEntity.setDetectState(DetectStateEnums.CHECKING);
        new Expectations() {
            {
                terminalDetectService.findInCurrentDate(anyString);
                result = terminalDetectionEntity;
            }
        };
        try {
            operatorService.detect(terminalId);
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_TERMINAL_DETECT_IS_DOING, e.getKey());
        }

        new Verifications() {
            {
                terminalDetectService.findInCurrentDate(anyString);
                times = 1;
                terminalDetectService.delete((UUID) any);
                times = 0;
            }
        };
    }

    /**
     * 测试检测,结果发送失败
     * 
     * @throws BusinessException 业务异常
     */
    @Test
    public void testDetectSendFail() throws BusinessException {
        String terminalId = "123";

        new Expectations() {
            {
                terminalDetectService.findInCurrentDate(anyString);
                result = new TerminalDetectionEntity();
            }
        };
        // 未完成
        operatorService.detect(terminalId);

        new Verifications() {
            {
                terminalDetectService.findInCurrentDate(anyString);
                times = 1;
                terminalDetectService.delete((UUID) any);
                times = 1;
                terminalDetectService.save(anyString);
                times = 1;
            }
        };
    }

    /**
     * 测试changePassword，参数为空
     * 
     * @throws Exception 异常
     */
    @Test
    public void testChangePasswordArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> operatorService.changePassword(null), "password 不能为空");
        assertTrue(true);
    }

    /**
     * 测试changePassword，onlineTerminalIdList为空
     * 
     * @param aesUtil mock aesUtil
     * @throws BusinessException 异常
     * @throws InterruptedException ex
     */
    @Test
    public void testChangePasswordOnlineTerminalIdListIsEmpty(@Mocked AesUtil aesUtil) throws BusinessException, InterruptedException {
        String password = "123";
        String encryptPwd = "yyy123";
        new Expectations() {
            {
                AesUtil.encrypt(password, Constants.TERMINAL_ADMIN_PASSWORD_AES_KEY);
                result = encryptPwd;
                globalParameterAPI.findParameter(Constants.RCDC_TERMINAL_ADMIN_PWD_GLOBAL_PARAMETER_KEY);
                result = "456";
                sessionManager.getOnlineTerminalId();
                result = Collections.emptyList();
            }
        };
        operatorService.changePassword(password);
        Thread.sleep(1000);

        new Verifications() {
            {
                globalParameterAPI.updateParameter(Constants.RCDC_TERMINAL_ADMIN_PWD_GLOBAL_PARAMETER_KEY, encryptPwd);
                times = 1;
                globalParameterAPI.findParameter(Constants.RCDC_TERMINAL_ADMIN_PWD_GLOBAL_PARAMETER_KEY);
                times = 1;
                sessionManager.getOnlineTerminalId();
                times = 1;
                sessionManager.getRequestMessageSender(anyString);
                times = 0;
            }
        };
    }

    /**
     * 测试changePassword，管理员密码为空
     * 
     * @param aesUtil mock aesUtil
     * @throws BusinessException 异常
     */
    @Test
    public void testChangePasswordAdminPasswordIsBlank(@Mocked AesUtil aesUtil) throws BusinessException {
        String password = "123";
        new Expectations() {
            {
                globalParameterAPI.findParameter(Constants.RCDC_TERMINAL_ADMIN_PWD_GLOBAL_PARAMETER_KEY);
                result = "";
            }
        };
        try {
            operatorService.changePassword(password);
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_TERMINAL_ADMIN_PWD_RECORD_NOT_EXIST, e.getKey());
        }
    }

    /**
     * 测试changePassword，DefaultRequestMessageSender为空
     * 
     * @param aesUtil mock aesUtil
     * @throws BusinessException 异常
     * @throws InterruptedException ex
     */
    @Test
    public void testChangePasswordDefaultRequestMessageSenderIsNull(@Mocked AesUtil aesUtil) throws BusinessException, InterruptedException {
        String password = "123";
        String encryptPwd = "yyy123";
        List<String> onlineTerminalIdList = new ArrayList<>();
        onlineTerminalIdList.add("1");
        new Expectations() {
            {
                AesUtil.encrypt(password, Constants.TERMINAL_ADMIN_PASSWORD_AES_KEY);
                result = encryptPwd;
                globalParameterAPI.findParameter(Constants.RCDC_TERMINAL_ADMIN_PWD_GLOBAL_PARAMETER_KEY);
                result = "456";
                sessionManager.getOnlineTerminalId();
                result = onlineTerminalIdList;
                sessionManager.getRequestMessageSender(anyString);
                result = null;
            }
        };
        operatorService.changePassword(password);

        Thread.sleep(1000);

        new Verifications() {
            {
                globalParameterAPI.updateParameter(Constants.RCDC_TERMINAL_ADMIN_PWD_GLOBAL_PARAMETER_KEY, encryptPwd);
                times = 1;
                globalParameterAPI.findParameter(Constants.RCDC_TERMINAL_ADMIN_PWD_GLOBAL_PARAMETER_KEY);
                times = 1;
                sessionManager.getOnlineTerminalId();
                times = 1;
                sessionManager.getRequestMessageSender(anyString);
                times = 1;
            }
        };
    }

    /**
     * 测试changePassword，DefaultRequestMessageSender为空
     * 
     * @param aesUtil mock aesUtil
     * @throws BusinessException 异常
     * @throws IOException 异常
     * @throws InterruptedException 异常
     */
    @Test
    public void testChangePassword(@Mocked AesUtil aesUtil) throws BusinessException, IOException, InterruptedException {
        String password = "123";
        String encryptPwd = "yyy123";
        List<String> onlineTerminalIdList = new ArrayList<>();
        onlineTerminalIdList.add("1");
        new Expectations() {
            {
                AesUtil.encrypt(password, Constants.TERMINAL_ADMIN_PASSWORD_AES_KEY);
                result = encryptPwd;
                globalParameterAPI.findParameter(Constants.RCDC_TERMINAL_ADMIN_PWD_GLOBAL_PARAMETER_KEY);
                result = "456";
                sessionManager.getOnlineTerminalId();
                result = onlineTerminalIdList;
                sessionManager.getRequestMessageSender(anyString);
                result = sender;
            }
        };
        operatorService.changePassword(password);
        Thread.sleep(1000);
        new Verifications() {
            {
                globalParameterAPI.updateParameter(Constants.RCDC_TERMINAL_ADMIN_PWD_GLOBAL_PARAMETER_KEY, encryptPwd);
                times = 1;
                globalParameterAPI.findParameter(Constants.RCDC_TERMINAL_ADMIN_PWD_GLOBAL_PARAMETER_KEY);
                times = 1;
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
     * 测试getTerminalPassword
     * 
     * @throws BusinessException 异常
     */
    @Test
    public void testGetTerminalPassword() throws BusinessException {
        new Expectations() {
            {
                globalParameterAPI.findParameter(Constants.RCDC_TERMINAL_ADMIN_PWD_GLOBAL_PARAMETER_KEY);
                returns("", "123");
            }
        };
        try {
            operatorService.getTerminalPassword();
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_TERMINAL_ADMIN_PWD_RECORD_NOT_EXIST, e.getKey());
        }
        assertEquals("123", operatorService.getTerminalPassword());
    }
}

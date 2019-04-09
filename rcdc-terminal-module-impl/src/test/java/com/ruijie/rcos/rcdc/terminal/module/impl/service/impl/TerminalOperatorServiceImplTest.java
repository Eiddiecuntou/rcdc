package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.util.Assert;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CollectLogStateEnums;
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
import com.ruijie.rcos.rcdc.terminal.module.impl.tx.TerminalDetectService;
import com.ruijie.rcos.sk.base.concorrent.executor.SkyengineScheduledThreadPoolExecutor;
import com.ruijie.rcos.sk.base.crypto.AesUtil;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.test.ThrowExceptionTester;
import com.ruijie.rcos.sk.commkit.base.message.Message;
import com.ruijie.rcos.sk.commkit.base.sender.DefaultRequestMessageSender;
import com.ruijie.rcos.sk.modulekit.api.tool.GlobalParameterAPI;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;
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
     */
    @Test
    public void testShutdownSuccess() {
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

        try {
            operatorService.shutdown(terminalId);
        } catch (BusinessException e) {
            fail();
        }

        new Verifications() {
            {
                Message message;
                sender.request(message = withCapture());
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
     */
    @Test
    public void testRestart() {
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

        try {
            operatorService.restart(terminalId);
        } catch (BusinessException e) {
            fail();
        }

        new Verifications() {
            {
                Message message;
                sender.request(message = withCapture());
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
        collectLogCache.setState(CollectLogStateEnums.DOING);
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
     */
    @Test
    public void testCollectLogNoExistsAndIsDoing() {
        String terminalId = "123";
        CollectLogCache logCache = new CollectLogCache();
        logCache.setState(CollectLogStateEnums.DOING);
        new Expectations() {
            {
                collectLogCacheManager.getCache(terminalId);
                result = null;
                collectLogCacheManager.addCache(terminalId);
                result = logCache;
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
     * @throws BusinessException 业务异常
     */
    @Test
    public void testCollectLogSend() throws BusinessException {
        CollectLogCache logCache = new CollectLogCache();
        logCache.setState(CollectLogStateEnums.DONE);
        new Expectations() {
            {
                collectLogCacheManager.getCache(anyString);
                result = logCache;
                sessionManager.getRequestMessageSender(anyString);
                result = sender;
                sender.request((Message) any);

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
                sender.request((Message) any);
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
        logCache.setState(CollectLogStateEnums.DONE);
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
     * 测试检测,参数为空
     * 
     * @throws Exception 异常
     */
    @Test
    public void testDetect0ArgumentIsNull() throws Exception {
        // 测试参数为空
        String[] terminalIdArr = new String[0];
        ThrowExceptionTester.throwIllegalArgumentException(() -> operatorService.detect(terminalIdArr), "terminalIdArr大小不能为0");
        assertTrue(true);
    }

    /**
     * 测试检测
     * 
     * @throws BusinessException 异常
     */
    @Test
    public void testDetect0() throws BusinessException {
        String[] terminalIdArr = new String[1];
        String terminalId = "123";
        terminalIdArr[0] = terminalId;
        new Expectations() {
            {
                terminalDetectService.findInCurrentDate(anyString);
                result = new TerminalDetectionEntity();
            }
        };
        operatorService.detect(terminalIdArr);

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
     * 测试检测,正在检测
     * 
     * @throws BusinessException 业务异常
     */
    @Test
    public void testDetectTerminalDetecting() throws BusinessException {
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
                sessionManager.getRequestMessageSender(anyString);
                result = null;
            }
        };
        // 未完成
        operatorService.detect(terminalId);

        new Verifications() {
            {
                terminalDetectService.findInCurrentDate(anyString);
                times = 1;
                terminalDetectionDAO.save((TerminalDetectionEntity) any);
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
     */
    @Test
    public void testChangePasswordOnlineTerminalIdListIsEmpty(@Mocked AesUtil aesUtil) throws BusinessException {
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
        new MockUp<SkyengineScheduledThreadPoolExecutor>() {
            @Mock
            public void execute(Runnable command) {
                Assert.notNull(command, "command can not be null");
                command.run();
            }
        };
        operatorService.changePassword(password);

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
     */
    @Test
    public void testChangePasswordDefaultRequestMessageSenderIsNull(@Mocked AesUtil aesUtil) throws BusinessException {
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
        new MockUp<SkyengineScheduledThreadPoolExecutor>() {
            @Mock
            public void execute(Runnable command) {
                Assert.notNull(command, "command can not be null");
                command.run();
            }
        };
        operatorService.changePassword(password);
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
     */
    @Test
    public void testChangePassword(@Mocked AesUtil aesUtil) throws BusinessException {
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
        new MockUp<SkyengineScheduledThreadPoolExecutor>() {
            @Mock
            public void execute(Runnable command) {
                Assert.notNull(command, "command can not be null");
                command.run();
            }
        };
        operatorService.changePassword(password);
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
                sender.request((Message) any);
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

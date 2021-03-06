package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.ruijie.rcos.rcdc.codec.adapter.base.sender.DefaultRequestMessageSender;
import com.ruijie.rcos.rcdc.terminal.module.def.PublicBusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbCollectLogStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalPlatformEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.CollectLogCache;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.CollectLogCacheManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.connect.SessionManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalDetectionDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalDetectionEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.DataDiskClearCodeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.DetectStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.SendTerminalEventEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalDetectService;
import com.ruijie.rcos.sk.base.concurrent.ThreadExecutor;
import com.ruijie.rcos.sk.base.crypto.AesUtil;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.i18n.LocaleI18nResolver;
import com.ruijie.rcos.sk.base.test.ThrowExceptionTester;
import com.ruijie.rcos.sk.commkit.base.message.Message;
import com.ruijie.rcos.sk.commkit.base.message.base.BaseMessage;
import com.ruijie.rcos.sk.modulekit.api.tool.GlobalParameterAPI;
import mockit.*;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.*;

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
     * before
     */
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

        new MockUp<ThreadExecutor>() {
            @Mock
            void execute(Runnable command) {
                command.run();
            }
        };
    }

    /**
     * ??????????????????
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
     * ????????????,????????????????????????
     */
    @Test
    public void testShutdownOfflineFail() {
        String terminalId = "123";

        new Expectations() {
            {
                sessionManager.getSessionByAlias(terminalId);
                result = null;
            }
        };

        try {
            operatorService.shutdown(terminalId);
            fail();
        } catch (BusinessException e) {
            assertEquals(PublicBusinessKey.RCDC_TERMINAL_OFFLINE_CANNOT_SHUTDOWN, e.getKey());
        }
        new Verifications() {
            {
                basicInfoDAO.findTerminalEntityByTerminalId(terminalId);
                times = 1;
            }
        };
    }


    /**
     * ????????????
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
     * ??????????????????-????????????????????????
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
     * ??????????????????????????????????????????????????????
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
     * ????????????????????????
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
     * ????????????????????????,DefaultRequestMessageSender??????
     * 
     * @throws BusinessException ????????????
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
            assertEquals(PublicBusinessKey.RCDC_TERMINAL_OFFLINE, e.getKey());
        }

        new Verifications() {
            {
                collectLogCacheManager.removeCache("123");
                times = 1;
            }
        };
    }

    /**
     * ????????????
     * 
     * @throws BusinessException ????????????
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
        // ?????????
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
     * ????????????,????????????
     * 
     * @throws Exception ??????
     */
    @Test
    public void testDetectArgumentIsNull() throws Exception {
        String terminalId = null;
        ThrowExceptionTester.throwIllegalArgumentException(() -> operatorService.detect(terminalId), "terminalId????????????");
        assertTrue(true);
    }

    /**
     * ????????????,TerminalDetectionEntity is null
     * 
     * @throws BusinessException ????????????
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
     * ????????????,????????????,?????????wait
     * 
     * @throws BusinessException ????????????
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
     * ????????????,????????????????????????checking
     *
     * @throws BusinessException ????????????
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
     * ????????????,??????????????????
     * 
     * @throws BusinessException ????????????
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
        // ?????????
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
     * ??????changePassword???????????????
     * 
     * @throws Exception ??????
     */
    @Test
    public void testChangePasswordArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> operatorService.changePassword(null), "password ????????????");
        assertTrue(true);
    }

    /**
     * ??????changePassword???onlineTerminalIdList??????
     * 
     * @param aesUtil mock aesUtil
     * @throws BusinessException ??????
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
     * ??????changePassword????????????????????????
     * 
     * @param aesUtil mock aesUtil
     * @throws BusinessException ??????
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
     * ??????changePassword???DefaultRequestMessageSender??????
     * 
     * @param aesUtil mock aesUtil
     * @throws BusinessException ??????
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
     * ??????changePassword???DefaultRequestMessageSender??????
     * 
     * @param aesUtil mock aesUtil
     * @throws BusinessException ??????
     * @throws IOException ??????
     * @throws InterruptedException ??????
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
     * ??????getTerminalPassword
     * 
     * @throws BusinessException ??????
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

    /**
     * testRelieveFault
     * 
     * @throws Exception ex
     */
    @Test
    public void testRelieveFault() throws Exception {
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

        operatorService.relieveFault(terminalId, null);

        new Verifications() {
            {
                sessionManager.getRequestMessageSender(anyString);
                times = 1;
                sender.syncRequest((Message) any);
                times = 1;
            }
        };
    }

    /**
     * ???????????????????????????
     *
     * @param json json
     * @throws Exception ??????
     */
    @Test
    public void testDiskClear(@Mocked JSON json) throws Exception {
        TerminalEntity entity = new TerminalEntity();
        entity.setState(CbbTerminalStateEnums.ONLINE);
        entity.setPlatform(CbbTerminalPlatformEnums.IDV);

        BaseMessage message = new BaseMessage("xxx", "xxx");
        new Expectations() {
            {
                basicInfoDAO.findTerminalEntityByTerminalId(anyString);
                result = entity;
                sender.syncRequest((Message) any);
                result = message;
            }
        };
        operatorService.diskClear("xxx");
        new Verifications() {
            {
                basicInfoDAO.findTerminalEntityByTerminalId(anyString);
                times = 1;
                sessionManager.getRequestMessageSender(anyString);
                times = 1;
                sender.syncRequest((Message) any);
                times = 1;
            }
        };
    }

    /**
     * ?????????????????????,???????????????
     *
     * @param json json
     * @throws Exception ??????
     */
    @Test(expected = BusinessException.class)
    public void testDiskClearTerminalNotExist(@Mocked JSON json) throws Exception {

        new Expectations() {
            {
                basicInfoDAO.findTerminalEntityByTerminalId(anyString);
                result = null;
            }
        };
        operatorService.diskClear("xxx");
        fail();
        new Verifications() {
            {
                basicInfoDAO.findTerminalEntityByTerminalId(anyString);
                times = 1;
                sessionManager.getRequestMessageSender(anyString);
                times = 0;
                sender.syncRequest((Message) any);
                times = 0;
            }
        };
    }

    /**
     * ?????????????????????,????????????
     *
     * @param json json
     * @throws Exception ??????
     */
    @Test(expected = BusinessException.class)
    public void testDiskClearTerminalNotOnline(@Mocked JSON json) throws Exception {
        TerminalEntity entity = new TerminalEntity();
        entity.setState(CbbTerminalStateEnums.OFFLINE);
        new Expectations() {
            {
                basicInfoDAO.findTerminalEntityByTerminalId(anyString);
                result = entity;
            }
        };
        operatorService.diskClear("xxx");
        fail();
        new Verifications() {
            {
                basicInfoDAO.findTerminalEntityByTerminalId(anyString);
                times = 1;
                sessionManager.getRequestMessageSender(anyString);
                times = 0;
            }
        };
    }

    /**
     * ?????????????????????,???????????????
     *
     * @param json json
     * @throws Exception ??????
     */
    @Test(expected = BusinessException.class)
    public void testDiskClearTerminalUpUpgrading(@Mocked JSON json) throws Exception {
        TerminalEntity entity = new TerminalEntity();
        entity.setState(CbbTerminalStateEnums.UPGRADING);
        new Expectations() {
            {
                basicInfoDAO.findTerminalEntityByTerminalId(anyString);
                result = entity;
            }
        };
        operatorService.diskClear("xxx");
        fail();
    }

    /**
     * ?????????????????????,??????
     *
     * @param json json
     * @throws Exception ??????
     */
    @Test(expected = BusinessException.class)
    public void testDiskClearNotIdvTerminal(@Mocked JSON json) throws Exception {
        TerminalEntity entity = new TerminalEntity();
        entity.setState(CbbTerminalStateEnums.ONLINE);
        entity.setPlatform(CbbTerminalPlatformEnums.VDI);
        new Expectations() {
            {
                basicInfoDAO.findTerminalEntityByTerminalId(anyString);
                result = entity;
            }
        };
        operatorService.diskClear("xxx");
        fail();
    }

    /**
     * ?????????????????????,??????????????????,?????????????????????
     *
     * @param json json
     * @throws Exception ??????
     */
    @Test(expected = BusinessException.class)
    public void testDiskClearDesktopRunning(@Mocked JSON json) throws Exception {
        TerminalEntity entity = new TerminalEntity();
        entity.setState(CbbTerminalStateEnums.ONLINE);
        entity.setPlatform(CbbTerminalPlatformEnums.IDV);
        new MockUp<TerminalOperatorServiceImpl>() {
            @Mock
            int operateTerminal(String terminalId, SendTerminalEventEnums terminalEvent, Object content, String operateActionKey)
                    throws BusinessException {
                return DataDiskClearCodeEnums.DESKTOP_ON_RUNNING.getCode();
            }
        };
        new Expectations() {
            {
                basicInfoDAO.findTerminalEntityByTerminalId(anyString);
                result = entity;
            }
        };
        operatorService.diskClear("xxx");
        fail();
    }

    /**
     * ?????????????????????,?????????????????????,?????????????????????
     *
     * @param json json
     * @throws Exception ??????
     */
    @Test(expected = BusinessException.class)
    public void testDiskClearOnRestoreDesktop(@Mocked JSON json) throws Exception {
        TerminalEntity entity = new TerminalEntity();
        entity.setState(CbbTerminalStateEnums.ONLINE);
        entity.setPlatform(CbbTerminalPlatformEnums.IDV);
        new MockUp<TerminalOperatorServiceImpl>() {
            @Mock
            int operateTerminal(String terminalId, SendTerminalEventEnums terminalEvent, Object content, String operateActionKey)
                    throws BusinessException {
                return DataDiskClearCodeEnums.TERMINAL_ON_RESTORE_DESKTOP.getCode();
            }
        };
        new Expectations() {
            {
                basicInfoDAO.findTerminalEntityByTerminalId(anyString);
                result = entity;
            }
        };
        operatorService.diskClear("xxx");
        fail();
    }

    /**
     * ?????????????????????,??????shine????????????????????????????????????
     *
     * @param json json
     * @throws Exception ??????
     */
    @Test(expected = BusinessException.class)
    public void testDiskClearOnDiskClearing(@Mocked JSON json) throws Exception {
        TerminalEntity entity = new TerminalEntity();
        entity.setState(CbbTerminalStateEnums.ONLINE);
        entity.setPlatform(CbbTerminalPlatformEnums.IDV);
        new MockUp<TerminalOperatorServiceImpl>() {
            @Mock
            int operateTerminal(String terminalId, SendTerminalEventEnums terminalEvent, Object content, String operateActionKey)
                    throws BusinessException {
                return DataDiskClearCodeEnums.TERMINAL_ON_DATA_DISK_CLEARING.getCode();
            }
        };
        new Expectations() {
            {
                basicInfoDAO.findTerminalEntityByTerminalId(anyString);
                result = entity;
            }
        };
        operatorService.diskClear("xxx");
        fail();
    }

    /**
     * ???????????????????????????????????????????????????????????????
     *
     * @param json json
     * @throws Exception ??????
     */
    @Test(expected = BusinessException.class)
    public void testDiskClearNotifyShineWebFail(@Mocked JSON json) throws Exception {
        TerminalEntity entity = new TerminalEntity();
        entity.setState(CbbTerminalStateEnums.ONLINE);
        entity.setPlatform(CbbTerminalPlatformEnums.IDV);
        new MockUp<TerminalOperatorServiceImpl>() {
            @Mock
            int operateTerminal(String terminalId, SendTerminalEventEnums terminalEvent, Object content, String operateActionKey)
                    throws BusinessException {
                return DataDiskClearCodeEnums.NOTIFY_SHINE_WEB_FAIL.getCode();
            }
        };
        new Expectations() {
            {
                basicInfoDAO.findTerminalEntityByTerminalId(anyString);
                result = entity;
            }
        };
        operatorService.diskClear("xxx");
        fail();
    }

    /**
     * ?????????????????????,???????????????????????????????????????????????????
     *
     * @param json json
     * @throws Exception ??????
     */
    @Test(expected = BusinessException.class)
    public void testDiskClearDataDiskNotCreate(@Mocked JSON json) throws Exception {
        TerminalEntity entity = new TerminalEntity();
        entity.setState(CbbTerminalStateEnums.ONLINE);
        entity.setPlatform(CbbTerminalPlatformEnums.IDV);
        new MockUp<TerminalOperatorServiceImpl>() {
            @Mock
            int operateTerminal(String terminalId, SendTerminalEventEnums terminalEvent, Object content, String operateActionKey)
                    throws BusinessException {
                return DataDiskClearCodeEnums.DATA_DISK_NOT_CREATE.getCode();
            }
        };
        new Expectations() {
            {
                basicInfoDAO.findTerminalEntityByTerminalId(anyString);
                result = entity;
            }
        };
        operatorService.diskClear("xxx");
        fail();
    }

    /**
     * ?????????????????????,???????????????????????????????????????????????????
     *
     * @param json json
     * @throws Exception ??????
     */
    @Test(expected = BusinessException.class)
    public void testDiskClearTerminalOnInit(@Mocked JSON json) throws Exception {
        TerminalEntity entity = new TerminalEntity();
        entity.setState(CbbTerminalStateEnums.ONLINE);
        entity.setPlatform(CbbTerminalPlatformEnums.IDV);
        new MockUp<TerminalOperatorServiceImpl>() {
            @Mock
            int operateTerminal(String terminalId, SendTerminalEventEnums terminalEvent, Object content, String operateActionKey)
                    throws BusinessException {
                return DataDiskClearCodeEnums.TERMINAL_ON_INITING.getCode();
            }
        };
        new Expectations() {
            {
                basicInfoDAO.findTerminalEntityByTerminalId(anyString);
                result = entity;
            }
        };
        operatorService.diskClear("xxx");
        fail();
    }


    /**
     * ????????????????????????????????????
     *
     * @param json json
     * @throws Exception ??????
     */
    @Test
    public void testSendDetectRequest(@Mocked JSON json) throws Exception {
        TerminalDetectionEntity entity = new TerminalDetectionEntity();
        entity.setTerminalId("xxx");
        BaseMessage message = new BaseMessage("xxx", "xxx");
        new Expectations() {
            {
                sender.syncRequest((Message) any);
                result = message;
            }
        };
        operatorService.sendDetectRequest(entity);
        new Verifications() {
            {
                sessionManager.getRequestMessageSender(anyString);
                times = 1;
                sender.syncRequest((Message) any);
                times = 1;
                terminalDetectionDAO.getOne((UUID) any);
                times = 1;
                terminalDetectionDAO.save((TerminalDetectionEntity) any);
                times = 1;
            }
        };
    }

    /**
     * ????????????????????????????????????
     *
     * @param json json
     * @param localeI18nResolver localeI18nResolver
     * @throws Exception ??????
     */
    @Test(expected = BusinessException.class)
    public void testSendDetectRequestWithException(@Mocked JSON json, @Mocked LocaleI18nResolver localeI18nResolver) throws Exception {
        TerminalDetectionEntity entity = new TerminalDetectionEntity();
        entity.setTerminalId("xxx");
        BaseMessage message = new BaseMessage("xxx", "xxx");
        new Expectations() {
            {
                sender.syncRequest((Message) any);
                result = new Exception();
            }
        };
        operatorService.sendDetectRequest(entity);
        fail();
    }


    /**
     * ??????idv????????????????????????,????????????IDV????????????????????????????????????
     *
     * @throws BusinessException ????????????
     */
    @Test
    public void testOfflineLoginSettingWithException() throws BusinessException {
        List<String> onlineTerminalIdList = Lists.newArrayList();
        onlineTerminalIdList.add("terminalOne");
        onlineTerminalIdList.add("terminalTwo");
        onlineTerminalIdList.add("terminalThree");
        TerminalEntity firstEntity = new TerminalEntity();
        TerminalEntity secondEntity = new TerminalEntity();
        secondEntity.setPlatform(CbbTerminalPlatformEnums.IDV);
        new Expectations() {
            {
                sessionManager.getOnlineTerminalId();
                result = onlineTerminalIdList;
                basicInfoDAO.findTerminalEntityByTerminalId(anyString);
                returns(null, firstEntity, secondEntity);
            }
        };
        operatorService.offlineLoginSetting(0);
        Assert.assertTrue(true);
    }

    /**
     * ??????idv??????????????????????????????????????????idv??????
     *
     * @throws BusinessException ????????????
     */
    @Test
    public void testOfflineLoginSettingWithNoOnlineIdv() throws BusinessException {
        List<String> onlineTerminalIdList = Lists.newArrayList();
        new Expectations() {
            {
                sessionManager.getOnlineTerminalId();
                result = onlineTerminalIdList;
            }
        };
        operatorService.offlineLoginSetting(0);
        new Verifications() {
            {
                sessionManager.getOnlineTerminalId();
                times = 1;
                basicInfoDAO.findTerminalEntityByTerminalId(anyString);
                times = 0;
            }
        };
    }

    /**
     * ??????idv????????????????????????
     *
     * @throws Exception exception
     */
    @Test
    public void testOfflineLoginSetting() throws Exception {
        List<String> onlineTerminalIdList = Lists.newArrayList();
        onlineTerminalIdList.add("terminalOne");
        TerminalEntity entity = new TerminalEntity();
        entity.setPlatform(CbbTerminalPlatformEnums.IDV);

        String action = "action";
        Map<String, Object> data = new HashMap<>();
        data.put("code", 100);
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
                sessionManager.getOnlineTerminalId();
                result = onlineTerminalIdList;
                basicInfoDAO.findTerminalEntityByTerminalId(anyString);
                result = entity;
            }
        };
        operatorService.offlineLoginSetting(0);
        Thread.sleep(1000);
        new Verifications() {
            {
                sessionManager.getOnlineTerminalId();
                times = 1;
                basicInfoDAO.findTerminalEntityByTerminalId(anyString);
                times = 1;
            }
        };
    }

    /**
     * ??????queryOfflineLoginSetting
     */
    @Test
    public void testQueryOfflineLoginSetting() {
        new Expectations() {
            {
                globalParameterAPI.findParameter(Constants.OFFLINE_LOGIN_TIME_KEY);
                result = "0";
            }
        };

        final String result = operatorService.queryOfflineLoginSetting();
        Assert.assertEquals("0", result);
    }

}

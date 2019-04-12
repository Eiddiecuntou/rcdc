package com.ruijie.rcos.rcdc.terminal.module.web.ctrl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTerminalBasicInfoAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTerminalOperatorAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbChangePasswordRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalIdRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalLogNameRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbTerminalCollectLogStatusResponse;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbTerminalLogFileInfoResponse;
import com.ruijie.rcos.rcdc.terminal.module.web.ctrl.batchtask.TerminalIdMappingUtils;
import com.ruijie.rcos.rcdc.terminal.module.web.ctrl.request.EditAdminPwdWebRequest;
import com.ruijie.rcos.rcdc.terminal.module.web.ctrl.request.TerminalIdArrWebRequest;
import com.ruijie.rcos.rcdc.terminal.module.web.ctrl.request.TerminalIdWebRequest;
import com.ruijie.rcos.rcdc.terminal.module.web.ctrl.request.TerminalLogDownLoadWebRequest;
import com.ruijie.rcos.sk.base.batch.BatchTaskBuilder;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.i18n.LocaleI18nResolver;
import com.ruijie.rcos.sk.base.test.ThrowExceptionTester;
import com.ruijie.rcos.sk.webmvc.api.optlog.ProgrammaticOptLogRecorder;
import com.ruijie.rcos.sk.webmvc.api.response.DefaultWebResponse;
import com.ruijie.rcos.sk.webmvc.api.response.DownloadWebResponse;
import com.ruijie.rcos.sk.webmvc.api.response.WebResponse.Status;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;

/**
 * 
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年1月25日
 * 
 * @author ls
 */
@RunWith(JMockit.class)
public class TerminalOperateControllerTest {

    @Tested
    private TerminalOperateController controller;

    @Injectable
    private CbbTerminalOperatorAPI terminalOperatorAPI;

    @Injectable
    private CbbTerminalBasicInfoAPI basicInfoAPI;

    /**
     * 测试shutdownTerminal,参数为空
     * 
     * @param optLogRecorder mock日志记录对象
     * @param builder mock批量任务处理对象
     * @throws Exception 异常
     */
    @Test
    public void testShutdownTerminalArgumentIsNull(@Mocked ProgrammaticOptLogRecorder optLogRecorder, @Mocked BatchTaskBuilder builder)
            throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> controller.shutdownTerminal(null, optLogRecorder, builder),
                "TerminalIdArrWebRequest不能为null");
        ThrowExceptionTester.throwIllegalArgumentException(() -> controller.shutdownTerminal(new TerminalIdArrWebRequest(), null, builder),
                "optLogRecorder不能为null");
        ThrowExceptionTester.throwIllegalArgumentException(() -> controller.shutdownTerminal(new TerminalIdArrWebRequest(), optLogRecorder, null),
                "builder不能为null");
        assertTrue(true);
    }

    /**
     * 测试shutdownTerminal,
     * 
     * @param optLogRecorder mock日志记录对象
     * @param builder mock批量任务处理对象
     * @param utils mock TerminalIdMappingUtils
     * @throws BusinessException 异常
     */
    @Test
    public void testShutdownTerminal(@Mocked ProgrammaticOptLogRecorder optLogRecorder, @Mocked BatchTaskBuilder builder,
            @Mocked TerminalIdMappingUtils utils) throws BusinessException {
        TerminalIdArrWebRequest request = new TerminalIdArrWebRequest();
        request.setIdArr(new String[] {UUID.randomUUID().toString(), UUID.randomUUID().toString()});
        UUID[] uuidArr = new UUID[2];
        uuidArr[0] = UUID.randomUUID();
        uuidArr[1] = UUID.randomUUID();
        new Expectations() {
            {
                TerminalIdMappingUtils.extractUUID((Map<UUID, String>) any);
                result = uuidArr;
            }
        };
        DefaultWebResponse response = controller.shutdownTerminal(request, optLogRecorder, builder);
        assertEquals(Status.SUCCESS, response.getStatus());
    }

    /**
     * 测试restartTerminal,参数为空
     * 
     * @param optLogRecorder mock日志记录对象
     * @param builder mock批量任务处理对象
     * @throws Exception 异常
     */
    @Test
    public void testRestartTerminalArgumentIsNull(@Mocked ProgrammaticOptLogRecorder optLogRecorder, @Mocked BatchTaskBuilder builder)
            throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> controller.restartTerminal(null, optLogRecorder, builder),
                "TerminalIdWebRequest不能为null");
        ThrowExceptionTester.throwIllegalArgumentException(() -> controller.restartTerminal(new TerminalIdArrWebRequest(), null, builder),
                "optLogRecorder不能为null");
        ThrowExceptionTester.throwIllegalArgumentException(() -> controller.restartTerminal(new TerminalIdArrWebRequest(), optLogRecorder, null),
                "builder不能为null");
        assertTrue(true);
    }

    /**
     * 测试restartTerminal,
     * 
     * @param optLogRecorder mock日志记录对象
     * @param builder mock批量任务处理对象
     * @param utils mock TerminalIdMappingUtils
     * @throws BusinessException 异常
     */
    @Test
    public void testRestartTerminal(@Mocked ProgrammaticOptLogRecorder optLogRecorder, @Mocked BatchTaskBuilder builder,
            @Mocked TerminalIdMappingUtils utils) throws BusinessException {
        TerminalIdArrWebRequest request = new TerminalIdArrWebRequest();
        request.setIdArr(new String[] {UUID.randomUUID().toString(), UUID.randomUUID().toString()});
        UUID[] uuidArr = new UUID[2];
        uuidArr[0] = UUID.randomUUID();
        uuidArr[1] = UUID.randomUUID();

        new Expectations() {
            {
                TerminalIdMappingUtils.extractUUID((Map<UUID, String>) any);
                result = uuidArr;
            }
        };
        DefaultWebResponse response = controller.restartTerminal(request, optLogRecorder, builder);
        assertEquals(Status.SUCCESS, response.getStatus());
    }

    /**
     * 测试changePassword,参数为空
     * 
     * @param optLogRecorder mock日志记录对象
     * @throws Exception 异常
     */
    @Test
    public void testChangePasswordArgumentIsNull(@Mocked ProgrammaticOptLogRecorder optLogRecorder) throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> controller.changePassword(null, optLogRecorder), "request不能为null");
        ThrowExceptionTester.throwIllegalArgumentException(() -> controller.changePassword(new EditAdminPwdWebRequest(), null),
                "optLogRecorder不能为null");
        assertTrue(true);
    }

    /**
     * 测试changePassword,失败
     * 
     * @param optLogRecorder mock日志记录对象
     * @param resolver mock日志记录对象
     * @throws BusinessException 异常
     */
    @Test
    public void testChangePasswordFail(@Mocked ProgrammaticOptLogRecorder optLogRecorder, @Mocked LocaleI18nResolver resolver)
            throws BusinessException {
        EditAdminPwdWebRequest request = new EditAdminPwdWebRequest();

        new Expectations() {
            {
                terminalOperatorAPI.changePassword((CbbChangePasswordRequest) any);
                result = new BusinessException("key");
            }
        };

        new MockUp<BusinessException>() {
            @Mock
            public String getI18nMessage() {
                return "message";
            }
        };
        DefaultWebResponse response = controller.changePassword(request, optLogRecorder);
        assertEquals(Status.ERROR, response.getStatus());
        new Verifications() {
            {
                terminalOperatorAPI.changePassword((CbbChangePasswordRequest) any);
                times = 1;
                optLogRecorder.saveOptLog(anyString);
                times = 0;
                optLogRecorder.saveOptLog(anyString, anyString);
                times = 1;
            }
        };
    }

    /**
     * 测试changePassword,
     * 
     * @param optLogRecorder mock日志记录对象
     * @param resolver mock日志记录对象
     * @throws BusinessException 异常
     */
    @Test
    public void testChangePassword(@Mocked ProgrammaticOptLogRecorder optLogRecorder, @Mocked LocaleI18nResolver resolver) throws BusinessException {
        EditAdminPwdWebRequest request = new EditAdminPwdWebRequest();
        DefaultWebResponse response = controller.changePassword(request, optLogRecorder);
        assertEquals(Status.SUCCESS, response.getStatus());
        new Verifications() {
            {
                CbbChangePasswordRequest changePwdRequest;
                terminalOperatorAPI.changePassword(changePwdRequest = withCapture());
                times = 1;
                assertEquals(request.getPwd(), changePwdRequest.getPassword());
                optLogRecorder.saveOptLog(anyString);
                times = 1;
            }
        };
    }

    /**
     * 测试collectLog,参数为空
     * 
     * @throws Exception 异常
     */
    @Test
    public void testCollectLogArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> controller.collectLog(null), "request不能为null");
        assertTrue(true);
    }

    /**
     * 测试collectLog,
     * 
     * @throws BusinessException 异常
     */
    @Test
    public void testCollectLog() throws BusinessException {
        TerminalIdWebRequest request = new TerminalIdWebRequest();
        DefaultWebResponse response = controller.collectLog(request);
        assertEquals(Status.SUCCESS, response.getStatus());
        new Verifications() {
            {
                CbbTerminalIdRequest idRequest;
                terminalOperatorAPI.collectLog(idRequest = withCapture());
                times = 1;
                assertEquals(request.getTerminalId(), idRequest.getTerminalId());
            }
        };
    }

    /**
     * 测试getCollectLog,参数为空
     * 
     * @throws Exception 异常
     */
    @Test
    public void testGetCollectLogArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> controller.getCollectLog(null), "request不能为null");
        assertTrue(true);
    }

    /**
     * 测试getCollectLog,
     * 
     * @throws BusinessException 异常
     */
    @Test
    public void testGetCollectLog() throws BusinessException {
        TerminalIdWebRequest request = new TerminalIdWebRequest();
        CbbTerminalCollectLogStatusResponse response = new CbbTerminalCollectLogStatusResponse();
        new Expectations() {
            {
                terminalOperatorAPI.getCollectLog((CbbTerminalIdRequest) any);
                result = response;
            }
        };
        DefaultWebResponse webResponse = controller.getCollectLog(request);
        assertEquals(Status.SUCCESS, webResponse.getStatus());
        assertEquals(response, (CbbTerminalCollectLogStatusResponse) webResponse.getContent());
        new Verifications() {
            {
                CbbTerminalIdRequest idRequest;
                terminalOperatorAPI.getCollectLog(idRequest = withCapture());
                times = 1;
                assertEquals(request.getTerminalId(), idRequest.getTerminalId());
            }
        };
    }

    /**
     * 测试downloadLog,参数为空
     * 
     * @throws Exception 异常
     */
    @Test
    public void testDownloadLogArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> controller.downloadLog(null), "request不能为null");
        assertTrue(true);
    }

    /**
     * 测试downloadLog,
     * 
     * @throws IOException 异常
     * @throws BusinessException 异常
     */
    @Test
    public void testDownloadLog() throws BusinessException, IOException {
        TerminalLogDownLoadWebRequest request = new TerminalLogDownLoadWebRequest();
        CbbTerminalLogFileInfoResponse response = new CbbTerminalLogFileInfoResponse();
        response.setLogFilePath("logFilePath");
        response.setLogFileName("logFileName");
        response.setSuffix(".log");
        new Expectations() {
            {
                terminalOperatorAPI.getTerminalLogFileInfo((CbbTerminalLogNameRequest) any);
                result = response;
            }
        };
        new MockUp<FileInputStream>() {
            @Mock
            public void $init(File file) {

            }

            @Mock
            public int available() {
                return 1;
            }
        };
        DownloadWebResponse webResponse = controller.downloadLog(request);
        assertEquals(".log", webResponse.getFileSuffix());
        assertEquals("logFileName", webResponse.getFileName());
        assertEquals(1, webResponse.getLength());
    }
}

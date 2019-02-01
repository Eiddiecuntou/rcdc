package com.ruijie.rcos.rcdc.terminal.module.web.ctrl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTerminalSystemUpgradeAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalSystemUpgradePackageInfoDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalSystemUpgradeTaskDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbAddTerminalSystemUpgradeTaskRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbRemoveTerminalSystemUpgradeTaskRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalSystemUpgradePackageListRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalUpgradePackageUploadRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbBaseListResponse;
import com.ruijie.rcos.rcdc.terminal.module.web.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.web.request.CreateTerminalSystemUpgradeRequest;
import com.ruijie.rcos.rcdc.terminal.module.web.request.DeleteTerminalSystemUpgradeRequest;
import com.ruijie.rcos.rcdc.terminal.module.web.request.ListTerminalSystemUpgradePackageRequest;
import com.ruijie.rcos.rcdc.terminal.module.web.request.ListTerminalSystemUpgradeRequest;
import com.ruijie.rcos.sk.base.batch.BatchTaskBuilder;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.test.ThrowExceptionTester;
import com.ruijie.rcos.sk.webmvc.api.optlog.ProgrammaticOptLogRecorder;
import com.ruijie.rcos.sk.webmvc.api.request.ChunkUploadFile;
import com.ruijie.rcos.sk.webmvc.api.response.DefaultWebResponse;
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
public class TerminalSystemUpgradeControllerTest {

    @Tested
    private TerminalSystemUpgradeController controller;
    
    @Injectable
    private CbbTerminalSystemUpgradeAPI cbbTerminalUpgradeAPI;
    
    @Mocked
    private BatchTaskBuilder builder;
    
    /**
     * 测试uploadPackage，参数为空
     * @param optLogRecorder mock日志记录对象
     * @throws Exception 异常
     */
    @Test
    public void testUploadPackageArgumentIsNull(@Mocked ProgrammaticOptLogRecorder optLogRecorder) throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> controller.uploadPackage(null, optLogRecorder), "file 不能为空");
        ThrowExceptionTester.throwIllegalArgumentException(() -> controller.uploadPackage(new ChunkUploadFile(), null), "optLogRecorder 不能为空");
        assertTrue(true);
    }
    
    /**
     * 测试uploadPackage，
     * @param optLogRecorder mock日志记录对象
     * @throws BusinessException 异常
     */
    @Test
    public void testUploadPackage(@Mocked ProgrammaticOptLogRecorder optLogRecorder) throws BusinessException {
        ChunkUploadFile file = new ChunkUploadFile();
        DefaultWebResponse response = controller.uploadPackage(file, optLogRecorder);
        assertEquals(Status.SUCCESS, response.getStatus());
        new Verifications() {
            {
                CbbTerminalUpgradePackageUploadRequest request;
                cbbTerminalUpgradeAPI.uploadUpgradeFile(request = withCapture());
                times = 1;
                assertEquals(file.getFilePath(), request.getFilePath());
                assertEquals(file.getFileName(), request.getFileName());
                assertEquals(file.getFileMD5(), request.getFileMD5());
                optLogRecorder.saveOptLog(anyString, anyString);
                times = 1;
                optLogRecorder.saveOptLog(anyString, anyString, anyString);
                times = 0;
            }
        };
    }
    
    /**
     * 测试uploadPackage，BusinessException
     * @param optLogRecorder mock日志记录对象
     * @throws BusinessException 异常
     */
    @Test
    public void testUploadPackageHasBusinessException(@Mocked ProgrammaticOptLogRecorder optLogRecorder) throws BusinessException {
        ChunkUploadFile file = new ChunkUploadFile();
        
        new Expectations() {
            {
                cbbTerminalUpgradeAPI.uploadUpgradeFile((CbbTerminalUpgradePackageUploadRequest) any);
                result = new BusinessException("key");
            }
        };
        new MockUp<BusinessException>() {
            @Mock
            public String getI18nMessage() {
                return "message";
            }
        };
        try {
            controller.uploadPackage(file, optLogRecorder);
            fail();
        } catch (BusinessException e) {
            assertEquals("key", e.getKey());
        }
        new Verifications() {
            {
                CbbTerminalUpgradePackageUploadRequest request;
                cbbTerminalUpgradeAPI.uploadUpgradeFile(request = withCapture());
                times = 1;
                assertEquals(file.getFilePath(), request.getFilePath());
                assertEquals(file.getFileName(), request.getFileName());
                assertEquals(file.getFileMD5(), request.getFileMD5());
                optLogRecorder.saveOptLog(anyString, anyString);
                times = 0;
                optLogRecorder.saveOptLog(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_PACKAGE_UPLOAD_FAIL_LOG,
                        file.getFileName(), "message");
                times = 1;
            }
        };
    }

    /**
     * 测试listPackage，参数为空
     * @throws Exception 异常
     */
    @Test
    public void testListPackageArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> controller.listPackage(null), "listRequest can not be null");
        assertTrue(true);
    }
    
    /**
     * 测试listPackage，
     * @throws BusinessException 异常
     */
    @Test
    public void testListPackage() throws BusinessException {
        ListTerminalSystemUpgradePackageRequest listRequest = new ListTerminalSystemUpgradePackageRequest();
        CbbBaseListResponse<CbbTerminalSystemUpgradePackageInfoDTO> resp = new CbbBaseListResponse<>();
                
        new Expectations() {
            {
                cbbTerminalUpgradeAPI.listSystemUpgradePackage((CbbTerminalSystemUpgradePackageListRequest) any);
                result = resp;
            }
        };
        DefaultWebResponse response = controller.listPackage(listRequest);
        assertEquals(Status.SUCCESS, response.getStatus());
        assertEquals(resp, (CbbBaseListResponse<CbbTerminalSystemUpgradePackageInfoDTO>)response.getContent());
    }
    
    /**
     * 测试create，参数为空
     * @param optLogRecorder mock日志记录对象
     * @throws Exception 异常
     */
    @Test
    public void testCreateArgumentIsNull(@Mocked ProgrammaticOptLogRecorder optLogRecorder) throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> controller.create(null, optLogRecorder, builder), 
                "CreateTerminalSystemUpgradeRequest can not be null");
        ThrowExceptionTester.throwIllegalArgumentException(() -> controller.create(new CreateTerminalSystemUpgradeRequest(), null, builder),
                "optLogRecorder can not be null");
        assertTrue(true);
    }
    
    /**
     * 测试create，BusinessException
     * @param optLogRecorder mock日志记录对象
     * @throws BusinessException 异常
     */
    @Test
    public void testCreateBusinessException(@Mocked ProgrammaticOptLogRecorder optLogRecorder) throws BusinessException {
        CreateTerminalSystemUpgradeRequest request = new CreateTerminalSystemUpgradeRequest();
        String[] terminalIdArr = new String[1];
        terminalIdArr[0] = "1";
        request.setTerminalIdArr(terminalIdArr);
        new Expectations() {
            {
                cbbTerminalUpgradeAPI.addSystemUpgradeTask((CbbAddTerminalSystemUpgradeTaskRequest) any);
                result = new BusinessException("key");
            }
        };
        new MockUp<BusinessException>() {
            @Mock
            public String getI18nMessage() {
                return "message";
            }
        };
        try {
            controller.create(request, optLogRecorder, builder);
            fail();
        } catch (BusinessException e) {
            assertEquals("key", e.getKey());
        }
        new Verifications() {
            {
                cbbTerminalUpgradeAPI.addSystemUpgradeTask((CbbAddTerminalSystemUpgradeTaskRequest) any);
                times = 1;
                optLogRecorder.saveOptLog(BusinessKey.RCDC_TERMINAL_CREATE_UPGRADE_TASK_FAIL_LOG, "1", "message");
                times = 1;
                optLogRecorder.saveOptLog(BusinessKey.RCDC_TERMINAL_CREATE_UPGRADE_TASK_SUCCESS_LOG, "1");
                times = 0;
            }
        };
    }
    
    /**
     * 测试create，
     * @param optLogRecorder mock日志记录对象
     * @throws BusinessException 异常
     */
    @Test
    public void testCreate(@Mocked ProgrammaticOptLogRecorder optLogRecorder) throws BusinessException {
        CreateTerminalSystemUpgradeRequest request = new CreateTerminalSystemUpgradeRequest();
        String[] terminalIdArr = new String[1];
        terminalIdArr[0] = "1";
        request.setTerminalIdArr(terminalIdArr);
        DefaultWebResponse response = controller.create(request, optLogRecorder, builder);
        assertEquals(Status.SUCCESS, response.getStatus());
        new Verifications() {
            {
                cbbTerminalUpgradeAPI.addSystemUpgradeTask((CbbAddTerminalSystemUpgradeTaskRequest) any);
                times = 1;
                optLogRecorder.saveOptLog(anyString, anyString, anyString);
                times = 0;
                optLogRecorder.saveOptLog(BusinessKey.RCDC_TERMINAL_CREATE_UPGRADE_TASK_SUCCESS_LOG, "1");
                times = 1;
            }
        };
    }
    
    /**
     * 测试delete,参数为空
     * @param optLogRecorder mock日志记录对象
     * @param builder mock批量任务处理对象
     * @throws Exception 异常
     */
    @Test
    public void testDeleteArgumentIsNull(@Mocked ProgrammaticOptLogRecorder optLogRecorder, @Mocked BatchTaskBuilder builder) throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> controller.delete(null, optLogRecorder, builder), "request can not be null");
        ThrowExceptionTester.throwIllegalArgumentException(() -> controller.delete(new DeleteTerminalSystemUpgradeRequest(), null, builder),
                "optLogRecorder can not be null");
        ThrowExceptionTester.throwIllegalArgumentException(() -> controller.delete(new DeleteTerminalSystemUpgradeRequest(), optLogRecorder, null),
                "builder can not be null");
        assertTrue(true);
    }
    
    /**
     * 测试delete,BusinessException
     * @param optLogRecorder mock日志记录对象
     * @param builder mock批量任务处理对象
     * @throws BusinessException 异常
     */
    @Test
    public void testDeleteBusinessException(@Mocked ProgrammaticOptLogRecorder optLogRecorder, @Mocked BatchTaskBuilder builder) 
            throws BusinessException {
        DeleteTerminalSystemUpgradeRequest request = new DeleteTerminalSystemUpgradeRequest();
        String[] terminalIdArr = new String[1];
        terminalIdArr[0] = "1";
        request.setIdArr(terminalIdArr);
        new Expectations() {
            {
                cbbTerminalUpgradeAPI.removeTerminalSystemUpgradeTask((CbbRemoveTerminalSystemUpgradeTaskRequest) any);
                result = new BusinessException("key");
            }
        };
        new MockUp<BusinessException>() {
            @Mock
            public String getI18nMessage() {
                return "message";
            }
        };
        try {
            controller.delete(request, optLogRecorder, builder);
            fail();
        } catch (BusinessException e) {
            assertEquals("key", e.getKey());
        }
        new Verifications() {
            {
                cbbTerminalUpgradeAPI.removeTerminalSystemUpgradeTask((CbbRemoveTerminalSystemUpgradeTaskRequest) any);
                times = 1;
                optLogRecorder.saveOptLog(BusinessKey.RCDC_TERMINAL_DELETE_UPGRADE_SUCCESS_LOG, "1");
                times = 0;
                optLogRecorder.saveOptLog(BusinessKey.RCDC_TERMINAL_DELETE_UPGRADE_FAIL_LOG, "1", "message");
                times = 1;
            }
        };
    }
    
    /**
     * 测试delete,
     * @param optLogRecorder mock日志记录对象
     * @param builder mock批量任务处理对象
     * @throws BusinessException 异常
     */
    @Test
    public void testDelete(@Mocked ProgrammaticOptLogRecorder optLogRecorder, @Mocked BatchTaskBuilder builder) 
            throws BusinessException {
        DeleteTerminalSystemUpgradeRequest request = new DeleteTerminalSystemUpgradeRequest();
        String[] terminalIdArr = new String[1];
        terminalIdArr[0] = "1";
        request.setIdArr(terminalIdArr);
        DefaultWebResponse response = controller.delete(request, optLogRecorder, builder);
        assertEquals(Status.SUCCESS, response.getStatus());
        new Verifications() {
            {
                cbbTerminalUpgradeAPI.removeTerminalSystemUpgradeTask((CbbRemoveTerminalSystemUpgradeTaskRequest) any);
                times = 1;
                optLogRecorder.saveOptLog(BusinessKey.RCDC_TERMINAL_DELETE_UPGRADE_SUCCESS_LOG, "1");
                times = 1;
                optLogRecorder.saveOptLog(anyString, anyString, anyString);
                times = 0;
            }
        };
    }

    /**
     * 测试list,参数为空
     * @throws Exception 异常
     */
    @Test
    public void testListArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> controller.list(null), "request can not be null");
        assertTrue(true);
    }
    
    /**
     * 测试list,参数为空
     * @throws BusinessException 异常
     */
    @Test
    public void testList() throws BusinessException {
        ListTerminalSystemUpgradeRequest request = new ListTerminalSystemUpgradeRequest();
        CbbBaseListResponse<CbbTerminalSystemUpgradeTaskDTO> resp = new CbbBaseListResponse<>();
        new Expectations() {
            {
                cbbTerminalUpgradeAPI.listTerminalSystemUpgradeTask();
                result = resp;
            }
        };
        DefaultWebResponse response = controller.list(request);
        assertEquals(Status.SUCCESS, response.getStatus());
        assertEquals(resp, (CbbBaseListResponse<CbbTerminalSystemUpgradeTaskDTO>)response.getContent());
        
        new Verifications() {
            {
                cbbTerminalUpgradeAPI.listTerminalSystemUpgradeTask();
                times = 1;
            }
        };
    }
}

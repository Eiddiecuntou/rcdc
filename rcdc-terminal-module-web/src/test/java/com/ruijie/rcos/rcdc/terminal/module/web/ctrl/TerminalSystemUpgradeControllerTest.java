package com.ruijie.rcos.rcdc.terminal.module.web.ctrl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.util.Map;
import java.util.UUID;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTerminalSystemUpgradeAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTerminalSystemUpgradePackageAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.TerminalListDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbAddSystemUpgradeTaskRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbCloseSystemUpgradeTaskRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalUpgradePackageUploadRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.PageSearchRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.AddSystemUpgradeTaskResponse;
import com.ruijie.rcos.rcdc.terminal.module.web.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.web.ctrl.batchtask.AddUpgradeTerminalBatchTaskHandler;
import com.ruijie.rcos.rcdc.terminal.module.web.ctrl.batchtask.TerminalIdMappingUtils;
import com.ruijie.rcos.rcdc.terminal.module.web.ctrl.request.AppendTerminalSystemUpgradeWebRequest;
import com.ruijie.rcos.rcdc.terminal.module.web.ctrl.request.CloseSystemUpgradeTaskWebRequest;
import com.ruijie.rcos.rcdc.terminal.module.web.ctrl.request.CreateTerminalSystemUpgradeWebRequest;
import com.ruijie.rcos.rcdc.terminal.module.web.ctrl.vo.CreateSystemUpgradeTaskContentVO;
import com.ruijie.rcos.sk.base.batch.BatchTaskBuilder;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.test.ThrowExceptionTester;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultPageResponse;
import com.ruijie.rcos.sk.webmvc.api.optlog.ProgrammaticOptLogRecorder;
import com.ruijie.rcos.sk.webmvc.api.request.ChunkUploadFile;
import com.ruijie.rcos.sk.webmvc.api.request.PageWebRequest;
import com.ruijie.rcos.sk.webmvc.api.response.DefaultWebResponse;
import com.ruijie.rcos.sk.webmvc.api.response.WebResponse.Status;
import com.ruijie.rcos.sk.webmvc.api.vo.ExactMatch;
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

    @Injectable
    private CbbTerminalSystemUpgradePackageAPI cbbTerminalUpgradePackageAPI;

    @Mocked
    private BatchTaskBuilder builder;

    /**
     * 测试uploadPackage，参数为空
     * 
     * @param optLogRecorder mock日志记录对象
     * @throws Exception 异常
     */
    @Test
    public void testUploadPackageArgumentIsNull(@Mocked ProgrammaticOptLogRecorder optLogRecorder) throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> controller.uploadPackage(null, optLogRecorder),
                "file can not be null");
        ThrowExceptionTester.throwIllegalArgumentException(() -> controller.uploadPackage(new ChunkUploadFile(), null),
                "optLogRecorder can not be null");
        assertTrue(true);
    }

    /**
     * 测试uploadPackage，上传失败
     * 
     * @param optLogRecorder mock日志记录对象
     * @throws BusinessException 异常
     */
    @Test
    public void testUploadPackageFail(@Mocked ProgrammaticOptLogRecorder optLogRecorder) throws BusinessException {
        new Expectations() {
            {
                cbbTerminalUpgradePackageAPI.uploadUpgradeFile((CbbTerminalUpgradePackageUploadRequest) any);
                result = new BusinessException("key");
            }
        };
        new MockUp<BusinessException>() {
            @Mock
            public String getI18nMessage() {
                return "message";
            }
        };
        ChunkUploadFile file = new ChunkUploadFile();
        file.setFilePath("filePath");
        file.setFileName("fileName");
        file.setFileMD5("fileMD5");
        try {
            controller.uploadPackage(file, optLogRecorder);
            fail();
        } catch (BusinessException e) {
            assertEquals("key", e.getKey());
        }

        new Verifications() {
            {
                optLogRecorder.saveOptLog(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_PACKAGE_UPLOAD_SUCCESS_LOG,
                        file.getFileName());
                times = 0;
                optLogRecorder.saveOptLog(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_PACKAGE_UPLOAD_FAIL_LOG,
                        file.getFileName(), "message");
                times = 1;
            }
        };
    }

    /**
     * 测试uploadPackage，上传成功
     * 
     * @param optLogRecorder mock日志记录对象
     * @throws BusinessException 异常
     */
    @Test
    public void testUploadPackageSuccess(@Mocked ProgrammaticOptLogRecorder optLogRecorder) throws BusinessException {
        ChunkUploadFile file = new ChunkUploadFile();
        file.setFilePath("filePath");
        file.setFileName("fileName");
        file.setFileMD5("fileMD5");
        DefaultWebResponse response = controller.uploadPackage(file, optLogRecorder);
        assertEquals(Status.SUCCESS, response.getStatus());
        new Verifications() {
            {
                optLogRecorder.saveOptLog(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_PACKAGE_UPLOAD_SUCCESS_LOG,
                        file.getFileName());
                times = 1;
                optLogRecorder.saveOptLog(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_PACKAGE_UPLOAD_FAIL_LOG,
                        file.getFileName(), anyString);
                times = 0;
            }
        };
    }

    /**
     * 测试listPackage，参数为空
     * 
     * @throws Exception 异常
     */
    @Test
    public void testlistPackageArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> controller.listPackage(null),
                "request can not be null");
        assertTrue(true);
    }

    /**
     * 测试listPackage，
     * 
     * @throws Exception 异常
     */
    @Test
    public void testlistPackage() throws Exception {
        PageWebRequest request = new PageWebRequest();
        DefaultWebResponse response = controller.listPackage(request);
        assertEquals(Status.SUCCESS, response.getStatus());

        new Verifications() {
            {
                cbbTerminalUpgradePackageAPI.listSystemUpgradePackage((PageSearchRequest) any);
                times = 1;
            }
        };
    }

    /**
     * 测试create，参数为空
     * 
     * @param optLogRecorder mock日志记录对象
     * @throws Exception 异常
     */
    @Test
    public void testCreateArgumentIsNull(@Mocked ProgrammaticOptLogRecorder optLogRecorder) throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> controller.create(null, optLogRecorder),
                "request can not be null");
        ThrowExceptionTester.throwIllegalArgumentException(() -> controller.create(new CreateTerminalSystemUpgradeWebRequest(), null),
                "optLogRecorder can not be null");
        assertTrue(true);
    }

    /**
     * 测试create，创建失败
     * 
     * @param optLogRecorder mock日志记录对象
     * @throws BusinessException 异常
     */
    @Test
    public void testCreateFail(@Mocked ProgrammaticOptLogRecorder optLogRecorder) throws BusinessException {
        new Expectations() {
            {
                cbbTerminalUpgradeAPI.addSystemUpgradeTask((CbbAddSystemUpgradeTaskRequest) any);
                result = new BusinessException("key");
            }
        };
        new MockUp<BusinessException>() {
            @Mock
            public String getI18nMessage() {
                return "message";
            }
        };
        CreateTerminalSystemUpgradeWebRequest request = new CreateTerminalSystemUpgradeWebRequest();
        try {
            controller.create(request, optLogRecorder);
            fail();
        } catch (BusinessException e) {
            assertEquals("key", e.getKey());
        }

        new Verifications() {
            {
                optLogRecorder.saveOptLog(BusinessKey.RCDC_CREATE_UPGRADE_TERMINAL_TASK_SUCCESS_LOG, (String[])any);
                times = 0;
                optLogRecorder.saveOptLog(BusinessKey.RCDC_CREATE_UPGRADE_TERMINAL_TASK_FAIL_LOG, "message");
                times = 1;
            }
        };
    }

    /**
     * 测试create，创建成功
     * 
     * @param optLogRecorder mock日志记录对象
     * @throws BusinessException 异常
     */
    @Test
    public void testCreateSuccess(@Mocked ProgrammaticOptLogRecorder optLogRecorder) throws BusinessException {
        AddSystemUpgradeTaskResponse response = new AddSystemUpgradeTaskResponse();
        UUID upgradeTaskId = UUID.randomUUID();
        response.setUpgradeTaskId(upgradeTaskId);
        new Expectations() {
            {
                cbbTerminalUpgradeAPI.addSystemUpgradeTask((CbbAddSystemUpgradeTaskRequest) any);
                result = response;
            }
        };
        
        CreateTerminalSystemUpgradeWebRequest request = new CreateTerminalSystemUpgradeWebRequest();
        DefaultWebResponse webResponse = controller.create(request, optLogRecorder);
        assertEquals(Status.SUCCESS, webResponse.getStatus());
        CreateSystemUpgradeTaskContentVO contentVO = (CreateSystemUpgradeTaskContentVO)webResponse.getContent();
        assertEquals(upgradeTaskId, contentVO.getUpgradeTaskId());
        
        new Verifications() {
            {
                optLogRecorder.saveOptLog(BusinessKey.RCDC_CREATE_UPGRADE_TERMINAL_TASK_SUCCESS_LOG, (String[])any);
                times = 1;
                optLogRecorder.saveOptLog(BusinessKey.RCDC_CREATE_UPGRADE_TERMINAL_TASK_FAIL_LOG, anyString);
                times = 0;
            }
        };
    }

    /**
     * 测试append，参数为空
     * 
     * @param optLogRecorder mock日志记录对象
     * @param builder mock对象
     * @throws Exception 异常
     */
    @Test
    public void testAppendArgumentIsNull(@Mocked ProgrammaticOptLogRecorder optLogRecorder,
            @Mocked BatchTaskBuilder builder) throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> controller.append(null, optLogRecorder, builder),
                "request can not be null");
        ThrowExceptionTester.throwIllegalArgumentException(() -> controller.append(new AppendTerminalSystemUpgradeWebRequest(), null, builder),
                "optLogRecorder can not be null");
        ThrowExceptionTester.throwIllegalArgumentException(() -> controller.append(new AppendTerminalSystemUpgradeWebRequest(), optLogRecorder, null),
                "builder can not be null");
        assertTrue(true);
    }
    
    /**
     * 测试append，
     * 
     * @param optLogRecorder mock日志记录对象
     * @param builder mock对象
     * @param handler mock对象
     * @param mappingUtils mock对象
     * @throws BusinessException 异常
     */
    @Test
    public void testAppend(@Mocked ProgrammaticOptLogRecorder optLogRecorder,
            @Mocked BatchTaskBuilder builder,
            @Mocked AddUpgradeTerminalBatchTaskHandler handler,
            @Mocked TerminalIdMappingUtils mappingUtils) throws BusinessException {
        AppendTerminalSystemUpgradeWebRequest request = new AppendTerminalSystemUpgradeWebRequest();
        DefaultWebResponse webResponse = controller.append(request, optLogRecorder, builder);
        assertEquals(Status.SUCCESS, webResponse.getStatus());
        
        new Verifications() {
            {
                TerminalIdMappingUtils.mapping((String[]) any);
                times = 1;
                TerminalIdMappingUtils.extractUUID((Map<UUID, String>) any);
                times = 1;
            }
        };
    }

    /**
     * 测试list，参数为空
     * @throws Exception 异常
     */
    @Test
    public void testListArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> controller.list(null),
                "request can not be null");
        assertTrue(true);
    }
    
    /**
     * 测试list，MatchEqualArr为空
     * @throws BusinessException 异常
     */
    @Test
    public void testListMatchEqualArrIsEmpty() throws BusinessException {
        PageWebRequest request = new PageWebRequest();
        
        DefaultWebResponse webResponse = controller.list(request);
        assertEquals(Status.SUCCESS, webResponse.getStatus());
        
        new Verifications() {
            {
                cbbTerminalUpgradeAPI.listSystemUpgradeTask((PageSearchRequest) any);
                times = 1;
            }
        };
    }
    
    /**
     * 测试list，MatchEqualArr不为空
     * @throws BusinessException 异常
     */
    @Test
    public void testListMatchEqualArrNotEmpty() throws BusinessException {
        PageWebRequest request = new PageWebRequest();
        
        ExactMatch[] exactMatchArr = new ExactMatch[2];
        ExactMatch exactMatch = new ExactMatch();
        exactMatch.setName("upgradeTaskState");
        String[] valueArr = {"UPGRADING"};
        exactMatch.setValueArr(valueArr);
        exactMatchArr[0] = exactMatch;
        
        ExactMatch exactMatch1 = new ExactMatch();
        exactMatch1.setName("sdfsdf");
        String[] value1Arr = {"UPGRADING"};
        exactMatch.setValueArr(value1Arr);
        exactMatchArr[1] = exactMatch1;
        
        request.setExactMatchArr(exactMatchArr);
        DefaultWebResponse webResponse = controller.list(request);
        assertEquals(Status.SUCCESS, webResponse.getStatus());
        
        new Verifications() {
            {
                cbbTerminalUpgradeAPI.listSystemUpgradeTask((PageSearchRequest) any);
                times = 1;
            }
        };
    }

    /**
     * 测试listTerminal，参数为空
     * @throws Exception 异常
     */
    @Test
    public void testListTerminalArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> controller.listTerminal(null),
                "request can not be null");
        assertTrue(true);
    }
    
    /**
     * 测试listTerminal，MatchEqualArr为空
     * @throws BusinessException 异常
     */
    @Test
    public void testListTerminalMatchEqualArrIsEmpty() throws BusinessException {
        PageWebRequest request = new PageWebRequest();
        
        DefaultWebResponse webResponse = controller.listTerminal(request);
        assertEquals(Status.SUCCESS, webResponse.getStatus());
        
        new Verifications() {
            {
                cbbTerminalUpgradeAPI.listSystemUpgradeTaskTerminal((PageSearchRequest) any);
                times = 1;
            }
        };
    }
    
    /**
     * 测试listTerminal，MatchEqualArr不为空
     * @throws BusinessException 异常
     */
    @Test
    public void testListTerminalMatchEqualArrNotEmpty() throws BusinessException {
        PageWebRequest request = new PageWebRequest();
        
        ExactMatch[] exactMatchArr = new ExactMatch[2];
        ExactMatch exactMatch = new ExactMatch();
        exactMatch.setName("terminalUpgradeState");
        String[] valueArr = {"UPGRADING"};
        exactMatch.setValueArr(valueArr);
        exactMatchArr[0] = exactMatch;
        
        ExactMatch exactMatch1 = new ExactMatch();
        exactMatch1.setName("sdfsdf");
        String[] value1Arr = {"UPGRADING"};
        exactMatch.setValueArr(value1Arr);
        exactMatchArr[1] = exactMatch1;
        
        request.setExactMatchArr(exactMatchArr);
        DefaultWebResponse webResponse = controller.listTerminal(request);
        assertEquals(Status.SUCCESS, webResponse.getStatus());
        
        new Verifications() {
            {
                cbbTerminalUpgradeAPI.listSystemUpgradeTaskTerminal((PageSearchRequest) any);
                times = 1;
            }
        };
    }

    /**
     * 测试close，参数为空
     * @throws Exception 异常
     */
    @Test
    public void testCloseArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> controller.close(null),
                "request can not be null");
        assertTrue(true);
    }
    
    /**
     * 测试close，
     * @throws BusinessException 异常
     */
    @Test
    public void testClose() throws BusinessException {
        CloseSystemUpgradeTaskWebRequest request = new CloseSystemUpgradeTaskWebRequest();
        DefaultWebResponse webResponse = controller.close(request);
        assertEquals(Status.SUCCESS, webResponse.getStatus());
        
        new Verifications() {
            {
                cbbTerminalUpgradeAPI.closeSystemUpgradeTask((CbbCloseSystemUpgradeTaskRequest) any);
                times = 1;
            }
        };
    }
    
    /**
     * 测试listTerminalBasicInfo，参数为空
     * @throws Exception 异常
     */
    @Test
    public void testListTerminalBasicInfoArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> controller.listTerminalBasicInfo(null),
                "request can not be null");
        assertTrue(true);
    }
    
    /**
     * 测试listTerminalBasicInfo，
     * @throws BusinessException 异常
     */
    @Test
    public void testListTerminalBasicInfo() throws BusinessException {
        PageWebRequest request = new PageWebRequest();
        DefaultPageResponse<TerminalListDTO> pageResp = new DefaultPageResponse<>();
        new Expectations() {
            {
                cbbTerminalUpgradeAPI.listUpgradeableTerminal((PageSearchRequest) any);
                result = pageResp;
            }
        };
        DefaultWebResponse webResponse = controller.listTerminalBasicInfo(request);
        assertEquals(Status.SUCCESS, webResponse.getStatus());
        assertEquals(pageResp, (DefaultPageResponse<TerminalListDTO>) webResponse.getContent());
        new Verifications() {
            {
                cbbTerminalUpgradeAPI.listUpgradeableTerminal((PageSearchRequest) any);
                times = 1;
            }
        };
    }
}

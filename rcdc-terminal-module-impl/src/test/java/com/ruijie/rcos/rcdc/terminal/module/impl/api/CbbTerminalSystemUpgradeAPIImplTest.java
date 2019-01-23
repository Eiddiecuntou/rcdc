package com.ruijie.rcos.rcdc.terminal.module.impl.api;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.util.FileCopyUtils;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalSystemUpgradeTaskDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbAddTerminalSystemUpgradeTaskRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbRemoveTerminalSystemUpgradeTaskRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalSystemUpgradePackageListRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalUpgradePackageUploadRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbBaseListResponse;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.TerminalPlatformEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.SystemUpgradeTask;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.SystemUpgradeTaskManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradePackageDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradePackageEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.TerminalSystemUpgradeMsg;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalUpgradeVersionFileInfo;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalSystemUpgradeService;
import com.ruijie.rcos.rcdc.terminal.module.impl.util.CmdExecuteUtil;
import com.ruijie.rcos.rcdc.terminal.module.impl.util.FileOperateUtil;
import com.ruijie.rcos.rcdc.terminal.module.impl.util.NfsServiceUtil;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.test.ThrowExceptionTester;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultResponse;
import com.ruijie.rcos.sk.modulekit.api.comm.Response;
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
 * Description: 终端系统升级api测试类
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年11月28日
 * 
 * @author nt
 */
@RunWith(JMockit.class)
public class CbbTerminalSystemUpgradeAPIImplTest {

    @Tested
    private CbbTerminalSystemUpgradeAPIImpl cbbTerminalSystemUpgradeAPIImpl;

    @Injectable
    private TerminalSystemUpgradePackageDAO termianlSystemUpgradePackageDAO;

    @Injectable
    private TerminalSystemUpgradeService terminalSystemUpgradeService;

    @Injectable
    private SystemUpgradeTaskManager systemUpgradeTaskManager;

    @Injectable
    private TerminalBasicInfoDAO basicInfoDAO;


    /**
     * 测试升级包上传
     * 
     * @throws BusinessException 业务异常
     */
    @Test
    public void testUploadUpgradeFile() throws BusinessException {

        // 将文件挂载操作全部mock
        CbbTerminalUpgradePackageUploadRequest request = new CbbTerminalUpgradePackageUploadRequest();
        request.setFileMD5("md5");
        request.setFileName("aaa.iso");
        request.setFilePath("/usr/data");

        new Expectations() {
            {
                terminalSystemUpgradeService.modifyTerminalUpgradePackageVersion((TerminalUpgradeVersionFileInfo) any);
            }
        };

        new MockUp<CmdExecuteUtil>() {
            @Mock
            public void executeCmd(String cmd) {
                
            }
        };

        new MockUp<CbbTerminalSystemUpgradeAPIImpl>() {
            @Mock
            private TerminalUpgradeVersionFileInfo getVersionInfo() {
                TerminalUpgradeVersionFileInfo versionInfo = new TerminalUpgradeVersionFileInfo();
                versionInfo.setImgName("package");
                versionInfo.setPackageType(TerminalPlatformEnums.IDV);
                versionInfo.setVersion("interVer");
                return versionInfo;
            }
        };
        new MockUp<FileOperateUtil>() {
            @Mock
            public void deleteFile(final String directoryPath, final String exceptFileName) {
                
            }
        };
        new MockUp<FileCopyUtils>() {
            @Mock
            public int copy(File in, File out) {
                return 10;
            }
        };
        cbbTerminalSystemUpgradeAPIImpl.uploadUpgradeFile(request);
        new Verifications() {
            {
                terminalSystemUpgradeService.modifyTerminalUpgradePackageVersion((TerminalUpgradeVersionFileInfo) any);
                times = 1;
            }
        };

    }

    /**
     * 测试升级包上传文件，有正在运行的任务
     * 
     * @throws BusinessException 业务异常
     */
    @Test
    public void testUploadUpgradeFileTaskIsUpgrading() throws BusinessException {

        // 将文件挂载炒作全部mock
        CbbTerminalUpgradePackageUploadRequest request = new CbbTerminalUpgradePackageUploadRequest();
        request.setFileMD5("md5");
        request.setFileName("aaa.ios");
        request.setFilePath("/usr/data");

        new Expectations() {
            {
                systemUpgradeTaskManager.countUpgradingNum();
                result = 1;
            }
        };

        try {
            cbbTerminalSystemUpgradeAPIImpl.uploadUpgradeFile(request);
            fail();
        } catch (BusinessException e) {
            Assert.assertEquals(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_TASK_IS_RUNNING, e.getKey());
        }

    }

    /**
     * 测试升级包上传文件类型校验不正确
     * 
     * @throws BusinessException 业务异常
     */
    @Test
    public void testUploadUpgradeFileFileTypeIncorrect() throws BusinessException {

        // 将文件挂载炒作全部mock
        CbbTerminalUpgradePackageUploadRequest request = new CbbTerminalUpgradePackageUploadRequest();
        request.setFileMD5("md5");
        request.setFileName("aaa.ios");
        request.setFilePath("/usr/data");

        try {
            cbbTerminalSystemUpgradeAPIImpl.uploadUpgradeFile(request);
            fail();
        } catch (BusinessException e) {
            Assert.assertEquals(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_UPLOAD_FILE_TYPE_ERROR, e.getKey());
        }

    }

    /**
     * 测试升级包上传文件文件属性为空
     * 
     * @throws BusinessException 业务异常
     */
    @Test
    public void testUploadUpgradeFileFileTypeIsNull() throws BusinessException {

        CbbTerminalUpgradePackageUploadRequest request = new CbbTerminalUpgradePackageUploadRequest();
        request.setFileMD5("md5");
        request.setFileName("aaa");
        request.setFilePath("/usr/data");

        try {
            cbbTerminalSystemUpgradeAPIImpl.uploadUpgradeFile(request);
            fail();
        } catch (BusinessException e) {
            Assert.assertEquals(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_UPLOAD_FILE_TYPE_ERROR, e.getKey());
        }

    }
    
    /**
     * 测试升级包上传文件,文件不存在
     * 
     * @throws BusinessException 业务异常
     */
    @Test
    public void testUploadUpgradeFileFileNotExist() throws BusinessException {
        
        CbbTerminalUpgradePackageUploadRequest request = new CbbTerminalUpgradePackageUploadRequest();
        request.setFileMD5("md5");
        request.setFileName("aaa.iso");
        request.setFilePath("/usr/data");
        
        new MockUp<CmdExecuteUtil>() {
            @Mock
            public void executeCmd(String cmd) {
                
            }
        };
        new MockUp<FileInputStream>() {
            @Mock
            public void $init(String filePath) throws FileNotFoundException {
                throw new FileNotFoundException();
            }
        };
        
        try {
            cbbTerminalSystemUpgradeAPIImpl.uploadUpgradeFile(request);
            fail();
        } catch (BusinessException e) {
            Assert.assertEquals(BusinessKey.RCDC_FILE_NOT_EXIST, e.getKey());
        }
    }
    
    /**
     * 测试升级包上传文件,IOException
     * 
     * @throws BusinessException 业务异常
     */
    @Test
    public void testUploadUpgradeFileIOException() throws BusinessException {
        
        CbbTerminalUpgradePackageUploadRequest request = new CbbTerminalUpgradePackageUploadRequest();
        request.setFileMD5("md5");
        request.setFileName("aaa.iso");
        request.setFilePath("/usr/data");
        
        new MockUp<CmdExecuteUtil>() {
            @Mock
            public void executeCmd(String cmd) {
                
            }
        };
        new MockUp<FileInputStream>() {
            @Mock
            public void $init(String filePath) throws IOException {
                throw new IOException();
            }
        };
        
        try {
            cbbTerminalSystemUpgradeAPIImpl.uploadUpgradeFile(request);
            fail();
        } catch (BusinessException e) {
            Assert.assertEquals(BusinessKey.RCDC_FILE_OPERATE_FAIL, e.getKey());
        }
    }
    
    /**
     * 测试升级包上传文件,TerminalUpgradeVersionFileInfo的PackageType为空
     * 
     * @throws BusinessException 业务异常
     */
    @Test
    public void testUploadUpgradeFileTerminalUpgradeVersionFileInfoPackageTypeIsNull() throws BusinessException {
        
        CbbTerminalUpgradePackageUploadRequest request = new CbbTerminalUpgradePackageUploadRequest();
        request.setFileMD5("md5");
        request.setFileName("aaa.iso");
        request.setFilePath("/usr/data");
        
        new MockUp<CmdExecuteUtil>() {
            @Mock
            public void executeCmd(String cmd) {
                
            }
        };
        new MockUp<CbbTerminalSystemUpgradeAPIImpl>() {
            @Mock
            private TerminalUpgradeVersionFileInfo getVersionInfo() {
                TerminalUpgradeVersionFileInfo versionInfo = new TerminalUpgradeVersionFileInfo();
                versionInfo.setImgName("package");
                versionInfo.setVersion("interVer");
                return versionInfo;
            }
        };
        
        try {
            cbbTerminalSystemUpgradeAPIImpl.uploadUpgradeFile(request);
            fail();
        } catch (BusinessException e) {
            Assert.assertEquals(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_PACKAGE_VERSION_FILE_INCORRECT, e.getKey());
        }
    }
    
    /**
     * 测试升级包上传文件,TerminalUpgradeVersionFileInfo的ImgName为空
     * 
     * @throws BusinessException 业务异常
     */
    @Test
    public void testUploadUpgradeFileTerminalUpgradeVersionFileInfoImgNameIsBlank() throws BusinessException {
        
        CbbTerminalUpgradePackageUploadRequest request = new CbbTerminalUpgradePackageUploadRequest();
        request.setFileMD5("md5");
        request.setFileName("aaa.iso");
        request.setFilePath("/usr/data");
        
        new MockUp<CmdExecuteUtil>() {
            @Mock
            public void executeCmd(String cmd) {
                
            }
        };
        new MockUp<CbbTerminalSystemUpgradeAPIImpl>() {
            @Mock
            private TerminalUpgradeVersionFileInfo getVersionInfo() {
                TerminalUpgradeVersionFileInfo versionInfo = new TerminalUpgradeVersionFileInfo();
                versionInfo.setImgName("");
                versionInfo.setPackageType(TerminalPlatformEnums.IDV);
                versionInfo.setVersion("interVer");
                return versionInfo;
            }
        };
        
        try {
            cbbTerminalSystemUpgradeAPIImpl.uploadUpgradeFile(request);
            fail();
        } catch (BusinessException e) {
            Assert.assertEquals(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_PACKAGE_VERSION_FILE_INCORRECT, e.getKey());
        }
    }

    /**
     * 测试获取系统升级包列表
     * @throws BusinessException 业务异常
     */ 
    @Test
    public void testListSystemUpgradePackage() throws BusinessException {
        CbbTerminalSystemUpgradePackageListRequest request = new CbbTerminalSystemUpgradePackageListRequest();
        request.setPaltform(TerminalPlatformEnums.IDV);
        List<TerminalSystemUpgradePackageEntity> packageList = buildPackageList();

        new Expectations() {
            {
                termianlSystemUpgradePackageDAO.findByPackageType((TerminalPlatformEnums) any);
                result = packageList;
            }
        };

        cbbTerminalSystemUpgradeAPIImpl.listSystemUpgradePackage(request);

        new Verifications() {
            {
                termianlSystemUpgradePackageDAO.findByPackageType((TerminalPlatformEnums) any);
                times = 1;
            }
        };

    }

    /**
     * 测试获取升级包列表为空
     * @throws BusinessException 业务异常
     */
    @Test
    public void testListSystemUpgradePackageFindEmpty() throws BusinessException {
        CbbTerminalSystemUpgradePackageListRequest request = new CbbTerminalSystemUpgradePackageListRequest();
        request.setPaltform(TerminalPlatformEnums.ALL);

        new Expectations() {
            {
                termianlSystemUpgradePackageDAO.findByPackageType((TerminalPlatformEnums) any);
                result = Collections.emptyList();
            }
        };

        cbbTerminalSystemUpgradeAPIImpl.listSystemUpgradePackage(request);

        new Verifications() {
            {
                termianlSystemUpgradePackageDAO.findByPackageType((TerminalPlatformEnums) any);
                times = 1;
            }
        };
    }

    /**
     * 测试添加系统升级任务
     * @throws BusinessException 业务异常
     */
    @Test
    public void testAddSystemUpgradeTask() throws BusinessException {

        CbbAddTerminalSystemUpgradeTaskRequest request = new CbbAddTerminalSystemUpgradeTaskRequest();
        request.setPlatform(TerminalPlatformEnums.VDI);
        request.setTerminalId("11");
        TerminalSystemUpgradePackageEntity entity = new TerminalSystemUpgradePackageEntity();
        TerminalEntity terminal = new TerminalEntity();
        new Expectations() {
            {
                termianlSystemUpgradePackageDAO.findFirstByPackageType((TerminalPlatformEnums) any);
                result = entity;

                basicInfoDAO.findTerminalEntityByTerminalId(anyString);
                result = terminal;
            }
        };
        new MockUp<CbbTerminalSystemUpgradeAPIImpl>() {
            @Mock
            public void addTask(TerminalSystemUpgradePackageEntity upgradePackage, TerminalEntity terminal) {
                
            }

        };
        DefaultResponse resp = cbbTerminalSystemUpgradeAPIImpl.addSystemUpgradeTask(request);
        Assert.assertEquals(resp.getStatus(), Response.Status.SUCCESS);
    }
    
    /**
     * 测试添加系统升级任务,upgradePackage为空
     */
    @Test
    public void testAddSystemUpgradeTaskUpgradePackageIsNull() {
        
        CbbAddTerminalSystemUpgradeTaskRequest request = new CbbAddTerminalSystemUpgradeTaskRequest();
        request.setPlatform(TerminalPlatformEnums.VDI);
        request.setTerminalId("11");
        new Expectations() {
            {
                termianlSystemUpgradePackageDAO.findFirstByPackageType((TerminalPlatformEnums) any);
                result = null;
            }
        };
        try {
            cbbTerminalSystemUpgradeAPIImpl.addSystemUpgradeTask(request);
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_PACKAGE_NOT_EXIST, e.getKey());
        }
    }

    /**
     * 测试添加系统升级任务,添加数量超过限制
     * @throws BusinessException 业务异常
     */
    @Test
    public void testAddSystemUpgradeTaskMoreThanMaxNum() throws BusinessException {

        CbbAddTerminalSystemUpgradeTaskRequest request = new CbbAddTerminalSystemUpgradeTaskRequest();
        request.setPlatform(TerminalPlatformEnums.VDI);
        request.setTerminalId("11");
        TerminalSystemUpgradePackageEntity entity = new TerminalSystemUpgradePackageEntity();
        new Expectations() {
            {
                termianlSystemUpgradePackageDAO.findFirstByPackageType((TerminalPlatformEnums) any);
                result = entity;

                systemUpgradeTaskManager.checkMaxAddNum();
                result = true;
            }
        };
        try {
            cbbTerminalSystemUpgradeAPIImpl.addSystemUpgradeTask(request);
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_NUM_EXCEED_LIMIT, e.getKey());
        }
    }
    
    /**
     * 测试添加系统升级任务,TerminalEntity为空
     * @throws BusinessException 业务异常
     */
    @Test
    public void testAddSystemUpgradeTaskTerminalEntityIsNull() throws BusinessException {

        CbbAddTerminalSystemUpgradeTaskRequest request = new CbbAddTerminalSystemUpgradeTaskRequest();
        request.setPlatform(TerminalPlatformEnums.VDI);
        request.setTerminalId("11");
        TerminalSystemUpgradePackageEntity entity = new TerminalSystemUpgradePackageEntity();
        new Expectations() {
            {
                termianlSystemUpgradePackageDAO.findFirstByPackageType((TerminalPlatformEnums) any);
                result = entity;

                basicInfoDAO.findTerminalEntityByTerminalId(anyString);
                result = null;
            }
        };
        try {
            cbbTerminalSystemUpgradeAPIImpl.addSystemUpgradeTask(request);
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_TERMINAL_NOT_FOUND_TERMINAL, e.getKey());
        }
    }

    /**
     * 测试添加系统升级任务,addTask时，终端状态非在线
     * @throws BusinessException 业务异常
     */
    @Test
    public void testAddSystemUpgradeTaskAddTaskTerminalIsNotOnline() throws BusinessException {

        CbbAddTerminalSystemUpgradeTaskRequest request = new CbbAddTerminalSystemUpgradeTaskRequest();
        request.setPlatform(TerminalPlatformEnums.VDI);
        request.setTerminalId("11");
        TerminalSystemUpgradePackageEntity entity = new TerminalSystemUpgradePackageEntity();
        TerminalEntity terminal = new TerminalEntity();
        terminal.setState(CbbTerminalStateEnums.OFFLINE);
        new Expectations() {
            {
                termianlSystemUpgradePackageDAO.findFirstByPackageType((TerminalPlatformEnums) any);
                result = entity;

                basicInfoDAO.findTerminalEntityByTerminalId(anyString);
                result = terminal;
            }
        };
        try {
            cbbTerminalSystemUpgradeAPIImpl.addSystemUpgradeTask(request);
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_TERMINAL_OFFLINE, e.getKey());
        }
    }
    
    /**
     * 测试添加系统升级任务,addTask时，有异常
     * @param util mock NfsServiceUtil
     * @throws BusinessException 业务异常
     */
    @Test
    public void testAddSystemUpgradeTaskAddTaskHasException(@Mocked NfsServiceUtil util) throws BusinessException {
        
        CbbAddTerminalSystemUpgradeTaskRequest request = new CbbAddTerminalSystemUpgradeTaskRequest();
        request.setPlatform(TerminalPlatformEnums.VDI);
        request.setTerminalId("11");
        TerminalSystemUpgradePackageEntity entity = new TerminalSystemUpgradePackageEntity();
        TerminalEntity terminal = new TerminalEntity();
        terminal.setState(CbbTerminalStateEnums.ONLINE);
        new Expectations() {
            {
                termianlSystemUpgradePackageDAO.findFirstByPackageType((TerminalPlatformEnums) any);
                result = entity;
                
                basicInfoDAO.findTerminalEntityByTerminalId(anyString);
                result = terminal;
                
                systemUpgradeTaskManager.addTask(terminal.getTerminalId(), terminal.getPlatform());
                result = new SystemUpgradeTask();
                
                terminalSystemUpgradeService.systemUpgrade(terminal.getTerminalId(), (TerminalSystemUpgradeMsg) any);
                result = new RuntimeException();
            }
        };
        DefaultResponse resp = cbbTerminalSystemUpgradeAPIImpl.addSystemUpgradeTask(request);
        Assert.assertEquals(resp.getStatus(), Response.Status.SUCCESS);
        new Verifications() {
            {
                termianlSystemUpgradePackageDAO.findFirstByPackageType((TerminalPlatformEnums) any);
                times = 1;
                basicInfoDAO.findTerminalEntityByTerminalId(anyString);
                times = 1;
                systemUpgradeTaskManager.addTask(terminal.getTerminalId(), terminal.getPlatform());
                times = 1;
                terminalSystemUpgradeService.systemUpgrade(terminal.getTerminalId(), (TerminalSystemUpgradeMsg) any);
                times = 1;
                systemUpgradeTaskManager.modifyTaskState(terminal.getTerminalId(), CbbSystemUpgradeStateEnums.WAIT);
                times = 1;
            }
        };
    }
    
    /**
     * 测试添加系统升级任务,addTask时，没有异常
     * @param util mock NfsServiceUtil
     * @throws BusinessException 业务异常
     */
    @Test
    public void testAddSystemUpgradeTaskAddTaskHasNotException(@Mocked NfsServiceUtil util) throws BusinessException {
        
        CbbAddTerminalSystemUpgradeTaskRequest request = new CbbAddTerminalSystemUpgradeTaskRequest();
        request.setPlatform(TerminalPlatformEnums.VDI);
        request.setTerminalId("11");
        TerminalSystemUpgradePackageEntity entity = new TerminalSystemUpgradePackageEntity();
        TerminalEntity terminal = new TerminalEntity();
        terminal.setState(CbbTerminalStateEnums.ONLINE);
        new Expectations() {
            {
                termianlSystemUpgradePackageDAO.findFirstByPackageType((TerminalPlatformEnums) any);
                result = entity;
                
                basicInfoDAO.findTerminalEntityByTerminalId(anyString);
                result = terminal;
                
                systemUpgradeTaskManager.addTask(terminal.getTerminalId(), terminal.getPlatform());
                result = new SystemUpgradeTask();
            }
        };
        DefaultResponse resp = cbbTerminalSystemUpgradeAPIImpl.addSystemUpgradeTask(request);
        Assert.assertEquals(resp.getStatus(), Response.Status.SUCCESS);
        new Verifications() {
            {
                termianlSystemUpgradePackageDAO.findFirstByPackageType((TerminalPlatformEnums) any);
                times = 1;
                basicInfoDAO.findTerminalEntityByTerminalId(anyString);
                times = 1;
                systemUpgradeTaskManager.addTask(terminal.getTerminalId(), terminal.getPlatform());
                times = 1;
                terminalSystemUpgradeService.systemUpgrade(terminal.getTerminalId(), (TerminalSystemUpgradeMsg) any);
                times = 1;
                systemUpgradeTaskManager.modifyTaskState(terminal.getTerminalId(), CbbSystemUpgradeStateEnums.WAIT);
                times = 0;
            }
        };
    }

    /**
     * 测试removeTerminalSystemUpgradeTask，参数为空
     * @throws Exception 异常
     */
    @Test
    public void testRemoveTerminalSystemUpgradeTaskArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> cbbTerminalSystemUpgradeAPIImpl.removeTerminalSystemUpgradeTask(null),
                "request can not be null");
        assertTrue(true);
    }
    
    /**
     * 测试removeTerminalSystemUpgradeTask，SystemUpgradeTask为null
     */
    @Test
    public void testRemoveTerminalSystemUpgradeTaskSystemUpgradeTaskIsNull() {
        CbbRemoveTerminalSystemUpgradeTaskRequest request = new CbbRemoveTerminalSystemUpgradeTaskRequest();
        new Expectations() {
            {
                systemUpgradeTaskManager.getTaskByTerminalId(request.getTerminalId());
                result = null;
            }
        };
        try {
            cbbTerminalSystemUpgradeAPIImpl.removeTerminalSystemUpgradeTask(request);
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_TASK_NOT_EXIST, e.getKey());
        }
    }
    
    /**
     * 测试removeTerminalSystemUpgradeTask，SystemUpgradeState为UPGRADING
     */
    @Test
    public void testRemoveTerminalSystemUpgradeTaskSystemUpgradeStateIsUpgrading() {
        CbbRemoveTerminalSystemUpgradeTaskRequest request = new CbbRemoveTerminalSystemUpgradeTaskRequest();
        SystemUpgradeTask task = new SystemUpgradeTask();
        task.setState(CbbSystemUpgradeStateEnums.UPGRADING);
        new Expectations() {
            {
                systemUpgradeTaskManager.getTaskByTerminalId(request.getTerminalId());
                result = task;
            }
        };
        try {
            cbbTerminalSystemUpgradeAPIImpl.removeTerminalSystemUpgradeTask(request);
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_TASK_IS_RUNNING, e.getKey());
        }
    }
    
    /**
     * 测试removeTerminalSystemUpgradeTask，TaskMapSize不为0
     * @param util mock NfsServiceUtil
     * @throws BusinessException 异常
     */
    @Test
    public void testRemoveTerminalSystemUpgradeTaskMapSizeNotZero(@Mocked NfsServiceUtil util) throws BusinessException {
        CbbRemoveTerminalSystemUpgradeTaskRequest request = new CbbRemoveTerminalSystemUpgradeTaskRequest();
        SystemUpgradeTask task = new SystemUpgradeTask();
        task.setState(CbbSystemUpgradeStateEnums.SUCCESS);
        Map<String, SystemUpgradeTask> map = new HashMap<>();
        map.put("key", new SystemUpgradeTask());
        new Expectations() {
            {
                systemUpgradeTaskManager.getTaskByTerminalId(request.getTerminalId());
                result = task;
                systemUpgradeTaskManager.getTaskMap();
                result = map;
            }
        };
        DefaultResponse resp = cbbTerminalSystemUpgradeAPIImpl.removeTerminalSystemUpgradeTask(request);
        Assert.assertEquals(resp.getStatus(), Response.Status.SUCCESS);
        new Verifications() {
            {
                NfsServiceUtil.shutDownService();
                times = 0;
            }
        };
    }
    
    /**
     * 测试removeTerminalSystemUpgradeTask，TaskMapSize为0
     * @param util mock NfsServiceUtil
     * @throws BusinessException 异常
     */
    @Test
    public void testRemoveTerminalSystemUpgradeTaskMapSizeIsZero(@Mocked NfsServiceUtil util) throws BusinessException {
        CbbRemoveTerminalSystemUpgradeTaskRequest request = new CbbRemoveTerminalSystemUpgradeTaskRequest();
        SystemUpgradeTask task = new SystemUpgradeTask();
        task.setState(CbbSystemUpgradeStateEnums.SUCCESS);
        Map<String, SystemUpgradeTask> map = new HashMap<>();
        new Expectations() {
            {
                systemUpgradeTaskManager.getTaskByTerminalId(request.getTerminalId());
                result = task;
                systemUpgradeTaskManager.getTaskMap();
                result = map;
            }
        };
        DefaultResponse resp = cbbTerminalSystemUpgradeAPIImpl.removeTerminalSystemUpgradeTask(request);
        Assert.assertEquals(resp.getStatus(), Response.Status.SUCCESS);
        new Verifications() {
            {
                NfsServiceUtil.shutDownService();
                times = 1;
            }
        };
    }
    
    /**
     * 测试listTerminalSystemUpgradeTask，taskList为空
     * @throws BusinessException 异常
     */
    @Test
    public void testListTerminalSystemUpgradeTaskTaskListIsEmpty() throws BusinessException {
        new Expectations() {
            {
                systemUpgradeTaskManager.getAllTasks();
                result = Collections.emptyList();
            }
        };
        CbbBaseListResponse<CbbTerminalSystemUpgradeTaskDTO> response = cbbTerminalSystemUpgradeAPIImpl.listTerminalSystemUpgradeTask();
        assertArrayEquals(null, response.getItemArr());
    }
    
    /**
     * 测试listTerminalSystemUpgradeTask，
     * @throws BusinessException 异常
     */
    @Test
    public void testListTerminalSystemUpgradeTask() throws BusinessException {
        List<SystemUpgradeTask> taskList = generateSystemUpgradeTaskList();
        new Expectations() {
            {
                systemUpgradeTaskManager.getAllTasks();
                result = taskList;
            }
        };
        CbbBaseListResponse<CbbTerminalSystemUpgradeTaskDTO> response = cbbTerminalSystemUpgradeAPIImpl.listTerminalSystemUpgradeTask();
        CbbTerminalSystemUpgradeTaskDTO[] itemArr = response.getItemArr();
        assertEquals("2", itemArr[0].getTerminalId());
        assertEquals("1", itemArr[1].getTerminalId());
        assertEquals("3", itemArr[2].getTerminalId());
    }

    private List<SystemUpgradeTask> generateSystemUpgradeTaskList() {
        List<SystemUpgradeTask> taskList = new ArrayList<>();
        SystemUpgradeTask upgradeTask = new SystemUpgradeTask();
        upgradeTask.setTerminalId("1");
        upgradeTask.setState(CbbSystemUpgradeStateEnums.SUCCESS);
        upgradeTask.setStartTime(100L);
        taskList.add(upgradeTask);
        SystemUpgradeTask upgradeTask1 = new SystemUpgradeTask();
        upgradeTask1.setTerminalId("2");
        upgradeTask1.setState(CbbSystemUpgradeStateEnums.SUCCESS);
        upgradeTask1.setStartTime(200L);
        taskList.add(upgradeTask1);
        SystemUpgradeTask upgradeTask2 = new SystemUpgradeTask();
        upgradeTask2.setTerminalId("3");
        upgradeTask2.setState(CbbSystemUpgradeStateEnums.UPGRADING);
        upgradeTask2.setStartTime(200L);
        taskList.add(upgradeTask2);
        return taskList;
    }
    
    
    
    
    
    private List<TerminalSystemUpgradePackageEntity> buildPackageList() {
        List<TerminalSystemUpgradePackageEntity> packageList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            TerminalSystemUpgradePackageEntity entity = new TerminalSystemUpgradePackageEntity();
            packageList.add(entity);
        }
        return packageList;
    }
}

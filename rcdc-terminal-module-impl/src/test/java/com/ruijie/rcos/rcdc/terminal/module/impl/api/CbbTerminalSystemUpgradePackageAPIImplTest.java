package com.ruijie.rcos.rcdc.terminal.module.impl.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.Test;
import org.springframework.data.domain.Page;
import com.google.common.io.Files;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalSystemUpgradePackageInfoDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeTaskStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalPlatformRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalUpgradePackageUploadRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.PageSearchRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbCheckUploadingResultResponse;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.TerminalPlatformEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradeDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradePackageDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradePackageEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.SimpleCmdReturnValueResolver;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalUpgradeVersionFileInfo;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalSystemUpgradePackageService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalSystemUpgradeService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.QuerySystemUpgradePackageListService;
import com.ruijie.rcos.rcdc.terminal.module.impl.util.FileOperateUtil;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.shell.ShellCommandRunner;
import com.ruijie.rcos.sk.base.test.ThrowExceptionTester;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultPageResponse;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultResponse;
import com.ruijie.rcos.sk.modulekit.api.comm.Response.Status;
import mockit.Deencapsulation;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;

/**
 * 
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年2月26日
 * 
 * @author ls
 */
public class CbbTerminalSystemUpgradePackageAPIImplTest {

    @Tested
    private CbbTerminalSystemUpgradePackageAPIImpl upgradePackageAPIImpl;
    
    @Injectable
    private TerminalSystemUpgradePackageDAO terminalSystemUpgradePackageDAO;

    @Injectable
    private TerminalSystemUpgradeDAO systemUpgradeDAO;

    @Injectable
    private TerminalSystemUpgradeService terminalSystemUpgradeService;

    @Injectable
    private TerminalSystemUpgradePackageService terminalSystemUpgradePackageService;

    @Injectable
    private QuerySystemUpgradePackageListService querySystemUpgradePackageListService;
    
    @Mocked
    private ShellCommandRunner shellCommandRunner;
    
    /**
     * 测试isUpgradeFileUploading，参数为空
     * @throws Exception 异常
     */
    @Test
    public void testIsUpgradeFileUploadingArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> upgradePackageAPIImpl.isUpgradeFileUploading(null),
                "request can not be null");
        assertTrue(true);
    }
    
    /**
     * 测试isUpgradeFileUploading，
     */
    @Test
    public void testIsUpgradeFileUploading() {
        Set<TerminalPlatformEnums> uploadingSet = Deencapsulation.getField
                (CbbTerminalSystemUpgradePackageAPIImpl.class, "SYS_UPGRADE_PACKAGE_UPLOADING");
        uploadingSet.add(TerminalPlatformEnums.VDI);
        CbbTerminalPlatformRequest request = new CbbTerminalPlatformRequest();
        request.setPlatform(TerminalPlatformEnums.VDI);
        CbbCheckUploadingResultResponse response = upgradePackageAPIImpl.isUpgradeFileUploading(request);
        assertTrue(response.isHasLoading());
        uploadingSet.clear();
    }
    
    /**
     * 测试uploadUpgradeFile，参数为空
     * @throws Exception 异常
     */
    @Test
    public void testUploadUpgradeFileArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> upgradePackageAPIImpl.uploadUpgradePackage(null),
                "request can not be null");
        assertTrue(true);
    }
    
    /**
     * 测试uploadUpgradeFile，文件类型错误
     * @throws BusinessException 异常
     */
    @Test
    public void testUploadUpgradeFileFileTypeError() {
        CbbTerminalUpgradePackageUploadRequest request = new CbbTerminalUpgradePackageUploadRequest();
        request.setFileName("sdsds.ds");
        try {
            upgradePackageAPIImpl.uploadUpgradePackage(request);
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_UPLOAD_FILE_TYPE_ERROR, e.getKey());
        }
    }
    
    /**
     * 测试uploadUpgradeFile，系统升级包版本文件不存在
     * @throws BusinessException 异常
     */
    @Test
    public void testUploadUpgradeFileSystemUpgradePackageVersionFileNotFoundException() {
        CbbTerminalUpgradePackageUploadRequest request = new CbbTerminalUpgradePackageUploadRequest();
        request.setFileName("sdsds.iso");
        try {
            upgradePackageAPIImpl.uploadUpgradePackage(request);
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_PACKAGE_VERSION_FILE_INCORRECT, e.getKey());
        }
    }
    
    /**
     * 测试uploadUpgradeFile，系统升级包版本文件IOException
     * @throws BusinessException 异常
     */
    @Test
    public void testUploadUpgradeFileSystemUpgradePackageVersionIOException() {
        new MockUp<FileInputStream>() {
            @Mock
            public void $init(String filePath) throws IOException {
                throw new IOException();
            }
        };
        CbbTerminalUpgradePackageUploadRequest request = new CbbTerminalUpgradePackageUploadRequest();
        request.setFileName("sdsds.iso");
        try {
            upgradePackageAPIImpl.uploadUpgradePackage(request);
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_PACKAGE_VERSION_FILE_INCORRECT, e.getKey());
        }
    }

    /**
     * 测试uploadUpgradeFile，挂载升级包失败
     * @throws BusinessException 异常
     */
    @Test
    public void testUploadUpgradeFileMountUpgradePackageFail() throws BusinessException {
        String path = CbbTerminalSystemUpgradePackageAPIImplTest.class.getResource("/").getPath() + "testVersion";
        new MockUp<CbbTerminalSystemUpgradePackageAPIImpl>() {
            @Mock
            public String getVersionFilePath() {
                return path;
            }
        };
        new MockUp<File>() {
            @Mock
            public boolean isDirectory() {
                return true;
            }
            
            @Mock
            public String[] list() {
                return new String[0];
            }
        };
        
        new Expectations() {
            {
                shellCommandRunner.execute((SimpleCmdReturnValueResolver) any);
                result = new BusinessException("key");
            }
        };
        CbbTerminalUpgradePackageUploadRequest request = new CbbTerminalUpgradePackageUploadRequest();
        request.setFileName("sdsds.iso");
        try {
            upgradePackageAPIImpl.uploadUpgradePackage(request);
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_SYSTEM_CMD_EXECUTE_FAIL, e.getKey());
        }
    }
    
    /**
     * 测试uploadUpgradeFile，镜像文件不存在
     * @throws BusinessException 异常
     */
    @Test
    public void testUploadUpgradeFileImgNoExist() {
        String path = CbbTerminalSystemUpgradePackageAPIImplTest.class.getResource("/").getPath() + "testVersion";
        new MockUp<CbbTerminalSystemUpgradePackageAPIImpl>() {
            @Mock
            public String getVersionFilePath() {
                return path;
            }
        };
        CbbTerminalUpgradePackageUploadRequest request = new CbbTerminalUpgradePackageUploadRequest();
        request.setFileName("sdsds.iso");
        try {
            upgradePackageAPIImpl.uploadUpgradePackage(request);
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_PACKAGE_VERSION_FILE_INCORRECT, e.getKey());
        }
    }
    
    /**
     * 测试uploadUpgradeFile，镜像文件不存在
     * @throws BusinessException 异常
     */
    @Test
    public void testUploadUpgradeFileImgNoExist1() {
        String path = CbbTerminalSystemUpgradePackageAPIImplTest.class.getResource("/").getPath() + "testVersion";
        new MockUp<CbbTerminalSystemUpgradePackageAPIImpl>() {
            @Mock
            public String getVersionFilePath() {
                return path;
            }
        };
        new MockUp<File>() {
            @Mock
            public boolean isDirectory() {
                return true;
            }
            
            @Mock
            public String[] list() {
                return new String[0];
            }
        };
        CbbTerminalUpgradePackageUploadRequest request = new CbbTerminalUpgradePackageUploadRequest();
        request.setFileName("sdsds.iso");
        try {
            upgradePackageAPIImpl.uploadUpgradePackage(request);
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_PACKAGE_VERSION_FILE_INCORRECT, e.getKey());
        }
    }
    
    /**
     * 测试uploadUpgradeFile，Version错误
     * @throws BusinessException 异常
     */
    @Test
    public void testUploadUpgradeFileVersionFail() {
        String path = CbbTerminalSystemUpgradePackageAPIImplTest.class.getResource("/").getPath() + "testVersion";
        new MockUp<CbbTerminalSystemUpgradePackageAPIImpl>() {
            @Mock
            public String getVersionFilePath() {
                return path;
            }
        };
        new MockUp<File>() {
            @Mock
            public boolean isDirectory() {
                return true;
            }
            
            @Mock
            public String[] list() {
                String[] fileArr = new String[1];
                fileArr[0] = "";
                return fileArr;
            }
        };
        CbbTerminalUpgradePackageUploadRequest request = new CbbTerminalUpgradePackageUploadRequest();
        request.setFileName("sdsds.iso");
        request.setFilePath("dsdsd");
        try {
            upgradePackageAPIImpl.uploadUpgradePackage(request);
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_PACKAGE_VERSION_FILE_INCORRECT, e.getKey());
        }
    }
    
    /**
     * 测试uploadUpgradeFile，系统升级包正在上传中
     * @throws BusinessException 异常
     */
    @Test
    public void testUploadUpgradeFileUpgradePackageUploading() {
        String path = CbbTerminalSystemUpgradePackageAPIImplTest.class.getResource("/").getPath() + "testVersion";
        new MockUp<CbbTerminalSystemUpgradePackageAPIImpl>() {
            @Mock
            public String getVersionFilePath() {
                return path;
            }
        };
        new MockUp<File>() {
            @Mock
            public boolean isDirectory() {
                return true;
            }
            
            @Mock
            public String[] list() {
                String[] fileArr = new String[1];
                fileArr[0] = "dfd";
                return fileArr;
            }
        };
        Set<TerminalPlatformEnums> upgradePackageUploadnigSet = Deencapsulation.getField(upgradePackageAPIImpl, "SYS_UPGRADE_PACKAGE_UPLOADING");
        upgradePackageUploadnigSet.add(TerminalPlatformEnums.VDI);
        CbbTerminalUpgradePackageUploadRequest request = new CbbTerminalUpgradePackageUploadRequest();
        request.setFileName("sdsds.iso");
        request.setFilePath("dsdsd");
        try {
            upgradePackageAPIImpl.uploadUpgradePackage(request);
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_PACKAGE_IS_UPLOADING, e.getKey());
        }
        upgradePackageUploadnigSet.clear();
    }
    
    /**
     * 测试uploadUpgradeFile，系统升级任务正在进行中
     * @throws BusinessException 异常
     */
    @Test
    public void testUploadUpgradeFileUpgrading() {
        String path = CbbTerminalSystemUpgradePackageAPIImplTest.class.getResource("/").getPath() + "testVersion";
        new MockUp<CbbTerminalSystemUpgradePackageAPIImpl>() {
            @Mock
            public String getVersionFilePath() {
                return path;
            }
        };
        new MockUp<File>() {
            @Mock
            public boolean isDirectory() {
                return true;
            }
            
            @Mock
            public String[] list() {
                String[] fileArr = new String[1];
                fileArr[0] = "dfd";
                return fileArr;
            }
        };
        
        new Expectations() {
            {
                terminalSystemUpgradePackageDAO.findFirstByPackageType((TerminalPlatformEnums) any);
                result = new TerminalSystemUpgradePackageEntity();
                terminalSystemUpgradeService.hasSystemUpgradeInProgress((UUID) any);
                result = true;
                
            }
        };
        CbbTerminalUpgradePackageUploadRequest request = new CbbTerminalUpgradePackageUploadRequest();
        request.setFileName("sdsds.iso");
        request.setFilePath("dsdsd");
        try {
            upgradePackageAPIImpl.uploadUpgradePackage(request);
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_TASK_IS_RUNNING, e.getKey());
        }
    }
    
    /**
     * 测试uploadUpgradeFile，不支持的升级包
     * @throws BusinessException 异常
     */
    @Test
    public void testUploadUpgradeFileUnSupportUpgradePackage() {
        String path = CbbTerminalSystemUpgradePackageAPIImplTest.class.getResource("/").getPath() + "testVersionTypeError";
        new MockUp<CbbTerminalSystemUpgradePackageAPIImpl>() {
            @Mock
            public String getVersionFilePath() {
                return path;
            }
        };
        new MockUp<File>() {
            @Mock
            public boolean isDirectory() {
                return true;
            }
            
            @Mock
            public String[] list() {
                String[] fileArr = new String[1];
                fileArr[0] = "dfd";
                return fileArr;
            }
        };
        
        new Expectations() {
            {
                terminalSystemUpgradePackageDAO.findFirstByPackageType((TerminalPlatformEnums) any);
                result = null;
                
            }
        };
        
        CbbTerminalUpgradePackageUploadRequest request = new CbbTerminalUpgradePackageUploadRequest();
        request.setFileName("sdsds.iso");
        request.setFilePath("dsdsd");
        try {
            upgradePackageAPIImpl.uploadUpgradePackage(request);
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_UPLOAD_FILE_PACKAGE_TYPE_UNSUPPORT, e.getKey());
        }
    }
    
    /**
     * 测试uploadUpgradeFile，刷机包移动失败
     * @throws BusinessException 异常
     */
    @Test
    public void testUploadUpgradeFileMoveUpgradePackageFail() {
        String path = CbbTerminalSystemUpgradePackageAPIImplTest.class.getResource("/").getPath() + "testVersion";
        new MockUp<Files>() {
            @Mock
            public void move(File from, File to) throws IOException {
                throw new IOException();
            }
        };
        new MockUp<CbbTerminalSystemUpgradePackageAPIImpl>() {
            @Mock
            public String getVersionFilePath() {
                return path;
            }
        };
        new MockUp<File>() {
            @Mock
            public boolean isDirectory() {
                return true;
            }
            
            @Mock
            public String[] list() {
                String[] fileArr = new String[1];
                fileArr[0] = "dfd";
                return fileArr;
            }
        };
        
        CbbTerminalUpgradePackageUploadRequest request = new CbbTerminalUpgradePackageUploadRequest();
        request.setFileName("sdsds.iso");
        request.setFilePath("dsdsd");
        try {
            upgradePackageAPIImpl.uploadUpgradePackage(request);
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_UPLOAD_FILE_FAIL, e.getKey());
        }
    }
    
    /**
     * 测试uploadUpgradeFile，
     * @param util mock对象
     * @throws BusinessException 异常
     */
    @Test
    public void testUploadUpgradeFile(@Mocked FileOperateUtil util) throws BusinessException {
        String path = CbbTerminalSystemUpgradePackageAPIImplTest.class.getResource("/").getPath() + "testVersion";
        new MockUp<Files>() {
            @Mock
            public void move(File from, File to) throws IOException {
                
            }
        };
        new MockUp<CbbTerminalSystemUpgradePackageAPIImpl>() {
            @Mock
            public String getVersionFilePath() {
                return path;
            }
        };
        new MockUp<File>() {
            @Mock
            public boolean isDirectory() {
                return true;
            }
            
            @Mock
            public String[] list() {
                String[] fileArr = new String[1];
                fileArr[0] = "dfd";
                return fileArr;
            }
        };
        
        CbbTerminalUpgradePackageUploadRequest request = new CbbTerminalUpgradePackageUploadRequest();
        request.setFileName("sdsds.iso");
        request.setFilePath("dsdsd");
        DefaultResponse response = upgradePackageAPIImpl.uploadUpgradePackage(request);
        assertEquals(Status.SUCCESS, response.getStatus());
        new Verifications() {
            {
                terminalSystemUpgradePackageService.saveTerminalUpgradePackage((TerminalUpgradeVersionFileInfo) any);
                times = 1;
            }
        };
    }
    
    /**
     * 测试listSystemUpgradePackage，参数为空
     * @throws Exception 异常
     */
    @Test
    public void testListSystemUpgradePackageArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> upgradePackageAPIImpl.listSystemUpgradePackage(null),
                "request can not be null");
        assertTrue(true);
    }
    
    /**
     * 测试listSystemUpgradePackage，NoUpgradingTask
     * @param page mock对象
     * @throws BusinessException 异常
     * @throws Exception 异常
     */
    @Test
    public void testListSystemUpgradePackageNoUpgradingTask(@Mocked Page<TerminalSystemUpgradePackageEntity> page) throws BusinessException {
        PageSearchRequest request = new PageSearchRequest();
        List<TerminalSystemUpgradePackageEntity> packageList = new ArrayList<>();
        TerminalSystemUpgradePackageEntity packageEntity = new TerminalSystemUpgradePackageEntity();
        packageList.add(packageEntity);
        new Expectations() {
            {
                querySystemUpgradePackageListService.pageQuery(request, TerminalSystemUpgradePackageEntity.class);
                result = page;
                page.getContent();
                result = packageList;
                page.getSize();
                result = 1;
                page.getTotalElements();
                result = 1;
                
            }
        };
        DefaultPageResponse<CbbTerminalSystemUpgradePackageInfoDTO> response = upgradePackageAPIImpl.listSystemUpgradePackage(request);
        assertEquals(Status.SUCCESS, response.getStatus());
        assertEquals(packageEntity.getPackageName(), response.getItemArr()[0].getName());
        
        new Verifications() {
            {
                querySystemUpgradePackageListService.pageQuery(request, TerminalSystemUpgradePackageEntity.class);
                times = 1;
                page.getContent();
                times = 1;
                page.getSize();
                times = 1;
                page.getTotalElements();
                times = 1;
                systemUpgradeDAO.findByUpgradePackageIdAndStateInOrderByCreateTimeAsc(
                        packageEntity.getId(), (List<CbbSystemUpgradeTaskStateEnums>) any);
                times = 1;
            }
        };
    }
    
    /**
     * 测试listSystemUpgradePackage，
     * @param page mock对象
     * @throws BusinessException 异常
     * @throws Exception 异常
     */
    @Test
    public void testListSystemUpgradePackage(@Mocked Page<TerminalSystemUpgradePackageEntity> page) throws BusinessException {
        PageSearchRequest request = new PageSearchRequest();
        List<TerminalSystemUpgradePackageEntity> packageList = new ArrayList<>();
        TerminalSystemUpgradePackageEntity packageEntity = new TerminalSystemUpgradePackageEntity();
        packageList.add(packageEntity);
        
        List<TerminalSystemUpgradeEntity> upgradingTaskList = new ArrayList<>();
        TerminalSystemUpgradeEntity upgradeEntity = new TerminalSystemUpgradeEntity();
        upgradingTaskList.add(upgradeEntity);
        new Expectations() {
            {
                querySystemUpgradePackageListService.pageQuery(request, TerminalSystemUpgradePackageEntity.class);
                result = page;
                page.getContent();
                result = packageList;
                page.getSize();
                result = 1;
                page.getTotalElements();
                result = 1;
                systemUpgradeDAO.findByUpgradePackageIdAndStateInOrderByCreateTimeAsc(
                        packageEntity.getId(), (List<CbbSystemUpgradeTaskStateEnums>) any);
                result = upgradingTaskList;
            }
        };
        DefaultPageResponse<CbbTerminalSystemUpgradePackageInfoDTO> response = upgradePackageAPIImpl.listSystemUpgradePackage(request);
        assertEquals(Status.SUCCESS, response.getStatus());
        
        new Verifications() {
            {
                querySystemUpgradePackageListService.pageQuery(request, TerminalSystemUpgradePackageEntity.class);
                times = 1;
                page.getContent();
                times = 1;
                page.getSize();
                times = 1;
                page.getTotalElements();
                times = 1;
                systemUpgradeDAO.findByUpgradePackageIdAndStateInOrderByCreateTimeAsc(
                        packageEntity.getId(), (List<CbbSystemUpgradeTaskStateEnums>) any);
                times = 1;
            }
        };
    }
}

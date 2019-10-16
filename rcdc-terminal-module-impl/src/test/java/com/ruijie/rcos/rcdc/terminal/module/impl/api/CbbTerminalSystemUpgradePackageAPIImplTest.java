package com.ruijie.rcos.rcdc.terminal.module.impl.api;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import com.google.common.collect.Lists;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalSystemUpgradePackageInfoDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeTaskStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbCheckAllowUploadPackageRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.*;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbSystemUpgradeDistributionModeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbSystemUpgradePackageOriginEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeEntity;
import com.ruijie.rcos.sk.base.i18n.LocaleI18nResolver;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultRequest;
import com.ruijie.rcos.sk.modulekit.api.comm.IdRequest;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;
import com.google.common.io.Files;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalPlatformRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalUpgradePackageUploadRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalPlatformEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradeDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradePackageDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradePackageEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.SimpleCmdReturnValueResolver;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalUpgradeVersionFileInfo;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalSystemUpgradePackageService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalSystemUpgradeService;
import com.ruijie.rcos.rcdc.terminal.module.impl.util.FileOperateUtil;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.shell.ShellCommandRunner;
import com.ruijie.rcos.sk.base.test.ThrowExceptionTester;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultResponse;
import com.ruijie.rcos.sk.modulekit.api.comm.Response.Status;
import mockit.*;

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

    @Mocked
    private ShellCommandRunner shellCommandRunner;

    /**
     * 测试isUpgradeFileUploading，参数为空
     *
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
        Set<CbbTerminalPlatformEnums> uploadingSet =
                Deencapsulation.getField(CbbTerminalSystemUpgradePackageAPIImpl.class, "SYS_UPGRADE_PACKAGE_UPLOADING");
        uploadingSet.add(CbbTerminalPlatformEnums.VDI);
        CbbTerminalPlatformRequest request = new CbbTerminalPlatformRequest();
        request.setPlatform(CbbTerminalPlatformEnums.VDI);
        CbbCheckUploadingResultResponse response = upgradePackageAPIImpl.isUpgradeFileUploading(request);
        assertTrue(response.isHasLoading());
        uploadingSet.clear();
    }

    /**
     * 测试uploadUpgradeFile，参数为空
     *
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
     *
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
     *
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
     *
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
     *
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
            assertEquals(BusinessKey.RCDC_TERMINAL_UPGRADE_PACKAGE_FILE_ILLEGAL, e.getKey());
        }
    }

    /**
     * 测试uploadUpgradeFile，镜像文件不存在
     *
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
     *
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
     *
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
     *
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
        Set<CbbTerminalPlatformEnums> upgradePackageUploadnigSet =
                Deencapsulation.getField(upgradePackageAPIImpl, "SYS_UPGRADE_PACKAGE_UPLOADING");
        upgradePackageUploadnigSet.add(CbbTerminalPlatformEnums.VDI);
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
     *
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
                terminalSystemUpgradePackageDAO.findFirstByPackageType((CbbTerminalPlatformEnums) any);
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
     *
     * @throws BusinessException 异常
     */
    @Test
    public void testUploadUpgradeFileUnSupportUpgradePackage() {
        String path =
                CbbTerminalSystemUpgradePackageAPIImplTest.class.getResource("/").getPath() + "testVersionTypeError";
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
                terminalSystemUpgradePackageDAO.findFirstByPackageType((CbbTerminalPlatformEnums) any);
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
     *
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
     *
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
     *
     * @throws Exception 异常
     */
    @Test
    public void testListSystemUpgradePackageArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> upgradePackageAPIImpl.listSystemUpgradePackage(null),
                "request can not be null");
        assertTrue(true);
    }

    /**
     * 测试listSystemUpgradePackage-无进行中的升级任务
     * 
     * @throws ParseException exception
     * @throws BusinessException exception
     */
    @Test
    public void testListSystemUpgradePackageNoRunningTask() throws ParseException, BusinessException {
        UUID packageId = UUID.fromString("7769c0c6-473c-4d4c-9f47-5a62bdeb30ba");
        List<TerminalSystemUpgradePackageEntity> packageList = buildPackageEntityList(packageId);

        new Expectations() {
            {
                terminalSystemUpgradePackageDAO.findByIsDelete(false);
                result = packageList;

                systemUpgradeDAO.findByUpgradePackageIdAndStateInOrderByCreateTimeAsc(packageId,
                        (List<CbbSystemUpgradeTaskStateEnums>) any);
                result = null;
            }
        };

        CbbListTerminalSystemUpgradePackageResponse response =
                upgradePackageAPIImpl.listSystemUpgradePackage(new DefaultRequest());

        CbbTerminalSystemUpgradePackageInfoDTO checkDTO =
                buildCheckDTO(packageId, CbbSystemUpgradeTaskStateEnums.FINISH, null);

        new Verifications() {
            {
                terminalSystemUpgradePackageDAO.findByIsDelete(false);
                times = 1;

                systemUpgradeDAO.findByUpgradePackageIdAndStateInOrderByCreateTimeAsc(packageId,
                        (List<CbbSystemUpgradeTaskStateEnums>) any);
                times = 1;

                // super.withEqual(checkDTO, response.getItemArr()[0]);
            }
        };

    }

    /**
     * 测试listSystemUpgradePackage-存在进行中的升级任务
     * 
     * @throws ParseException exception
     * @throws BusinessException exception
     */
    @Test
    public void testListSystemUpgradePackageHasRunningTask() throws ParseException, BusinessException {
        UUID packageId = UUID.fromString("7769c0c6-473c-4d4c-9f47-5a62bdeb30ba");
        List<TerminalSystemUpgradePackageEntity> packageList = buildPackageEntityList(packageId);

        UUID systemUpgradeId = UUID.randomUUID();
        TerminalSystemUpgradeEntity systemUpgradeEntity = new TerminalSystemUpgradeEntity();
        systemUpgradeEntity.setId(systemUpgradeId);
        systemUpgradeEntity.setState(CbbSystemUpgradeTaskStateEnums.UPGRADING);
        List<TerminalSystemUpgradeEntity> upgradingTaskList = Lists.newArrayList(systemUpgradeEntity);
        new Expectations() {
            {
                terminalSystemUpgradePackageDAO.findByIsDelete(false);
                result = packageList;

                systemUpgradeDAO.findByUpgradePackageIdAndStateInOrderByCreateTimeAsc(packageId,
                        (List<CbbSystemUpgradeTaskStateEnums>) any);
                result = upgradingTaskList;
            }
        };

        CbbListTerminalSystemUpgradePackageResponse response =
                upgradePackageAPIImpl.listSystemUpgradePackage(new DefaultRequest());

        CbbTerminalSystemUpgradePackageInfoDTO checkDTO =
                buildCheckDTO(packageId, CbbSystemUpgradeTaskStateEnums.UPGRADING, systemUpgradeId);

        new Verifications() {
            {
                terminalSystemUpgradePackageDAO.findByIsDelete(false);
                times = 1;

                systemUpgradeDAO.findByUpgradePackageIdAndStateInOrderByCreateTimeAsc(packageId,
                        (List<CbbSystemUpgradeTaskStateEnums>) any);
                times = 1;

                // super.withEqual(checkDTO, response.getItemArr()[0]);
            }
        };

    }

    /**
     * 测试删除升级包参数为空
     * 
     * @throws Exception 异常
     */
    @Test
    public void testDeleteUpgradePackageParamIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> upgradePackageAPIImpl.deleteUpgradePackage(null),
                "request can not be null");
        assertTrue(true);
    }

    /**
     * 测试删除升级包 - 升级包存在进行中的升级任务
     * 
     * @throws ParseException exception
     * @throws BusinessException exception
     */
    @Test
    public void testDeleteUpgradePackageHasRunningTask() throws ParseException, BusinessException {
        IdRequest request = new IdRequest(UUID.randomUUID());

        TerminalSystemUpgradePackageEntity systemUpgradePackage = buildSystemUpgradePackageEntity(request.getId());
        new Expectations() {
            {
                terminalSystemUpgradePackageService.getSystemUpgradePackage(request.getId());
                result = systemUpgradePackage;

                terminalSystemUpgradeService.hasSystemUpgradeInProgress(request.getId());
                result = true;
            }
        };

        try {
            upgradePackageAPIImpl.deleteUpgradePackage(request);
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_TERMINAL_UPGRADE_PACKAGE_HAS_RUNNING_TASK_NOT_ALLOW_DELETE, e.getKey());
        }

        new Verifications() {
            {
                terminalSystemUpgradePackageService.getSystemUpgradePackage(request.getId());
                times = 1;

                terminalSystemUpgradeService.hasSystemUpgradeInProgress(request.getId());
                times = 1;
            }
        };

    }

    /**
     * 测试删除升级包 - 升级包不存在进行中的升级任务
     * 
     * @throws ParseException exception
     * @throws BusinessException exception
     */
    @Test
    public void testDeleteUpgradePackageNoRunningTask() throws ParseException, BusinessException {
        IdRequest request = new IdRequest(UUID.randomUUID());

        TerminalSystemUpgradePackageEntity systemUpgradePackage = buildSystemUpgradePackageEntity(request.getId());
        new Expectations() {
            {
                terminalSystemUpgradePackageService.getSystemUpgradePackage(request.getId());
                result = systemUpgradePackage;

                terminalSystemUpgradeService.hasSystemUpgradeInProgress(request.getId());
                result = false;

                terminalSystemUpgradePackageService.deleteSoft(request.getId());
            }
        };

        CbbUpgradePackageNameResponse response = upgradePackageAPIImpl.deleteUpgradePackage(request);
        assertEquals(systemUpgradePackage.getPackageName(), response.getPackageName());

        new Verifications() {
            {
                terminalSystemUpgradePackageService.getSystemUpgradePackage(request.getId());
                times = 1;

                terminalSystemUpgradeService.hasSystemUpgradeInProgress(request.getId());
                times = 1;

                terminalSystemUpgradePackageService.deleteSoft(request.getId());
                times = 1;
            }
        };

    }

    /**
     * 测试根据id获取升级包 - 参数为空
     * 
     * @throws Exception 异常
     */
    @Test
    public void testGetByIdParamIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> upgradePackageAPIImpl.getById(null),
                "request can not be null");
        assertTrue(true);
    }

    /**
     * 测试根据id获取升级包 - 无进行中的升级任务
     * 
     * @throws ParseException 转换日期异常
     * @throws BusinessException 业务异常
     */
    @Test
    public void testGetByIdNoUpgradingTask() throws ParseException, BusinessException {
        UUID packageId = UUID.fromString("7769c0c6-473c-4d4c-9f47-5a62bdeb30ba");
        TerminalSystemUpgradePackageEntity packageEntity = buildSystemUpgradePackageEntity(packageId);

        new Expectations() {
            {
                terminalSystemUpgradePackageService.getSystemUpgradePackage(packageId);
                result = packageEntity;

                systemUpgradeDAO.findByUpgradePackageIdAndStateInOrderByCreateTimeAsc(packageId,
                        (List<CbbSystemUpgradeTaskStateEnums>) any);
                result = null;
            }
        };

        CbbUpgradePackageResponse response = upgradePackageAPIImpl.getById(new IdRequest(packageId));

        CbbTerminalSystemUpgradePackageInfoDTO checkDTO =
                buildCheckDTO(packageId, CbbSystemUpgradeTaskStateEnums.FINISH, null);

        new Verifications() {
            {
                terminalSystemUpgradePackageService.getSystemUpgradePackage(packageId);
                times = 1;

                systemUpgradeDAO.findByUpgradePackageIdAndStateInOrderByCreateTimeAsc(packageId,
                        (List<CbbSystemUpgradeTaskStateEnums>) any);
                times = 1;

                // super.withEqual(checkDTO, response.getPackageInfo());
            }
        };
    }

    /**
     * 测试根据id获取升级包 - 存在进行中的升级任务
     * 
     * @throws ParseException 转换日期异常
     * @throws BusinessException 业务异常
     */
    @Test
    public void testGetByIdHasUpgradingTask() throws ParseException, BusinessException {
        UUID packageId = UUID.fromString("7769c0c6-473c-4d4c-9f47-5a62bdeb30ba");
        TerminalSystemUpgradePackageEntity packageEntity = buildSystemUpgradePackageEntity(packageId);

        UUID systemUpgradeId = UUID.randomUUID();
        TerminalSystemUpgradeEntity systemUpgradeEntity = new TerminalSystemUpgradeEntity();
        systemUpgradeEntity.setId(systemUpgradeId);
        systemUpgradeEntity.setState(CbbSystemUpgradeTaskStateEnums.UPGRADING);
        List<TerminalSystemUpgradeEntity> upgradingTaskList = Lists.newArrayList(systemUpgradeEntity);
        new Expectations() {
            {
                terminalSystemUpgradePackageService.getSystemUpgradePackage(packageId);
                result = packageEntity;

                systemUpgradeDAO.findByUpgradePackageIdAndStateInOrderByCreateTimeAsc(packageId,
                        (List<CbbSystemUpgradeTaskStateEnums>) any);
                result = upgradingTaskList;
            }
        };

        CbbUpgradePackageResponse response = upgradePackageAPIImpl.getById(new IdRequest(packageId));

        CbbTerminalSystemUpgradePackageInfoDTO checkDTO =
                buildCheckDTO(packageId, CbbSystemUpgradeTaskStateEnums.UPGRADING, systemUpgradeId);

        new Verifications() {
            {
                terminalSystemUpgradePackageService.getSystemUpgradePackage(packageId);
                times = 1;

                systemUpgradeDAO.findByUpgradePackageIdAndStateInOrderByCreateTimeAsc(packageId,
                        (List<CbbSystemUpgradeTaskStateEnums>) any);
                times = 1;

                // super.withEqual(checkDTO, response.getPackageInfo());
            }
        };
    }

    private CbbTerminalSystemUpgradePackageInfoDTO buildCheckDTO(UUID packageId, CbbSystemUpgradeTaskStateEnums state,
            UUID upgradeTaskId) throws ParseException {
        CbbTerminalSystemUpgradePackageInfoDTO checkDTO = new CbbTerminalSystemUpgradePackageInfoDTO();
        checkDTO.setName("packageName");
        checkDTO.setState(state);
        checkDTO.setDistributionMode(CbbSystemUpgradeDistributionModeEnums.FAST_UPGRADE);
        checkDTO.setId(packageId);
        checkDTO.setOrigin(CbbSystemUpgradePackageOriginEnums.USER_UPLOAD);
        checkDTO.setPackageType(CbbTerminalPlatformEnums.VDI);
        checkDTO.setUploadTime(DateUtils.parseDate("2019-09-17 10:10:10", "yyyy-MM-dd HH:mm:ss"));
        checkDTO.setUpgradeTaskId(upgradeTaskId);

        return checkDTO;
    }

    private List<TerminalSystemUpgradePackageEntity> buildPackageEntityList(UUID packageId) throws ParseException {
        return Lists.newArrayList(buildSystemUpgradePackageEntity(packageId));
    }

    private TerminalSystemUpgradePackageEntity buildSystemUpgradePackageEntity(UUID packageId) throws ParseException {
        TerminalSystemUpgradePackageEntity packageEntity = new TerminalSystemUpgradePackageEntity();
        packageEntity.setPackageType(CbbTerminalPlatformEnums.VDI);
        packageEntity.setIsDelete(false);
        packageEntity.setFilePath("filepath");
        packageEntity.setDistributionMode(CbbSystemUpgradeDistributionModeEnums.FAST_UPGRADE);
        packageEntity.setImgName("imgName");
        packageEntity.setOrigin(CbbSystemUpgradePackageOriginEnums.USER_UPLOAD);
        packageEntity.setPackageName("packageName");
        packageEntity.setPackageVersion("packageVersion");
        packageEntity.setUploadTime(DateUtils.parseDate("2019-09-17 10:10:10", "yyyy-MM-dd HH:mm:ss"));
        packageEntity.setId(packageId);
        packageEntity.setVersion(101);

        return packageEntity;
    }

    /**
     * 测试检查是否允许上传升级包- 请求参数为空
     *
     * @throws Exception 异常
     */
    @Test
    public void testCheckAllowUploadPackageParamIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> upgradePackageAPIImpl.checkAllowUploadPackage(null),
                "request can not be null");
        assertTrue(true);
    }

    /**
     * 测试检查是否允许上传升级包- 刷机任务在运行、磁盘空间不足
     *
     * @throws Exception 异常
     */
    @Test
    public void testCheckAllowUploadPackageHasRunningTaskDiskSpaceEnough() throws Exception {
        CbbCheckAllowUploadPackageRequest request = new CbbCheckAllowUploadPackageRequest(10L);

        new Expectations() {
            {
                terminalSystemUpgradeService.hasSystemUpgradeInProgress();
                result = true;
            }
        };

        new MockUp<File>() {
            @Mock
            public long getUsableSpace() {
                return 5L;
            }
        };

        new MockUp<LocaleI18nResolver>() {
            @Mock
            public String resolve(String key, String... args) {
                return key;
            }
        };

        CbbCheckAllowUploadPackageResponse response = upgradePackageAPIImpl.checkAllowUploadPackage(request);
        assertEquals(false, response.getAllowUpload());
        assertEquals(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_TASK_IS_RUNNING, response.getErrorList().get(0));
        assertEquals(BusinessKey.RCDC_TERMINAL_UPGRADE_PACKAGE_DISK_SPACE_NOT_ENOUGH, response.getErrorList().get(1));

        new Verifications() {
            {
                terminalSystemUpgradeService.hasSystemUpgradeInProgress();
                times = 1;
            }
        };
    }

    /**
     * 测试检查是否允许上传升级包- 请求参数为空
     *
     * @throws Exception 异常
     */
    @Test
    public void testCheckAllowUploadPackage() throws Exception {
        CbbCheckAllowUploadPackageRequest request = new CbbCheckAllowUploadPackageRequest(10L);

        new Expectations() {
            {
                terminalSystemUpgradeService.hasSystemUpgradeInProgress();
                result = false;
            }
        };

        new MockUp<File>() {
            @Mock
            public long getUsableSpace() {
                return 11L;
            }
        };

        CbbCheckAllowUploadPackageResponse response = upgradePackageAPIImpl.checkAllowUploadPackage(request);
        assertEquals(true, response.getAllowUpload());
        assertEquals(0, response.getErrorList().size());

        new Verifications() {
            {
                terminalSystemUpgradeService.hasSystemUpgradeInProgress();
                times = 1;
            }
        };
    }
}

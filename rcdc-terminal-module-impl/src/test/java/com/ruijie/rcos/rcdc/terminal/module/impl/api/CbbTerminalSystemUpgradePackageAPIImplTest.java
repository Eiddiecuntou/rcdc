package com.ruijie.rcos.rcdc.terminal.module.impl.api;

import com.alibaba.fastjson.JSONObject;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalTypeRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalUpgradePackageUploadRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbCheckUploadingResultResponse;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.TerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradeDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradePackageDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalSystemUpgradePackageService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalSystemUpgradeService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.LinuxVDISystemUpgradeHandler;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.TerminalSystemUpgradeHandler;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.TerminalSystemUpgradeHandlerFactory;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.shell.ShellCommandRunner;
import com.ruijie.rcos.sk.base.test.ThrowExceptionTester;
import mockit.*;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.*;

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

    @Injectable
    private TerminalSystemUpgradeHandlerFactory handlerFactory;

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
        Set<TerminalTypeEnums> uploadingSet =
                Deencapsulation.getField(CbbTerminalSystemUpgradePackageAPIImpl.class, "SYS_UPGRADE_PACKAGE_UPLOADING");
        uploadingSet.add(TerminalTypeEnums.VDI_LINUX);
        CbbTerminalTypeRequest request = new CbbTerminalTypeRequest();
        request.setTerminalType(TerminalTypeEnums.VDI_LINUX);
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
    public void testUploadUpgradePackageArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> upgradePackageAPIImpl.uploadUpgradePackage(null), "request can not be null");
        assertTrue(true);
    }


    /**
     * 测试uploadUpgradeFile，文件类型错误
     *
     * @throws BusinessException 异常
     *测试uploadUpgradePackage，有升级包在上传
     *
     * @throws Exception 异常
     */
    @Test
    public void testUploadUpgradePackageIsUploading() throws Exception {
        TerminalSystemUpgradeHandler handler = new LinuxVDISystemUpgradeHandler();
        CbbTerminalUpgradePackageUploadRequest request = new CbbTerminalUpgradePackageUploadRequest();
        request.setFileName("123.iso");
        request.setFilePath("/temp");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("platType", "VDI");
        jsonObject.put("osType", "Linux");
        request.setCustomData(jsonObject);
        Set<TerminalTypeEnums> upgradePackageUploadnigSet = Deencapsulation.getField(upgradePackageAPIImpl, "SYS_UPGRADE_PACKAGE_UPLOADING");
        upgradePackageUploadnigSet.add(TerminalTypeEnums.VDI_LINUX);
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
    public void testUploadUpgradePackage() throws Exception {

        TerminalSystemUpgradeHandler handler = new LinuxVDISystemUpgradeHandler();
        CbbTerminalUpgradePackageUploadRequest request = new CbbTerminalUpgradePackageUploadRequest();
        request.setFileName("123.iso");
        request.setFilePath("/temp");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("platType", "VDI");
        jsonObject.put("osType", "Linux");
        request.setCustomData(jsonObject);
        new Expectations() {
            {
                handlerFactory.getHandler((TerminalTypeEnums) any);
                result = handler;
            }
        };
        new MockUp<LinuxVDISystemUpgradeHandler>() {

            @Mock
            public void uploadUpgradePackage(CbbTerminalUpgradePackageUploadRequest request) {

            }
        };
        upgradePackageAPIImpl.uploadUpgradePackage(request);
        new Verifications() {
            {
                handlerFactory.getHandler((TerminalTypeEnums) any);
                times = 1;
                handler.uploadUpgradePackage(request);
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

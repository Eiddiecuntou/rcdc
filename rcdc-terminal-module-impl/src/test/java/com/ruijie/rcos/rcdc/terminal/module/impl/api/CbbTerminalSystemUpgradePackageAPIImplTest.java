package com.ruijie.rcos.rcdc.terminal.module.impl.api;

import com.google.common.collect.Lists;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalSystemUpgradePackageInfoDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeTaskStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbCheckAllowUploadPackageDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbCheckAllowUploadPackageResultDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbSystemUpgradeDistributionModeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbSystemUpgradePackageOriginEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradeDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradePackageDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradePackageEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalSystemPackageUploadingService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalSystemUpgradePackageService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalSystemUpgradeService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.systemupgrade.TerminalSystemUpgradeHandlerFactory;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.systemupgrade.TerminalSystemUpgradePackageHandler;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.systemupgrade.TerminalSystemUpgradePackageHandlerFactory;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.i18n.LocaleI18nResolver;
import com.ruijie.rcos.sk.base.shell.ShellCommandRunner;
import com.ruijie.rcos.sk.base.test.ThrowExceptionTester;
import com.ruijie.rcos.sk.modulekit.api.comm.IdRequest;
import mockit.*;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.text.ParseException;
import java.util.List;
import java.util.UUID;

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
    private TerminalSystemPackageUploadingService terminalSystemPackageUploadingService;

    @Injectable
    private TerminalSystemUpgradePackageService terminalSystemUpgradePackageService;

    @Mocked
    private ShellCommandRunner shellCommandRunner;

    @Injectable
    private TerminalSystemUpgradeHandlerFactory handlerFactory;

    @Injectable
    private TerminalSystemUpgradePackageDAO termianlSystemUpgradePackageDAO;

    @Injectable
    private TerminalSystemUpgradePackageHandlerFactory terminalSystemUpgradePackageHandlerFactory;

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

        CbbTerminalSystemUpgradePackageInfoDTO[] dtoArr =
                upgradePackageAPIImpl.listSystemUpgradePackage();

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

        CbbTerminalSystemUpgradePackageInfoDTO[] dtoArr =
                upgradePackageAPIImpl.listSystemUpgradePackage();

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
                "packageId can not be null");
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
        UUID packageId = UUID.randomUUID();

        TerminalSystemUpgradePackageEntity systemUpgradePackage = buildSystemUpgradePackageEntity(packageId);
        new Expectations() {
            {
                terminalSystemUpgradePackageService.getSystemUpgradePackage(packageId);
                result = systemUpgradePackage;

                terminalSystemUpgradeService.hasSystemUpgradeInProgress(packageId);
                result = true;
            }
        };

        try {
            upgradePackageAPIImpl.deleteUpgradePackage(packageId);
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_TERMINAL_UPGRADE_PACKAGE_HAS_RUNNING_TASK_NOT_ALLOW_DELETE, e.getKey());
        }

        new Verifications() {
            {
                terminalSystemUpgradePackageService.getSystemUpgradePackage(packageId);
                times = 1;

                terminalSystemUpgradeService.hasSystemUpgradeInProgress(packageId);
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
        UUID packageId = UUID.randomUUID();

        TerminalSystemUpgradePackageEntity systemUpgradePackage = buildSystemUpgradePackageEntity(packageId);
        new Expectations() {
            {
                terminalSystemUpgradePackageService.getSystemUpgradePackage(packageId);
                result = systemUpgradePackage;

                terminalSystemUpgradeService.hasSystemUpgradeInProgress(packageId);
                result = false;

                terminalSystemUpgradePackageService.deleteSoft(packageId);
            }
        };

        String packageName = upgradePackageAPIImpl.deleteUpgradePackage(packageId);
        assertEquals(systemUpgradePackage.getPackageName(), packageName);

        new Verifications() {
            {
                terminalSystemUpgradePackageService.getSystemUpgradePackage(packageId);
                times = 1;

                terminalSystemUpgradeService.hasSystemUpgradeInProgress(packageId);
                times = 1;

                terminalSystemUpgradePackageService.deleteSoft(packageId);
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
        ThrowExceptionTester.throwIllegalArgumentException(() -> upgradePackageAPIImpl.findById(null),
                "packageId can not be null");
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

        CbbTerminalSystemUpgradePackageInfoDTO dto = upgradePackageAPIImpl.findById(packageId);

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

        CbbTerminalSystemUpgradePackageInfoDTO dto = upgradePackageAPIImpl.findById(packageId);

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
        checkDTO.setPackageType(CbbTerminalTypeEnums.VDI_LINUX);
        checkDTO.setUploadTime(DateUtils.parseDate("2019-09-17 10:10:10", "yyyy-MM-dd HH:mm:ss"));
        checkDTO.setUpgradeTaskId(upgradeTaskId);

        return checkDTO;
    }

    private List<TerminalSystemUpgradePackageEntity> buildPackageEntityList(UUID packageId) throws ParseException {
        return Lists.newArrayList(buildSystemUpgradePackageEntity(packageId));
    }

    private TerminalSystemUpgradePackageEntity buildSystemUpgradePackageEntity(UUID packageId) throws ParseException {
        TerminalSystemUpgradePackageEntity packageEntity = new TerminalSystemUpgradePackageEntity();
        packageEntity.setPackageType(CbbTerminalTypeEnums.VDI_LINUX);
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
     * 测试检查是否允许上传升级包- 请求参数为空
     *
     * @throws Exception 异常
     */
    @Test
    public void testCheckAllowUploadPackage(@Injectable TerminalSystemUpgradePackageHandler handler) throws Exception {
        CbbCheckAllowUploadPackageDTO request = new CbbCheckAllowUploadPackageDTO("20211104144107.zip", 10L);

        new Expectations() {
            {
                terminalSystemUpgradePackageHandlerFactory.getHandler((CbbTerminalTypeEnums) any);
                result = handler;
                handler.checkServerDiskSpaceIsEnough(anyLong, anyString);
                result = true;
            }
        };

        CbbCheckAllowUploadPackageResultDTO response = upgradePackageAPIImpl.checkAllowUploadPackage(request);
        assertEquals(true, response.getAllowUpload());
        assertEquals(0, response.getErrorList().size());
    }

    /**
     * 测试检查是否允许上传升级包
     *
     * @throws Exception 异常
     */
    @Test
    public void testCheckAllowUploadPackageWithPackageNull(@Injectable TerminalSystemUpgradePackageHandler handler) throws Exception {
        CbbCheckAllowUploadPackageDTO request = new CbbCheckAllowUploadPackageDTO("20211104144107.zip", 10L);

        new Expectations() {
            {
                terminalSystemUpgradePackageHandlerFactory.getHandler((CbbTerminalTypeEnums) any);
                result = handler;
                handler.checkServerDiskSpaceIsEnough(anyLong, anyString);
                handler.checkFileNameDuplicate(anyString);
                result = false;
            }
        };

        new MockUp<LocaleI18nResolver>() {
            @Mock
            public String resolve(String key, String... args) {
                return key;
            }
        };

        CbbCheckAllowUploadPackageResultDTO response = upgradePackageAPIImpl.checkAllowUploadPackage(request);
        assertEquals(false, response.getAllowUpload());
        assertEquals(2, response.getErrorList().size());
    }
}

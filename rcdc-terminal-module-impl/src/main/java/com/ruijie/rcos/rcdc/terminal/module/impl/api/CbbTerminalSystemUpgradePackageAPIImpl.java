package com.ruijie.rcos.rcdc.terminal.module.impl.api;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTerminalSystemUpgradePackageAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalSystemUpgradePackageInfoDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeTaskStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbCheckAllowUploadPackageRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalTypeRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalUpgradePackageModifyRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalUpgradePackageUploadRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.*;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradeDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradePackageDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradePackageEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalSystemUpgradePackageService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalSystemUpgradeService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.TerminalSystemUpgradeHandler;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.TerminalSystemUpgradeHandlerFactory;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.i18n.LocaleI18nResolver;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultRequest;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultResponse;
import com.ruijie.rcos.sk.modulekit.api.comm.IdRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.util.*;
import java.util.stream.Stream;

/**
 * 
 * Description: 终端系统升级实现类
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年11月19日
 * 
 * @author nt
 */
public class CbbTerminalSystemUpgradePackageAPIImpl implements CbbTerminalSystemUpgradePackageAPI {

    private static final Logger LOGGER = LoggerFactory.getLogger(CbbTerminalSystemUpgradePackageAPIImpl.class);

    @Autowired
    private TerminalSystemUpgradeHandlerFactory handlerFactory;

    private static final BeanCopier PACKAGE_BEAN_COPIER =
            BeanCopier.create(TerminalSystemUpgradePackageEntity.class, CbbTerminalSystemUpgradePackageInfoDTO.class, false);

    private static final Set<CbbTerminalTypeEnums> SYS_UPGRADE_PACKAGE_UPLOADING = new HashSet<>();

    private static final Object LOCK = new Object();

    @Autowired
    private TerminalSystemUpgradePackageDAO terminalSystemUpgradePackageDAO;

    @Autowired
    private TerminalSystemUpgradeDAO systemUpgradeDAO;

    @Autowired
    private TerminalSystemUpgradeService terminalSystemUpgradeService;

    @Autowired
    private TerminalSystemUpgradePackageService terminalSystemUpgradePackageService;

    @Autowired
    private TerminalSystemUpgradePackageDAO termianlSystemUpgradePackageDAO;

    private static final String PLAT_TYPE = "platType";

    private static final String OS_TYPE = "osType";

    @Override
    public CbbCheckUploadingResultResponse isUpgradeFileUploading(CbbTerminalTypeRequest request) {
        Assert.notNull(request, "request can not be null");

        CbbCheckUploadingResultResponse response = new CbbCheckUploadingResultResponse();
        response.setHasLoading(SYS_UPGRADE_PACKAGE_UPLOADING.contains(request.getTerminalType()));
        return response;
    }

    @Override
    public CbbCheckAllowUploadPackageResponse checkAllowUploadPackage(CbbCheckAllowUploadPackageRequest request) throws BusinessException {
        Assert.notNull(request, "request can not be null");

        boolean allowUpload = true;
        boolean hasRunningTask = false;
        List<String> errorList = Lists.newArrayList();
        TerminalSystemUpgradePackageEntity upgradePackage = termianlSystemUpgradePackageDAO.findFirstByPackageType(request.getTerminalType());
        if (upgradePackage != null) {
            hasRunningTask = terminalSystemUpgradeService.hasSystemUpgradeInProgress(upgradePackage.getId());
        }
        if (hasRunningTask) {
            LOGGER.debug("system upgrade task is running");
            allowUpload = false;
            errorList.add(LocaleI18nResolver.resolve(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_TASK_IS_RUNNING, new String[] {}));
        }

        // 判断磁盘大小是否满足
        final boolean isEnough = checkPackageDiskSpaceIsEnough(request.getFileSize());
        if (!isEnough) {
            allowUpload = false;
            errorList.add(LocaleI18nResolver.resolve(BusinessKey.RCDC_TERMINAL_UPGRADE_PACKAGE_DISK_SPACE_NOT_ENOUGH, new String[] {}));
        }

        CbbCheckAllowUploadPackageResponse respone = new CbbCheckAllowUploadPackageResponse();
        respone.setAllowUpload(allowUpload);
        respone.setErrorList(errorList);

        return respone;
    }

    @Override
    public DefaultResponse uploadUpgradePackage(CbbTerminalUpgradePackageUploadRequest request) throws BusinessException {
        Assert.notNull(request, "request can not be null");
        JSONObject jsonObject = request.getCustomData();
        String platType = jsonObject.getString(PLAT_TYPE);
        String osType = jsonObject.getString(OS_TYPE);
        CbbTerminalTypeEnums terminalType = CbbTerminalTypeEnums.convert(platType, osType);
        synchronized (LOCK) {
            if (SYS_UPGRADE_PACKAGE_UPLOADING.contains(terminalType)) {
                throw new BusinessException(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_PACKAGE_IS_UPLOADING);
            }
            SYS_UPGRADE_PACKAGE_UPLOADING.add(terminalType);
        }
        // 根据升级包类型判断是否存在旧升级包，及是否存在旧升级包的正在进行中的升级任务，是则不允许替换升级包
        boolean hasRunningTask = isExistRunningTask(terminalType);
        if (hasRunningTask) {
            LOGGER.debug("system upgrade task is running, can not upload file ");
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_TASK_IS_RUNNING);
        }
        TerminalSystemUpgradeHandler handler = handlerFactory.getHandler(terminalType);
        handler.uploadUpgradePackage(request);
        // 完成清除上传标志缓存内记录
        SYS_UPGRADE_PACKAGE_UPLOADING.remove(terminalType);
        return DefaultResponse.Builder.success();
    }

    @Override
    public DefaultResponse modifyUpgradePackage(CbbTerminalUpgradePackageModifyRequest request) throws BusinessException {
        Assert.notNull(request, "request can not be null");
        TerminalSystemUpgradePackageEntity packageEntity =
                terminalSystemUpgradePackageService.getSystemUpgradePackage(request.getPackageId());
        packageEntity.setUpgradeMode(request.getUpgradeMode());
        terminalSystemUpgradePackageDAO.save(packageEntity);
        return DefaultResponse.Builder.success();
    }

    /**
     * 检验是否存在正在进行的升级任务
     *
     * @param packageType 升级包类型
     * @return
     */
    private boolean isExistRunningTask(CbbTerminalTypeEnums packageType) {
        TerminalSystemUpgradePackageEntity upgradePackage = terminalSystemUpgradePackageDAO.findFirstByPackageType(packageType);
        if (upgradePackage == null) {
            return false;
        }

        return terminalSystemUpgradeService.hasSystemUpgradeInProgress(upgradePackage.getId());
    }

    /**
     * 检验磁盘空间是否满足升级包上传
     * 
     * @param fileSize 文件大小
     * @return 磁盘空间是否足够
     */
    private boolean checkPackageDiskSpaceIsEnough(Long fileSize) {
        File packageDir = new File(Constants.TERMINAL_UPGRADE_PACKAGE_PATH);
        final long usableSpace = packageDir.getUsableSpace();
        if (usableSpace >= fileSize) {
            return true;
        }

        return false;
    }

    @Override
    public CbbListTerminalSystemUpgradePackageResponse listSystemUpgradePackage(DefaultRequest request) throws BusinessException {
        Assert.notNull(request, "request can not be null");

        List<TerminalSystemUpgradePackageEntity> packageList = terminalSystemUpgradePackageDAO.findByIsDelete(false);

        CbbTerminalSystemUpgradePackageInfoDTO[] dtoArr = new CbbTerminalSystemUpgradePackageInfoDTO[packageList.size()];
        Stream.iterate(0, i -> i + 1).limit(packageList.size()).forEach(i -> {
            CbbTerminalSystemUpgradePackageInfoDTO dto = new CbbTerminalSystemUpgradePackageInfoDTO();
            final TerminalSystemUpgradePackageEntity packageEntity = packageList.get(i);
            PACKAGE_BEAN_COPIER.copy(packageEntity, dto, null);
            dto.setName(packageEntity.getPackageName());
            // 设置刷机包刷机状态
            completeUpgradingTaskInfo(dto, packageEntity);
            dtoArr[i] = dto;
        });

        return new CbbListTerminalSystemUpgradePackageResponse(dtoArr);
    }

    @Override
    public CbbUpgradePackageNameResponse deleteUpgradePackage(IdRequest request) throws BusinessException {
        Assert.notNull(request, "request can not be null");

        final UUID packageId = request.getId();
        final TerminalSystemUpgradePackageEntity systemUpgradePackage = terminalSystemUpgradePackageService.getSystemUpgradePackage(packageId);
        if (terminalSystemUpgradeService.hasSystemUpgradeInProgress(packageId)) {
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_UPGRADE_PACKAGE_HAS_RUNNING_TASK_NOT_ALLOW_DELETE);
        }

        terminalSystemUpgradePackageService.deleteSoft(packageId);

        return new CbbUpgradePackageNameResponse(systemUpgradePackage.getPackageName());
    }

    @Override
    public CbbUpgradePackageResponse getById(IdRequest request) throws BusinessException {
        Assert.notNull(request, "request can not be null");

        final TerminalSystemUpgradePackageEntity packageEntity =
                terminalSystemUpgradePackageService.getSystemUpgradePackage(request.getId());
        CbbTerminalSystemUpgradePackageInfoDTO dto = new CbbTerminalSystemUpgradePackageInfoDTO();
        PACKAGE_BEAN_COPIER.copy(packageEntity, dto, null);
        dto.setName(packageEntity.getPackageName());
        // 设置刷机包刷机状态
        completeUpgradingTaskInfo(dto, packageEntity);
        return new CbbUpgradePackageResponse(dto);
    }

    private void completeUpgradingTaskInfo(CbbTerminalSystemUpgradePackageInfoDTO dto, final TerminalSystemUpgradePackageEntity packageEntity) {
        final UUID packageId = packageEntity.getId();
        final TerminalSystemUpgradeEntity upgradingTask = getUpgradingTask(packageId);
        if (upgradingTask == null) {
            dto.setState(CbbSystemUpgradeTaskStateEnums.FINISH);
            return;
        }
        dto.setState(upgradingTask.getState());
        dto.setUpgradeTaskId(upgradingTask.getId());
    }

    private TerminalSystemUpgradeEntity getUpgradingTask(UUID packageId) {
        List<CbbSystemUpgradeTaskStateEnums> stateList = Arrays
                .asList(new CbbSystemUpgradeTaskStateEnums[] {CbbSystemUpgradeTaskStateEnums.UPGRADING, CbbSystemUpgradeTaskStateEnums.CLOSING});
        final List<TerminalSystemUpgradeEntity> upgradingTaskList =
                systemUpgradeDAO.findByUpgradePackageIdAndStateInOrderByCreateTimeAsc(packageId, stateList);
        if (CollectionUtils.isEmpty(upgradingTaskList)) {
            // 无升级中的任务
            return null;
        }
        // 同一时间只存在一个正在刷机中的任务
        return upgradingTaskList.get(0);
    }

}

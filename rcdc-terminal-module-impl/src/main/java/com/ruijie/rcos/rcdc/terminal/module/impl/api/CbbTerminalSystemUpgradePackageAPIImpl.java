package com.ruijie.rcos.rcdc.terminal.module.impl.api;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTerminalSystemUpgradePackageAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalSystemUpgradePackageInfoDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeTaskStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbCheckAllowUploadPackageRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalPlatformRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalUpgradePackageUploadRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.*;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradeDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradePackageDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradePackageEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.TerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.UpgradeFileTypeEnums;
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
    TerminalSystemUpgradeHandlerFactory handlerFactory;

    private static final BeanCopier PACKAGE_BEAN_COPIER =
            BeanCopier.create(TerminalSystemUpgradePackageEntity.class, CbbTerminalSystemUpgradePackageInfoDTO.class, false);

    private static final Set<TerminalTypeEnums> SYS_UPGRADE_PACKAGE_UPLOADING = new HashSet<>();

    private static final Object LOCK = new Object();

    @Autowired
    private TerminalSystemUpgradePackageDAO terminalSystemUpgradePackageDAO;

    @Autowired
    private TerminalSystemUpgradeDAO systemUpgradeDAO;

    @Autowired
    private TerminalSystemUpgradeService terminalSystemUpgradeService;

    @Autowired
    private TerminalSystemUpgradePackageService terminalSystemUpgradePackageService;

    private static final String PLAT_TYPE = "platType";

    @Override
    public CbbCheckUploadingResultResponse isUpgradeFileUploading(CbbTerminalPlatformRequest request) {
        Assert.notNull(request, "request can not be null");

        CbbCheckUploadingResultResponse response = new CbbCheckUploadingResultResponse();
        response.setHasLoading(SYS_UPGRADE_PACKAGE_UPLOADING.contains(request.getPlatform()));
        return response;
    }

    @Override
    public CbbCheckAllowUploadPackageResponse checkAllowUploadPackage(CbbCheckAllowUploadPackageRequest request) throws BusinessException {
        Assert.notNull(request, "request can not be null");

        boolean allowUpload = true;
        List<String> errorList = Lists.newArrayList();
        boolean hasRunningTask = terminalSystemUpgradeService.hasSystemUpgradeInProgress();
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
        TerminalTypeEnums platType = jsonObject.getObject(PLAT_TYPE, TerminalTypeEnums.class);
        synchronized (LOCK) {
            if (SYS_UPGRADE_PACKAGE_UPLOADING.contains(platType)) {
                throw new BusinessException(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_PACKAGE_IS_UPLOADING);
            }
            SYS_UPGRADE_PACKAGE_UPLOADING.add(platType);
        }
        TerminalSystemUpgradeHandler handler = handlerFactory.getHandler(platType);
        handler.uploadUpgradePackage(request);
        // 完成清除上传标志缓存内记录
        SYS_UPGRADE_PACKAGE_UPLOADING.remove(platType);
        return DefaultResponse.Builder.success();
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

    private boolean checkFileType(String fileName) {
        String fileType = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        // 文件类型校验
        if (UpgradeFileTypeEnums.contains(fileType)) {
            LOGGER.debug("file type [{}] is correct", fileType);
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

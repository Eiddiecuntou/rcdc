package com.ruijie.rcos.rcdc.terminal.module.impl.api;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import com.google.common.collect.Lists;
import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTerminalSystemUpgradePackageAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.*;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeTaskStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradeDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradePackageDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradePackageEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalSystemPackageUploadingService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalSystemUpgradePackageService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalSystemUpgradeService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.systemupgrade.TerminalSystemUpgradePackageHandler;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.systemupgrade.TerminalSystemUpgradePackageHandlerFactory;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.i18n.LocaleI18nResolver;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;

/**
 * Description: 终端系统升级实现类
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年11月19日
 *
 * @author nt
 */
public class CbbTerminalSystemUpgradePackageAPIImpl implements CbbTerminalSystemUpgradePackageAPI {

    private static final Logger LOGGER = LoggerFactory.getLogger(CbbTerminalSystemUpgradePackageAPIImpl.class);

    private static final BeanCopier PACKAGE_BEAN_COPIER =
            BeanCopier.create(TerminalSystemUpgradePackageEntity.class, CbbTerminalSystemUpgradePackageInfoDTO.class, false);

    @Autowired
    private TerminalSystemUpgradePackageDAO terminalSystemUpgradePackageDAO;

    @Autowired
    private TerminalSystemUpgradeDAO systemUpgradeDAO;

    @Autowired
    private TerminalSystemUpgradeService terminalSystemUpgradeService;

    @Autowired
    private TerminalSystemUpgradePackageService terminalSystemUpgradePackageService;

    @Autowired
    private TerminalSystemPackageUploadingService terminalSystemPackageUploadingService;

    @Autowired
    private TerminalSystemUpgradePackageHandlerFactory terminalSystemUpgradePackageHandlerFactory;

    @Override
    public CbbCheckAllowUploadPackageResultDTO checkAllowUploadPackage(CbbCheckAllowUploadPackageDTO request) throws BusinessException {
        Assert.notNull(request, "request can not be null");

        List<String> errorList = Lists.newArrayList();
        LOGGER.info("上传升级包类型[{}]", request.getTerminalType());

        // 判断磁盘大小是否满足
        TerminalSystemUpgradePackageHandler handler = terminalSystemUpgradePackageHandlerFactory.getHandler(request.getTerminalType());
        final boolean isEnough = handler.checkServerDiskSpaceIsEnough(request.getFileSize(), handler.getUpgradePackageFileDir());
        boolean allowUpload = true;
        if (!isEnough) {
            allowUpload = false;
            errorList.add(LocaleI18nResolver.resolve(BusinessKey.RCDC_TERMINAL_UPGRADE_PACKAGE_DISK_SPACE_NOT_ENOUGH, new String[] {}));
        }

        CbbCheckAllowUploadPackageResultDTO respone = new CbbCheckAllowUploadPackageResultDTO();
        respone.setAllowUpload(allowUpload);
        respone.setErrorList(errorList);

        return respone;
    }

    @Override
    public List<CbbSystemUpgradePackageInfoDTO> listUpgradePackageByPackageTypeAndIsDelete(CbbTerminalTypeEnums packageType, Boolean isDelete) {
        Assert.notNull(packageType, "id can not be null");
        Assert.notNull(isDelete, "seedMd5 can not be null or empty");
        LOGGER.info("升级包类型和是否删除状态，查询所有升级包信息，packageType {}，isDelete {}", packageType, isDelete);
        List<TerminalSystemUpgradePackageEntity> terminalSystemUpgradePackageEntityList =
                terminalSystemUpgradePackageDAO.findByPackageTypeAndIsDelete(packageType, isDelete);
        return terminalSystemUpgradePackageEntityList.stream().map(terminalSystemUpgradePackageEntity -> {
            CbbSystemUpgradePackageInfoDTO cbbSystemUpgradePackageInfoDTO = new CbbSystemUpgradePackageInfoDTO();
            BeanUtils.copyProperties(terminalSystemUpgradePackageEntity, cbbSystemUpgradePackageInfoDTO);
            return cbbSystemUpgradePackageInfoDTO;
        }).collect(Collectors.toList());

    }

    @Override
    public void updateSeedMd5ById(UUID id, String seedMd5) {
        Assert.notNull(id, "id can not be null");
        Assert.hasText(seedMd5, "seedMd5 can not be null or empty");
        LOGGER.info("更新升级包种子md5值，id {}，seedMd5 {}", id, seedMd5);
        terminalSystemUpgradePackageDAO.updateSeedMd5ById(id, seedMd5);
    }

    @Override
    public void uploadUpgradePackage(CbbTerminalUpgradePackageUploadDTO request) throws BusinessException {
        Assert.notNull(request, "request can not be null");
        CbbTerminalTypeEnums terminalType = request.getTerminalType();

        terminalSystemPackageUploadingService.uploadUpgradePackage(request, terminalType);
    }


    @Override
    public CbbTerminalSystemUpgradePackageInfoDTO[] listSystemUpgradePackage() throws BusinessException {

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

        return dtoArr;
    }

    @Override
    public String deleteUpgradePackage(UUID packageId) throws BusinessException {
        Assert.notNull(packageId, "packageId can not be null");

        final TerminalSystemUpgradePackageEntity systemUpgradePackage = terminalSystemUpgradePackageService.getSystemUpgradePackage(packageId);
        if (terminalSystemUpgradeService.hasSystemUpgradeInProgress(packageId)) {
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_UPGRADE_PACKAGE_HAS_RUNNING_TASK_NOT_ALLOW_DELETE);
        }

        terminalSystemUpgradePackageService.deleteSoft(packageId);

        return systemUpgradePackage.getPackageName();
    }

    @Override
    public CbbTerminalSystemUpgradePackageInfoDTO findById(UUID packageId) throws BusinessException {
        Assert.notNull(packageId, "packageId can not be null");

        final TerminalSystemUpgradePackageEntity packageEntity = terminalSystemUpgradePackageService.getSystemUpgradePackage(packageId);
        CbbTerminalSystemUpgradePackageInfoDTO dto = new CbbTerminalSystemUpgradePackageInfoDTO();
        PACKAGE_BEAN_COPIER.copy(packageEntity, dto, null);
        dto.setName(packageEntity.getPackageName());
        // 设置刷机包刷机状态
        completeUpgradingTaskInfo(dto, packageEntity);
        return dto;
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
        List<CbbSystemUpgradeTaskStateEnums> stateList =
                Arrays.asList(new CbbSystemUpgradeTaskStateEnums[] {CbbSystemUpgradeTaskStateEnums.UPGRADING});
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

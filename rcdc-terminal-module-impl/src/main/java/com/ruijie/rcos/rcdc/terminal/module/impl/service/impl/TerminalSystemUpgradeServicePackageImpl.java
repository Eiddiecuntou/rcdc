package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbSystemUpgradeDistributionModeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbSystemUpgradePackageOriginEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradePackageDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradePackageEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalSystemUpgradeInfo;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalUpgradeVersionFileInfo;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalSystemUpgradePackageService;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;

/**
 * 
 * Description: 终端升级服务实现类
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年11月20日
 * 
 * @author nt
 */
@Service
public class TerminalSystemUpgradeServicePackageImpl implements TerminalSystemUpgradePackageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TerminalSystemUpgradeServicePackageImpl.class);

    @Autowired
    private TerminalSystemUpgradePackageDAO termianlSystemUpgradePackageDAO;

    @Override
    public void saveTerminalUpgradePackage(TerminalUpgradeVersionFileInfo versionInfo) throws BusinessException {
        Assert.notNull(versionInfo, "terminalUpgradeVersionFileInfo 不能为空");

        TerminalSystemUpgradePackageEntity upgradePackage = termianlSystemUpgradePackageDAO.findFirstByPackageType(versionInfo.getPackageType());
        if (upgradePackage == null) {
            upgradePackage = new TerminalSystemUpgradePackageEntity();
        }

        saveSystemUpgradePackage(upgradePackage, versionInfo);
    }

    private void saveSystemUpgradePackage(TerminalSystemUpgradePackageEntity upgradePackage, TerminalUpgradeVersionFileInfo versionInfo) {
        completeSystemUpgradePackageEntity(upgradePackage, versionInfo);
        termianlSystemUpgradePackageDAO.save(upgradePackage);
    }

    private void completeSystemUpgradePackageEntity(TerminalSystemUpgradePackageEntity upgradePackage, TerminalUpgradeVersionFileInfo versionInfo) {
        upgradePackage.setPackageName(versionInfo.getPackageName());
        upgradePackage.setImgName(versionInfo.getImgName());
        upgradePackage.setOrigin(CbbSystemUpgradePackageOriginEnums.USER_UPLOAD);
        upgradePackage.setDistributionMode(CbbSystemUpgradeDistributionModeEnums.FAST_UPGRADE);
        upgradePackage.setPackageType(versionInfo.getPackageType());
        upgradePackage.setPackageVersion(versionInfo.getVersion());
        upgradePackage.setUploadTime(new Date());
        upgradePackage.setFilePath(versionInfo.getFilePath());
        upgradePackage.setFileMD5(versionInfo.getFileMD5());
        upgradePackage.setSeedPath(versionInfo.getSeedLink());
        upgradePackage.setSeedMD5(versionInfo.getFileMD5());
        upgradePackage.setUpgradeMode(versionInfo.getUpgradeMode());
        upgradePackage.setIsDelete(false);
    }

    @Override
    public List<TerminalSystemUpgradeInfo> readSystemUpgradeSuccessStateFromFile() throws BusinessException {
        return getStatusFromFile(Constants.TERMINAL_UPGRADE_END_SATTUS_FILE_PATH, CbbSystemUpgradeStateEnums.SUCCESS);
    }

    @Override
    public List<TerminalSystemUpgradeInfo> readSystemUpgradeStartStateFromFile() throws BusinessException {
        return getStatusFromFile(Constants.TERMINAL_UPGRADE_START_SATTUS_FILE_PATH, CbbSystemUpgradeStateEnums.UPGRADING);
    }

    private List<TerminalSystemUpgradeInfo> getStatusFromFile(String fileDir, CbbSystemUpgradeStateEnums state) throws BusinessException {
        // 读取升级成功文件夹，通过文件名称获取mac即终端id
        File upgradeSuccessDir = new File(fileDir);
        if (!upgradeSuccessDir.isDirectory()) {
            LOGGER.error("terminal upgrade status directory not exist, file path: {}", fileDir);
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_UPGRADE_SUCCESS_STATUS_DIRECTORY_NOT_EXIST);
        }

        List<TerminalSystemUpgradeInfo> upgradeInfoList = new ArrayList<>();
        String[] fileNameArr = upgradeSuccessDir.list();
        for (String fileName : fileNameArr) {
            String fileNameWithoutSuffix = getFileNameWithoutSuffix(fileName);
            TerminalSystemUpgradeInfo upgradeInfo = new TerminalSystemUpgradeInfo();
            upgradeInfo.setTerminalId(fileNameWithoutSuffix);
            upgradeInfo.setState(state);
            upgradeInfoList.add(upgradeInfo);
        }
        return upgradeInfoList;
    }

    private String getFileNameWithoutSuffix(String fileName) {
        int index = fileName.lastIndexOf(Constants.FILE_SUFFIX_DOT);
        if (index == -1) {
            return fileName;
        }

        return fileName.substring(0, index);
    }

    @Override
    public void deleteSoft(UUID packageId) throws BusinessException {
        Assert.notNull(packageId, "packageId can not be null");

        final TerminalSystemUpgradePackageEntity systemUpgradePackage = getSystemUpgradePackage(packageId);
        systemUpgradePackage.setIsDelete(true);
        termianlSystemUpgradePackageDAO.save(systemUpgradePackage);

        // 删除升级包文件
        deletePackageFile(systemUpgradePackage.getFilePath());
    }

    private void deletePackageFile(String filePath) {
        if (StringUtils.isBlank(filePath)) {
            LOGGER.error("升级包文件路径不存在，文件路径：{}", filePath);
            return;
        }
        File file = new File(filePath);
        if (file.isFile()) {
            file.delete();
        }
    }

    @Override
    public TerminalSystemUpgradePackageEntity getSystemUpgradePackage(UUID upgradePackageId) throws BusinessException {
        Assert.notNull(upgradePackageId, "upgradePackage");

        final Optional<TerminalSystemUpgradePackageEntity> upgradePackageOpt = termianlSystemUpgradePackageDAO.findById(upgradePackageId);
        if (!upgradePackageOpt.isPresent()) {
            LOGGER.error("刷机包不存在， 刷机包id: {}", upgradePackageId);
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_PACKAGE_NOT_EXIST);
        }
        return upgradePackageOpt.get();
    }


}

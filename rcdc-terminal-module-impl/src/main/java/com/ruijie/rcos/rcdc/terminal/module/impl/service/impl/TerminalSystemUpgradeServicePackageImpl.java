package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.SystemUpgradeDistributionModeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.SystemUpgradePackageOriginEnums;
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

        TerminalSystemUpgradePackageEntity upgradePackage =
                termianlSystemUpgradePackageDAO.findFirstByPackageType(versionInfo.getPackageType());
        if (upgradePackage == null) {
            upgradePackage = new TerminalSystemUpgradePackageEntity();
        }

        saveSystemUpgradePackage(upgradePackage, versionInfo);
    }

    private void saveSystemUpgradePackage(TerminalSystemUpgradePackageEntity upgradePackage,
            TerminalUpgradeVersionFileInfo versionInfo) {
        completeSystemUpgradePackageEntity(upgradePackage, versionInfo);
        termianlSystemUpgradePackageDAO.save(upgradePackage);
    }

    private void completeSystemUpgradePackageEntity(TerminalSystemUpgradePackageEntity upgradePackage,
            TerminalUpgradeVersionFileInfo versionInfo) {
        upgradePackage.setPackageName(versionInfo.getPackageName());
        upgradePackage.setImgName(versionInfo.getImgName());
        upgradePackage.setOrigin(SystemUpgradePackageOriginEnums.USER_UPLOAD);
        upgradePackage.setDistributionMode(SystemUpgradeDistributionModeEnums.FAST_UPGRADE);
        upgradePackage.setPackageType(versionInfo.getPackageType());
        upgradePackage.setPackageVersion(versionInfo.getVersion());
        upgradePackage.setUploadTime(new Date());
        upgradePackage.setFilePath(versionInfo.getFilePath());
    }

    @Override
    public List<TerminalSystemUpgradeInfo> readSystemUpgradeStateFromFile() throws BusinessException {
        // 读取升级成功文件夹，通过文件名称获取mac即终端id
        File upgradeSuccessDir = new File(Constants.TERMINAL_UPGRADE_END_SATTUS_FILE_PATH);
        if (!upgradeSuccessDir.isDirectory()) {
            LOGGER.error("terminal upgrade success status directory not exist, file path: {}",
                    Constants.TERMINAL_UPGRADE_END_SATTUS_FILE_PATH);
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_UPGRADE_SUCCESS_STATUS_DIRECTORY_NOT_EXIST);
        }

        List<TerminalSystemUpgradeInfo> upgradeInfoList = new ArrayList<>();
        String[] fileNameArr = upgradeSuccessDir.list();
        for (String fileName : fileNameArr) {
            String fileNameWithoutSuffix = getFileNameWithoutSuffix(fileName);
            TerminalSystemUpgradeInfo upgradeInfo = new TerminalSystemUpgradeInfo();
            upgradeInfo.setTerminalId(fileNameWithoutSuffix);
            upgradeInfo.setState(CbbSystemUpgradeStateEnums.SUCCESS);
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
    public TerminalSystemUpgradePackageEntity getSystemUpgradePackage(UUID upgradePackageId) throws BusinessException {
        Assert.notNull(upgradePackageId, "upgradePackage");

        final Optional<TerminalSystemUpgradePackageEntity> upgradePackageOpt =
                termianlSystemUpgradePackageDAO.findById(upgradePackageId);
        if (!upgradePackageOpt.isPresent()) {
            LOGGER.error("刷机包不存在， 刷机包id: {}", upgradePackageId);
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_PACKAGE_NOT_EXIST);
        }
        return upgradePackageOpt.get();
    }

}

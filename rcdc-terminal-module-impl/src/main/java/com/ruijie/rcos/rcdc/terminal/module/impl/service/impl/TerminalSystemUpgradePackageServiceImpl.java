package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

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
import com.ruijie.rcos.sk.base.filesystem.SkyengineFile;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.File;
import java.util.*;

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
public class TerminalSystemUpgradePackageServiceImpl implements TerminalSystemUpgradePackageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TerminalSystemUpgradePackageServiceImpl.class);

    @Autowired
    private TerminalSystemUpgradePackageDAO terminalSystemUpgradePackageDAO;

    @Override
    public void saveTerminalUpgradePackage(TerminalUpgradeVersionFileInfo versionInfo) throws BusinessException {
        Assert.notNull(versionInfo, "terminalUpgradeVersionFileInfo 不能为空");

        terminalSystemUpgradePackageDAO.save(buildSystemUpgradePackageEntity(versionInfo));
    }

    private TerminalSystemUpgradePackageEntity buildSystemUpgradePackageEntity(TerminalUpgradeVersionFileInfo versionInfo) {
        TerminalSystemUpgradePackageEntity upgradePackage = new TerminalSystemUpgradePackageEntity();

        upgradePackage.setPackageName(versionInfo.getPackageName());
        upgradePackage.setImgName(versionInfo.getImgName());
        upgradePackage.setOrigin(CbbSystemUpgradePackageOriginEnums.USER_UPLOAD);
        upgradePackage.setDistributionMode(CbbSystemUpgradeDistributionModeEnums.FAST_UPGRADE);
        upgradePackage.setPackageType(versionInfo.getPackageType());
        upgradePackage.setPackageVersion(versionInfo.getVersion());
        upgradePackage.setUploadTime(new Date());
        upgradePackage.setFilePath(versionInfo.getFilePath());
        upgradePackage.setFileMd5(versionInfo.getFileMD5());
        upgradePackage.setSeedPath(versionInfo.getSeedLink());
        upgradePackage.setSeedMd5(versionInfo.getSeedMD5());
        upgradePackage.setOtaScriptPath(versionInfo.getOtaScriptPath());
        upgradePackage.setOtaScriptMd5(versionInfo.getOtaScriptMD5());
        upgradePackage.setIsDelete(false);
        upgradePackage.setUpgradeMode(versionInfo.getUpgradeMode());

        upgradePackage.setCpuArch(versionInfo.getCpuArch());
        upgradePackage.setSupportCpu(versionInfo.getSupportCpu());

        return upgradePackage;
    }

    @Override
    public List<TerminalSystemUpgradeInfo> readSystemUpgradeSuccessStateFromFile() throws BusinessException {
        return getStatusFromFile(Constants.PXE_SAMBA_LINUX_VDI_UPGRADE_SUCCESS_FILE_PATH, CbbSystemUpgradeStateEnums.SUCCESS);
    }

    @Override
    public List<TerminalSystemUpgradeInfo> readSystemUpgradeStartStateFromFile() throws BusinessException {
        return getStatusFromFile(Constants.PXE_SAMBA_LINUX_VDI_UPGRADE_BEGIN_FILE_PATH, CbbSystemUpgradeStateEnums.UPGRADING);
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
            if (StringUtils.isBlank(fileNameWithoutSuffix)) {
                continue;
            }

            TerminalSystemUpgradeInfo upgradeInfo = new TerminalSystemUpgradeInfo();
            upgradeInfo.setTerminalId(fileNameWithoutSuffix.toLowerCase());
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
        terminalSystemUpgradePackageDAO.save(systemUpgradePackage);

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
            new SkyengineFile(file).delete(false);
        }
    }

    @Override
    public TerminalSystemUpgradePackageEntity getSystemUpgradePackage(UUID upgradePackageId) throws BusinessException {
        Assert.notNull(upgradePackageId, "upgradePackage");

        final Optional<TerminalSystemUpgradePackageEntity> upgradePackageOpt = terminalSystemUpgradePackageDAO.findById(upgradePackageId);
        if (!upgradePackageOpt.isPresent()) {
            LOGGER.error("刷机包不存在， 刷机包id: {}", upgradePackageId);
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_PACKAGE_NOT_EXIST);
        }
        return upgradePackageOpt.get();
    }


}

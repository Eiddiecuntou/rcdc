package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.systemupgrade;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.ruijie.rcos.base.sysmanage.module.def.dto.SeedFileInfoDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.UpgradeFileTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalUpgradeVersionFileInfo;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.BtClientService;
import com.ruijie.rcos.rcdc.terminal.module.impl.util.FileOperateUtil;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.base.util.StringUtils;
import com.ruijie.rcos.sk.base.zip.ZipUtil;

/**
 * Description: LINUX VDI的ZIP升级包处理类
 * Copyright: Copyright (c) 2021
 * Company: Ruijie Co., Ltd.
 * Create Time: 2021/8/25 13:51
 *
 * @author TING
 */
@Service
public class LinuxVDISystemUpgradeZipPackageResolver extends AbstractSystemUpgradePackageResolver {

    private static final Logger LOGGER = LoggerFactory.getLogger(LinuxVDISystemUpgradeZipPackageResolver.class);

    @Autowired
    private BtClientService btClientService;

    @Override
    protected CbbTerminalTypeEnums getTerminalType() {
        return CbbTerminalTypeEnums.VDI_LINUX;
    }


    public UpgradeFileTypeEnums getUpgradeFileType() {
        return UpgradeFileTypeEnums.ISO;
    }

    @Override
    protected void validatePackage(String fileName, String filePath) throws BusinessException {
        Assert.hasText(fileName, "fileName can not be null");
        Assert.hasText(filePath, "filePath can not be null");

        // 无需校验

    }

    @Override
    protected TerminalUpgradeVersionFileInfo readPackageConfig(String fileName, String filePath) throws BusinessException {
        TerminalUpgradeVersionFileInfo versionInfo;
        String unzipPath = getUnzipPath();
        try {

            // 校验目录
            checkAndCreateNecessaryDir(Lists.newArrayList(unzipPath));

            // 解压zip
            unZipUpgradePackage(filePath, unzipPath);

            // 读取校验文件内容
            String packageConfigFilePath = getVersionFilePath(unzipPath);
            versionInfo = getVersionInfo(packageConfigFilePath);
            versionInfo.setImgName(UUID.randomUUID().toString());
        } catch (Exception e) {
            LOGGER.error("check version file error", e);

            // 异常情况下需删除文件
            FileOperateUtil.deleteFile(new File(unzipPath));

            throw new BusinessException(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_PACKAGE_VERSION_FILE_INCORRECT, e);
        }

        // 校验包内读取的平台信息
        if (versionInfo.getPackageType() == null || StringUtils.isBlank(versionInfo.getVersion())) {
            LOGGER.debug("version file info error: {}", versionInfo.toString());
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_PACKAGE_VERSION_FILE_INCORRECT);
        }

        completeVersionInfo(fileName, unzipPath, versionInfo);

        return versionInfo;
    }

    private void unZipUpgradePackage(String filePath, String unzipPath) throws BusinessException {
        try {
            ZipUtil.unzipFile(new File(filePath), new File(unzipPath));
        } catch (IOException e) {
            LOGGER.error("解压ZIP包【" + filePath + "】异常", e);
            throw new BusinessException(BusinessKey.RCDC_FILE_OPERATE_FAIL, e);
        }
    }

    @Override
    protected void movePackage(String filePath, TerminalUpgradeVersionFileInfo versionInfo) throws BusinessException {
        Assert.notNull(versionInfo, "versionInfo can not be null");

        String imgFilePath = versionInfo.getUnzipPath() + File.separator + versionInfo.getRealFileName();
        String imgScriptPath = versionInfo.getUnzipPath() + File.separator + versionInfo.getOtaScriptFileName();
        moveUpgradePackage(versionInfo.getFilePath(), imgFilePath);
        moveUpgradePackage(versionInfo.getOtaScriptPath(), imgScriptPath);


        LOGGER.info("设置升级包脚本权限，路径【{}】", versionInfo.getOtaScriptPath());
        File imgScriptFile = new File(versionInfo.getOtaScriptPath());
        imgScriptFile.setReadable(true, false);
        imgScriptFile.setExecutable(true, false);

        FileOperateUtil.deleteFile(new File(filePath));
        FileOperateUtil.deleteFile(new File(versionInfo.getUnzipPath()));
    }

    @Override
    protected void lastStep(TerminalUpgradeVersionFileInfo versionInfo) throws BusinessException {
        Assert.notNull(versionInfo, "versionInfo can not be null");

        // 制作OTA包种子文件
        SeedFileInfoDTO seedInfo = btClientService.makeBtSeed(versionInfo.getFilePath(), Constants.TERMINAL_UPGRADE_LINUX_VDI_OTA_SEED_FILE);
        versionInfo.setSeedLink(seedInfo.getSeedFilePath());
        versionInfo.setSeedMD5(seedInfo.getSeedFileMD5());
    }

    private void completeVersionInfo(String fileName, String unzipPath, TerminalUpgradeVersionFileInfo versionInfo) throws BusinessException {

        File zipDir = new File(unzipPath);
        File[] fileArr = zipDir.listFiles();
        Assert.notEmpty(fileArr, "fileArr can not be empty");

        String imgFileName = "";
        String imgFilePath = "";
        String scriptFileName = "";
        String scriptFilePath = "";
        for (File file : fileArr) {
            if (file.getName().contains(Constants.FILE_TYPE_IMG_SUFFIX)) {
                imgFileName = file.getName();
                imgFilePath = file.getPath();
            }

            if (file.getName().contains(Constants.FILE_TYPE_BASH_SUFFIX)) {
                scriptFileName = file.getName();
                scriptFilePath = file.getPath();
            }
        }

        versionInfo.setPackageName(fileName);

        versionInfo.setRealFileName(imgFileName);
        versionInfo.setOtaScriptFileName(scriptFileName);
        versionInfo.setUnzipPath(unzipPath);
        versionInfo.setOtaScriptMD5(calFileMd5(scriptFilePath));
        // TODO 比对MD5值，校验
        String fileMD5 = calFileMd5(imgFilePath);
        versionInfo.setFileMD5(fileMD5);
        versionInfo.setUpgradeMode(DEFAULT_UPGRADE_MODE);
        versionInfo.setPackageType(CbbTerminalTypeEnums.VDI_LINUX);
        versionInfo.setFileSaveDir(Constants.TERMINAL_UPGRADE_VDI_OTA_PACKAGE_PATH);
        String realFileName = UUID.randomUUID() + Constants.FILE_SUFFIX_DOT + Constants.FILE_TYPE_IMG_SUFFIX;
        versionInfo.setFilePath(versionInfo.getFileSaveDir() + File.separator + realFileName);
        String scriptSaveDir = Constants.TERMINAL_UPGRADE_LINUX_VDI_OTA_SCRIPT_DIR + UUID.randomUUID();
        versionInfo.setOtaScriptPath(scriptSaveDir + File.separator + scriptFileName);

        checkAndCreateNecessaryDir(Lists.newArrayList(versionInfo.getFileSaveDir(), scriptSaveDir));
    }

    private String getUnzipPath() {
        return Constants.TERMINAL_UPGRADE_LINUX_VDI_ISO_MOUNT_PATH + UUID.randomUUID().toString();

    }

    private String getVersionFilePath(String unzipPath) {
        return unzipPath + Constants.TERMINAL_UPGRADE_VDI_ZIP_PACKAGE_VERSION_FILE_RELATE_PATH;
    }

}

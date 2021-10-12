package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.systemupgrade;

import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbCpuArchType;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalUpgradeVersionFileInfo;
import com.ruijie.rcos.rcdc.terminal.module.impl.util.FileOperateUtil;
import com.ruijie.rcos.sk.base.crypto.Md5Builder;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.io.IoUtil;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.base.util.StringUtils;
import com.ruijie.rcos.sk.base.zip.ZipUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Properties;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/10/10
 *
 * @author hs
 */
@Service
public class AndroidVDISystemUpgradePackageHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(AndroidVDISystemUpgradePackageHelper.class);

    /**
     * 解压文件
     *
     * @param zipfilePath ota包路径
     * @param savePackageName 保存文件名称
     * @return 保存路径
     * @throws BusinessException 业务异常
     */
    public String unZipPackage(String zipfilePath, String savePackageName) throws BusinessException {
        Assert.hasText(zipfilePath, "filePath can not be blank");
        Assert.hasText(savePackageName, "unZipFilePath can not be blank");

        String unZipFilePath = Constants.TERMINAL_UPGRADE_OTA_PACKAGE;
        File unZipFile = new File(unZipFilePath);
        String savePackagePath = unZipFilePath + savePackageName;
        File zipFile = new File(zipfilePath);
        FileOperateUtil.createFileDirectory(unZipFile);
        try {
            ZipUtil.unzipFile(zipFile, unZipFile);
        } catch (IOException e) {
            LOGGER.debug("version file read error, file path[{}]", zipFile);
            throw new BusinessException(BusinessKey.RCDC_FILE_OPERATE_FAIL, e);
        }

        // 校验安卓ota升级zip文件是否存在
        File rainrcdFile = new File(Constants.TERMINAL_UPGRADE_OTA_PACKAGE_ZIP);
        if (!rainrcdFile.exists()) {
            LOGGER.error("上传的ota升级包不合法，升级文件不存在");
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_OTA_UPGRADE_PACKAGE_ILLEGAL);
        }

        File savePackageFile = new File(savePackagePath);
        try {
            IoUtil.copy(rainrcdFile, savePackageFile);
            Files.deleteIfExists(rainrcdFile.toPath());
        } catch (IOException e) {
            LOGGER.debug("move upgrade file to target directory fail");
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_OTA_UPGRADE_PACKAGE_MOVE_FAIL, e);
        }

        return savePackagePath;

    }

    /**
     * 校验版本
     *
     * @param packagePath zip包路径
     * @param versionPath 版本文件路径
     * @return OTA版本信息
     * @throws BusinessException 业务异常
     */
    public TerminalUpgradeVersionFileInfo checkVersionInfo(String packagePath, String versionPath) throws BusinessException {
        Assert.hasText(packagePath, "packagePath can not be blank");
        Assert.hasText(versionPath, "versionPath can not be blank");

        Properties prop = new Properties();

        try (InputStream inputStream = new FileInputStream(versionPath)) {
            prop.load(inputStream);
        } catch (IOException e) {
            LOGGER.debug("version file read error, file path[{}]", versionPath);
            throw new BusinessException(BusinessKey.RCDC_FILE_OPERATE_FAIL, e);
        }
        String fileMD5 = prop.getProperty(Constants.TERMINAL_UPGRADE_OTA_VERSION_FILE_KEY_PACKAGE_MD5);
        String platType = prop.getProperty(Constants.TERMINAL_UPGRADE_OTA_VERSION_FILE_KEY_PACKAGE_PLAT);
        String version = prop.getProperty(Constants.TERMINAL_UPGRADE_OTA_VERSION_FILE_KEY_PACKAGE_VERSION);
        // 校验OTA包
        checkOtaUpgradePackage(platType, fileMD5, packagePath);
        TerminalUpgradeVersionFileInfo upgradeInfo = new TerminalUpgradeVersionFileInfo();
        upgradeInfo.setFileMD5(fileMD5);
        upgradeInfo.setVersion(version);

        // 获取架构及支持cpu
        String cpuArch = prop.getProperty(Constants.TERMINAL_UPGRADE_PACKAGE_VERSION_FILE_KEY_CPU_ARCH);
        upgradeInfo.setCpuArch(StringUtils.isBlank(cpuArch) ? CbbCpuArchType.ARM : CbbCpuArchType.convert(cpuArch));

        String cpu = prop.getProperty(Constants.TERMINAL_UPGRADE_PACKAGE_VERSION_FILE_KEY_CPU);
        upgradeInfo.setSupportCpu(StringUtils.isBlank(cpu) ? Constants.TERMINAL_SYSTEM_UPGRADE_CPU_SUPPORT_ALL : cpu);

        return upgradeInfo;
    }

    private void checkOtaUpgradePackage(String platType, String fileMD5, String packagePath) throws BusinessException {
        Assert.notNull(platType, "platType can not be null");
        Assert.notNull(fileMD5, "fileMD5 can not be null");
        Assert.notNull(packagePath, "packagePath can not be null");
        String packageMD5 = generateFileMD5(packagePath);
        if (!fileMD5.equals(packageMD5) || !platType.equals(Constants.TERMINAL_UPGRADE_OTA_PLATFORM_TYPE)) {
            LOGGER.error("terminal ota upgrade package has error, fileMD5[{}], platType[{}]", fileMD5, platType);
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_OTA_UPGRADE_PACKAGE_HAS_ERROR);
        }

    }

    /**
     * 计算MD5值
     */
    private String generateFileMD5(String filePath) throws BusinessException {

        File seedFile = new File(filePath);
        String seedMD5 = null;
        try {
            seedMD5 = StringUtils.bytes2Hex(Md5Builder.computeFileMd5(seedFile));
        } catch (IOException e) {
            LOGGER.error("compute seed file md5 fail, seed file path[{}]", filePath);
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_OTA_UPGRADE_COMPUTE_SEED_FILE_MD5_FAIL, e);
        }
        return seedMD5;

    }

}

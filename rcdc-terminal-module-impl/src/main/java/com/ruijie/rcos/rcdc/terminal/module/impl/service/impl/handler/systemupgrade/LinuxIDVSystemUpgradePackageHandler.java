package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.systemupgrade;

import java.io.File;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.ruijie.rcos.base.sysmanage.module.def.dto.SeedFileInfoDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeModeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbCpuArchType;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalPlatformEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.UpgradeFileTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalUpgradeVersionFileInfo;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.BtClientService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalSystemUpgradePackageService;
import com.ruijie.rcos.rcdc.terminal.module.impl.util.FileOperateUtil;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.base.util.IsoFileUtil;
import com.ruijie.rcos.sk.base.util.StringUtils;

/**
 * Description: Linux IDV 终端系统升级包处理类
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/2/12 15:52
 *
 * @author zhangyichi
 */
@Service
public class LinuxIDVSystemUpgradePackageHandler extends AbstractSystemUpgradePackageHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(LinuxIDVSystemUpgradePackageHandler.class);

    private static final CbbSystemUpgradeModeEnums DEFAULT_UPGRADE_MODE = CbbSystemUpgradeModeEnums.MANUAL;

    /**
     * OTA文件列表长度
     */
    private static final Integer OTA_FILE_LIST_SIZE = 2;

    /**
     * OTA包文件名
     */
    private static final String OTA_PACKAGE_NAME = "rainos.sqfs";

    /**
     * OTA脚本文件名
     */
    private static final String OTA_SCRIPT_NAME = "OTAPreRunFun.bash";

    @Autowired
    private TerminalSystemUpgradePackageService terminalSystemUpgradePackageService;

    @Autowired
    private LinuxIDVSystemUpgradePackageHelper helper;

    @Autowired
    private BtClientService btClientService;

    @Override
    protected TerminalSystemUpgradePackageService getSystemUpgradePackageService() {
        return terminalSystemUpgradePackageService;
    }

    @Override
    public void preUploadPackage() {
        LOGGER.info("Linux IDV系统升级包无需上传前处理流程");
    }

    @Override
    public void postUploadPackage() {
        LOGGER.info("Linux IDV系统升级包无需上传后处理流程");
    }

    @Override
    public String getUpgradePackageFileDir() {
        return Constants.TERMINAL_UPGRADE_OTA_LINUX_IDV_AND_ANDROID_VDI_DIR;
    }

    @Override
    protected CbbTerminalTypeEnums getPackageType() {
        return CbbTerminalTypeEnums.IDV_LINUX;
    }

    @Override
    protected TerminalUpgradeVersionFileInfo getPackageInfo(String fileName, String filePath) throws BusinessException {
        Assert.hasText(fileName, "fileName must have text!");
        Assert.hasText(filePath, "filePath must have text!");

        // 校验ISO文件
        checkFileType(fileName);
        checkISOMd5(filePath);

        // 解析ISO文件
        String mountPath = getISOMountPath();
        checkAndMakeDirs(mountPath);
//        prepareDirectories();
        IsoFileUtil.mountISOFile(filePath, mountPath);
        TerminalUpgradeVersionFileInfo versionInfo = new TerminalUpgradeVersionFileInfo();
        versionInfo.setMountPath(mountPath);
        readVersionFile(versionInfo);
        readOtaList(versionInfo);

        // 取消ISO挂载、删除（此处不删除出厂自带的ISO）
        IsoFileUtil.unmountISOFile(mountPath);
        if (!filePath.contains(Constants.TERMINAL_UPGRADE_LINUX_IDV_OTA)) {
            FileOperateUtil.deleteFile(new File(filePath));
        }

        // 制作OTA包种子文件
        String storeSeedDir = Constants.TERMINAL_UPGRADE_LINUX_IDV_OTA_SEED_FILE;
        checkAndMakeDirs(storeSeedDir);
        SeedFileInfoDTO seedInfo =
                btClientService.makeBtSeed(versionInfo.getFilePath(), storeSeedDir);

        // 组装升级包信息
        versionInfo.setPackageType(CbbTerminalTypeEnums.IDV_LINUX);
        versionInfo.setPackageName(fileName);
        versionInfo.setSeedLink(seedInfo.getSeedFilePath());
        versionInfo.setSeedMD5(seedInfo.getSeedFileMD5());
        versionInfo.setUpgradeMode(DEFAULT_UPGRADE_MODE);
        return versionInfo;
    }

    private String getISOMountPath() {
        return Constants.TERMINAL_UPGRADE_LINUX_IDV_ISO_MOUNT_PATH + UUID.randomUUID();
    }

    private void checkFileType(String fileName) throws BusinessException {
        String fileType = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        // 文件类型校验
        if (!UpgradeFileTypeEnums.contains(fileType)) {
            LOGGER.debug("terminal system upgrade file type error, file name [{}] ", fileName);
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_UPLOAD_FILE_TYPE_ERROR);
        }
        LOGGER.debug("file type [{}] is correct", fileType);
    }

    private void readVersionFile(TerminalUpgradeVersionFileInfo versionInfo) throws BusinessException {
        String versionFilePath = getVersionFilePath(versionInfo.getMountPath());
        Properties prop = helper.getVersionProperties(versionFilePath);

        String platType = prop.getProperty(Constants.TERMINAL_UPGRADE_PACKAGE_VERSION_FILE_KEY_PACKAGE_TYPE);
        if (!StringUtils.hasText(platType) || !platType.equals(CbbTerminalPlatformEnums.IDV.name())) {
            LOGGER.debug("升级包类型错误，期望[IDV]，实际[{}]", platType);
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_UPLOAD_FILE_PACKAGE_TYPE_ERROR,
                    CbbTerminalPlatformEnums.IDV.name(), platType);
        }

        versionInfo.setVersion(prop.getProperty(Constants.TERMINAL_UPGRADE_PACKAGE_VERSION_FILE_KEY_VERSION));

        // 获取架构及支持cpu
        String cpuArch = prop.getProperty(Constants.TERMINAL_UPGRADE_PACKAGE_VERSION_FILE_KEY_CPU_ARCH);
        versionInfo.setCpuArch(StringUtils.isBlank(cpuArch) ? CbbCpuArchType.X86_64 : CbbCpuArchType.convert(cpuArch));

        String cpu = prop.getProperty(Constants.TERMINAL_UPGRADE_PACKAGE_VERSION_FILE_KEY_CPU);
        versionInfo.setSupportCpu(StringUtils.isBlank(cpu) ? Constants.TERMINAL_SYSTEM_UPGRADE_CPU_SUPPORT_ALL : cpu);
    }

    private String getVersionFilePath(String mountPath) {
        return mountPath + Constants.TERMINAL_UPGRADE_ISO_VERSION_FILE_PATH;
    }

    private void readOtaList(TerminalUpgradeVersionFileInfo versionInfo) throws BusinessException {
        String mountPath = versionInfo.getMountPath();
        String otaListPath = getOtaListPath(mountPath);

        // 读取OTA文件列表
        List<String> otaFileList = helper.getOtaFilesInfo(otaListPath);

        Assert.isTrue(otaFileList.size() == OTA_FILE_LIST_SIZE, "ota file list is invalid!");
        for (String otaListItem : otaFileList) {
            if (otaListItem.endsWith(OTA_PACKAGE_NAME)) {
                // 解析OTA包文件信息，并复制到对应目录
                String storePackagePath = Constants.TERMINAL_UPGRADE_LINUX_IDV_OTA_PACKAGE_DIR;
                checkAndMakeDirs(storePackagePath);
                LinuxIDVSystemUpgradePackageHelper.OtaFileInfo otaFileInfo =
                        helper.handleOtaListItem(otaListItem, storePackagePath, OTA_PACKAGE_NAME, mountPath);
                versionInfo.setFileMD5(otaFileInfo.getMd5());
                versionInfo.setFilePath(otaFileInfo.getFilePath());
                versionInfo.setFileSaveDir(storePackagePath);
                versionInfo.setRealFileName(OTA_PACKAGE_NAME);

            } else if (otaListItem.endsWith(OTA_SCRIPT_NAME)) {
                // 解析OTA脚本文件信息，并复制到对应目录
                String scriptStoreDir = Constants.TERMINAL_UPGRADE_LINUX_IDV_OTA_SCRIPT_DIR;
                checkAndMakeDirs(scriptStoreDir);
                LinuxIDVSystemUpgradePackageHelper.OtaFileInfo otaFileInfo =
                        helper.handleOtaListItem(otaListItem, scriptStoreDir, OTA_SCRIPT_NAME, mountPath);
                versionInfo.setOtaScriptMD5(otaFileInfo.getMd5());
                versionInfo.setOtaScriptPath(otaFileInfo.getFilePath());

            } else {
                LOGGER.error("ota file list content is incorrect!");
                throw new BusinessException(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_PACKAGE_OTA_LIST_INCORRECT);
            }
        }
    }

    private String getOtaListPath(String mountPath) {
        return mountPath + Constants.TERMINAL_UPGRADE_LINUX_IDV_OTA_LIST_PATH;
    }

    private void checkAndMakeDirs(String dirPath) {
        File dir = new File(dirPath);
        if (!dir.isDirectory()) {
            FileOperateUtil.createFileDirectory(dir);
        }
    }
}

package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.systemupgrade;

import com.google.common.collect.Lists;
import com.ruijie.rcos.base.sysmanage.module.def.dto.SeedFileInfoDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalPlatformEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.UpgradeFileTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalUpgradeVersionFileInfo;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalSystemUpgradePackageService;
import com.ruijie.rcos.rcdc.terminal.module.impl.util.FileOperateUtil;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.base.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.io.*;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

/**
 * Description: Linux IDV 终端系统升级包处理类
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/2/12 15:52
 *
 * @author zhangyichi
 */
public class LinuxIDVSystemUpgradePackageHandler extends AbstractSystemUpgradePackageHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(LinuxIDVSystemUpgradePackageHandler.class);

    /**
     * OTA文件列表长度
     */
    private static final Integer OTA_FILE_LIST_SIZE = 2;

    /**
     * OTA包文件名
     */
    private static final String OTA_PACKAGE_NAME = "rainos-img.squashfs";

    /**
     * OTA脚本文件名
     */
    private static final String OTA_SCRIPT_NAME = "OTAPreRunFun.bash";

    /**
     * OTA文件列表分隔符
     */
    private static final String OTA_LIST_SEPARATOR = " ";

    private static final Integer FILE_MD5_INDEX = 0;

    private static final Integer FILE_PATH_INDEX = 1;

    @Autowired
    private TerminalSystemUpgradePackageService terminalSystemUpgradePackageService;

    @Override
    protected TerminalSystemUpgradePackageService getSystemUpgradePackageService() {
        return terminalSystemUpgradePackageService;
    }

    @Override
    protected TerminalUpgradeVersionFileInfo getPackageInfo(String fileName, String filePath) throws BusinessException {
        Assert.hasText(fileName, "fileName must have text!");
        Assert.hasText(filePath, "filePath must have text!");

        // 校验ISO文件
        checkFileType(fileName);
        checkISOMd5(filePath);

        // 解析ISO文件
        prepareDirectories();
        mountUpgradePackage(fileName, Constants.TERMINAL_UPGRADE_LINUX_IDV_ISO_MOUNT_PATH);
        TerminalUpgradeVersionFileInfo versionInfo = new TerminalUpgradeVersionFileInfo();
        readVersionFile(versionInfo);
        readOtaList(versionInfo);

        // 取消ISO挂载、删除
        umountUpgradePackage(Constants.TERMINAL_UPGRADE_LINUX_IDV_ISO_MOUNT_PATH);
        FileOperateUtil.deleteFile(new File(filePath));

        // 制作OTA包种子文件
        SeedFileInfoDTO seedInfo = makeBtSeed(versionInfo.getFilePath(), Constants.TERMINAL_UPGRADE_LINUX_IDV_OTA_SEED_FILE);

        // 组装升级包信息
        versionInfo.setPackageType(CbbTerminalTypeEnums.IDV_LINUX);
        versionInfo.setPackageName(OTA_PACKAGE_NAME);
        versionInfo.setSeedLink(seedInfo.getSeedFilePath());
        versionInfo.setSeedMD5(seedInfo.getSeedFileMD5());
        return versionInfo;
    }

    private void prepareDirectories() {
        checkAndMakeDirs(Constants.TERMINAL_UPGRADE_LINUX_IDV_ISO_MOUNT_PATH);
        checkAndMakeDirs(Constants.TERMINAL_UPGRADE_LINUX_IDV_OTA_PACKAGE);
        checkAndMakeDirs(Constants.TERMINAL_UPGRADE_LINUX_IDV_OTA_SCRIPT_FILE);
    }

    private void checkAndMakeDirs(String dirPath) {
        File dir = new File(dirPath);
        if (!dir.isDirectory()) {
            dir.mkdirs();
        }
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
        String versionFilePath = Constants.TERMINAL_UPGRADE_LINUX_IDV_ISO_MOUNT_PATH + Constants.TERMINAL_UPGRADE_ISO_VERSION_FILE_PATH;
        Properties prop = new Properties();
        try (InputStream in = new FileInputStream(versionFilePath)) {
            prop.load(in);
        } catch (FileNotFoundException e) {
            LOGGER.debug("version file not found, file path[{}]", versionFilePath);
            throw new BusinessException(BusinessKey.RCDC_FILE_NOT_EXIST, e);
        } catch (IOException e) {
            LOGGER.debug("version file read error, file path[{}]", versionFilePath);
            throw new BusinessException(BusinessKey.RCDC_FILE_OPERATE_FAIL, e);
        }

        CbbTerminalPlatformEnums platType = CbbTerminalPlatformEnums.
                valueOf(prop.getProperty(Constants.TERMINAL_UPGRADE_ISO_VERSION_FILE_KEY_PACKAGE_TYPE));
        if (platType != CbbTerminalPlatformEnums.IDV) {
            LOGGER.debug("升级包类型错误，期望[IDV]，实际[{}]", platType);
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_UPLOAD_FILE_PACKAGE_TYPE_ERROR,
                    CbbTerminalPlatformEnums.IDV.name(), platType.name());
        }

        versionInfo.setVersion(prop.getProperty(Constants.TERMINAL_UPGRADE_ISO_VERSION_FILE_KEY_VERSION));
    }

    private void readOtaList(TerminalUpgradeVersionFileInfo versionInfo) throws BusinessException {
        String otaListPath = Constants.TERMINAL_UPGRADE_LINUX_IDV_ISO_MOUNT_PATH + Constants.TERMINAL_UPGRADE_LINUX_IDV_OTA_LIST_PATH;

        // 读取OTA文件列表
        List<String> otaFileList = Lists.newArrayList();
        try (FileReader reader = new FileReader(otaListPath);
             BufferedReader buffer = new BufferedReader(reader)) {
            String line;
            if ((line = buffer.readLine()) != null && StringUtils.isNotBlank(line)) {
                otaFileList.add(line.trim());
            }
        } catch (FileNotFoundException e) {
            LOGGER.debug("ota list file not found, file path[{}]", otaListPath);
            throw new BusinessException(BusinessKey.RCDC_FILE_NOT_EXIST, e);
        } catch (IOException e) {
            LOGGER.debug("ota list file read error, file path[{}]", otaListPath);
            throw new BusinessException(BusinessKey.RCDC_FILE_OPERATE_FAIL, e);
        }

        Assert.isTrue(otaFileList.size() == OTA_FILE_LIST_SIZE, "ota file list is invalid!");
        for (String otaListItem : otaFileList) {
            if (otaListItem.endsWith(OTA_PACKAGE_NAME)) {
                // 解析OTA包文件信息，并复制到对应目录
                String[] messagesArr = handleOtaListItem(otaListItem, Constants.TERMINAL_UPGRADE_LINUX_IDV_OTA_PACKAGE, OTA_PACKAGE_NAME);
                versionInfo.setFileMD5(messagesArr[FILE_MD5_INDEX]);
                versionInfo.setFilePath(messagesArr[FILE_PATH_INDEX]);
                versionInfo.setFileSaveDir(Constants.TERMINAL_UPGRADE_LINUX_IDV_OTA_PACKAGE);
                versionInfo.setRealFileName(OTA_PACKAGE_NAME);
            } else if (otaListItem.endsWith(OTA_SCRIPT_NAME)) {
                // 解析OTA脚本文件信息，并复制到对应目录
                String[] messagesArr = handleOtaListItem(otaListItem, Constants.TERMINAL_UPGRADE_LINUX_IDV_OTA_SCRIPT_FILE, OTA_SCRIPT_NAME);
                versionInfo.setOtaScriptMD5(messagesArr[FILE_MD5_INDEX]);
                versionInfo.setOtaScriptPath(messagesArr[FILE_PATH_INDEX]);
            } else {
                LOGGER.error("ota file list content is incorrect!");
                throw new BusinessException(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_PACKAGE_OTA_LIST_INCORRECT);
            }
        }
    }

    private String[] handleOtaListItem(String otaListItem, String fileSaveDirPath, String fileName) throws BusinessException {
        // 获取文件MD5、路径
        String[] messagesArr = resolveOtaListItem(otaListItem);

        File srcFile = new File(Constants.TERMINAL_UPGRADE_LINUX_IDV_ISO_MOUNT_PATH + messagesArr[FILE_PATH_INDEX]);
        if (!srcFile.isFile()) {
            LOGGER.error("ISO file is incomplete, [{}] not found!", srcFile.getPath());
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_PACKAGE_FILE_INCOMPLETE, srcFile.getPath());
        }

        // 将文件从ISO中复制到对应文件夹
        File destFile = copyAndCoverFile(fileSaveDirPath, fileName, srcFile);

        messagesArr[FILE_PATH_INDEX] = destFile.getPath();
        return messagesArr;
    }

    private File copyAndCoverFile(String destFileSaveDir, String fileName, File srcFile) throws BusinessException {
        if (!checkDiskSpaceIsEnough(srcFile.length(), destFileSaveDir)) {
            LOGGER.error("disk space not enough, required space: {}bytes", srcFile.length());
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_UPGRADE_PACKAGE_DISK_SPACE_NOT_ENOUGH);
        }

        String saveFileName = UUID.randomUUID().toString() + fileName.substring(fileName.lastIndexOf("."));
        File destFile = new File(destFileSaveDir + saveFileName);
        try {
            FileOperateUtil.copyfile(srcFile.getPath(), destFile.getPath());
            Assert.isTrue(destFile.exists(), "destination file not exist!");
        } catch (Exception e) {
            LOGGER.error("error in copy file!", e);
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_UPLOAD_FILE_FAIL, e);
        }
        return destFile;
    }

    private String[] resolveOtaListItem(String fileInfo) throws BusinessException {
        String fileMD5;
        String filePathInIso;
        try {
            String[] messagesArr = fileInfo.split(OTA_LIST_SEPARATOR);
            fileMD5 = messagesArr[0];
            filePathInIso = messagesArr[messagesArr.length - 1];
            Assert.isTrue(!fileMD5.equals(filePathInIso), "ota file list is invalid!");
        } catch (Exception e) {
            LOGGER.error("ota file list content is incorrect!");
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_PACKAGE_OTA_LIST_INCORRECT, e);
        }
        return new String[]{fileMD5, filePathInIso};
    }

}

package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.systemupgrade;

import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.util.FileOperateUtil;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import org.springframework.util.Assert;

import java.io.File;
import java.util.UUID;

/**
 * Description: IDV终端系统升级包上传帮助类
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/2/17 21:54
 *
 * @author zhangyichi
 */
public class LinuxIDVSystemUpgradePackageHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(LinuxIDVSystemUpgradePackageHelper.class);

    /**
     * OTA文件列表分隔符
     */
    private static final String OTA_LIST_SEPARATOR = " ";

    /**
     * 解析OTA文件列表项，并复制到指定目录
     * @param otaListItem OTA文件列表项
     * @param fileSaveDirPath 目标目录
     * @param fileName 目标文件名
     * @return 文件当前路径、MD5
     * @throws BusinessException 业务异常
     */
    OtaFileInfo handleOtaListItem(String otaListItem, String fileSaveDirPath, String fileName) throws BusinessException {
        // 获取文件MD5、路径
        OtaFileInfo otaFileInfo = resolveOtaListItem(otaListItem);

        File srcFile = new File(Constants.TERMINAL_UPGRADE_LINUX_IDV_ISO_MOUNT_PATH + otaFileInfo.getFilePath());
        if (!srcFile.isFile()) {
            LOGGER.error("ISO file is incomplete, [{}] not found!", srcFile.getPath());
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_PACKAGE_FILE_INCOMPLETE, srcFile.getPath());
        }

        // 将文件从ISO中复制到对应文件夹
        File destFile = copyAndCoverFile(srcFile, fileSaveDirPath, fileName);

        otaFileInfo.setFilePath(destFile.getPath());
        return otaFileInfo;
    }

    private OtaFileInfo resolveOtaListItem(String fileInfo) throws BusinessException {
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
        return new OtaFileInfo(filePathInIso, fileMD5);
    }

    private File copyAndCoverFile(File srcFile, String destFileSaveDir, String fileName) throws BusinessException {
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

    private boolean checkDiskSpaceIsEnough(Long expectedFileSize, String targetDirPath) {
        File packageDir = new File(targetDirPath);
        final long usableSpace = packageDir.getUsableSpace();
        return usableSpace >= expectedFileSize;
    }

    /**
     * OTA列表解析出的文件信息
     */
    static class OtaFileInfo {
        private String filePath;

        private String md5;

        OtaFileInfo(String filePath, String md5) {
            this.filePath = filePath;
            this.md5 = md5;
        }

        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }

        public String getMd5() {
            return md5;
        }

        public void setMd5(String md5) {
            this.md5 = md5;
        }
    }
}

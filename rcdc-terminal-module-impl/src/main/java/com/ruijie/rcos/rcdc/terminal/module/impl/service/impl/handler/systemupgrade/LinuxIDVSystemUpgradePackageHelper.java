package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.systemupgrade;

import com.google.common.collect.Lists;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.util.FileOperateUtil;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.base.util.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.*;
import java.util.List;
import java.util.Properties;

/**
 * Description: IDV终端系统升级包上传帮助类
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/2/17 21:54
 *
 * @author zhangyichi
 */
@Service
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
     * @param mountPath iso挂载路径
     * @return 文件当前路径、MD5
     * @throws BusinessException 业务异常
     */
    OtaFileInfo handleOtaListItem(String otaListItem, String fileSaveDirPath, String fileName, String mountPath) throws BusinessException {
        // 获取文件MD5、路径
        OtaFileInfo otaFileInfo = resolveOtaListItem(otaListItem);

        File srcFile = new File(mountPath + File.separator + otaFileInfo.getFilePath());
        if (!srcFile.isFile()) {
            LOGGER.error("ISO file is incomplete, [{}] not found!", srcFile.getPath());
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_PACKAGE_FILE_INCOMPLETE, srcFile.getPath());
        }

        // 将文件从ISO中复制到对应文件夹，并设置权限
        File destFile = copyAndCoverFile(srcFile, fileSaveDirPath, fileName);
        destFile.setReadable(true, false);
        destFile.setExecutable(true, false);

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

        File destFile = new File(destFileSaveDir + File.separator + fileName);
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
     * 读取升级包版本文件
     * @param versionFilePath 版本文件路径
     * @return 升级包版本信息
     * @throws BusinessException 业务异常
     */
    Properties getVersionProperties(String versionFilePath) throws BusinessException {
        Properties prop = new Properties();
        try (InputStream in = new FileInputStream(versionFilePath)) {
            prop.load(in);
        } catch (IOException e) {
            LOGGER.error("version file read error, file path: " + versionFilePath, e);
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_PACKAGE_VERSION_FILE_ERROR, e);
        }
        return prop;
    }

    /**
     * 读取OTA文件列表信息
     * @param otaListPath 文件列表路径
     * @return 文件列表项
     * @throws BusinessException 业务异常
     */
    List<String> getOtaFilesInfo(String otaListPath) throws BusinessException {
        List<String> otaFileList = Lists.newArrayList();
        try (FileReader reader = new FileReader(otaListPath);
             BufferedReader buffer = new BufferedReader(reader)) {
            String line;
            while ((line = buffer.readLine()) != null && StringUtils.isNotBlank(line)) {
                otaFileList.add(line.trim());
            }
        } catch (IOException e) {
            LOGGER.error("ota list file read error, file path: " + otaListPath, e);
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_PACKAGE_OTA_FILE_ERROR, e);
        }
        return otaFileList;
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

package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.systemupgrade;

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
import com.ruijie.rcos.sk.base.zip.ZipUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Properties;
import java.util.UUID;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/10/10
 *
 * @author hs
 */
@Service
public class LinuxVDISystemUpgradePackageHandler extends AbstractSystemUpgradePackageHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(LinuxVDISystemUpgradePackageHandler.class);

    private static final CbbSystemUpgradeModeEnums DEFAULT_UPGRADE_MODE = CbbSystemUpgradeModeEnums.MANUAL;

    @Autowired
    private TerminalSystemUpgradePackageService terminalSystemUpgradePackageService;

    @Autowired
    private BtClientService btClientService;

    @Override
    protected TerminalSystemUpgradePackageService getSystemUpgradePackageService() {
        return terminalSystemUpgradePackageService;
    }

    @Override
    public void preUploadPackage() {
        LOGGER.info("Linux VDI系统升级包无需上传前处理流程");
    }

    @Override
    public void postUploadPackage() {
        LOGGER.info("Linux VDI系统升级包无需上传后处理流程");
    }

    @Override
    public String getUpgradePackageFileDir() {
        return Constants.PXE_SAMBA_PACKAGE_PATH;
    }

    @Override
    protected TerminalUpgradeVersionFileInfo getPackageInfo(String fileName, String filePath) throws BusinessException {
        Assert.notNull(fileName, "fileName can not be null");
        Assert.notNull(filePath, "filePath can not be null");

        // TODO 需优化
        // 校验文件类型
        boolean isCorrectType = checkFileType(fileName);
        if (!isCorrectType) {
            LOGGER.debug("terminal system upgrade file type error, file name [{}] ", fileName);
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_UPLOAD_FILE_TYPE_ERROR);
        }

        String fileType = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        if (fileType.toLowerCase().equals(UpgradeFileTypeEnums.ISO.getFileType())) {
            //使用checkisomd5校验升级包
            checkISOMd5(filePath);
        }

        String mountPath = Constants.TERMINAL_UPGRADE_ISO_MOUNT_PATH + UUID.randomUUID().toString();
        checkNecessaryDirExist(mountPath);

        // 读取ISO升级包中的升级信息
        TerminalUpgradeVersionFileInfo versionInfo = readPackageInfo(fileName, filePath, mountPath);

        // 将新升级文件移动到目录下
        String storePackageName = UUID.randomUUID() + fileName.substring(fileName.lastIndexOf("."));

        String toPath;
        if (fileType.toLowerCase().equals(UpgradeFileTypeEnums.ISO.getFileType())) {
            toPath = Constants.PXE_SAMBA_LINUX_VDI_ISO_PATH + storePackageName;
            versionInfo.setFileSaveDir(Constants.PXE_SAMBA_LINUX_VDI_ISO_PATH);
        } else {
            toPath = Constants.TERMINAL_UPGRADE_ISO_PATH_VDI + storePackageName;
            versionInfo.setFileSaveDir(Constants.TERMINAL_UPGRADE_ISO_PATH_VDI);
        }

        final String packagePath = moveUpgradePackage(toPath, filePath);

        if (fileType.toLowerCase().equals(UpgradeFileTypeEnums.ZIP.getFileType())) {
            // 制作OTA包种子文件
            SeedFileInfoDTO seedInfo = btClientService.makeBtSeed(packagePath, Constants.TERMINAL_UPGRADE_LINUX_VDI_OTA_SEED_FILE);
            versionInfo.setSeedLink(seedInfo.getSeedFilePath());
            versionInfo.setSeedMD5(seedInfo.getSeedFileMD5());
        }

        // 更新升级包信息入库
        versionInfo.setFilePath(packagePath);
        versionInfo.setPackageType(CbbTerminalTypeEnums.VDI_LINUX);
        versionInfo.setRealFileName(storePackageName);
        versionInfo.setUpgradeMode(DEFAULT_UPGRADE_MODE);
        return versionInfo;
    }

    private void checkNecessaryDirExist(String mountPath) {
        // iso挂载路径
        File mountDir = new File(mountPath);
        if (!mountDir.isDirectory()) {
            mountDir.mkdirs();
        }

        // linux ISO存放路径
        File linuxVDIPackageDir = new File(Constants.PXE_SAMBA_LINUX_VDI_ISO_PATH);
        if (!linuxVDIPackageDir.isDirectory()) {
            linuxVDIPackageDir.mkdirs();
        }

        // linux ISO存放路径
        File linuxArmVDIPackageDir = new File(Constants.TERMINAL_UPGRADE_ISO_PATH_VDI);
        if (!linuxArmVDIPackageDir.isDirectory()) {
            linuxArmVDIPackageDir.mkdirs();
        }

    }

    private TerminalUpgradeVersionFileInfo readPackageInfo(String fileName, String filePath, String mountPath) throws BusinessException {
        Assert.hasText(fileName, "fileName can not be null");
        Assert.hasText(filePath, "filePath can not be null");
        Assert.hasText(mountPath, "mountPath can not be null");

        String fileType = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();

        TerminalUpgradeVersionFileInfo versionInfo = null;
        if (fileType.toLowerCase().equals(UpgradeFileTypeEnums.ISO.getFileType())) {
            versionInfo = getISOVersionInfo(filePath, mountPath);
        } else {
            versionInfo = getZipVersionInfo(fileName, filePath, mountPath);
        }


        versionInfo.setPackageName(fileName);
        return versionInfo;
    }

    private TerminalUpgradeVersionFileInfo getZipVersionInfo(String fileName, String filePath, String mountPath) throws BusinessException {

        try {
            ZipUtil.unzipFile(new File(filePath), new File(mountPath));
        } catch (IOException e) {
            throw new BusinessException(BusinessKey.RCDC_FILE_OPERATE_FAIL);
        }


        TerminalUpgradeVersionFileInfo versionInfo;
        try {
            // 读取校验文件内容
            versionInfo = checkZipVersionFile(fileName, mountPath);
        } catch (Exception e) {
            LOGGER.error("check version file error", e);
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_PACKAGE_VERSION_FILE_INCORRECT, e);
        } finally {
            // 删除文件
            FileOperateUtil.deleteFile(new File(mountPath));

        }

        return versionInfo;
    }

    private TerminalUpgradeVersionFileInfo checkZipVersionFile(String fileName, String mountPath) throws BusinessException {
        // 获取升级文件信息
        String filePath = mountPath + "/" + fileName.substring(0, fileName.lastIndexOf(".")) + "/version";
        TerminalUpgradeVersionFileInfo verInfo = getVersionInfo(mountPath, filePath);
        if (verInfo.getPackageType() == null || StringUtils.isBlank(verInfo.getImgName()) || StringUtils.isBlank(verInfo.getVersion())) {
            LOGGER.debug("version file info error: {}", verInfo.toString());
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_PACKAGE_VERSION_FILE_INCORRECT);
        }

        return verInfo;
    }

    private TerminalUpgradeVersionFileInfo getISOVersionInfo(String filePath, String mountPath) throws BusinessException {
        // 挂载升级包文件
        IsoFileUtil.mountISOFile(filePath, mountPath);

        TerminalUpgradeVersionFileInfo versionInfo;
        try {
            // 读取校验文件内容
            versionInfo = checkISOVersionFile(mountPath);
        } catch (Exception e) {
            LOGGER.error("check version file error", e);
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_PACKAGE_VERSION_FILE_INCORRECT, e);
        } finally {
            // 取消挂载
            IsoFileUtil.unmountISOFile(mountPath);
            FileOperateUtil.deleteFile(new File(mountPath));
        }

        return versionInfo;
    }


    private boolean checkFileType(String fileName) {
        String fileType = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        // 文件类型校验
        if (UpgradeFileTypeEnums.contains(fileType)) {
            LOGGER.debug("file type [{}] is correct", fileType);
            return true;
        }
        return false;
    }

    private TerminalUpgradeVersionFileInfo checkISOVersionFile(String mountPath) throws BusinessException {
        // 获取升级文件信息
        // 从文件中获取升级文件信息
        String filePath = getVersionFilePath(mountPath);
        TerminalUpgradeVersionFileInfo verInfo = getVersionInfo(mountPath, filePath);
        if (verInfo.getPackageType() == null || StringUtils.isBlank(verInfo.getImgName()) || StringUtils.isBlank(verInfo.getVersion())) {
            LOGGER.debug("version file info error: {}", verInfo.toString());
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_PACKAGE_VERSION_FILE_INCORRECT);
        }

        return verInfo;
    }

    private TerminalUpgradeVersionFileInfo getVersionInfo(String mountPath, String filePath) throws BusinessException {

        Properties prop = new Properties();
        try (InputStream in = new FileInputStream(filePath)) {
            prop.load(in);
        } catch (FileNotFoundException e) {
            LOGGER.debug("version file not found, file path[{}]", filePath);
            throw new BusinessException(BusinessKey.RCDC_FILE_NOT_EXIST, e);
        } catch (IOException e) {
            LOGGER.debug("version file read error, file path[{}]", filePath);
            throw new BusinessException(BusinessKey.RCDC_FILE_OPERATE_FAIL, e);
        }

        CbbTerminalPlatformEnums platType = CbbTerminalPlatformEnums.
                valueOf(prop.getProperty(Constants.TERMINAL_UPGRADE_PACKAGE_VERSION_FILE_KEY_PACKAGE_TYPE));
        if (platType != CbbTerminalPlatformEnums.VDI) {
            LOGGER.debug("暂不支持的升级包类型：{}", platType);
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_UPLOAD_FILE_PACKAGE_TYPE_UNSUPPORT, platType.name());
        }


        TerminalUpgradeVersionFileInfo versionInfo = new TerminalUpgradeVersionFileInfo();
        versionInfo.setPackageType(CbbTerminalTypeEnums.VDI_LINUX);
        versionInfo.setVersion(prop.getProperty(Constants.TERMINAL_UPGRADE_PACKAGE_VERSION_FILE_KEY_VERSION));

        String cpuArch = prop.getProperty(Constants.TERMINAL_UPGRADE_PACKAGE_VERSION_FILE_KEY_CPU_ARCH);
        versionInfo.setCpuArch(StringUtils.isBlank(cpuArch) ? CbbCpuArchType.X86_64 : CbbCpuArchType.convert(cpuArch));

        String cpu = prop.getProperty(Constants.TERMINAL_UPGRADE_PACKAGE_VERSION_FILE_KEY_CPU);
        versionInfo.setSupportCpu(StringUtils.isBlank(cpu) ? "ALL" : cpu);

        String imgName;
        // 获取镜像名称
        if (StringUtils.isBlank(cpu)) {
            imgName = getImgName(mountPath);
        } else {
            imgName = UUID.randomUUID().toString();
        }
        versionInfo.setImgName(imgName);
        return versionInfo;
    }


    private String getVersionFilePath(String mountPath) {
        return mountPath + Constants.TERMINAL_UPGRADE_ISO_VERSION_FILE_PATH;
    }

    private String getImgName(String mountPath) throws BusinessException {
        String imgPath = mountPath + Constants.TERMINAL_UPGRADE_ISO_IMG_FILE_PATH;
        File file = new File(imgPath);
        if (!file.isDirectory()) {
            LOGGER.debug("system upgrade file incorrect, img direction not exist, file path[{}]", imgPath);
            throw new BusinessException(BusinessKey.RCDC_FILE_NOT_EXIST);
        }
        String[] fileNameArr = file.list();
        if (fileNameArr == null || fileNameArr.length == 0) {
            LOGGER.debug("system upgrade file incorrect, img file not exist, file path[{}]", imgPath);
            throw new BusinessException(BusinessKey.RCDC_FILE_NOT_EXIST);
        }
        return fileNameArr[0];
    }

    private String moveUpgradePackage(String toPath, String fromPath)
            throws BusinessException {
        Assert.notNull(toPath, "toPath can not be null");
        Assert.notNull(fromPath, "fromPath can not be null");
        File to = new File(toPath);
        File from = new File(fromPath);

        // 再次校验磁盘空间是否足够
        final boolean isEnough = checkPackageDiskSpaceIsEnough(from.length());

        if (!isEnough) {
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_UPGRADE_PACKAGE_DISK_SPACE_NOT_ENOUGH);
        }

        try {
            Files.move(from.toPath(), to.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            LOGGER.debug("move upgrade file to target directory fail");
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_UPLOAD_FILE_FAIL, e);
        }

        return toPath;
    }

    private boolean checkPackageDiskSpaceIsEnough(Long fileSize) {
        File packageDir = new File(Constants.PXE_SAMBA_PACKAGE_PATH);
        final long usableSpace = packageDir.getUsableSpace();
        if (usableSpace >= fileSize) {
            return true;
        }

        return false;
    }

}

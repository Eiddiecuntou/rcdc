package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.systemupgrade;

import java.io.File;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.UpgradeFileTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalUpgradeVersionFileInfo;
import com.ruijie.rcos.rcdc.terminal.module.impl.util.FileOperateUtil;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.base.util.IsoFileUtil;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2021
 * Company: Ruijie Co., Ltd.
 * Create Time: 2021/8/25 13:51
 *
 * @author TING
 */
@Service
public class LinuxVDISystemUpgradeIsoPackageResolver extends AbstractSystemUpgradePackageResolver {

    private static final Logger LOGGER = LoggerFactory.getLogger(SystemUpgradePackageResolver.class);

    public UpgradeFileTypeEnums getUpgradeFileType() {
        return UpgradeFileTypeEnums.ISO;
    }

    @Override
    protected CbbTerminalTypeEnums getTerminalType() {
        return CbbTerminalTypeEnums.VDI_LINUX;
    }

    @Override
    protected void validatePackage(String fileName, String filePath) throws BusinessException {
        Assert.hasText(fileName, "fileName can not be null");
        Assert.hasText(filePath, "filePath can not be null");

        checkISOMd5(filePath);
    }

    @Override
    protected TerminalUpgradeVersionFileInfo readPackageConfig(String fileName, String filePath) throws BusinessException {
        TerminalUpgradeVersionFileInfo versionInfo;
        String mountPath = getISOMountPath();
        try {

            // 校验目录
            checkNecessaryDirExist(mountPath);

            // 挂载ISO
            IsoFileUtil.mountISOFile(filePath, mountPath);

            // 读取校验文件内容
            String packageConfigFilePath = getVersionFilePath(mountPath);
            versionInfo = getVersionInfo(packageConfigFilePath);
            versionInfo.setImgName(getImgName(mountPath));
        } catch (Exception e) {
            LOGGER.error("check version file error", e);
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_PACKAGE_VERSION_FILE_INCORRECT, e);
        } finally {
            // 取消挂载
            IsoFileUtil.unmountISOFile(mountPath);
            FileOperateUtil.deleteFile(new File(mountPath));
        }

        if (validateVersionInfo(versionInfo)) {
            LOGGER.debug("version file info error: {}", versionInfo.toString());
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_PACKAGE_VERSION_FILE_INCORRECT);
        }

        completeVersionInfo(fileName, filePath, mountPath, versionInfo);

        return versionInfo;
    }

    private boolean validateVersionInfo(TerminalUpgradeVersionFileInfo versionInfo) {
        return versionInfo.getPackageType() == null || StringUtils.isBlank(versionInfo.getImgName()) || StringUtils.isBlank(versionInfo.getVersion());
    }

    @Override
    protected void movePackage(String filePath, TerminalUpgradeVersionFileInfo versionInfo) throws BusinessException {
        Assert.notNull(versionInfo, "versionInfo can not be null");

        String storePackageName = UUID.randomUUID() + Constants.FILE_SUFFIX_DOT + getUpgradeFileType().getFileType();
        String storePackagePath = Constants.PXE_SAMBA_LINUX_VDI_ISO_PATH + storePackageName;
        versionInfo.setFileSaveDir(Constants.PXE_SAMBA_LINUX_VDI_ISO_PATH);
        versionInfo.setFilePath(storePackagePath);

        // 移动iso包
        moveUpgradePackage(storePackagePath, filePath);
    }

    @Override
    protected void lastStep(TerminalUpgradeVersionFileInfo versionInfo) throws BusinessException {
        Assert.notNull(versionInfo, "versionInfo can not be null");

        // do nothing
    }

    private void completeVersionInfo(String fileName, String filePath, String mountPath, TerminalUpgradeVersionFileInfo versionInfo)
            throws BusinessException {
        String storePackageName = UUID.randomUUID() + getUpgradeFileType().getFileType();
        String toPath = Constants.PXE_SAMBA_LINUX_VDI_ISO_PATH + storePackageName;

        versionInfo.setFilePath(toPath);
        versionInfo.setPackageName(fileName);
        versionInfo.setRealFileName(storePackageName);
        versionInfo.setFileMD5(calFileMd5(filePath));
        versionInfo.setPackageType(CbbTerminalTypeEnums.VDI_LINUX);
        versionInfo.setUpgradeMode(DEFAULT_UPGRADE_MODE);
        versionInfo.setFileSaveDir(Constants.PXE_SAMBA_LINUX_VDI_ISO_PATH);
        versionInfo.setFileSaveDir(Constants.PXE_SAMBA_LINUX_VDI_ISO_PATH);
    }

    private String getISOMountPath() {
        return Constants.TERMINAL_UPGRADE_LINUX_VDI_ISO_MOUNT_PATH + UUID.randomUUID().toString();

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
    }

}

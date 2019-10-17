package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler;

import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalUpgradePackageUploadRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.TerminalPlatformEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.TerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradePackageDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradePackageEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.UpgradeFileTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.SimpleCmdReturnValueResolver;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalUpgradeVersionFileInfo;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalSystemUpgradePackageService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalSystemUpgradeService;
import com.ruijie.rcos.rcdc.terminal.module.impl.util.FileOperateUtil;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.base.shell.ShellCommandRunner;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.io.*;
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
public class LinuxVDISystemUpgradeHandler extends AbstractTerminalSystemUpgradeHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(LinuxVDISystemUpgradeHandler.class);


    @Autowired
    private TerminalSystemUpgradePackageDAO terminalSystemUpgradePackageDAO;

    @Autowired
    private TerminalSystemUpgradeService terminalSystemUpgradeService;

    @Autowired
    private TerminalSystemUpgradePackageService terminalSystemUpgradePackageService;



    @Override
    public void uploadUpgradePackage(CbbTerminalUpgradePackageUploadRequest request) throws BusinessException {
        Assert.notNull(request, "request can not be null");
        final String fileName = request.getFileName();
        final String filePath = request.getFilePath();
        // 校验文件类型
        boolean isCorrectType = checkFileType(fileName);
        if (!isCorrectType) {
            LOGGER.debug("terminal system upgrade file type error, file name [{}] ", fileName);
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_UPLOAD_FILE_TYPE_ERROR);
        }

        TerminalUpgradeVersionFileInfo versionInfo = getPackageInfo(fileName, filePath);

        // 根据升级包类型判断是否存在旧升级包，及是否存在旧升级包的正在进行中的升级任务，是则不允许替换升级包
        boolean hasRunningTask = isExistRunningTask(versionInfo.getPackageType());
        if (hasRunningTask) {
            LOGGER.debug("system upgrade task is running, can not upload file ", fileName);
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_TASK_IS_RUNNING);
        }

        // 将新升级文件移动到目录下
        String storePackageName = UUID.randomUUID() + fileName.substring(fileName.lastIndexOf("."));
        final String toPath = Constants.TERMINAL_UPGRADE_ISO_PATH_VDI + storePackageName;
        final String packagePath = moveUpgradePackage(toPath, filePath);
        // 更新升级包信息入库
        versionInfo.setFilePath(packagePath);
        terminalSystemUpgradePackageService.saveTerminalUpgradePackage(versionInfo);

        // 替换升级文件,清除原升级包目录下旧文件
        FileOperateUtil.emptyDirectory(Constants.TERMINAL_UPGRADE_ISO_PATH_VDI, storePackageName);
    }

    @Override
    public TerminalUpgradeVersionFileInfo getPackageInfo(String fileName, String filePath) throws BusinessException {
        Assert.notNull(fileName, "fileName can not be null");
        Assert.notNull(filePath, "filePath can not be null");
        // 挂载升级包文件
        mountUpgradePackage(filePath);

        TerminalUpgradeVersionFileInfo versionInfo = null;
        try {
            // 读取校验文件内容
            versionInfo = checkVersionFile();
        } catch (Exception e) {
            LOGGER.error("check version file error", e);
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_PACKAGE_VERSION_FILE_INCORRECT, e);
        } finally {
            // 取消挂载
            umountUpgradePackage();
        }

        versionInfo.setPackageName(fileName);
        return versionInfo;
    }

    /**
     * 检验是否存在正在进行的升级任务
     *
     * @param packageType 升级包类型
     * @return
     */
    private boolean isExistRunningTask(TerminalTypeEnums packageType) {
        TerminalSystemUpgradePackageEntity upgradePackage = terminalSystemUpgradePackageDAO.findFirstByPackageType(packageType);
        if (upgradePackage == null) {
            return false;
        }

        return terminalSystemUpgradeService.hasSystemUpgradeInProgress(upgradePackage.getId());
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

    private void mountUpgradePackage(final String filePath) throws BusinessException {
        LOGGER.debug("mount package, path is [{}]", filePath);
        String mountCmd = String.format(Constants.SYSTEM_CMD_MOUNT_UPGRADE_ISO, filePath, Constants.TERMINAL_UPGRADE_ISO_MOUNT_PATH);

        LOGGER.info("mount package, cmd : {}", mountCmd);
        runShellCommand(mountCmd);
        LOGGER.info("mount package success");
    }

    private void umountUpgradePackage() throws BusinessException {
        LOGGER.debug("umount package, path is [{}]", Constants.TERMINAL_UPGRADE_ISO_MOUNT_PATH);
        String umountCmd = String.format(Constants.SYSTEM_CMD_UMOUNT_UPGRADE_ISO, Constants.TERMINAL_UPGRADE_ISO_MOUNT_PATH);

        LOGGER.info("umount package, cmd : {}", umountCmd);
        runShellCommand(umountCmd);
        LOGGER.info("umount package success");
    }

    private void runShellCommand(String cmd) throws BusinessException {
        ShellCommandRunner runner = new ShellCommandRunner();
        runner.setCommand(cmd);
        try {
            String outStr = runner.execute(new SimpleCmdReturnValueResolver());
            LOGGER.debug("out String is :{}", outStr);
        } catch (BusinessException e) {
            LOGGER.error("shell command execute error", e);
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_UPGRADE_PACKAGE_FILE_ILLEGAL, e);
        }
    }

    private TerminalUpgradeVersionFileInfo checkVersionFile() throws BusinessException {
        // 获取升级文件信息
        TerminalUpgradeVersionFileInfo verInfo = getVersionInfo();
        if (verInfo.getPackageType() == null || StringUtils.isBlank(verInfo.getImgName()) || StringUtils.isBlank(verInfo.getVersion())) {
            LOGGER.debug("version file info error: {}", verInfo.toString());
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_PACKAGE_VERSION_FILE_INCORRECT);
        }

        return verInfo;
    }

    private TerminalUpgradeVersionFileInfo getVersionInfo() throws BusinessException {
        // 从文件中获取升级文件信息
        String filePath = getVersionFilePath();
        Properties prop = new Properties();
        try (InputStream in = new FileInputStream(filePath);) {
            prop.load(in);
        } catch (FileNotFoundException e) {
            LOGGER.debug("version file not found, file path[{}]", filePath);
            throw new BusinessException(BusinessKey.RCDC_FILE_NOT_EXIST, e);
        } catch (IOException e) {
            LOGGER.debug("version file read error, file path[{}]", filePath);
            throw new BusinessException(BusinessKey.RCDC_FILE_OPERATE_FAIL, e);
        }

        TerminalPlatformEnums platType = TerminalPlatformEnums.
                valueOf(prop.getProperty(Constants.TERMINAL_UPGRADE_ISO_VERSION_FILE_KEY_PACKAGE_TYPE));
        if (platType != TerminalPlatformEnums.VDI) {
            LOGGER.debug("暂不支持的升级包类型：{}", platType);
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_UPLOAD_FILE_PACKAGE_TYPE_UNSUPPORT, platType.name());
        }

        // 获取镜像名称
        String imgName = getImgName();
        TerminalUpgradeVersionFileInfo versionInfo = new TerminalUpgradeVersionFileInfo();
        versionInfo.setPackageType(TerminalTypeEnums.VDI_LINUX);
        versionInfo.setVersion(prop.getProperty(Constants.TERMINAL_UPGRADE_ISO_VERSION_FILE_KEY_VERSION));
        versionInfo.setImgName(imgName);
        return versionInfo;
    }

    private String getVersionFilePath() {
        return Constants.TERMINAL_UPGRADE_ISO_MOUNT_PATH + Constants.TERMINAL_UPGRADE_ISO_VERSION_FILE_PATH;
    }

    private String getImgName() throws BusinessException {
        String imgPath = Constants.TERMINAL_UPGRADE_ISO_MOUNT_PATH + Constants.TERMINAL_UPGRADE_ISO_IMG_FILE_PATH;
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

}

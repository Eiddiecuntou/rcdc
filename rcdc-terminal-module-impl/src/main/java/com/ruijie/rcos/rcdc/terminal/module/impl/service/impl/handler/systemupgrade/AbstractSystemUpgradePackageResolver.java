package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.systemupgrade;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Properties;

import com.ruijie.rcos.rcdc.terminal.module.impl.util.FileOperateUtil;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.springframework.util.Assert;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeModeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbCpuArchType;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalPlatformEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.UpgradeFileTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalUpgradeVersionFileInfo;
import com.ruijie.rcos.sk.base.crypto.Md5Builder;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.base.util.StringUtils;

/**
 * Description: 升级包处理对象
 * Copyright: Copyright (c) 2021
 * Company: Ruijie Co., Ltd.
 * Create Time: 2021/8/18 15:02
 *
 * @author TING
 */
public abstract class AbstractSystemUpgradePackageResolver implements SystemUpgradePackageResolver {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSystemUpgradePackageResolver.class);

    protected static final CbbSystemUpgradeModeEnums DEFAULT_UPGRADE_MODE = CbbSystemUpgradeModeEnums.MANUAL;

    @Override
    public boolean checkFileType(String fileName) {
        Assert.hasText(fileName, "fileName can not be blank");

        String fileType = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        // 文件类型校验
        if (UpgradeFileTypeEnums.contains(fileType)) {
            LOGGER.debug("file type [{}] is correct", fileType);
            return true;
        }
        return false;
    }

    @Override
    public TerminalUpgradeVersionFileInfo dealAndReadPackageConfig(String fileName, String filePath) throws BusinessException {
        Assert.hasText(fileName, "fileName can not be blank");
        Assert.hasText(filePath, "filePath can not be blank");

        // 获取类型
        UpgradeFileTypeEnums fileType = getFileType(fileName);

        // 校验升级包
        validatePackage(fileName, filePath);

        // 读取版本文件信息
        TerminalUpgradeVersionFileInfo versionInfo = readPackageConfig(fileName, filePath);

        // 将新升级文件移动到目录下
        movePackage(filePath, versionInfo);

        // 执行最后步骤，如ota包制种等
        lastStep(versionInfo);

        return versionInfo;

    }

    /**
     * 升级包校验
     *
     * @param fileName 升级包文件名
     * @param filePath 升级包文件路径
     * @throws BusinessException 业务异常
     */
    protected abstract void validatePackage(String fileName, String filePath) throws BusinessException;

    protected abstract void movePackage(String filePath, TerminalUpgradeVersionFileInfo versionInfo) throws BusinessException;

    protected abstract void lastStep(TerminalUpgradeVersionFileInfo versionInfo) throws BusinessException;

    protected abstract TerminalUpgradeVersionFileInfo readPackageConfig(String fileName, String filePath) throws BusinessException;

    protected abstract CbbTerminalTypeEnums getTerminalType();

    /**
     * 获取文件类型
     *
     * @param fileName 文件名
     * @return 文件类型枚举对象
     */
    protected UpgradeFileTypeEnums getFileType(String fileName) {

        String fileType = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        LOGGER.info("文件[{}]的文件类型为[{}]", fileName, fileType);

        return UpgradeFileTypeEnums.convert(fileType);
    }

    protected TerminalUpgradeVersionFileInfo getVersionInfo(String packageConfigFilePath) throws BusinessException {

        // 读取配置文件
        Properties prop = new Properties();
        try (InputStream in = new FileInputStream(packageConfigFilePath)) {
            prop.load(in);
        } catch (FileNotFoundException e) {
            LOGGER.error("version file not found, file path[{}]", packageConfigFilePath);
            throw new BusinessException(BusinessKey.RCDC_FILE_NOT_EXIST, e);
        } catch (IOException e) {
            LOGGER.error("version file read error, file path[{}]", packageConfigFilePath);
            throw new BusinessException(BusinessKey.RCDC_FILE_OPERATE_FAIL, e);
        }

        String packageType = prop.getProperty(Constants.TERMINAL_UPGRADE_PACKAGE_VERSION_FILE_KEY_PACKAGE_TYPE);
        CbbTerminalPlatformEnums platType = CbbTerminalPlatformEnums.valueOf(packageType);
        if (!getTerminalType().getPlatform().equals(packageType.trim().toUpperCase())) {
            LOGGER.error("暂不支持的升级包类型：{}", platType);
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_UPLOAD_FILE_PACKAGE_TYPE_UNSUPPORT, platType.name());
        }


        TerminalUpgradeVersionFileInfo versionInfo = new TerminalUpgradeVersionFileInfo();
        versionInfo.setPackageType(getTerminalType());
        versionInfo.setVersion(prop.getProperty(Constants.TERMINAL_UPGRADE_PACKAGE_VERSION_FILE_KEY_VERSION));

        String cpuArch = prop.getProperty(Constants.TERMINAL_UPGRADE_PACKAGE_VERSION_FILE_KEY_CPU_ARCH);
        versionInfo.setCpuArch(StringUtils.isBlank(cpuArch) ? CbbCpuArchType.X86_64 : CbbCpuArchType.convert(cpuArch));

        String cpu = prop.getProperty(Constants.TERMINAL_UPGRADE_PACKAGE_VERSION_FILE_KEY_CPU);
        versionInfo.setSupportCpu(StringUtils.isBlank(cpu) ? Constants.TERMINAL_SYSTEM_UPGRADE_CPU_SUPPORT_ALL : cpu);

        return versionInfo;
    }

    protected String moveUpgradePackage(String toPath, String fromPath) throws BusinessException {
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

    protected String calFileMd5(String filePath) throws BusinessException {
        try {
            return StringUtils.bytes2Hex(Md5Builder.computeFileMd5(new File(filePath)));
        } catch (IOException e) {
            LOGGER.error("计算文件【" + filePath + "】MD5值异常", e);
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_OTA_UPGRADE_COMPUTE_FILE_MD5_FAIL, e);
        }
    }

    /**
     * 校验文件MD%
     * FIXME zyc 需要下沉到框架
     *
     * @param filePath 文件路径
     * @throws BusinessException 业务异常
     */
    protected void checkISOMd5(String filePath) throws BusinessException {
        LOGGER.debug("check iso md5，file path: {}", filePath);
        String mountCmd = String.format(Constants.SYSTEM_CMD_CHECK_ISO_MD5, filePath);

        LOGGER.debug("check iso md5, cmd : {}", mountCmd);
        String outStr = executeCommand(mountCmd);
        LOGGER.info("check iso md5, outStr: {}", outStr);

        if (StringUtils.isBlank(outStr) || !outStr.contains(Constants.ISO_MD5_CHECK_SUCCESS_VALUE)) {
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_UPGRADE_PACKAGE_FILE_MD5_CHECK_ERROR);
        }
        LOGGER.info("check iso md5 success");
    }

    /**
     * 执行shell命令
     *
     * @param cmd 命令
     * @return 执行结果
     * @throws BusinessException 业务异常
     */
    protected String executeCommand(String cmd) throws BusinessException {
        ByteArrayOutputStream stdout = new ByteArrayOutputStream();
        PumpStreamHandler psh = new PumpStreamHandler(stdout);
        CommandLine cl = CommandLine.parse(cmd);
        DefaultExecutor exec = new DefaultExecutor();
        exec.setStreamHandler(psh);
        try {
            exec.execute(cl);
            return stdout.toString();
        } catch (IOException e) {
            LOGGER.error("exec.execute [{}] has IOException", e);
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_UPGRADE_PACKAGE_FILE_ILLEGAL, e);
        } finally {
            try {
                stdout.close();
            } catch (IOException e) {
                LOGGER.error("stdout.close() has IOException", e);
            }
        }
    }

    private boolean checkPackageDiskSpaceIsEnough(Long fileSize) {
        File packageDir = new File(Constants.PXE_SAMBA_PACKAGE_PATH);
        final long usableSpace = packageDir.getUsableSpace();
        if (usableSpace >= fileSize) {
            return true;
        }

        return false;
    }

    /**
     *  检验并创建文件目录
     *
     * @param filePathList 文件目录列表
     */
    protected void checkAndCreateNecessaryDir(List<String> filePathList) {
        Assert.notNull(filePathList, "filePathList can not be null");

        filePathList.stream().forEach(filePath -> {
            File file = new File(filePath);
            if (!file.isDirectory()) {
                LOGGER.info("创建的目录文件路径：{}", filePath);
                FileOperateUtil.createFileDirectory(file);
            }
        });
    }
}

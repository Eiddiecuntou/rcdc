package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.systemupgrade;

import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalUpgradePackageUploadRequest;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.SimpleCmdReturnValueResolver;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalUpgradeVersionFileInfo;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalSystemUpgradePackageService;
import com.ruijie.rcos.rcdc.terminal.module.impl.util.FileOperateUtil;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.base.shell.ShellCommandRunner;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/11/11
 *
 * @author nt
 */
public abstract class AbstractSystemUpgradePackageHandler implements TerminalSystemUpgradePackageHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSystemUpgradePackageHandler.class);

    private static final String ISO_FILE_MD5_CHECK_SUCCESS_FLAG = "PASS";

    @Override
    public void uploadUpgradePackage(CbbTerminalUpgradePackageUploadRequest request) throws BusinessException {
        Assert.notNull(request, "request can not be null");
        String fileName = request.getFileName();
        String filePath = request.getFilePath();

        LOGGER.info("上传终端升级包：{}", request.getFileName());
        TerminalUpgradeVersionFileInfo upgradeInfo = getPackageInfo(fileName, filePath);
        getSystemUpgradePackageService().saveTerminalUpgradePackage(upgradeInfo);

        // 替换升级文件,清除原升级包目录下旧文件
        FileOperateUtil.emptyDirectory(upgradeInfo.getFileSaveDir(), upgradeInfo.getRealFileName());
    }

    protected abstract TerminalUpgradeVersionFileInfo getPackageInfo(String fileName, String filePath) throws BusinessException;

    protected abstract TerminalSystemUpgradePackageService getSystemUpgradePackageService();


    protected void checkISOMd5(String filePath) throws BusinessException {
        LOGGER.debug("check iso md5，file path: {}", filePath);
        String mountCmd = String.format(Constants.SYSTEM_CMD_CHECK_ISO_MD5, filePath);

        LOGGER.debug("check iso md5, cmd : {}", mountCmd);
        String outStr = executeCommand(mountCmd);
        LOGGER.info("check iso md5, outStr: {}", outStr);

        if (StringUtils.isBlank(outStr) || !outStr.contains(ISO_FILE_MD5_CHECK_SUCCESS_FLAG) ) {
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_UPGRADE_PACKAGE_FILE_MD5_CHECK_ERROR);
        }
        LOGGER.info("check iso md5 success");
    }

    private String executeCommand(String mountCmd) throws BusinessException {
        ByteArrayOutputStream stdout = new ByteArrayOutputStream();
        PumpStreamHandler psh = new PumpStreamHandler(stdout);
        CommandLine cl = CommandLine.parse(mountCmd);
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

    protected void mountUpgradePackage(String sourceFilePath, String targetDir) throws BusinessException {
        LOGGER.debug("mount package, path is [{}]", sourceFilePath);
        String mountCmd = String.format(Constants.SYSTEM_CMD_MOUNT_UPGRADE_ISO, sourceFilePath, targetDir);

        LOGGER.info("mount package, cmd : {}", mountCmd);
        runShellCommand(mountCmd);
        LOGGER.info("mount package success");
    }

    protected void umountUpgradePackage(String mountDir) throws BusinessException {
        LOGGER.debug("umount package, path is [{}]", mountDir);
        String umountCmd = String.format(Constants.SYSTEM_CMD_UMOUNT_UPGRADE_ISO, mountDir);

        LOGGER.info("umount package, cmd : {}", umountCmd);
        runShellCommand(umountCmd);
        LOGGER.info("umount package success");
    }

    private String runShellCommand(String cmd) throws BusinessException {
        ShellCommandRunner runner = new ShellCommandRunner();
        runner.setCommand(cmd);
        String outStr;
        try {
            outStr = runner.execute(new SimpleCmdReturnValueResolver());
            LOGGER.debug("out String is :{}", outStr);
        } catch (BusinessException e) {
            LOGGER.error("shell command execute error", e);
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_UPGRADE_PACKAGE_FILE_ILLEGAL, e);
        }

        return outStr;
    }
}

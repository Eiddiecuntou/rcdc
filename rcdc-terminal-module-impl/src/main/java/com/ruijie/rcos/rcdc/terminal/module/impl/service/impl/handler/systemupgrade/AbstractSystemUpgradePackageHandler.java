package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.systemupgrade;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.springframework.util.Assert;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalUpgradePackageUploadDTO;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalUpgradeVersionFileInfo;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalSystemUpgradePackageService;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.base.util.StringUtils;

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

    @Override
    public void uploadUpgradePackage(CbbTerminalUpgradePackageUploadDTO request) throws BusinessException {
        Assert.notNull(request, "request can not be null");
        String fileName = request.getFileName();
        String filePath = request.getFilePath();

        LOGGER.info("上传终端升级包：{}", request.getFileName());
        TerminalUpgradeVersionFileInfo upgradeInfo = getPackageInfo(fileName, filePath);
        getSystemUpgradePackageService().saveTerminalUpgradePackage(upgradeInfo);
    }

    @Override
    public boolean checkFileNameNotDuplicate(String fileName) {

        Assert.notNull(fileName, "fileName can not be null");
        final boolean flag = getSystemUpgradePackageService().existsTerminalUpdatePackage(getPackageType(), fileName);

        LOGGER.info("升级包文件：{}，类型：{}，是否名称重复：{}", fileName, getPackageType(), flag);
        return !flag;
    }

    @Override
    public boolean checkServerDiskSpaceIsEnough(Long fileSize, String fileStorePath) {
        Assert.notNull(fileSize, "fileSize can not be null");
        Assert.notNull(fileStorePath, "fileStorePath can not be null");

        File packageDir = new File(fileStorePath);
        final long usableSpace = packageDir.getUsableSpace();

        LOGGER.info("升级包文件大小校验，磁盘路径：{}，文件大小：{}，磁盘可用容量大小：{}", fileStorePath, fileSize, usableSpace);
        return usableSpace >= fileSize;
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

    protected abstract CbbTerminalTypeEnums getPackageType();

    protected abstract TerminalUpgradeVersionFileInfo getPackageInfo(String fileName, String filePath) throws BusinessException;

    protected abstract TerminalSystemUpgradePackageService getSystemUpgradePackageService();

}

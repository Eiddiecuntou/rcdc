package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler;

import com.google.common.io.Files;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import org.springframework.util.Assert;

import java.io.File;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/10/14
 *
 * @author hs
 */
public abstract class AbstractTerminalSystemUpgradeHandler implements TerminalSystemUpgradeHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractTerminalSystemUpgradeHandler.class);

    /**
     *
     * @param fileName 文件名
     * @param toPath 目的路径
     * @param filePath 原路径
     * @return 升级包保存路径
     * @throws BusinessException 异常
     */
    public String moveUpgradePackage(String fileName, String toPath, String filePath)
            throws BusinessException {
        Assert.notNull(fileName, "fileName can not be null");
        Assert.notNull(toPath, "toPath can not be null");
        Assert.notNull(filePath, "filePath can not be null");
        LOGGER.info("开始移动刷机包[{}]到路径[{}]", fileName, Constants.TERMINAL_UPGRADE_ISO_PATH_VDI);
        File to = new File(toPath);
        File from = new File(filePath);

        // 再次校验磁盘空间是否足够
        final boolean isEnough = checkPackageDiskSpaceIsEnough(from.length());
        if (!isEnough) {
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_UPGRADE_PACKAGE_DISK_SPACE_NOT_ENOUGH);
        }
        try {
            Files.move(from, to);
        } catch (Exception e) {
            LOGGER.debug("move upgrade file to target directory fail, fileName : {}", fileName);
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_UPLOAD_FILE_FAIL, e);
        }
        LOGGER.info("完成移动刷机包");

        return toPath;
    }

    private boolean checkPackageDiskSpaceIsEnough(Long fileSize) {
        File packageDir = new File(Constants.TERMINAL_UPGRADE_PACKAGE_PATH);
        final long usableSpace = packageDir.getUsableSpace();
        if (usableSpace >= fileSize) {
            return true;
        }

        return false;
    }


}

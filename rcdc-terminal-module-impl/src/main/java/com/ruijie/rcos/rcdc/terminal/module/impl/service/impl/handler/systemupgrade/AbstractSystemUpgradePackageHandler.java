package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.systemupgrade;

import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.systemupgrade.TerminalSystemUpgradePackageHandler;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import org.springframework.util.Assert;

import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalUpgradePackageUploadRequest;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalUpgradeVersionFileInfo;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalSystemUpgradePackageService;
import com.ruijie.rcos.rcdc.terminal.module.impl.util.FileOperateUtil;
import com.ruijie.rcos.sk.base.exception.BusinessException;

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
}

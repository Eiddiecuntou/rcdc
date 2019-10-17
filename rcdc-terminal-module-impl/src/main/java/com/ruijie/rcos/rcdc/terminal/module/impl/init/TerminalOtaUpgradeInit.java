package com.ruijie.rcos.rcdc.terminal.module.impl.init;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeModeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.TerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalUpgradeVersionFileInfo;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalSystemUpgradePackageService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.TerminalSystemUpgradeHandler;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.TerminalSystemUpgradeHandlerFactory;
import com.ruijie.rcos.rcdc.terminal.module.impl.util.FileOperateUtil;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.modulekit.api.bootstrap.SafetySingletonInitializer;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.util.List;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/10/16
 *
 * @author hs
 */
public class TerminalOtaUpgradeInit implements SafetySingletonInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(TerminalOtaUpgradeInit.class);

    @Autowired
    private TerminalSystemUpgradeHandlerFactory handlerFactory;

    @Autowired
    private TerminalSystemUpgradePackageService terminalSystemUpgradePackageService;

    @Override
    public void safeInit() {

        String basePath = Constants.TERMINAL_UPGRADE_OTA;
        List<File> fileList = FileOperateUtil.listFile(basePath);
        if (fileList.size() == 0) {
            LOGGER.error("OTA升级包不存在");
            return;
        }
        File file = fileList.get(0);
        String fileName = file.getName();
        String filePath = file.getPath();
        try {
            TerminalSystemUpgradeHandler handler = handlerFactory.getHandler(TerminalTypeEnums.VDI_ANDROID);
            TerminalUpgradeVersionFileInfo upgradeInfo = handler.getPackageInfo(fileName, filePath);
            upgradeInfo.setUpgradeMode(CbbSystemUpgradeModeEnums.AUTO);
            terminalSystemUpgradePackageService.saveTerminalUpgradePackage(upgradeInfo);
            // 替换升级文件,清除原升级包目录下旧文件
            FileOperateUtil.emptyDirectory(Constants.TERMINAL_UPGRADE_OTA_PACKAGE, upgradeInfo.getPackageName());
        } catch (BusinessException e) {
            LOGGER.error("获取OTA包信息失败", e);
        }


    }
}

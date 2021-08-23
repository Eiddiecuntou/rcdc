package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.systemupgrade;

import com.ruijie.rcos.rcdc.terminal.module.impl.enums.UpgradeFileTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalUpgradeVersionFileInfo;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import org.springframework.util.Assert;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2021
 * Company: Ruijie Co., Ltd.
 * Create Time: 2021/8/18 15:02
 *
 * @author TING
 */
public class AbstractSystemUpgradePackageResolver implements SystemUpgradePackageResolver {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSystemUpgradePackageResolver.class);

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
    public TerminalUpgradeVersionFileInfo getPackageConfig(String fileName) {
        return null;
    }
}

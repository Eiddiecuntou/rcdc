package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalUpgradePackageUploadDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalSystemPackageUploadingService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.systemupgrade.TerminalSystemUpgradePackageHandler;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.systemupgrade.TerminalSystemUpgradePackageHandlerFactory;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.HashSet;
import java.util.Set;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/10/30
 *
 * @author hs
 */
@Service
public class TerminalSystemPackageUploadingServiceImpl implements TerminalSystemPackageUploadingService {

    private static final Set<CbbTerminalTypeEnums> SYS_UPGRADE_PACKAGE_UPLOADING = new HashSet<>();

    private static final Object LOCK = new Object();

    @Autowired
    private TerminalSystemUpgradePackageHandlerFactory handlerFactory;

    @Override
    public boolean isUpgradeFileUploading(CbbTerminalTypeEnums terminalType) {
        Assert.notNull(terminalType, "request can not be null");
        boolean hasLoading = false;
        if (SYS_UPGRADE_PACKAGE_UPLOADING.contains(terminalType)) {
            hasLoading = true;
        }
        return hasLoading;
    }

    @Override
    public void uploadUpgradePackage(CbbTerminalUpgradePackageUploadDTO request, CbbTerminalTypeEnums terminalType) throws BusinessException {
        Assert.notNull(request, "request can not be null");
        Assert.notNull(terminalType, "terminalType can not be null");

        // TODO ???????????????????????????
        synchronized (LOCK) {
            if (SYS_UPGRADE_PACKAGE_UPLOADING.contains(terminalType)) {
                throw new BusinessException(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_PACKAGE_IS_UPLOADING);
            }
            SYS_UPGRADE_PACKAGE_UPLOADING.add(terminalType);
        }
        TerminalSystemUpgradePackageHandler handler = null;
        try {
            handler = handlerFactory.getHandler(terminalType);
            // ???????????????
            handler.preUploadPackage();
            handler.uploadUpgradePackage(request);
        } finally {
            // ???????????????????????????????????????
            SYS_UPGRADE_PACKAGE_UPLOADING.remove(terminalType);
            // ???????????????
            if (handler != null) {
                handler.postUploadPackage();
            }
        }
    }


}

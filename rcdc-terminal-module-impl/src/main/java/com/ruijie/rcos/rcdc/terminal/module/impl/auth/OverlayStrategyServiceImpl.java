package com.ruijie.rcos.rcdc.terminal.module.impl.auth;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalLicenseTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalLicenseService;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * Description: 叠加策略
 * Copyright: Copyright (c) 2021
 * Company: Ruijie Co., Ltd.
 * Create Time: 2021/7/15 17:46
 *
 * @author TING
 */
@Service("overlayStrategyService")
public class OverlayStrategyServiceImpl extends AbstractStrategyServiceImpl {

    private static final Logger LOGGER = LoggerFactory.getLogger(OverlayStrategyServiceImpl.class);

    @Override
    public boolean allocate(List<CbbTerminalLicenseTypeEnums> licenseTypeList) {
        Assert.notNull(licenseTypeList, "licenseTypeList can not be null");

        if (CollectionUtils.isEmpty(licenseTypeList)) {
            LOGGER.info("授权策略的授权证书类型为空，不符合预期，返回授权失败");
            return false;
        }

        for (CbbTerminalLicenseTypeEnums licenseType : licenseTypeList) {
            TerminalLicenseService licenseService = getTerminalLicenseService(licenseType);
//            boolean isAuthed = licenseService.auth();
//            if (!isAuthed) {
//
//            }
        }
        return false;
    }

    @Override
    public boolean recycle(List<CbbTerminalLicenseTypeEnums> licenseTypeList) {
        Assert.notNull(licenseTypeList, "licenseTypeList can not be null");

        if (CollectionUtils.isEmpty(licenseTypeList)) {
            LOGGER.info("授权策略的授权证书类型为空，不符合预期，返回授权回收失败");
            return false;
        }

        for (CbbTerminalLicenseTypeEnums licenseType : licenseTypeList) {
            TerminalLicenseService licenseService = getTerminalLicenseService(licenseType);
//            boolean isAuthed = licenseService.recycle();
//            if (isAuthed) {
//                return true;
//            }
        }

        return false;
    }
}

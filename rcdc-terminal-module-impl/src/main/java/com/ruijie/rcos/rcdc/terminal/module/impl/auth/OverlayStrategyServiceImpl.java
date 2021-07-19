package com.ruijie.rcos.rcdc.terminal.module.impl.auth;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalLicenseTypeEnums;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Description: Function Description
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
        // TODO 根据授权类型，获取授权策略，然后按顺序进行授权，授权成功就返回

        return false;
    }

    @Override
    public boolean recycle(List<CbbTerminalLicenseTypeEnums> licenseTypeList) {
        // TODO 根据授权类型，获取回收策略，然后按顺序进行回收，回收成功就返回
        return false;
    }
}

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
 * Create Time: 2021/7/15 17:47
 *
 * @author TING
 */
@Service("priorityStrategyService")
public class PriortyStrategyServiceImpl extends AbstractStrategyServiceImpl {

    private static final Logger LOGGER = LoggerFactory.getLogger(PriortyStrategyServiceImpl.class);

    @Override
    public boolean allocate(List<CbbTerminalLicenseTypeEnums> licenseTypeList) {
        return false;
    }

    @Override
    public boolean recycle(List<CbbTerminalLicenseTypeEnums> licenseTypeList) {
        return false;
    }
}

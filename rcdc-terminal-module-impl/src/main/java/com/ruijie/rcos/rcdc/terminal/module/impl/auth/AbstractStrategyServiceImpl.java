package com.ruijie.rcos.rcdc.terminal.module.impl.auth;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalLicenseTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.auth.dto.TempLicCreateDTO;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalLicenseService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.factory.CbbTerminalLicenseFactoryProvider;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2021
 * Company: Ruijie Co., Ltd.
 * Create Time: 2021/7/15 17:31
 *
 * @author TING
 */
public abstract class AbstractStrategyServiceImpl implements StrategyService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractStrategyServiceImpl.class);

    @Autowired
    private CbbTerminalLicenseFactoryProvider terminalLicenseFactoryProvider;


    @Override
    public void init(TempLicCreateDTO tempLicCreateDTO) {
        // TODO 初始化临时授权证书
    }

    /**
     *  获取授权认证服务对象
     *
     * @param licenseType 授权认证类型
     * @return 授权认证服务对象
     */
    public TerminalLicenseService getTerminalLicenseService(CbbTerminalLicenseTypeEnums licenseType) {
        Assert.notNull(licenseType, "licenseType can not be null");

        return terminalLicenseFactoryProvider.getService(licenseType);
    }
}

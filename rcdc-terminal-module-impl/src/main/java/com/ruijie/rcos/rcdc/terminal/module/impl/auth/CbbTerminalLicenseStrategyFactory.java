package com.ruijie.rcos.rcdc.terminal.module.impl.auth;

import com.ruijie.rcos.rcdc.terminal.module.impl.auth.dto.TerminalLicenseStrategyConfigDTO;
import com.ruijie.rcos.rcdc.terminal.module.impl.auth.enums.CbbTerminalLicenseStrategyEnums;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * Description: 授权策略工程类
 * Copyright: Copyright (c) 2021
 * Company: Ruijie Co., Ltd.
 * Create Time: 2021/7/15 17:15
 *
 * @author TING
 */
@Service
public class CbbTerminalLicenseStrategyFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(CbbTerminalLicenseStrategyFactory.class);

    private TerminalLicenseStrategyConfigDTO strategyConfig;

    @Autowired
    @Qualifier("overlayStrategyServiceImpl")
    private StrategyService overlayStrategyService;

    @Autowired
    @Qualifier("priorityStrategyService")
    private StrategyService priorityStrategyService;

    /**
     * 初始化授权策略配置
     *
     * @param strategyConfig 授权策略
     */
    public void initConfig(TerminalLicenseStrategyConfigDTO strategyConfig) {
        Assert.notNull(strategyConfig, "strategyConfig can not be null");

        this.strategyConfig = strategyConfig;
    }

    public TerminalLicenseStrategyConfigDTO getStrategyConfig() {
        return strategyConfig;
    }

    /**
     * 获取授权策略服务对象
     *
     * @param licenseStrategy 策略类型
     * @return 授权策略服务对象
     */
    public StrategyService getService(CbbTerminalLicenseStrategyEnums licenseStrategy) {
        Assert.notNull(licenseStrategy, "licenseStrategy can not be null");

        switch (licenseStrategy) {
            case OVERLAY:
                return overlayStrategyService;
            case PRIORITY:
                return priorityStrategyService;
            default:
                throw new IllegalStateException("不支持的策略类型【{" + licenseStrategy + "}】");
        }
    }
}

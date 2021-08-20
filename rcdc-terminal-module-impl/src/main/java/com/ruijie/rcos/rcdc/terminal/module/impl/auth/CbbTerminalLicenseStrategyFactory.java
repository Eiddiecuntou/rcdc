package com.ruijie.rcos.rcdc.terminal.module.impl.auth;

import com.alibaba.fastjson.JSON;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.auth.dto.TerminalLicenseStrategyConfigDTO;
import com.ruijie.rcos.rcdc.terminal.module.impl.auth.enums.CbbTerminalLicenseStrategyEnums;
import com.ruijie.rcos.sk.base.config.ConfigFacade;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

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

    public static final String TERMINAL_AUTH_DEFAULT_STRATEGY_JSON = "terminal_auth_default_strategy_json";

    /**
     * 业务设置的策略
     */
    private TerminalLicenseStrategyConfigDTO strategyConfig;

    @Autowired
    @Qualifier("overlayStrategyService")
    private StrategyService overlayStrategyService;

    @Autowired
    @Qualifier("priorityStrategyService")
    private StrategyService priorityStrategyService;

    @Autowired
    private ConfigFacade configFacade;

    /**
     * 初始化授权策略配置
     *
     * @param strategyConfig 授权策略
     */
    public void initConfig(TerminalLicenseStrategyConfigDTO strategyConfig) {
        Assert.notNull(strategyConfig, "strategyConfig can not be null");

        this.strategyConfig = strategyConfig;
    }

    /**
     * 获取策略
     *
     * @return 配置策略
     * @throws BusinessException 业务异常
     */
    public TerminalLicenseStrategyConfigDTO getStrategyConfig() throws BusinessException {
        if (needLoadStrategyByConfig(strategyConfig)) {
            LOGGER.error("授权分配策略为空，无法授权，使用默认授权策略");
            strategyConfig = loadDefaultStrategyConfig();
        }

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


    private boolean needLoadStrategyByConfig(TerminalLicenseStrategyConfigDTO strategy) {
        LOGGER.error("当前授权策略为：{}", JSON.toJSONString(strategy));

        return strategy == null || CollectionUtils.isEmpty(strategy.getAllocateList()) || CollectionUtils.isEmpty(strategy.getRecycleList());
    }

    private TerminalLicenseStrategyConfigDTO loadDefaultStrategyConfig() throws BusinessException {

        String strategyJson = configFacade.read(TERMINAL_AUTH_DEFAULT_STRATEGY_JSON);
        LOGGER.info("加载默认授权策略配置:{}", strategyJson);

        if (StringUtils.isEmpty(strategyJson)) {
            LOGGER.warn("加载默认授权策略配置信息异常或文件不存在");
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_LOAD_DEFAULT_LICENSE_STRATEGY_ERROR);
        }

        return JSON.parseObject(strategyJson, TerminalLicenseStrategyConfigDTO.class);
    }
}

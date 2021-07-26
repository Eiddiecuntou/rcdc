package com.ruijie.rcos.rcdc.terminal.module.impl.auth;

import com.alibaba.fastjson.JSON;
import com.ruijie.rcos.rcdc.terminal.module.impl.auth.dto.TerminalLicenseStrategyConfigDTO;
import com.ruijie.rcos.rcdc.terminal.module.impl.auth.enums.CbbTerminalLicenseStrategyEnums;
import com.ruijie.rcos.sk.base.filesystem.common.FileUtils;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ResourceBundle;

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

    private static final String DEFAULT_AUTH_CONFIG_FILE = "/config/auth.json";

    /**
     *  业务设置的策略
     */
    private TerminalLicenseStrategyConfigDTO strategyConfig;

    /**
     * 默认的策略
     */
    private TerminalLicenseStrategyConfigDTO defaultStrategyConfig;

    @Autowired
    @Qualifier("overlayStrategyService")
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

    public TerminalLicenseStrategyConfigDTO getDefaultStrategyConfig() {
        if (defaultStrategyConfig == null) {
            defaultStrategyConfig = loadDefaultStrategyConfig();
        }

        return defaultStrategyConfig;
    }

    private TerminalLicenseStrategyConfigDTO loadDefaultStrategyConfig() {
        URL url = this.getClass().getResource(DEFAULT_AUTH_CONFIG_FILE);
        File file = new File(url.getPath());
        if (file.exists()) {
            try {
                String content = FileUtils.readFileToString(file, Charset.forName("UTF-8"));
                return JSON.parseObject(content, TerminalLicenseStrategyConfigDTO.class);
            } catch (IOException e) {
                LOGGER.error("加载默认授权策略文件异常", e);
            }
        }

        LOGGER.warn("加载默认授权策略配置文件异常或文件不存在");
        return null;
    }
}

package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.componentupgrade;

import com.google.common.collect.Maps;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.TerminalOsArchType;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Map;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/8/2
 *
 * @author nt
 */
@Service
public class TerminalComponentUpgradeHandlerFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(TerminalComponentUpgradeHandlerFactory.class);

    private static Map<TerminalOsArchType, TerminalComponentUpgradeHandler> upgradeHandlerHolder = Maps.newHashMap();

    static {
        LOGGER.info("=======================注册终端组件升级处理器=================");
        upgradeHandlerHolder.put(TerminalOsArchType.LINUX_X86, new LinuxComponentUpgradeHandler());
        upgradeHandlerHolder.put(TerminalOsArchType.LINUX_ARM, new LinuxArmComponentUpgradeHandler());
        upgradeHandlerHolder.put(TerminalOsArchType.WINDOWS_X86, new WinAppComponentUpgradeHandler());
        upgradeHandlerHolder.put(TerminalOsArchType.ANDROID_ARM, new AndroidComponentUpgradeHandler());
        upgradeHandlerHolder.put(TerminalOsArchType.UOS_X86, new UosAppComponentUpgradeHandler());
        upgradeHandlerHolder.put(TerminalOsArchType.NEOKYLIN_X86, new NeoKylinAppComponentUpgradeHandler());
        LOGGER.info("=======================完成注册终端组件升级处理器=================");
    }

    /**
     * 获取终端组件升级处理对象
     * 
     * @param osArchType 终端系统架构类型
     * @return 组件升级处理对象
     * @throws BusinessException 业务异常
     */
    public TerminalComponentUpgradeHandler getHandler(TerminalOsArchType osArchType) throws BusinessException {
        Assert.notNull(osArchType, "osArchType can not be null");

        TerminalComponentUpgradeHandler handler = upgradeHandlerHolder.get(osArchType);

        if (handler == null) {
            LOGGER.error("终端系统类型为[{}]的组件升级处理对象不存在", osArchType.name());
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_COMPONENT_UPGRADE_HANDLER_NOT_EXIST,
                    new String[] {osArchType.name()});
        }

        return handler;
    }
}

package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.componentupgrade;

import com.google.common.collect.Maps;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalOsTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
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

    private static Map<CbbTerminalOsTypeEnums, TerminalComponentUpgradeHandler> upgradeHandlerHolder = Maps.newHashMap();

    static {
        LOGGER.info("=======================注册终端组件升级处理器=================");
        upgradeHandlerHolder.put(CbbTerminalOsTypeEnums.LINUX, new LinuxComponentUpgradeHandler());
        upgradeHandlerHolder.put(CbbTerminalOsTypeEnums.WINDOWS, new WinAppComponentUpgradeHandler());
        upgradeHandlerHolder.put(CbbTerminalOsTypeEnums.ANDROID, new AndroidComponentUpgradeHandler());
        upgradeHandlerHolder.put(CbbTerminalOsTypeEnums.UOS, new UosAppComponentUpgradeHandler());
        upgradeHandlerHolder.put(CbbTerminalOsTypeEnums.NEOKYLIN, new NeoKylinAppComponentUpgradeHandler());
        LOGGER.info("=======================完成注册终端组件升级处理器=================");
    }

    /**
     * 获取终端组件升级处理对象
     * 
     * @param osType 终端系统类型
     * @return 组件升级处理对象
     * @throws BusinessException 业务异常
     */
    public TerminalComponentUpgradeHandler getHandler(CbbTerminalOsTypeEnums osType) throws BusinessException {
        Assert.notNull(osType, "osType can not be null");

        TerminalComponentUpgradeHandler handler = upgradeHandlerHolder.get(osType);

        if (handler == null) {
            LOGGER.error("终端系统类型为[{}]的组件升级处理对象不存在", osType.name());
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_COMPONENT_UPGRADE_HANDLER_NOT_EXIST,
                    new String[] {osType.name()});
        }

        return handler;
    }
}

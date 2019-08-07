package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import com.google.common.collect.Maps;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.TerminalPlatformEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;

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

    private static Map<TerminalPlatformEnums, HashMap<CbbTerminalTypeEnums, AbstractTerminalComponentUpgradeHandler>> upgradeHandlerHolder =
            Maps.newHashMap();

    static {
        LOGGER.info("=======================注册终端组件升级处理器=================");
        HashMap VDIHandlerHolder = Maps.newHashMap();
        VDIHandlerHolder.put(CbbTerminalTypeEnums.LINUX, new LinuxVDIComponentUpgradeHandler());

        HashMap softHandlerHolder = Maps.newHashMap();
        softHandlerHolder.put(CbbTerminalTypeEnums.WINDOWS, new WinSoftComponentUpgradeHandler());

        upgradeHandlerHolder.put(TerminalPlatformEnums.VDI, VDIHandlerHolder);
        upgradeHandlerHolder.put(TerminalPlatformEnums.APP, softHandlerHolder);
    }

    public TerminalComponentUpgradeHandler getHandler(TerminalPlatformEnums platform, CbbTerminalTypeEnums terminalType)
            throws BusinessException {
        Assert.notNull(platform, "platform can not be null");
        Assert.notNull(terminalType, "terminal type can not be null");

        HashMap<CbbTerminalTypeEnums, AbstractTerminalComponentUpgradeHandler> platformHandlerHolder =
                upgradeHandlerHolder.get(platform);
        if (CollectionUtils.isEmpty(platformHandlerHolder)) {
            LOGGER.error("平台类型为[{}]的组件升级处理对象不存在", platform.name());
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_COMPONENT_UPGRADE_HANDLER_NOT_EXIST,
                    new String[] {platform.name(), terminalType.name()});
        }
        AbstractTerminalComponentUpgradeHandler handler = platformHandlerHolder.get(terminalType);
        if (handler == null) {
            LOGGER.error("平台类型为[{}]、终端类型为[{}]的组件升级处理对象不存在", platform.name(), terminalType.name());
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_COMPONENT_UPGRADE_HANDLER_NOT_EXIST,
                    new String[] {platform.name(), terminalType.name()});
        }

        return handler;
    }
}

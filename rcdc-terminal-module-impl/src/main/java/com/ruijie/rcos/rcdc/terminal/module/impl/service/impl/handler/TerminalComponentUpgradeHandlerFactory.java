package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.google.common.collect.Maps;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.TerminalTypeEnums;
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

    private static Map<TerminalTypeEnums, TerminalComponentUpgradeHandler> upgradeHandlerHolder = Maps.newHashMap();

    static {
        LOGGER.info("=======================注册终端组件升级处理器=================");
        upgradeHandlerHolder.put(TerminalTypeEnums.VDI_LINUX, new LinuxVDIComponentUpgradeHandler());
        upgradeHandlerHolder.put(TerminalTypeEnums.APP_WINDOWS, new WinAppComponentUpgradeHandler());
        LOGGER.info("=======================完成注册终端组件升级处理器=================");
    }

    /**
     * 获取终端组件升级处理对象
     * 
     * @param terminalType 终端类型
     * @return 组件升级处理对象
     * @throws BusinessException 业务异常
     */
    public TerminalComponentUpgradeHandler getHandler(TerminalTypeEnums terminalType) throws BusinessException {
        Assert.notNull(terminalType, "terminal type can not be null");

        TerminalComponentUpgradeHandler handler = upgradeHandlerHolder.get(terminalType);

        if (handler == null) {
            LOGGER.error("终端类型为[{}]的组件升级处理对象不存在", terminalType.name());
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_COMPONENT_UPGRADE_HANDLER_NOT_EXIST,
                    new String[] {terminalType.name()});
        }

        return handler;
    }
}

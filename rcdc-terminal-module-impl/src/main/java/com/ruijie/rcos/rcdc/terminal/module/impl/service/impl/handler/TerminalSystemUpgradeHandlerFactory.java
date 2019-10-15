package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler;

import com.google.common.collect.Maps;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.TerminalTypeEnums;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Map;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/10/10
 *
 * @author hs
 */
@Service
public class TerminalSystemUpgradeHandlerFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(TerminalSystemUpgradeHandlerFactory.class);

    private static Map<TerminalTypeEnums, TerminalSystemUpgradeHandler> systemUpgradeHandlerHolder = Maps.newHashMap();

    static {
        systemUpgradeHandlerHolder.put(TerminalTypeEnums.VDI_LINUX, new LinuxVDISystemUpgradeHandler());
        systemUpgradeHandlerHolder.put(TerminalTypeEnums.VDI_ANDROID, new AndroidVDISystemUpgradeHandler());
    }

    /**
     * 获取终端组件升级处理对象
     *
     * @param terminalType 终端类型
     * @return 组件升级处理对象
     * @throws BusinessException 业务异常
     */
    public TerminalSystemUpgradeHandler getHandler(TerminalTypeEnums terminalType) throws BusinessException {
        Assert.notNull(terminalType, "terminal type can not be null");

        TerminalSystemUpgradeHandler handler = systemUpgradeHandlerHolder.get(terminalType);

        if (handler == null) {
            LOGGER.error("终端类型为[{}]的系统升级处理对象不存在", terminalType.name());
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_HANDLER_NOT_EXIST,
                    new String[] {terminalType.name()});
        }

        return handler;
    }


}

package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler;

import com.google.common.collect.Maps;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.modulekit.api.bootstrap.SafetySingletonInitializer;
import org.springframework.beans.factory.annotation.Autowired;
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
public class TerminalSystemUpgradeHandlerFactory implements SafetySingletonInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(TerminalSystemUpgradeHandlerFactory.class);

    private static Map<CbbTerminalTypeEnums, TerminalSystemUpgradeHandler> systemUpgradeHandlerHolder = Maps.newHashMap();

    @Autowired
    private LinuxVDISystemUpgradeHandler linuxVDISystemUpgradeHandler;

    @Autowired
    private AndroidVDISystemUpgradeHandler androidVDISystemUpgradeHandler;

    @Override
    public void safeInit() {
        systemUpgradeHandlerHolder.put(CbbTerminalTypeEnums.VDI_LINUX, linuxVDISystemUpgradeHandler);
        systemUpgradeHandlerHolder.put(CbbTerminalTypeEnums.VDI_ANDROID, androidVDISystemUpgradeHandler);
    }

    /**
     * 获取终端组件升级处理对象
     *
     * @param terminalType 终端类型
     * @return 组件升级处理对象
     * @throws BusinessException 业务异常
     */
    public TerminalSystemUpgradeHandler getHandler(CbbTerminalTypeEnums terminalType) throws BusinessException {
        Assert.notNull(terminalType, "terminal type can not be null");

        TerminalSystemUpgradeHandler handler = systemUpgradeHandlerHolder.get(terminalType);

        if (handler == null) {
            LOGGER.error("终端类型为[{}]的系统升级处理对象不存在", terminalType.name());
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_OTA_UPGRADE_HANDLER_NOT_EXIST,
                    new String[] {terminalType.name()});
        }

        return handler;
    }


}

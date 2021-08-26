package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.systemupgrade;

import com.google.common.collect.Maps;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.TerminalTypeArchType;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

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

    private static final Map<TerminalTypeArchType, TerminalSystemUpgradeHandler> SYSTEM_UPGRADE_HANDLER = Maps.newHashMap();

    @Autowired
    private LinuxVDISystemUpgradeHandler linuxVDISystemUpgradeHandler;

    @Autowired
    private SystemOtaUpgradeHandler systemOtaUpgradeHandler;

    /**
     * 获取终端组件升级处理对象
     *
     * @param terminalArchType 终端类型
     * @return 组件升级处理对象
     * @throws BusinessException 业务异常
     */
    public TerminalSystemUpgradeHandler getHandler(TerminalTypeArchType terminalArchType) throws BusinessException {
        Assert.notNull(terminalArchType, "terminalArchType can not be null");

        if (CollectionUtils.isEmpty(SYSTEM_UPGRADE_HANDLER)) {
            synchronized (SYSTEM_UPGRADE_HANDLER) {
                if (CollectionUtils.isEmpty(SYSTEM_UPGRADE_HANDLER)) {
                    LOGGER.info("初始化终端系统升级处理对象工厂");
                    init();
                }
            }
        }

        TerminalSystemUpgradeHandler handler = SYSTEM_UPGRADE_HANDLER.get(terminalArchType);

        if (handler == null) {
            LOGGER.error("终端类型为[{}]的系统升级处理对象不存在", terminalArchType.name());
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_HANDLER_NOT_EXIST,
                    new String[]{terminalArchType.name()});
        }

        return handler;
    }

    private void init() {
        SYSTEM_UPGRADE_HANDLER.put(TerminalTypeArchType.VDI_LINUX_X86, linuxVDISystemUpgradeHandler);
        SYSTEM_UPGRADE_HANDLER.put(TerminalTypeArchType.VDI_LINUX_ARM, systemOtaUpgradeHandler);
        SYSTEM_UPGRADE_HANDLER.put(TerminalTypeArchType.VDI_ANDROID_ARM, systemOtaUpgradeHandler);
        SYSTEM_UPGRADE_HANDLER.put(TerminalTypeArchType.IDV_LINUX_X86, systemOtaUpgradeHandler);
    }
}

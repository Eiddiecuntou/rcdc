package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.systemupgrade;

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
public class TerminalSystemUpgradePackageHandlerFactory implements SafetySingletonInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(TerminalSystemUpgradePackageHandlerFactory.class);

    private static Map<CbbTerminalTypeEnums, TerminalSystemUpgradePackageHandler> systemUpgradeHandlerHolder = Maps.newHashMap();

    @Autowired
    private LinuxVDISystemUpgradePackageHandler linuxVDISystemUpgradePackageHandler;

    @Autowired
    private AndroidVDISystemUpgradePackageHandler androidVDISystemUpgradePackageHandler;

    @Autowired
    private LinuxIDVSystemUpgradePackageHandler linuxIDVSystemUpgradePackageHandler;

    @Override
    public void safeInit() {
        systemUpgradeHandlerHolder.put(CbbTerminalTypeEnums.VDI_LINUX, linuxVDISystemUpgradePackageHandler);
        systemUpgradeHandlerHolder.put(CbbTerminalTypeEnums.VDI_ANDROID, androidVDISystemUpgradePackageHandler);
        systemUpgradeHandlerHolder.put(CbbTerminalTypeEnums.IDV_LINUX, linuxIDVSystemUpgradePackageHandler);
    }

    /**
     * 获取终端系统升级包处理对象
     *
     * @param terminalType 终端类型
     * @return 终端系统升级包处理对象
     * @throws BusinessException 业务异常
     */
    public TerminalSystemUpgradePackageHandler getHandler(CbbTerminalTypeEnums terminalType) throws BusinessException {
        Assert.notNull(terminalType, "terminal type can not be null");

        checkReady();
        TerminalSystemUpgradePackageHandler handler = systemUpgradeHandlerHolder.get(terminalType);

        if (handler == null) {
            LOGGER.error("终端类型为[{}]的系统升级包处理对象不存在", terminalType.name());
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_HANDLER_NOT_EXIST,
                    new String[] {terminalType.name()});
        }

        return handler;
    }

    private void checkReady() {
        if (systemUpgradeHandlerHolder.isEmpty()) {
            LOGGER.info("系统升级包holder未初始化");
            safeInit();
            LOGGER.info("完成系统升级holder初始化");
        }
    }
}

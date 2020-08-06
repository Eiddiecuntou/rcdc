package com.ruijie.rcos.rcdc.terminal.module.impl.spi;

import com.ruijie.rcos.base.sysmanage.module.def.dto.BaseUpgradeDTO;
import com.ruijie.rcos.base.sysmanage.module.def.spi.BaseMaintenanceModeNotifySPI;
import com.ruijie.rcos.rcdc.terminal.module.impl.spi.maintenance.BeforeEnteringMaintenanceHandler;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.modulekit.api.comm.DispatcherImplemetion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

/**
 * Description: 维护模式通知
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/04/02
 *
 * @author nt
 */
@DispatcherImplemetion("MaintenanceModeNotifySPIImpl")
public class MaintenanceModeNotifySPIImpl implements BaseMaintenanceModeNotifySPI {

    private static final Logger LOGGER = LoggerFactory.getLogger(MaintenanceModeNotifySPIImpl.class);

    @Autowired
    private BeforeEnteringMaintenanceHandler beforeEnteringMaintenanceHandler;

    @Override
    public void beforeEnteringMaintenance(String dispatchKey, BaseUpgradeDTO baseUpgradeDTO) throws BusinessException {
        Assert.hasText(dispatchKey, "dispatchKey can not be blank");
        LOGGER.info("维护：rcdc-terminal：beforeEnteringMaintenance-before");
        beforeEnteringMaintenanceHandler.handle();
        LOGGER.info("维护：rcdc-terminal：beforeEnteringMaintenance-after");
    }

    @Override
    public void afterEnteringMaintenance(String dispatchKey, BaseUpgradeDTO baseUpgradeDTO) throws BusinessException {
        Assert.hasText(dispatchKey, "dispatchKey can not be blank");

        // 不需处理，直接响应成功
    }

    @Override
    public void afterUnderMaintenance(String dispatchKey, BaseUpgradeDTO baseUpgradeDTO) {
        Assert.hasText(dispatchKey, "dispatchKey can not be blank");

        // 不需处理，直接响应成功
    }

    @Override
    public void afterMaintenanceEnd(String dispatchKey, BaseUpgradeDTO baseUpgradeDTO) throws BusinessException {
        Assert.hasText(dispatchKey, "dispatchKey can not be blank");

        // 不需处理，直接响应成功
    }
}

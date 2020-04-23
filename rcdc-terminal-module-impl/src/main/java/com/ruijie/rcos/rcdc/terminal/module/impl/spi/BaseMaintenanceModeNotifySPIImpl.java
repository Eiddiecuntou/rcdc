package com.ruijie.rcos.rcdc.terminal.module.impl.spi;

import com.ruijie.rcos.base.sysmanage.module.def.dto.BaseUpgradeDTO;
import com.ruijie.rcos.base.sysmanage.module.def.spi.BaseMaintenanceModeNotifySPI;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.modulekit.api.comm.DispatcherImplemetion;
import com.ruijie.rcos.sk.modulekit.api.comm.Response;

/**
 * Description: 监听维护模式
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/4/22
 *
 * @author XiaoJiaXin
 */
@DispatcherImplemetion("BaseMaintenanceModeNotifySPIImpl")
public class BaseMaintenanceModeNotifySPIImpl implements BaseMaintenanceModeNotifySPI {

    @Override
    public Response beforeEnteringMaintenance(String s, BaseUpgradeDTO baseUpgradeDTO) throws BusinessException {
        return null;
    }

    @Override
    public Response afterEnteringMaintenance(String s, BaseUpgradeDTO baseUpgradeDTO) throws BusinessException {
        return null;
    }

    @Override
    public Response afterUnderMaintenance(String s, BaseUpgradeDTO baseUpgradeDTO) {
        return null;
    }

    @Override
    public Response afterMaintenanceEnd(String s, BaseUpgradeDTO baseUpgradeDTO) throws BusinessException {
        return null;
    }
}

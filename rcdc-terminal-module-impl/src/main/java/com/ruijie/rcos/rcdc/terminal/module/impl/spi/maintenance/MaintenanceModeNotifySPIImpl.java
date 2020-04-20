package com.ruijie.rcos.rcdc.terminal.module.impl.spi.maintenance;

import com.ruijie.rcos.base.sysmanage.module.def.spi.BaseMaintenanceModeNotifySPI;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultResponse;
import com.ruijie.rcos.sk.modulekit.api.comm.DispatcherImplemetion;
import com.ruijie.rcos.sk.modulekit.api.comm.DispatcherKey;
import com.ruijie.rcos.sk.modulekit.api.comm.Response;
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
@DispatcherImplemetion(Constants.MAINTENANCE_MODE_TERMINAL)
public class MaintenanceModeNotifySPIImpl implements BaseMaintenanceModeNotifySPI {

    @Autowired
    private BeforeEnteringMaintenanceHandler beforeEnteringMaintenanceHandler;

    @Override
    public Response beforeEnteringMaintenance(@DispatcherKey String dispatchKey) throws BusinessException {
        Assert.hasText(dispatchKey, "dispatchKey can not be blank");

        beforeEnteringMaintenanceHandler.handle();

        return DefaultResponse.Builder.success();
    }

    @Override
    public Response afterEnteringMaintenance(@DispatcherKey String dispatchKey) throws BusinessException {
        Assert.hasText(dispatchKey, "dispatchKey can not be blank");

        // 不需处理，直接响应成功
        return DefaultResponse.Builder.success();
    }

    @Override
    public Response afterUnderMaintenance(@DispatcherKey String dispatchKey) {
        Assert.hasText(dispatchKey, "dispatchKey can not be blank");

        // 不需处理，直接响应成功
        return DefaultResponse.Builder.success();
    }

    @Override
    public Response afterMaintenanceEnd(@DispatcherKey String dispatchKey) throws BusinessException {
        Assert.hasText(dispatchKey, "dispatchKey can not be blank");

        // 不需处理，直接响应成功
        return DefaultResponse.Builder.success();
    }
}

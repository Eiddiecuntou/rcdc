package com.ruijie.rcos.rcdc.terminal.module.impl.spi;

import com.ruijie.rcos.rcdc.terminal.module.def.spi.CbbDispatcherHandlerSPI;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.request.CbbDispatcherRequest;
import com.ruijie.rcos.sk.modulekit.api.comm.DispatcherImplemetion;

/**
 * 
 * Description: ip变更
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年1月16日
 * 
 * @author nt
 */
@DispatcherImplemetion("ipchange")
public class IpChangeSPIImpl implements CbbDispatcherHandlerSPI {

    @Override
    public void dispatch(CbbDispatcherRequest request) {
        // TODO ip变更spi还未提供

    }
}
